package controller.fifo;
import java.util.Random;

import constant.Constant;
import model.Event;
import model.EventList;

public class Source {
	private Random rs;
	private EventList futureEventList;
	private Router router;
	
	public Source(EventList futureEventList,Router router,long seed){
		this.rs=new Random(seed);
		this.futureEventList=futureEventList;
		this.router=router;
	}
	
	public void processDeparture(Event e) {
		router.scheduleArrival(e.get_time());
		scheduleDeparture(e.get_time());
	}

	public void scheduleDeparture(double clock) {
		double interDepartureTime=generateInterDepartureTime();
		Event event=new Event(Constant.DEPARTURE_SOURCE,clock+interDepartureTime);
		futureEventList.enqueue(event);
	}
	
	public double generateInterDepartureTime() {
		return -(1/Constant.DEPARTURE_RATE_SOURCE) * Math.log(rs.nextDouble());
	}
	
}
