package waterSimulation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import repast.simphony.context.Context;
import waterSimulation.Tip;
import waterSimulation.Water;
import waterSimulation.Root.*;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace ;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid ;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
import repast.simphony.util.SimUtilities;
import repast.simphony.util.collections.IndexedIterable;
import repast.simphony.parameter.Parameters;

public class Water 
{
	private ContinuousSpace < Object > space;
	private Grid < Object > grid;
	private boolean Attraction_found, going_to_stem, at_stem;
	public boolean tracingRoot;
	private double move_angle;
	private List<Stem> stems;
	private int count_reset_angle, turn_interval, sensing_radius;
	private NdPoint stem_location, tip_location;
	private Stem nearest_stem;
	private RootNode root_tip;
	private Groot groot;
	private RootSystem rootSystem;
	private Tip nearest_tip;
	private List<GridPoint> memory;
	public int age, ticks, total_tip_count;
	private int[] last_clash = {-1, -1};
	private double[] dest_coordinates= {0,0};
	private RootNode rootNodeAttraction;
	private double prevDistance=0;
	
	public static int waterIntakeCount=0;
	public static int waterIntakeCountPrev=0;
	
	
	public Water( ContinuousSpace <Object> space , Grid <Object> grid, double move_angle, List<Stem> stems, int age, RootSystem rootSystem) 
	{
		this.space = space;
		this.grid = grid;
		Parameters params = RunEnvironment.getInstance().getParameters();
		this.sensing_radius = (Integer) params.getValue("sensing_radius");
		this.turn_interval = (Integer) params.getValue("turn_interval");
		this.total_tip_count = (Integer) params.getValue("tip_count");
		this.move_angle = move_angle;
		this.Attraction_found = false;
		this.count_reset_angle = 0;
		this.stems = stems;
		this.memory = new ArrayList<GridPoint>();
		this.going_to_stem = false;
		this.at_stem = false;
		this.age = age;
		this.ticks = 0;
		this.rootSystem = rootSystem;
		this.tracingRoot = false;

	}
	
		@ScheduledMethod(start = 1, interval = 1)
		public void step() 
		{
			ticks++;
			
			if(waterIntakeCount>waterIntakeCountPrev)
			{
				//System.out.println("at tick: " + ticks+ " , water intake count: " + waterIntakeCount);
				waterIntakeCountPrev=waterIntakeCount;
			}
			
//			System.out.println("ticks: " +);
			
			Context context = ContextUtils.getContext(this);
			Parameters params = RunEnvironment.getInstance().getParameters();
			GridPoint pt = grid.getLocation(this); // get the grid location of this water
			GridCellNgh<Tip> nghCreator = new GridCellNgh<Tip>(grid, pt, Tip.class, 1, 1); // use the GridCellNgh class to create GridCells for the surrounding neighborhood.
			List<GridCell<Tip>> gridCells = nghCreator.getNeighborhood(true);
			SimUtilities.shuffle(gridCells, RandomHelper.getUniform());
			
			//The following is a global algorithm for the water
			if(!going_to_stem) //If the water was not in the process of going towards the stem
			{
				if (ticks%10 == 0)
				{
				//find tips to move towards
				moveTowardsRootTip();
				
				//find water to move towards
				moveTowardsWater();
				}

				if(tracingRoot)
				{
//					this.rootNodeAttraction;
					double [] dest_coordinates= {0,0};
					if(this.rootNodeAttraction.getParent() ==null)
					{
//						System.out.println("at groot");
						
						dest_coordinates[0]=  this.rootNodeAttraction.getOriginX();
						dest_coordinates[1]=  this.rootNodeAttraction.getOriginY();
//						double distance = grid.getDistance(pt, new GridPoint((int) dest_coordinates[0], (int) dest_coordinates[1]));			
//						System.out.println(pt.toString());
					
					}
					else
					{
//						System.out.println("at another rootNode");
						dest_coordinates[0] = (space.getLocation(this.rootNodeAttraction.getParent())).getX();
						dest_coordinates[1] =(space.getLocation(this.rootNodeAttraction.getParent())).getY();
					}
					
					
					
					//find out nearest stem
					int num_stems = stems.size();
//					System.out.println(num_stems);
					List<Object> stem_dist = new ArrayList<Object>();
					for(int i=0; i<num_stems; i++) 
					{
						NdPoint stem_loc = space.getLocation(stems.get(i));
						//Get the distance between the water and the stem
						stem_dist.add(space.getDistance(space.getLocation(this), stem_loc));
					}
					int nearest_stem_index = 0;
					double min_distance = -1;
					//If there are more than one stem available then find out the closest one
					if(stem_dist != null & stem_dist.size() > 1) 
					{
						for(int j=0; j < stem_dist.size(); j++) {
//							if((Double)stem_dist.get(j) < min_distance || min_distance == -1) 
							if((Double)stem_dist.get(j) > min_distance) 
							{
								min_distance = (Double) stem_dist.get(j);
								nearest_stem_index = j;				
							}
						}
					}
					//set these values so that they can be used later
					this.stem_location = space.getLocation(stems.get(nearest_stem_index));
					nearest_stem = stems.get(nearest_stem_index);
					this.stem_location = space.getLocation(nearest_stem);
					
					NdPoint pt_space= space.getLocation(this);
//					double distance = grid.getDistanceSq(space.getLocation(this), new GridPoint((int) dest_coordinates[0], (int) dest_coordinates[1]));
					double distance= Math.sqrt(Math.pow((pt_space.getX()-dest_coordinates[0]),2)+Math.pow((pt_space.getY()-dest_coordinates[1]),2));
					
//					System.out.println(distance);
					if (distance ==0)
					{
						
						if(this.rootNodeAttraction.getParent() == null)
						
						{
							going_to_stem = true;
							tracingRoot = false;
							
						}
						else
						{
							this.rootNodeAttraction = this.rootNodeAttraction.getParent();
						}
					}
					NdPoint currentLocation = space.getLocation(this);
					double x1x2= currentLocation.getX() - dest_coordinates[0];
					double y1y2 = currentLocation.getY() - dest_coordinates[1];
					double slope = (y1y2)/ (x1x2);
					
					
					if (ticks%1 == 0)
					{
						
						double randomDistance= (Math.random() * (.2 - .1)+0.1); //10% to 20% of the remaining distance
						//System.out.println(randomDistance);
						if(distance >=1 && Double.isFinite(slope)) // keep moving
						{
//							double distance = grid.getDistance(pt, new GridPoint((int) dest_coordinates[0], (int) dest_coordinates[1]));
//							System.out.println(distance+" "+ prevDistance);
							prevDistance=distance;
							double x = currentLocation.getX() -x1x2*randomDistance; //setting x2 
							double y= slope*(x-currentLocation.getX())+currentLocation.getY();
							space.moveTo(this,x,y);
//							space.moveTo(this, dest_coordinates[0], dest_coordinates[1]);
//						    grid.moveTo(this, (int) currentLocation.getX()-1,(int) currentLocation.getY()+1);
						}
						else if (distance >=1 &&  Math.abs(y1y2)<=0.01)
						{
//							System.out.println("infinity slope");
							double x = currentLocation.getX() -x1x2*randomDistance;
							space.moveTo(this,x,currentLocation.getY());
						}
						else if (distance >=1 && Math.abs(slope)<=0.01)
						{
//							System.out.println("zero slope");
							double y= currentLocation.getY() -y1y2*randomDistance;
							space.moveTo(this,currentLocation.getX(),y);
						}
						else
						{
//							System.out.println("going to destination "+dest_coordinates[0]+ " "+ dest_coordinates[1] );
							space.moveTo(this, dest_coordinates[0], dest_coordinates[1]);
						}
					}
					

				}
				
				
				else
				{
					
					moveDownward(); //The water moves downward
				}
				Attraction_found = false;			
			}
			else if (at_stem)
			{
				context.remove(this);

			}
			else //water is going to stem
			{
				
				
//				System.out.println);

				double yError=stem_location.getY()-space.getLocation(this).getY();
				double xError=stem_location.getX()-space.getLocation(this).getX();
				

				if(xError ==0 && yError==0)//at the stem
				{
					at_stem=true;
//					if(this.tracingRoot==true)
//					{
					waterIntakeCount = waterIntakeCount+1; // variable used for charts to measure water intake by RSA

//					}
				}
				else 
				{

					double randomDistance;
					if(yError > 0.5 || xError >0.5)
					{
						 randomDistance= (int)(0+(Math.random() * ((5 - 0)+1)))/10.0;
					}
					else
					{
						 randomDistance= (int)(5+(Math.random() * ((10 - 5)+1)))/10.0;
					}
					

					double x= space.getLocation(this).getX()+(stem_location.getX()-space.getLocation(this).getX())*randomDistance;
					double y=space.getLocation(this).getY() +(stem_location.getY()-space.getLocation(this).getY())*randomDistance;
					space.moveTo(this, x,y);
//					}
				}
				
			}	
			
			IndexedIterable<Object> stems = context.getObjects( Stem.class );
			int total_tips_in_stem = 0;
			for (int i=0; i<stems.size(); i++) 
			{
				Stem stem = (Stem)stems.get(i);
				if (stem.getSizeOfPile() > 0)
					total_tips_in_stem = total_tips_in_stem + stem.getSizeOfPile();
			
			}
			if(total_tips_in_stem == total_tip_count) 
			{
				//RunEnvironment.getInstance().endAt(ticks+1);
			}
		}
		

	
	

	private void moveDownward() 
	{
		// move water 1 unit every 10 ticks
		NdPoint currentLocation = space.getLocation(this);
		if (ticks%20 == 0)
		{
			int xDir=1;
			double randomYDistance= (Math.random() * (.4 - .05)+0.05);
			double randomXDistance= (Math.random() * (.2 - .05)+0.05);
			if(Math.random()>0.5) 
			{
				xDir=-1;
			}
			
			if(currentLocation.getY() > 5) // if water doesn't hit bottom floor, keep going down
			{
				space.moveTo(this, currentLocation.getX()+xDir*randomXDistance, currentLocation.getY()-randomYDistance);
//			    grid.moveTo(this, (int) currentLocation.getX(), (int) currentLocation.getY()-1);
			}
		}
	}

	private boolean moveTowardsRootTip() 
	{
		GridPoint pt = grid.getLocation(this);
		stem_location = space.getLocation(nearest_stem);
		//Tip nearest_tip = null;
		RootNode nearest_tip = null;
		double nearest_tip_distance = -1;
	
		int[] this_coordinates  = pt.toIntArray(null); 
		//This code works for the first time or
		//when water is outside the stem
		if(stem_location==null || 
				(stem_location!=null && grid.getDistance(pt, new GridPoint((int) stem_location.getX(), (int) stem_location.getY())) > sensing_radius)) 
		{
			//Check all around the water's radius for tip
			for (int i=(-1*sensing_radius/2); i<=sensing_radius/2; i++) 
			{
				for (int j=(-1*sensing_radius/2); j<=sensing_radius/2; j++) {
					int[] neighbour = {this_coordinates[0]+i, this_coordinates[1]+j};
					if (!withinBounds(neighbour)){continue;} 
					
//					for (Object obj : grid.getObjectsAt(neighbour[0], neighbour[1])) 
//					{
					{
						Object obj=grid.getRandomObjectAt(neighbour[0], neighbour[1]);
						if (obj instanceof Tip) 
						{
//						if (obj instanceof RootNode) 	
//						{
							if ((double)( Math.abs(i)+Math.abs(j)) < nearest_tip_distance || nearest_tip_distance == -1) 
							{
								//store information about the nearest tip found
								nearest_tip_distance = (double)( Math.abs(i)+Math.abs(j));
								//nearest_tip = (Tip) obj;
								nearest_tip = (RootNode) obj;
							}
							break;
						}
					}
				}
			}
		}
		
		if (nearest_tip != null) 
		{
			//If tip was found, go to that location
			tip_location = space.getLocation(nearest_tip);
			
//			System.out.println(tip_location.toString());
			int [] coords = {(int) tip_location.getX(), (int) tip_location.getY()};
			if (move(coords)) 
			{
				
				return true;
			}
			else 
				return false;
			
		}
		else 
		{
			//No tip was found
			return false;
		}					
	}
	
	private boolean moveTowardsWater() 
	{
		GridPoint pt = grid.getLocation(this);
		stem_location = space.getLocation(nearest_stem);
		//Tip nearest_tip = null;
		Water nearest_tip = null;
		double nearest_tip_distance = -1;
	
		int[] this_coordinates  = pt.toIntArray(null); 
		//This code works for the first time or
		//when water is outside the stem
		if(stem_location==null || 
				(stem_location!=null && grid.getDistance(pt, new GridPoint((int) stem_location.getX(), (int) stem_location.getY())) > sensing_radius)) 
		{
			//Check all around the water's radius for tip
			for (int i=(-1*sensing_radius/2); i<=sensing_radius/2; i++) 
			{
				for (int j=(-1*sensing_radius/2); j<=sensing_radius/2; j++) {
					int[] neighbour = {this_coordinates[0]+i, this_coordinates[1]+j};
					if (!withinBounds(neighbour)){continue;} 
//					for (Object obj : grid.getObjectsAt(neighbour[0], neighbour[1])) 
//					{
					{
						Object obj=grid.getRandomObjectAt(neighbour[0], neighbour[1]);
						if (obj instanceof Tip) 
//						if (obj instanceof Water) 	
						{
							if ((double)( Math.abs(i)+Math.abs(j)) < nearest_tip_distance || nearest_tip_distance == -1) 
							{
								//store information about the nearest tip found
								nearest_tip_distance = (double)( Math.abs(i)+Math.abs(j));
								//nearest_tip = (Tip) obj;
								nearest_tip = (Water) obj;
							}
							break;
						}
					}
				}
			}
		}
		
		if (nearest_tip != null) 
		{
			//If tip was found, go to that location
			tip_location = space.getLocation(nearest_tip);
			
//			System.out.println(tip_location.toString());
			int [] coords = {(int) tip_location.getX(), (int) tip_location.getY()};
			if (move(coords)) 
			{
				
				return true;
			}
			else 
				return false;
			
		}
		else 
		{
			//No tip was found
			return false;
		}					
	}
		
	private void goToStem() 
	{
		int num_stems = stems.size();
		List<Object> stem_dist = new ArrayList<Object>();
		for(int i=0; i<num_stems; i++) 
		{
			NdPoint stem_loc = space.getLocation(stems.get(i));
			//Get the distance between the water and the stem
			stem_dist.add(space.getDistance(space.getLocation(this), stem_loc));
		}
		int nearest_stem_index = 0;
		double min_distance = -1;
		//If there are more than one stem available then find out the closest one
		if(stem_dist != null & stem_dist.size() > 1) 
		{
			for(int j=0; j < stem_dist.size(); j++) {
				if((Double)stem_dist.get(j) < min_distance || min_distance == -1) 
				{
					min_distance = (Double) stem_dist.get(j);
					nearest_stem_index = j;				
				}
			}
		}
		//set these values so that they can be used later
		stem_location = space.getLocation(stems.get(nearest_stem_index));
		nearest_stem = stems.get(nearest_stem_index);
		going_to_stem = true;
		
		at_stem = true;
		
		
	}
	
	private void dropTip() 
	{
		//drop tip at stem
		nearest_stem.insertTip();
	}

	// Function to verify weather there is a Attraction trail in the surroundings of the water (max distance 1)
	// If more than one Attraction trail is found with the same intensity, which is the max intensity at that moment,
	// run a lottery to pick any one of them.
	private GridPoint lookForAttraction() 
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
				if (!withinBounds(neighbour)){continue;} 
				for (Object obj : grid.getObjectsAt(neighbour[0], neighbour[1])) 
				{
					if (obj instanceof Attraction2) {
						
						//if  (pathInMemory(neighbour)) break;					
						if (((Attraction2) obj).getIntensity() > maxIntensity) 
						{
							maxIntensity = ((Attraction2) obj).getIntensity();	
							lottery = new ArrayList<GridPoint>();
							lottery.add(new GridPoint(neighbour));
						}else if (((Attraction2) obj).getIntensity() == maxIntensity)
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
	
	// Function that verifies whether there is already attraction trail or a new one has to be created.
	private void addAttraction() 
	{
		//This functions adds an attraction to the water's location
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

	
	// Add the path in the memory, remove the oldest one, so that size 5 is kept.
	private void addPathToMemory(GridPoint path) 
	{
		if (memory.size() >= 5) 
		{
			memory.remove(0);
		}
		memory.add(path);
	}
	
	private boolean pathInMemory(int[] path) 
	{
		for( GridPoint mem : memory) 
		{
			//If a path is found then return true
			if (Arrays.equals(mem.toIntArray(null), path)) 
				return true;
		}
		return false;
	}
	
	private boolean withinBounds(int[] coordinates ) 
	{
		Parameters params = RunEnvironment.getInstance().getParameters();
		int grid_size = (Integer) params.getValue("grid_size");
		//Returns true if the coordinates being tested are within the bounds of the grid.
		return !(coordinates[0] < 0 || coordinates[1] < 0 || coordinates[0] > grid_size || coordinates[1] > grid_size || (coordinates[0] == 0 && coordinates[1] == 0 ));		
	}
	
	private int[] toIntVector(double[] double_coordinates) 
	{
		//This function converts any double values to integers values
		int[] int_coordinates = new int[double_coordinates.length];
		for(int i=0; i<int_coordinates.length; i++) 
		{
			int_coordinates[i] = (int) double_coordinates[i];
		}
		return int_coordinates;
	}
	
	// Code Fix 2017-12-04 (new function added)
	public int getAge()
	{
		//Return the age of the water
		return age;
	}
	
	// Code Fix 2017-12-04 (new function added)
	private boolean move(int [] coords) 
	{
		// Check whether there are more waters 
		GridPoint pt = grid.getLocation(this);
		for (Object obj : grid.getObjectsAt(pt.toIntArray(null))) 
		{
			if (obj instanceof Water) 
			{				
				if (obj == this) 
				{
					//If the object represents the same water then ignore
					continue;
				}
				else 
				{
					if (last_clash != coords) 
					{
						//If the last clash between this water and other waters wasn't at this coordinate then store it.
						last_clash = coords;
						return false;
					}
					Water otherWater = (Water) obj;
					if (otherWater.getAge() < this.age) 
					{
						//There is atleast one water that is elder than this water
						//This water may not move
						return false;
					}
				}
			}
		}
		// If you are the oldest or the only water in that location then you have priority to move
		// Move to next location
		space.moveTo(this, coords[0], coords[1]);
		grid.moveTo(this, coords[0], coords[1]);
		return true;
	}
	
	// Code Fix 2017-12-04 (old function modified)
	private boolean moveWithAngle(double angle) 
	{
		//Move the water in the angle specified by one unit
		grid.moveByVector(this, 1, angle, 0);
		return move(grid.getLocation(this).toIntArray(null));
	}
	
	public boolean traceRoot(RootNode rootNode) 
	{
		
		//System.out.println("-_-");
		// Check whether there are more waters 
//		GridPoint pt = grid.getLocation(this);
//		for (Object obj : grid.getObjectsAt(pt.toIntArray(null))) 
//		{
//			if (obj instanceof Water) 
//			{				
//				if (obj == this) 
//				{
//					//If the object represents the same water then ignore
//					continue;
//				}
//				else 
//				{
//					if (last_clash != coords) 
//					{
//						//If the last clash between this water and other waters wasn't at this coordinate then store it.
//						last_clash = coords;
//						return false;
//					}
//					Water otherWater = (Water) obj;
//					if (otherWater.getAge() < this.age) 
//					{
//						//There is atleast one water that is elder than this water
//						//This water may not move
//						return false;
//					}
//				}
//			}
//		}
		// If you are the oldest or the only water in that location then you have priority to move
		// Move to next location
		//space.moveTo(this, coords[0], coords[1]);
		//grid.moveTo(this, coords[0], coords[1]);
//		this.dest_coordinates=coords;
		this.rootNodeAttraction=rootNode;
		this.tracingRoot = true;
		
		//System.out.println("-_- :/");
		return true;
	}
	
	public int[] getCoords()
	{
		GridPoint pt = grid.getLocation(this);
		int[] int_coordinates = new int[2];
		int_coordinates[0] = pt.getX();
		int_coordinates[1] = pt.getY();
		return int_coordinates;
	}
	
}
