package test;

import constant.Constant;
import controller.Client;
import controller.Router;
import controller.Source;
import model.Event;
import model.EventList;

public class Test {
	public static void main(String[] args) {
		EventList futureEventList = new EventList();
		long seed=6977;
		
		Client client=new Client(futureEventList);
		Router router = new Router(futureEventList,client,seed);
		Source source = new Source(futureEventList,router,seed);
		
		source.scheduleDeparture(0);
		
		long numPacketsSimulated=0;
		
		while (!futureEventList.isEmpty()) {
			Event event = futureEventList.getMin();
			futureEventList.dequeue();
			if (event.get_type() == Constant.DEPARTURE_SOURCE) {
				source.processDeparture(event);
				numPacketsSimulated++;

				if(event.get_time()>1) {
					System.out.println(numPacketsSimulated);
					break;
				}
			} else if (event.get_type() == Constant.ARRIVAL_ROUTER) {
				router.processArrival(event);
			} else if (event.get_type() == Constant.ARRIVAL_CLIENT) {
				router.processDeparture(event);
				
				if (numPacketsSimulated > Constant.SIMULATION_LENGTH) {
//					System.out.println("loop break");
//					break;
				}
			}
		}
	}
}
