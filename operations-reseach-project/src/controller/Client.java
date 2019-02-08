package controller;

import constant.Constant;
import model.Event;
import model.EventList;
import model.Packet;

public class Client {

	private EventList futureEventList;
	private double averageDelay;
	private double[] delay;
	private int i;

	public Client(EventList futureEventList) {
		this.futureEventList = futureEventList;
		this.averageDelay = 0;
		this.delay=new double[(int) Constant.SIMULATION_LENGTH+1];
		this.i=0;
	}

	public double [] getDelay() {
		return delay;
	}
	
	public double getAverageDelay() {
		return this.averageDelay / Constant.SIMULATION_LENGTH;
	}

	public void scheduleArrival(Packet packet, double clock) {
		double transmissionDelay = Constant.TRANSMISSION_DELAY_ROUTER;
		Event event = new Event(Constant.ARRIVAL_CLIENT, clock + transmissionDelay, packet);
		futureEventList.enqueue(event);
	}

	public void processArrival(Event event) {
		Packet packet = event.get_packet();
		delay[i]= packet.getDelay(event.get_time());
		this.averageDelay += delay[i];
		i++;
	}
	

}