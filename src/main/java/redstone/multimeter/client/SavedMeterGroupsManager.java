package redstone.multimeter.client;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;

import redstone.multimeter.RedstoneMultimeterMod;
import redstone.multimeter.client.gui.text.Text;
import redstone.multimeter.client.gui.text.Texts;
import redstone.multimeter.client.meter.ClientMeterGroup;
import redstone.multimeter.client.option.Options;
import redstone.multimeter.common.meter.Meter;
import redstone.multimeter.common.meter.MeterProperties;
import redstone.multimeter.common.network.packets.SetMetersPacket;

public class SavedMeterGroupsManager {

	private static final String SAVED_METER_GROUPS_FILE_NAME = "saved_meter_groups.nbt";
	private static final int SLOT_COUNT = 9;
	private static final int SLOT_OFFSET = -1;
	private static final int WARNING_TIME = 60;

	private final MultimeterClient client;
	private final SavedMeterGroup[] meterGroups;
	private final Path file;

	private State state = State.IDLE;
	private int lastLoadWarningSlot;
	private int bypassLoadWarningTicks;
	private int lastSaveWarningSlot;
	private int bypassSaveWarningTicks;
	private int previewSlot;

	public SavedMeterGroupsManager(MultimeterClient client) {
		this.client = client;
		this.meterGroups = new SavedMeterGroup[SLOT_COUNT];
		this.file = this.client.getConfigDirectory().resolve(SAVED_METER_GROUPS_FILE_NAME);

		this.lastLoadWarningSlot = -1;
		this.bypassLoadWarningTicks = -1;
		this.lastSaveWarningSlot = -1;
		this.bypassSaveWarningTicks = -1;
	}

	public void load() {
		try {
			CompoundTag nbt = NbtIo.read(file);

			if (nbt == null) {
				return;
			}

			for (int i = 0; i < meterGroups.length; i++) {
				int slot = i - SLOT_OFFSET;
				String key = "slot_" + slot;

				Optional<CompoundTag> meterGroupNbtMaybe = nbt.getCompound(key);

				if (meterGroupNbtMaybe.isPresent()) {
					CompoundTag meterGroupNbt = meterGroupNbtMaybe.get();

					String name = meterGroupNbt.getStringOr("name", "");
					ListTag metersNbt = meterGroupNbt.getListOrEmpty("meters");
					List<MeterProperties> meters = new ArrayList<>();

					for (int j = 0; j < metersNbt.size(); j++) {
						meters.add(MeterProperties.fromNbt(metersNbt.getCompound(j).get()));
					}

					meterGroups[i] = new SavedMeterGroup(name, meters);
				}
			}
		} catch (IOException e) {
			RedstoneMultimeterMod.LOGGER.warn("failed to load saved meter groups", e);
		}
	}

	public void save() {
		try {
			CompoundTag nbt = new CompoundTag();

			for (int i = 0; i < meterGroups.length; i++) {
				int slot = i - SLOT_OFFSET;
				String key = "slot_" + slot;

				SavedMeterGroup meterGroup = meterGroups[i];

				if (meterGroup == null) {
					continue;
				}

				CompoundTag meterGroupNbt = new CompoundTag();

				meterGroupNbt.putString("name", meterGroup.getName());
				ListTag metersNbt = new ListTag();
				meterGroupNbt.put("meters", metersNbt);

				for (MeterProperties meter : meterGroup.getMeters()) {
					metersNbt.add(meter.toNbt());
				}

				nbt.put(key, meterGroupNbt);
			}

			NbtIo.write(nbt, file);
		} catch (IOException e) {
			RedstoneMultimeterMod.LOGGER.warn("failed to save saved meter groups", e);
		}
	}

	public SavedMeterGroup getSavedMeterGroup(int slot) {
		return meterGroups[slot + SLOT_OFFSET];
	}

	public void tick() {
		if (bypassLoadWarningTicks >= 0) {
			bypassLoadWarningTicks--;
		}
		if (bypassSaveWarningTicks >= 0) {
			bypassSaveWarningTicks--;
		}
	}

	// called when the Load Meter Group key is pressed
	public void setLoading() {
		if (state == State.LOADING) {
			return;
		}

		state = State.LOADING;

		resetLoadWarning();
		stopPreviewing();

		if (Options.RedstoneMultimeter.PREVIEW_METER_GROUPS.get()) {
			client.sendMessage(Texts.translatable("rsmm.meterGroups.preview"), true);
		} else {
			client.sendMessage(Texts.translatable("rsmm.meterGroups.load"), true);
		}
	}

	// called when a number key is pressed
	public boolean loadSlot(int slot) {
		if (state != State.LOADING) {
			return false;
		}

		if (Options.RedstoneMultimeter.PREVIEW_METER_GROUPS.get()) {
			SavedMeterGroup meterGroup = meterGroups[slot + SLOT_OFFSET];

			if (slot != previewSlot) {
				// not previewing that slot yet, start
				if (meterGroup == null) {
					return previewSlot(slot, null, null);
				} else {
					String name = meterGroup.getName();
					List<MeterProperties> meters = meterGroup.getMeters();

					return previewSlot(slot, name, meters);
				}
			} else {
				// previewing that slot, load it
				slot = previewSlot;
			}
		}

		SavedMeterGroup meterGroup = meterGroups[slot + SLOT_OFFSET];

		if (meterGroup == null) {
			return loadMeterGroup(slot, null, null);
		} else {
			String name = meterGroup.getName();
			List<MeterProperties> meters = meterGroup.getMeters();

			return loadMeterGroup(slot, name, meters);
		}
	}

	private boolean previewSlot(int slot, String name, List<MeterProperties> meters) {
		SavedMeterGroup meterGroup = meterGroups[slot + SLOT_OFFSET];

		if (meterGroup == null) {
			client.sendMessage(Texts.translatable("rsmm.meterGroups.preview.failure.emptySlot", slot), true);
		} else {
			client.getMeterGroupPreview().preview(slot, name, meters);
			client.sendMessage(Texts.translatable("rsmm.meterGroups.preview.success", name, slot), true);
		}

		previewSlot = slot;

		return true;
	}

	private boolean loadMeterGroup(int slot, String name, List<MeterProperties> meters) {
		if (slot != lastLoadWarningSlot) {
			resetLoadWarning();
		}

		boolean bypassWarnings = (bypassLoadWarningTicks >= 0) || Options.RedstoneMultimeter.BYPASS_WARNINGS.get();

		if (name == null) {
			if (client.hasSubscription()) {
				if (bypassWarnings) {
					client.unsubscribeFromMeterGroup();
					client.sendMessage(Texts.translatable("rsmm.meterGroups.load.success.unsubscribed", slot), true);

					setIdle();
				} else {
					sendLoadWarning(slot, Texts.translatable("rsmm.meterGroups.load.failure.emptySlot", slot));
				}
			} else {
				// not currently subscribed to a meter group and selected slot is empty
				// i.e. nothing to do here
			}
		} else {
			client.subscribeToMeterGroup(slot, name);
			client.sendMessage(Texts.translatable("rsmm.meterGroups.load.success.subscribed", name, slot), true);

			SetMetersPacket packet = new SetMetersPacket(meters);
			client.sendPacket(packet);

			setIdle();
		}

		return true;
	}

	private void resetLoadWarning() {
		lastLoadWarningSlot = -1;
		bypassLoadWarningTicks = -1;
	}

	private void sendLoadWarning(int slot, Text warning) {
		lastLoadWarningSlot = slot;
		bypassLoadWarningTicks = WARNING_TIME;

		client.sendMessage(warning, true);
	}

	// called when the Save Meter Group key is pressed
	public void setSaving() {
		if (state == State.SAVING) {
			return;
		}

		state = State.SAVING;

		resetSaveWarning();
		stopPreviewing();

		if (client.hasSubscription()) {
			client.sendMessage(Texts.translatable("rsmm.meterGroups.save"), true);
		} else {
			client.sendMessage(Texts.translatable("rsmm.meterGroups.clear"), true);
		}
	}

	// called when a number key is pressed
	public boolean saveSlot(int slot) {
		if (state != State.SAVING) {
			return false;
		}

		if (!client.hasSubscription()) {
			return saveMeterGroup(slot, null, null);
		} else {
			ClientMeterGroup meterGroup = client.getMeterGroup();

			String name = meterGroup.getName();
			List<Meter> meters = meterGroup.getMeters();

			return saveMeterGroup(slot, name, meters);
		}
	}

	private boolean saveMeterGroup(int slot, String name, List<Meter> meters) {
		if (slot != lastSaveWarningSlot) {
			resetSaveWarning();
		}

		boolean bypassWarnings = (bypassSaveWarningTicks >= 0) || Options.RedstoneMultimeter.BYPASS_WARNINGS.get();

		if (name == null) {
			if (bypassWarnings) {
				meterGroups[slot + SLOT_OFFSET] = null;
				client.sendMessage(Texts.translatable("rsmm.meterGroups.clear.success", slot), true);
			} else {
				sendSaveWarning(slot, Texts.translatable("rsmm.meterGroups.clear.failure.emptyMeterGroup", slot));
			}
		} else {
			if (meters.isEmpty() && !bypassWarnings) {
				sendSaveWarning(slot, Texts.translatable("rsmm.meterGroups.save.failure.emptyMeterGroup", slot));
			} else {
				List<MeterProperties> savedMeters = new ArrayList<>();

				for (Meter meter : meters) {
					savedMeters.add(meter.getProperties());
				}

				meterGroups[slot + SLOT_OFFSET] = new SavedMeterGroup(name, savedMeters);
				client.sendMessage(Texts.translatable("rsmm.meterGroups.save.success", name, slot), true);
			}
		}

		return true;
	}

	private void resetSaveWarning() {
		lastSaveWarningSlot = -1;
		bypassSaveWarningTicks = -1;
	}

	private void sendSaveWarning(int slot, Text warning) {
		lastSaveWarningSlot = slot;
		bypassSaveWarningTicks = WARNING_TIME;

		client.sendMessage(warning, true);
	}

	public void setIdle() {
		if (state == State.IDLE) {
			return;
		}

		state = State.IDLE;

		resetLoadWarning();
		resetSaveWarning();
		stopPreviewing();
	}

	private void stopPreviewing() {
		if (previewSlot != -1) {
			client.getMeterGroupPreview().stopPreviewing();
		}

		previewSlot = -1;
	}

	private enum State {
		IDLE, LOADING, SAVING
	}
}
