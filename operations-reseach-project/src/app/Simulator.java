package app;

import constant.Constant;
import controller.Client;
import controller.Router;
import controller.Source;
import model.Event;
import model.EventList;

public class Simulator {
	public static void main(String[] args) {
		
		long numPacketsSimulated = 0;
		long seed=9439;
		EventList futureEventList = new EventList();
		Client client=new Client(futureEventList);
		Router router = new Router(futureEventList,client,seed);
		Source source = new Source(futureEventList,router,seed);
		
		source.scheduleDeparture(0);
		
		long intervalDropCount[]=new long[Constant.NUM_INTERVALS+1];
		long intervalOutOfOrderCount[]=new long[Constant.NUM_INTERVALS+1];
		
		double intervalMeanDropCount[]=new double[Constant.NUM_INTERVALS+1];
		double intervalMeanOutOfOrderCount[] = new double[Constant.NUM_INTERVALS+1];
		
		
		boolean intervalCovered[]=new boolean[Constant.NUM_INTERVALS+1];
		double intervalLength=Constant.SIMULATION_LENGTH/Constant.NUM_INTERVALS;
		intervalCovered[0]=true;
		
		double sumArrivals=0;
		double intervalArrivals;
		long sumDropCount=0;
		long sumOutOfOrderCount=0;
		
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
			
			int interval=(int) (numPacketsSimulated/intervalLength);
			if(!intervalCovered[interval]) {
				
				intervalDropCount[interval-1]=router.getDropCount()-sumDropCount;
				intervalOutOfOrderCount[interval-1]=router.getOutOfOrderCount()-sumOutOfOrderCount;
				
				intervalArrivals=router.numArrivals()-sumArrivals;
				
				
				intervalMeanDropCount[interval-1]=intervalDropCount[interval-1]/intervalArrivals;
				intervalMeanOutOfOrderCount[interval-1]=intervalOutOfOrderCount[interval-1]/intervalArrivals;
				
				sumDropCount=router.getDropCount();
				sumOutOfOrderCount=router.getOutOfOrderCount();
				sumArrivals=router.numArrivals();
				intervalCovered[interval]=true;
			}
			
		}
		
	

	
		
		System.out.println("Seed                                          " + seed);
		System.out.println("Server utilization                            " + router.getServerUtilization());
		System.out.println("Average drop rate                             " + router.getDropRate());
		System.out.println("Average out of order rate                     " + router.getOutOfOrderRate());
		System.out.println("Average delay                                 " + client.getAverageDelay());

		
	}
}