package app.fifo;

import constant.Constant;
import controller.fifo.Client;
import controller.fifo.Router;
import controller.fifo.Source;
import model.Event;
import model.EventList;

public class Simulator {
	public static void main(String[] args) {
		
		long numPacketsSimulated = 0;
		long seed=3432;
		EventList futureEventList = new EventList();
		Client client=new Client(futureEventList);
		Router router = new Router(futureEventList,client,seed);
		Source source = new Source(futureEventList,router,seed);
		
		source.scheduleDeparture(0);
		
		while (!futureEventList.isEmpty()) {
			Event event = futureEventList.getMin();
			futureEventList.dequeue();
			if (event.get_type() == Constant.DEPARTURE_SOURCE) {
				source.processDeparture(event);
			} else if (event.get_type() == Constant.ARRIVAL_ROUTER) {
				router.processArrival(event);
			} else if (event.get_type() == Constant.DEPARTURE_ROUTER) {
				router.processDeparture(event);
				numPacketsSimulated++;
				if (numPacketsSimulated > Constant.SIMULATION_LENGTH) {
					break;
				}
			} else if(event.get_type()==Constant.ARRIVAL_CLIENT) {
				client.processArrival(event);
			}
		}

		System.out.println("Server utilization                            " + router.getServerUtilization());
		System.out.println("Average drop rate                             " + router.getDropRate());
		System.out.println("Average out of order rate                     " + router.getOutOfOrderRate());
		System.out.println("Avarage delay                                 " + client.getAverageDelay());
//
//		double[] delay=client.getDelay();
//		for(int i=0;i<delay.length;i++) {
//			System.out.println(delay[i]);
//		}
	}
}