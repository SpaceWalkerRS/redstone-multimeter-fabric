package rsmm.fabric.common;

public class MultimeterTask {
	
	private final Runnable runnable;
	
	public MultimeterTask(Runnable runnable) {
		this.runnable = runnable;
	}
	
	public void run() {
		runnable.run();
	}
}
