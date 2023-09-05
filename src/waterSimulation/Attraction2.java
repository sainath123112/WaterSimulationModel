package waterSimulation;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
/**
 * Class that represents the attractions released by the agents in the environment.
 * It is created with intensity 1, but can be increased as ants pass to its position.
 *
 */
public class Attraction2 
{

	//int time_to_vanish;
	//int counter_dissipation = 0;
	int intensity = 1;
	//boolean vanished = false;
	
	
	public Attraction2() 
	{
		Parameters p = RunEnvironment.getInstance().getParameters();
		//time_to_vanish = (Integer)p.getValue("attraction_interval");
		//counter_dissipation = time_to_vanish;
		
	}
	
	// Method run at each tick to update the timer for attraction dissipation
	// and after the timer is zero, decreases the intensity
	@ScheduledMethod(start = 1, interval = 1)
	public void step() 
	{
		Context context = ContextUtils.getContext(this);
	}
	
	public int getIntensity() 
	{
		return intensity;
	}

	
}
