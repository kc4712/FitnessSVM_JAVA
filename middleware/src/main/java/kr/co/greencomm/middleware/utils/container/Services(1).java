package kr.co.greencomm.middleware.utils.container;

import java.util.ArrayList;
import java.util.UUID;

public final class Services {
	private int isPrimary;
	private UUID serviceid;
	private ArrayList<UUID> charid;
	protected int getIsPrimary() {
		return isPrimary;
	}
	protected void setIsPrimary(int isPrimary) {
		this.isPrimary = isPrimary;
	}
	protected UUID getServiceid() {
		return serviceid;
	}
	protected void setServiceid(UUID serviceid) {
		this.serviceid = serviceid;
	}
	protected ArrayList<UUID> getCharid() {
		return charid;
	}
	protected void setCharid(ArrayList<UUID> charid) {
		this.charid = charid;
	}
	
}