package model;

// event representation
@SuppressWarnings("rawtypes")
public class Event implements Comparable {

	public Event(int a_type, double a_time) {
		this._type = a_type;
		this.time = a_time;
	}
	
	public Event(int a_type,double a_time,Packet packet) {
		this._type = a_type;
		this.time = a_time;
		this.packet=packet;
	}

	public double time;
	private int _type;
	private Packet packet;

	public int get_type() {
		return _type;
	}

	public double get_time() {
		return time;
	}
	
	public Packet get_packet() {
		return packet;
	}

	public Event leftlink, rightlink, uplink;

	public int compareTo(Object _cmpEvent) {
		double _cmp_time = ((Event) _cmpEvent).get_time();
		if (this.time < _cmp_time)
			return -1;
		if (this.time == _cmp_time)
			return 0;
		return 1;
	}
};
