package redstone.multimeter.common;

import java.util.Arrays;
import java.util.List;

import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.EndTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import redstone.multimeter.util.NBTUtils;
import redstone.multimeter.util.TextUtils;

public class TickPhase {
	
	public static final TickPhase UNKNOWN = new TickPhase(new TickTask[] { TickTask.UNKNOWN });
	
	private final TickTask[] tasks;
	
	public TickPhase(TickTask[] tasks) {
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
		String string = tasks[0].getName();
		
		for (int index = 1; index < tasks.length; index++) {
			string += " > " + tasks[index].getName();
		}
		
		return string;
	}
	
	public void addTextForTooltip(List<Text> lines) {
		TextUtils.addFancyText(lines, "tick phase", tasks[0].getName());
		
		// used to indent subsequent lines
		String whitespace = "              ";
		
		for (int index = 1; index < tasks.length; index++) {
			String text = whitespace + "> " + tasks[index].getName();
			lines.add(new LiteralText(text));
			
			whitespace += "  ";
		}
	}
	
	public TickPhase startTask(TickTask task) {
		if (this == UNKNOWN) {
			return new TickPhase(new TickTask[] { task });
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
	
	public Tag toNBT() {
		if (this == UNKNOWN) {
			return new EndTag();
		}
		
		byte[] array = new byte[tasks.length];
		
		for (int index = 0; index < array.length; index++) {
			array[index] = (byte)tasks[index].getIndex();
		}
		
		return new ByteArrayTag(array);
	}
	
	public static TickPhase fromNBT(Tag nbt) {
		if (nbt.getType() != NBTUtils.TYPE_BYTE_ARRAY) {
			return UNKNOWN;
		}
		
		ByteArrayTag array = (ByteArrayTag)nbt;
		TickTask[] tasks = new TickTask[array.size()];
		
		for (int index = 0; index < tasks.length; index++) {
			int taskIndex = array.get(index).getByte();
			tasks[index] = TickTask.fromIndex(taskIndex);
		}
		
		return new TickPhase(tasks);
	}
}
