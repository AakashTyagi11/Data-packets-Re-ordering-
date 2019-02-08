package controller.fifo;

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
	private Queue queue;
	// largest sequence of any packet seen so far
	private long currentSequence;

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

	public Router(EventList futureEventList, Client client, long seed) {
		this.futureEventList = futureEventList;
		this.queue = new Queue();
		this.routerBusy = false;
		this.dropCount = 0;
		this.outOfOrderCount = 0;
		this.clock = 0;
		this.routerBusyTime = 0;
		this.lastEventTime = 0;
		this.numArrivals = 0;
		this.currentSequence=0;
		this.client = client;
		rs = new Random(seed);
	}

	public double getDropRate() {
		return (double) dropCount / numArrivals;
	}

	public double getOutOfOrderRate() {
		return (double) outOfOrderCount / numArrivals;
	}

	public double getServerUtilization() {
		return routerBusyTime / clock;
	}

	public double numArrivals() {
		return this.numArrivals;
	}

	public void scheduleArrival(double clock) {
		double transmissionTime = Constant.PACKET_SIZE / Constant.TRANSMISSION_SPEED_SOURCE;
		double transmissionDelay = generateTransmissionDelay();
		transmissionTime += transmissionDelay;
		Packet packet = new Packet(clock);
		Event event = new Event(Constant.ARRIVAL_ROUTER, clock + transmissionTime, packet);
		futureEventList.enqueue(event);
	}

	public void processArrival(Event event) {
		clock = event.get_time();
		Packet packet = event.get_packet();
		if(currentSequence>packet.getSequenceNumber()) {
			outOfOrderCount++;
		}
		else {
			currentSequence=packet.getSequenceNumber();
		}
		if (routerBusy) {
			routerBusyTime += clock - lastEventTime;
			if (queue.numElements() > Constant.MAX_QUEUE_SIZE) {
				dropCount++;
			} else {
				queue.enqueue(packet);
			}

		} else {
			scheduleDeparture(packet);
		}
		numArrivals++;
		lastEventTime = clock;
	}

	public void scheduleDeparture(Packet packet) {
		routerBusy = true;
		double serviceTime = Constant.PACKET_SIZE / Constant.TRANSMISSION_SPEED_ROUTER;
		Event event = new Event(Constant.DEPARTURE_ROUTER, clock + serviceTime, packet);
		futureEventList.enqueue(event);
	}

	public void processDeparture(Event event) {
		clock = event.get_time();
		client.scheduleArrival(event.get_packet(), clock);
		if (!queue.empty()) {
			scheduleDeparture((Packet) queue.dequeue());
		} else {
			routerBusy = false;
		}
		lastEventTime = clock;
	}

	private double generateTransmissionDelay() {
		double delay = rs.nextGaussian() * Constant.SIGMA_TRANSMISSION_DELAY_SOURCE
				+ Constant.MEAN_TRANSMISSION_DELAY_SOURCE;
		if (delay > 0) {
			return delay;
		}
		return 0;
	}
}
