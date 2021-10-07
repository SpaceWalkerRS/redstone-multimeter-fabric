package rsmm.fabric.client.option;

public abstract class Option<T> implements IOption {
	
	private final String name;
	private final String description;
	private final T defaultValue;
	
	private T value;
	private OptionListener listener;
	
	protected Option(String name, String description, T defaultValue) {
		this.name = name;
		this.description = description;
		this.defaultValue = defaultValue;
		
		this.value = this.defaultValue;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String getDescription() {
		return description;
	}
	
	@Override
	public boolean isDefault() {
		return value.equals(defaultValue);
	}
	
	@Override
	public void reset() {
		set(defaultValue);
	}
	
	@Override
	public String getAsString() {
		return value.toString();
	}
	
	@Override
	public void setListener(OptionListener listener) {
		this.listener = listener;
	}
	
	public T getDefault() {
		return defaultValue;
	}
	
	public T get() {
		return value;
	}
	
	public void set(T value) {
		this.value = value;
		
		if (listener != null) {
			listener.valueChanged();
		}
	}
}
