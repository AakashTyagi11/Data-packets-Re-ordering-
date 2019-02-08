package model;
import constant.Constant;

public class Packet {
	//number of packets generated so far
	static long numPacketsGenerated;
	
	//unique numbers (starting from 0 )that identifies the packet
	private long sequenceNumber;
	
	//packet size in number of bytes
	private double size;
	
	
	private double arrivalTime;

	public Packet(double clock){
		this.sequenceNumber=numPacketsGenerated++;
		this.size=Constant.PACKET_SIZE;
		this.arrivalTime=clock;
	}

	public double getDelay(double clock) {
		return clock - this.arrivalTime;
	}
	
	public long getSequenceNumber() {
		return sequenceNumber;
	}
	
	public double getSize() {
		return size;
	}
}
