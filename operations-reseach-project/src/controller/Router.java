package controller;

import java.util.Random;

import constant.Constant;
import model.Event;
import model.EventList;
import model.Packet;
import model.Queue;

public class Router {
	// stream of random numbers
	private Random rs;
	// simulation clock
	private double clock;
	// time in seconds for which the router is busy
	private double routerBusyTime;
	// clock time for the last event in router
	private double lastEventTime;
	// largest sequence of any packet seen so far
	private long currentSequence;
	// queue where out of order packets are added
	private Queue highPriorityQueue;
	// queue where in order packets are added
	private Queue lowPriorityQueue;
	// status of router
	private double maxHighPriorityQueueLength;
	private double maxLowPriorityQueueLength;
	private boolean routerBusy;
	// number of packets dropped
	private long dropCount;
	// number of packets out of order
	private long outOfOrderCount;
	// client object
	private Client client;
	// data structure for saving future events
	private EventList futureEventList;
	
	private double numArrivals;

	public Router(EventList futureEventList,Client client,long seed) {
		this.futureEventList = futureEventList;
		this.lowPriorityQueue = new Queue();
		this.highPriorityQueue = new Queue();
		this.currentSequence = 0;
		this.routerBusy = false;
		this.dropCount = 0;
		this.outOfOrderCount = 0;
		this.clock=0;
		this.routerBusyTime=0;
		this.lastEventTime=0;
		this.numArrivals=0;
		this.maxHighPriorityQueueLength=0;
		this.maxLowPriorityQueueLength=0;
		this.client=client;
		rs=new Random(seed);
	}

	public double getDropRate() {
		return (double)dropCount/numArrivals;
	}
	public double getOutOfOrderRate() {
		return (double)outOfOrderCount/numArrivals;
	}
	public double getServerUtilization() {
		return routerBusyTime/clock;
	}
	public double maxLowPriorityLength() {
		return this.maxLowPriorityQueueLength;
	}
	public double maxHighPriorityLength() {
		return this.maxHighPriorityQueueLength;
	}
	
	public double numArrivals() {
		return this.numArrivals;
	}
	
	public long getDropCount() {
		return this.dropCount;
	}
	
	public long getOutOfOrderCount() {
		return this.outOfOrderCount;
	}
	
	public void scheduleArrival(double clock){
		double transmissionTime=Constant.PACKET_SIZE/Constant.TRANSMISSION_SPEED_SOURCE;
		double transmissionDelay=generateTransmissionDelay();
		transmissionTime+=transmissionDelay;
		Packet packet=new Packet(clock);
		Event event=new Event(Constant.ARRIVAL_ROUTER,clock+transmissionTime,packet);
		futureEventList.enqueue(event);
	}
	
	public void processArrival(Event event) {
		clock=event.get_time();
		Packet packet = event.get_packet();
		if (routerBusy) {
			routerBusyTime += clock - lastEventTime;
			if (packet.getSequenceNumber() > currentSequence) {
				if (lowPriorityQueue.numElements() > Constant.MAX_QUEUE_SIZE) {
					dropCount++;
				} else {
					lowPriorityQueue.enqueue(packet);
				}
				currentSequence = packet.getSequenceNumber();

			} else {
				outOfOrderCount++;
				if (highPriorityQueue.numElements() > Constant.MAX_QUEUE_SIZE) {
					dropCount++;
				} else {
					highPriorityQueue.enqueue(packet);
				}
			}
			if(maxHighPriorityQueueLength<highPriorityQueue.numElements()) {
				maxHighPriorityQueueLength=highPriorityQueue.numElements();
			}
			if(maxLowPriorityQueueLength<lowPriorityQueue.numElements()) {
				maxLowPriorityQueueLength=lowPriorityQueue.numElements();
			}
		} else {
			scheduleDeparture(packet);
		}
		numArrivals++;
		lastEventTime=clock;
	}
	
	public void scheduleDeparture(Packet packet) {
		routerBusy=true;
		double serviceTime = Constant.PACKET_SIZE / Constant.TRANSMISSION_SPEED_ROUTER;
		Event event = new Event(Constant.DEPARTURE_ROUTER, clock + serviceTime,packet);
		futureEventList.enqueue(event);
	}
	
	public void processDeparture(Event event) {
		clock=event.get_time();
		client.scheduleArrival(event.get_packet(), clock);
		if (!highPriorityQueue.empty()) {
			scheduleDeparture((Packet) highPriorityQueue.dequeue());
		} else if (!lowPriorityQueue.empty()) {
			scheduleDeparture((Packet) lowPriorityQueue.dequeue());
		} else {
			routerBusy = false;
		}
		lastEventTime=clock;
	}

	private double generateTransmissionDelay() {
		double delay=rs.nextGaussian()*Constant.SIGMA_TRANSMISSION_DELAY_SOURCE+Constant.MEAN_TRANSMISSION_DELAY_SOURCE;
		if(delay>0) {
			return delay;
		}
		return 0;
	}
}
