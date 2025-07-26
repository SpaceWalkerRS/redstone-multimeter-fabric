package redstone.multimeter.client.gui;

import java.util.Stack;

// a disgusting hack to abuse depth in GUI rendering
public class DepthOverride {

	private static Stack<Float> depth = new Stack<>();

	static {
		reset();
	}

	public static void reset() {
		depth.clear();
		depth.push(null); // default: no override
	}

	public static Float peek() {
		return depth.peek();
	}

	public static void push() {
		depth.push(depth.peek());
	}

	public static void translate(Float d) {
		Float v = depth.pop();
		depth.push(v == null || d == null ? d : (Float) (v + d));
	}

	public static void pop() {
		depth.pop();
	}
}
