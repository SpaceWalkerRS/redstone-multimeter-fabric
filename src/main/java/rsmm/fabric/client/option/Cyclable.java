package rsmm.fabric.client.option;

public interface Cyclable<T extends Cyclable<T>> {
	
	public String getName();
	
	public T next();
	
	public T prev();
	
}
