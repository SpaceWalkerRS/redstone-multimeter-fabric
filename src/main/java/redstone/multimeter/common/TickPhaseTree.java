package redstone.multimeter.common;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import net.minecraft.nbt.NbtByteArray;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;

import redstone.multimeter.RedstoneMultimeterMod;
import redstone.multimeter.interfaces.mixin.INbtList;
import redstone.multimeter.util.NbtUtils;

public class TickPhaseTree {

	public final Node root;

	private Node current;
	private boolean building;
	private boolean complete;

	public TickPhaseTree() {
		this.root = new Node(null, TickTask.RUN_LOOP);

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

	public void reset() {
		if (building) {
			RedstoneMultimeterMod.LOGGER.warn("Cannot reset tick phase tree: currently building!");
		} else {
			root.children.clear();
			current = root;
			building = false;
			complete = false;
		}
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

			prune();
		} else {
			RedstoneMultimeterMod.LOGGER.warn("Cannot complete tick phase tree: not building!");
		}
	}

	private void prune() {
		new Pruner().run();
	}

	public void startTask(TickTask task, String... args) {
		if (building) {
			current = new Node(current, task, args);
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

	private void addNode(NbtList tasks, NbtList args, Node node, int depth) {
		if (depth > 0) { // depth 0 is root
			byte[] array = new byte[3];
			array[0] = (byte)depth;
			array[1] = (byte)node.task.getId();
			array[2] = (byte)node.args.length;
			NbtByteArray taskNbt = new NbtByteArray(array);

			tasks.add(taskNbt);

			for (int index = 0; index < node.args.length; index++) {
				String arg = node.args[index];
				NbtString argNbt = new NbtString(arg);

				args.add(argNbt);
			}
		}

		depth++;

		for (int index = 0; index < node.children.size(); index++) {
			addNode(tasks, args, node.children.get(index), depth);
		}
	}

	public void fromNbt(NbtCompound nbt) {
		NbtList tasks = nbt.getList("tasks", NbtUtils.TYPE_BYTE_ARRAY);
		NbtList args = nbt.getList("args", NbtUtils.TYPE_STRING);

		if (tasks.size() > 0) {
			start();
			addNode(tasks, args, 0, 0, 0);
			end();
		}
	}

	private void addNode(NbtList tasks, NbtList args, int taskIndex, int argIndex, int lastDepth) {
		NbtByteArray taskNbt = ((INbtList)tasks).getByteArray(taskIndex);
		byte[] array = taskNbt.getByteArray();
		int depth = array[0];
		TickTask task = TickTask.byId(array[1]);
		int argsLength = array[2];

		String[] taskArgs;

		if (argsLength > 0) {
			taskArgs = new String[argsLength];

			for (int i = 0; i < argsLength && argIndex < args.size();) {
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

	public class Node {

		public final Node parent;
		public final List<Node> children;
		public final TickTask task;
		public final String[] args;

		public Node(Node parent, TickTask task, String... args) {
			this.parent = parent;
			this.children = new ArrayList<>();
			this.task = task;
			this.args = args;
		}
	}

	private class Pruner {

		private final Set<TickTask> tasks = EnumSet.noneOf(TickTask.class);
		private final Stack<Set<TickTask>> layers = new Stack<>();
		private final Stack<TickTask> phase = new Stack<>();

		private void run() {
			tasks.clear();
			layers.clear();
			phase.clear();

			layers.push(EnumSet.noneOf(TickTask.class));

			prune(root);
		}

		private boolean prune(Node node) {
			TickTask task = node.task;
			PruneType type = task.getPruneType();

			if (type.is(PruneType.TREE) && tasks.contains(task)) {
				return true;
			}

			Set<TickTask> layer = layers.peek();

			if (type.is(PruneType.SIBLING) && layer.contains(task)) {
				return true;
			}
			if (type.is(PruneType.BRANCH) && phase.contains(task)) {
				return true;
			}

			tasks.add(task);
			layer.add(task);

			phase.push(task);
			layers.push(EnumSet.noneOf(TickTask.class));

			for (Iterator<Node> it = node.children.iterator(); it.hasNext(); ) {
				Node child = it.next();

				if (prune(child)) {
					it.remove();
				}
			}

			phase.pop();
			layers.pop();

			return false;
		}
	}
}
