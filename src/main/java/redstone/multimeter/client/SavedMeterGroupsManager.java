package redstone.multimeter.client;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

import redstone.multimeter.RedstoneMultimeterMod;
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
			CompoundTag nbt = NbtIo.read(file.toFile());

			if (nbt == null) {
				return;
			}

			for (int i = 0; i < meterGroups.length; i++) {
				int slot = i - SLOT_OFFSET;
				String key = "slot_" + slot;

				if (nbt.contains(key)) {
					CompoundTag meterGroupNbt = nbt.getCompound(key);

					String name = meterGroupNbt.getString("name");
					ListTag metersNbt = meterGroupNbt.getList("meters", Tag.TAG_COMPOUND);
					List<MeterProperties> meters = new ArrayList<>();

					for (int j = 0; j < metersNbt.size(); j++) {
						meters.add(MeterProperties.fromNbt(metersNbt.getCompound(j)));
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

			NbtIo.write(nbt, file.toFile());
		} catch (IOException e) {
			RedstoneMultimeterMod.LOGGER.warn("failed to save saved meter groups", e);
		}
	}

	public void tick() {
		if (bypassLoadWarningTicks >= 0) {
			bypassLoadWarningTicks--;
		}
		if (bypassSaveWarningTicks >= 0) {
			bypassSaveWarningTicks--;
		}
	}

	public void loadSlot(int slot) {
		SavedMeterGroup meterGroup = meterGroups[slot + SLOT_OFFSET];

		if (meterGroup == null) {
			loadMeterGroup(slot, null, null);
		} else {
			String name = meterGroup.getName();
			List<MeterProperties> meters = meterGroup.getMeters();

			loadMeterGroup(slot, name, meters);
		}
	}

	private void loadMeterGroup(int slot, String name, List<MeterProperties> meters) {
		if (slot != lastLoadWarningSlot) {
			lastLoadWarningSlot = -1;
			bypassLoadWarningTicks = -1;
		}

		boolean bypassWarnings = (bypassLoadWarningTicks >= 0) || Options.RedstoneMultimeter.BYPASS_WARNINGS.get();

		if (name == null) {
			if (client.getMeterGroup().isSubscribed()) {
				if (bypassWarnings) {
					client.unsubscribeFromMeterGroup();
				} else {
					Component warning = new TextComponent("That slot is empty! Are you sure you want to unsubscribe from your current meter group?");
					loadSlotWarning(slot, warning);
				}
			} else {
				Component warning = new TextComponent(String.format("Could not load meter group from slot %d: that slot is empty!", slot));
				client.sendMessage(warning, true);
			}
		} else {
			client.subscribeToMeterGroup(name);

			SetMetersPacket packet = new SetMetersPacket(meters);
			client.sendPacket(packet);

			Component message = new TextComponent("Loaded meter group \'" + name + "\' from slot " + slot);
			client.sendMessage(message, true);
		}
	}

	private void loadSlotWarning(int slot, Component warning) {
		lastLoadWarningSlot = slot;
		bypassLoadWarningTicks = WARNING_TIME;

		client.sendMessage(warning, true);
	}

	public void saveSlot(int slot) {
		ClientMeterGroup meterGroup = client.getMeterGroup();

		if (slot != lastSaveWarningSlot) {
			lastSaveWarningSlot = -1;
			bypassSaveWarningTicks = -1;
		}

		boolean bypassWarnings = (bypassSaveWarningTicks >= 0) || Options.RedstoneMultimeter.BYPASS_WARNINGS.get();

		if (!meterGroup.isSubscribed()) {
			if (bypassWarnings) {
				meterGroups[slot + SLOT_OFFSET] = null;

				Component message = new TextComponent("Cleared saved meter group slot " + slot);
				client.sendMessage(message, true);
			} else {
				Component warning = new TextComponent("You are not subscribed to a meter group! Are you sure you want to clear that slot?");
				saveSlotWarning(slot, warning);
			}
		} else if (!meterGroup.hasMeters() && !bypassWarnings) {
			Component warning = new TextComponent("Your current meter group is empty! Are you sure you want to save it?");
			saveSlotWarning(slot, warning);
		} else {
			String name = meterGroup.getName();
			List<MeterProperties> meters = new ArrayList<>();

			for (Meter meter : meterGroup.getMeters()) {
				meters.add(meter.getProperties());
			}

			meterGroups[slot + SLOT_OFFSET] = new SavedMeterGroup(name, meters);

			Component message = new TextComponent("Saved meter group \'" + name + "\'to slot " + slot);
			client.sendMessage(message, true);
		}
	}

	private void saveSlotWarning(int slot, Component warning) {
		lastSaveWarningSlot = slot;
		bypassSaveWarningTicks = WARNING_TIME;

		client.sendMessage(warning, true);
	}

	public void previewSlot(int slot) {
		SavedMeterGroup meterGroup = meterGroups[slot + SLOT_OFFSET];

		if (meterGroup == null) {
			Component warning = new TextComponent("That slot is empty!");
			client.sendMessage(warning, true);
		} else {
			String name = meterGroup.getName();
			List<MeterProperties> meters = meterGroup.getMeters();

			client.previewMeterGroup(name, meters);

			Component message = new TextComponent("Previewing meter group \'" + name + "\' from slot " + slot);
			client.sendMessage(message, true);

			previewSlot = slot;
		}
	}

	public void loadPreviewSlot() {
		if (previewSlot == -1) {
			Component message = new TextComponent("Not previewing any meter group!");
			client.sendMessage(message, true);
		} else {
			loadSlot(previewSlot);
		}

		previewSlot = -1;
	}

	public void stopPreviewing() {
		if (previewSlot != -1) {
			SavedMeterGroup meterGroup = meterGroups[previewSlot + SLOT_OFFSET];

			if (meterGroup != null) {
				Component message = new TextComponent("Stopped previewing meter group \'" + meterGroup.getName() + "\' from slot " + previewSlot);
				client.sendMessage(message, true);
			}

			previewSlot = -1;
		}
	}

	public boolean isPreviewing(int slot) {
		return slot != -1 && slot == previewSlot;
	}
}
