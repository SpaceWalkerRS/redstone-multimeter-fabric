package rsmm.fabric.server;

public class MultimeterTask {
	
	private final Runnable runnable;
	
	public MultimeterTask(Runnable runnable) {
		this.runnable = runnable;
	}
	
	public void run() {
		runnable.run();
	}
}
