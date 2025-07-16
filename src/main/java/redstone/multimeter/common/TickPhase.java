package redstone.multimeter.common;

import java.util.Arrays;

import net.minecraft.nbt.NbtByteArray;
import net.minecraft.nbt.NbtElement;

import redstone.multimeter.client.gui.text.Texts;
import redstone.multimeter.client.gui.tooltip.TooltipBuilder;
import redstone.multimeter.util.NbtUtils;

public class TickPhase {

	public static final TickPhase UNKNOWN = new TickPhase(TickTask.UNKNOWN);

	private final TickTask[] tasks;

	public TickPhase(TickTask... tasks) {
		this.tasks = tasks;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof TickPhase)) {
			return false;
		}

		return Arrays.equals(tasks, ((TickPhase)obj).tasks);
	}

	@Override
	public String toString() {
		String string = tasks[0].getName().buildString();

		for (int index = 1; index < tasks.length; index++) {
			string += " > " + tasks[index].getName().buildString();
		}

		return string;
	}

	public void buildTooltip(TooltipBuilder builder) {
		builder.line(Texts.keyValue("tick phase", tasks[0].getName()));

		// used to indent subsequent lines
		String whitespace = "              ";

		for (int index = 1; index < tasks.length; index++) {
			builder.line(Texts.composite(
				whitespace,
				"> ",
				tasks[index].getName()
			));

			whitespace += "  ";
		}
	}

	public TickPhase startTask(TickTask task) {
		if (this == UNKNOWN || tasks.length == 0) {
			return new TickPhase(task);
		}

		TickTask[] array = new TickTask[tasks.length + 1];

		for (int index = 0; index < tasks.length; index++) {
			array[index] = tasks[index];
		}
		array[tasks.length] = task;

		return new TickPhase(array);
	}

	public TickPhase endTask() {
		if (this == UNKNOWN || tasks.length == 1) {
			return UNKNOWN;
		}

		TickTask[] array = new TickTask[tasks.length - 1];

		for (int index = 0; index < array.length; index++) {
			array[index] = tasks[index];
		}

		return new TickPhase(array);
	}

	public TickPhase swapTask(TickTask task) {
		if (this == UNKNOWN || tasks.length == 1) {
			return new TickPhase(new TickTask[] { task });
		}

		TickTask[] array = new TickTask[tasks.length];

		for (int index = 0; index < tasks.length; index++) {
			array[index] = tasks[index];
		}
		array[array.length - 1] = task;

		return new TickPhase(array);
	}

	public TickTask peekTask() {
		return tasks.length == 0 ? TickTask.UNKNOWN : tasks[tasks.length - 1];
	}

	public NbtElement toNbt() {
		if (this == UNKNOWN) {
			return NbtUtils.NULL;
		}

		byte[] array = new byte[tasks.length];

		for (int index = 0; index < array.length; index++) {
			array[index] = (byte)tasks[index].getId();
		}

		return new NbtByteArray(null, array);
	}

	public static TickPhase fromNbt(NbtElement nbt) {
		if (nbt.getType() != NbtUtils.TYPE_BYTE_ARRAY) {
			return UNKNOWN;
		}

		NbtByteArray nbtArray = (NbtByteArray)nbt;
		byte[] array = nbtArray.value;
		TickTask[] tasks = new TickTask[array.length];

		for (int index = 0; index < tasks.length; index++) {
			tasks[index] = TickTask.byId(array[index]);
		}

		return new TickPhase(tasks);
	}
}
