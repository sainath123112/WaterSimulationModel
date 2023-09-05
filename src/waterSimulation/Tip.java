
package waterSimulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid ;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;



public class Tip 
{
	private int units;
	private ContinuousSpace < Object > space;
	private Grid < Object > grid;
	private boolean Attraction_found;

	public Tip(int units) 
	{
		this.units = units;
		this.Attraction_found = false;
	}
	
	public void addUnit(int units) 
	{
		this.units += units;
	}
	
	
	public boolean isAttraction_found() 
	{
		return Attraction_found;
	}

	public void setAttraction_found(boolean attraction_found) 
	{
		Attraction_found = attraction_found;
	}

	public int getUnits() 
	{
		return this.units;
	}
	
	private boolean withinBounds(int[] coordinates ) 
	{
		Parameters params = RunEnvironment.getInstance().getParameters();
		int grid_size = (Integer) params.getValue("grid_size");
		//Returns true if the coordinates being tested are within the bounds of the grid.
		return !(coordinates[0] < 0 || coordinates[1] < 0 || coordinates[0] > grid_size || coordinates[1] > grid_size || (coordinates[0] == 0 && coordinates[1] == 0 ));		
	}
	
	// Function to verify weather there is a Attraction in the surroundings of the tip (max distance 1)
	// If more than one Attraction is found with the same intensity, which is the max intensity at that moment,
	// run a lottery to pick any one of them
	public GridPoint lookForAttractionAtTip() 
		{
			int maxIntensity = 0;
			GridPoint pt = grid.getLocation(this);
			
			List<GridPoint> lottery = new ArrayList<GridPoint>();
			
			int[] this_coordinates  = pt.toIntArray(null);
			for (int i=(-1*1); i<=1; i++) 
			{
				for (int j=(-1*1); j<=1; j++) 
				{
					int[] neighbour = {this_coordinates[0]+i, this_coordinates[1]+j};
					if (!withinBounds(neighbour))
						{continue;} 
					for (Object obj : grid.getObjectsAt(neighbour[0], neighbour[1])) 
					{
						if (obj instanceof Attraction2) 
						{
							//if  (pathInMemory(neighbour)) break;					
							if (((Attraction2) obj).getIntensity() > maxIntensity) 
							{
								maxIntensity = ((Attraction2) obj).getIntensity();	
								lottery = new ArrayList<GridPoint>();
								lottery.add(new GridPoint(neighbour));
							}
							else if (((Attraction2) obj).getIntensity() == maxIntensity)
							{							
								lottery.add(new GridPoint(neighbour));
							}
							break;
						}
					}
				}
			}	
			
			int size_lottery = lottery.size();
			
			if (size_lottery > 0) 
			{
				Attraction_found = true;
			}
			else 
			{
				return null;
			}
			
			return lottery.get(new Random().nextInt(size_lottery));
		}
		
	// Function that verifies whether there is already attraction or a new one has to be created
	public void addAttractionToTip() 
	{
		//This functions adds an attraction to the tip's location
		GridPoint pt = grid.getLocation(this);
		for (Object obj : grid.getObjectsAt(pt.getX(), pt.getY())) 
		{
			if (obj instanceof Attraction2) 
			{
				return;	
			}
		}
		Context context = ContextUtils.getContext(this);		
		Attraction2 ph = new Attraction2();
		context.add(ph);
		space.moveTo(ph, pt.getX(), pt.getY());
		grid.moveTo(ph, pt.getX(), pt.getY());
	}
	
}
