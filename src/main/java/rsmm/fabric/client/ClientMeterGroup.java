package rsmm.fabric.client;

import rsmm.fabric.common.MeterGroup;

public class ClientMeterGroup extends MeterGroup {
	
	private final MultimeterClient client;
	private final ClientLogManager logManager;
	
	public ClientMeterGroup(MultimeterClient client, String name) {
		super(name);
		
		this.client = client;
		this.logManager = new ClientLogManager(this);
	}
	
	public MultimeterClient getMultimeterClient() {
		return client;
	}
	
	public ClientLogManager getLogManager() {
		return logManager;
	}
}
