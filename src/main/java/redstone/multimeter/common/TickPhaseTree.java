package redstone.multimeter.common;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NbtByteArray;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;

import redstone.multimeter.RedstoneMultimeterMod;

public class TickPhaseTree {
	
	public final TickTaskNode root;
	
	private TickTaskNode current;
	private boolean building;
	private boolean complete;
	
	public TickPhaseTree() {
		this.root = new TickTaskNode(null, TickTask.UNKNOWN);
		
		this.current = root;
		this.building = false;
		this.complete = false;
	}
	
	public boolean isComplete() {
		return complete;
	}
	
	public boolean isBuilding() {
		return building;
	}
	
	public void start() {
		if (building) {
			RedstoneMultimeterMod.LOGGER.warn("Cannot start building tick phase tree: already building!");
		} else {
			root.children.clear();
			current = root;
			building = true;
			complete = false;
		}
	}
	
	public void end() {
		if (building) {
			building = false;
			complete = true;
		} else {
			RedstoneMultimeterMod.LOGGER.warn("Cannot complete tick phase tree: not building!");
		}
	}
	
	public void startTask(TickTask task, String... args) {
		if (building) {
			current = new TickTaskNode(current, task, args);
			current.parent.children.add(current);
		}
	}
	
	public void endTask() {
		if (building) {
			current = current.parent;
			
			if (current == null) {
				current = root; // we should never get here
			}
		}
	}
	
	public void swapTask(TickTask task, String... args) {
		if (building) {
			endTask();
			startTask(task, args);
		}
	}
	
	public NbtCompound toNbt() {
		NbtList tasks = new NbtList();
		NbtList args = new NbtList();
		
		addNode(tasks, args, root, 0);
		
		NbtCompound nbt = new NbtCompound();
		nbt.put("tasks", tasks);
		nbt.put("args", args);
		
		return nbt;
	}
	
	private void addNode(NbtList tasks, NbtList args, TickTaskNode node, int depth) {
		if (depth > 0) { // depth 0 is root
			byte[] array = new byte[3];
			array[0] = (byte)depth;
			array[1] = (byte)node.task.getIndex();
			array[2] = (byte)node.args.length;
			NbtByteArray taskNbt = new NbtByteArray(array);
			
			tasks.add(taskNbt);
			
			for (int index = 0; index < node.args.length; index++) {
				String arg = node.args[index];
				NbtString argNbt = NbtString.of(arg);
				
				args.add(argNbt);
			}
		}
		
		depth++;
		
		for (int index = 0; index < node.children.size(); index++) {
			addNode(tasks, args, node.children.get(index), depth);
		}
	}
	
	public void fromNbt(NbtCompound nbt) {
		NbtList tasks = nbt.getList("tasks", NbtElement.BYTE_ARRAY_TYPE);
		NbtList args = nbt.getList("args", NbtElement.STRING_TYPE);
		
		if (!tasks.isEmpty()) {
			start();
			addNode(tasks, args, 0, 0, 0);
			end();
		}
	}
	
	private void addNode(NbtList tasks, NbtList args, int taskIndex, int argIndex, int lastDepth) {
		NbtByteArray taskNbt = (NbtByteArray)tasks.get(taskIndex);
		byte[] array = taskNbt.getByteArray();
		int depth = array[0];
		TickTask task = TickTask.fromIndex(array[1]);
		int argsLength = array[2];
		
		String[] taskArgs;
		
		if (argsLength > 0) {
			taskArgs = new String[argsLength];
			
			for (int i = 0; i < argsLength && argIndex < args.size(); ) {
				taskArgs[i++] = args.getString(argIndex++);
			}
		} else {
			taskArgs = new String[0];
		}
		
		int endedTasks = lastDepth - depth;
		
		while (endedTasks-- > 0) {
			endTask();
		}
		if (depth > lastDepth) {
			startTask(task, taskArgs);
		} else {
			swapTask(task, taskArgs);
		}
		
		if (++taskIndex < tasks.size()) {
			addNode(tasks, args, taskIndex, argIndex, depth);
		}
	}
	
	public class TickTaskNode {
		
		public final TickTaskNode parent;
		public final List<TickTaskNode> children;
		public final TickTask task;
		public final String[] args;
		
		public TickTaskNode(TickTaskNode parent, TickTask task, String... args) {
			this.parent = parent;
			this.children = new ArrayList<>();
			this.task = task;
			this.args = args;
		}
	}
}
