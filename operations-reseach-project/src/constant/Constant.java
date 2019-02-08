package constant;

public class Constant {

	// number of packets to simulate
	public static long SIMULATION_LENGTH = 15000;
	// packet size in number of bytes
	public static final double PACKET_SIZE = Math.pow(10, 3);
	// queue size in number of packets
	public static final long MAX_QUEUE_SIZE = (long) (Math.pow(10, 7) / PACKET_SIZE);

	// number of packets generated by source per second
	public static final double DEPARTURE_RATE_SOURCE = 1125;
	// transmission speed in bytes per second
	public static final double TRANSMISSION_SPEED_SOURCE = Math.pow(10, 7) / 8;
	// represented by x in problem
	public static final double MEAN_TRANSMISSION_DELAY_SOURCE = 0.05;
	// represented by y in problem
	public static final double SIGMA_TRANSMISSION_DELAY_SOURCE = 0.05;

	// transmission speed in bytes per second
	public static final double TRANSMISSION_SPEED_ROUTER = TRANSMISSION_SPEED_SOURCE/4;
	// transmission delay in seconds
	public static final double TRANSMISSION_DELAY_ROUTER = 0.05;
	// departure event at source
	public static final int DEPARTURE_SOURCE = 1;
	// arrival event at router
	public static final int ARRIVAL_ROUTER = 2;
	// departure event at router
	public static final int DEPARTURE_ROUTER = 3;
	// departure event at router
	public static final int ARRIVAL_CLIENT = 4;
	
	public static final int NUM_INTERVALS = 5;
}
