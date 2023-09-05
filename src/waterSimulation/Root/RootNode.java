package waterSimulation.Root;

import java.lang.Math;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
import repast.simphony.util.SimUtilities;
import repast.simphony.util.collections.IndexedIterable;
import waterSimulation.Attraction2;
import waterSimulation.Stem;
import waterSimulation.Tip;
import waterSimulation.Water;

public class RootNode 
{

    private double diameter;
    private double length;
    private double rootTipX;
    private double rootTipY;
    private double originX;
    private double originY;
    int rootID;
    int hd;
    RootNode leftChild;
    RootNode rightChild;
    private RootNode parent;
	private boolean Attraction_found;
	private ContinuousSpace < Object > space;
	private Grid < Object > grid;
	public int ticks;
	private int sensing_radius;
	private boolean waterFound;
    
    public RootNode (ContinuousSpace <Object> space , Grid <Object> grid, double diameter, double length, double originX, double originY, double rootAngle, double lr_flag, int rootID)  //base case
    {
//		System.out.println("cs"+grid);
    	this.space = space;
		this.grid = grid;
    	this.diameter=diameter;
        this.length=length;
        this.rootID = rootID;
        this.parent = null;
//        System.out.println(lr_flag);
//        System.out.println(this.length* Math.cos(Math.toRadians(rootAngle)) +"  "+( lr_flag*(this.length* Math.cos(Math.toRadians(rootAngle)))));
    	this.rootTipX = originX+lr_flag*this.length* Math.cos(Math.toRadians(lr_flag*rootAngle)); //laying the next origin 
    	this.rootTipY = originY+lr_flag*this.length* Math.sin(Math.toRadians(lr_flag*rootAngle)); //making the roots go slightly downward/    	System.out.println(this.rootTipX + " "+ this.rootTipY);
//    	System.out.println(this.rootTipX + " "+ this.rootTipY);
    	this.rightChild = null;
        this.leftChild = null;
        this.hd = Integer.MAX_VALUE; 
        this.ticks = 0;
        Parameters params = RunEnvironment.getInstance().getParameters();
		this.sensing_radius = (Integer) params.getValue("sensing_radius");
		this.waterFound = false;
		this.originX=originX;
		this.originY=originY;
    }

    public RootNode ( ContinuousSpace <Object> space, Grid <Object> grid, int rootID, RootNode parent, double rootAngle, double lr_flag, int lr) //lr =1 for left, lr=-1 for right
    {
//		System.out.println("cs"+grid);
    	this.space = space;
		this.grid = grid;
        this.rootID = rootID;
        this.parent = parent;
    	this.diameter=this.parent.getDiameter()*.80; // make the diamter 80% of parent
    	this.length=this.parent.getLength()*.80; //make the length 50% of parent
    	this.rootTipX = this.parent.getRootTipX()+lr_flag*this.length* Math.cos(Math.toRadians(lr*(rootAngle+35.0))); //laying the next origin 
    	this.rootTipY = this.parent.getRootTipY()+lr_flag*this.length* Math.sin(Math.toRadians(lr*(rootAngle+35.0)));
//    	System.out.println(this.rootTipX + " "+ this.rootTipY);
        this.rightChild = null;
        this.leftChild = null;
        this.originX=this.parent.getRootTipX();
        this.originY=this.parent.getRootTipY();
        this.hd = Integer.MAX_VALUE; 
        this.ticks = 0;
        Parameters params = RunEnvironment.getInstance().getParameters();
		this.sensing_radius = (Integer) params.getValue("sensing_radius");
		this.waterFound = false;
    }

    public void setParent(RootNode parent) 
    {
        this.parent = parent;
    }

    public RootNode getParent() 
    {
        return this.parent;
    }

    public void setLeftChild(RootNode leftChild) 
    {
        this.leftChild = leftChild;
    }

    public RootNode getLeftChild() 
    {
        return this.leftChild;
    }

    public void setRightChild(RootNode rightChild) 
    {
        this.rightChild = rightChild;
    }

    public RootNode getRightChild() 
    {
        return this.rightChild;
    }

    public double getDiameter() 
    {
        return this.diameter;
    }
    
    public void setDiameter(double baseRootDiameter)
    {
    	this.diameter = baseRootDiameter;
    }
    
    public double getLength()
    {
    	return this.length;
    }
    
    public void setLength(double length)
    {
    	this.length = length;
    }

	public int getRootID() 
	{
		return rootID;
	}

	public void setRootID(int rootID) 
	{
		this.rootID = rootID;
	}

	public double getRootTipX() 
	{
		return rootTipX;
	}

	public void setRootTipX(double rootTipX) 
	{
		this.rootTipX = rootTipX;
	}

	public double getRootTipY() 
	{
		return rootTipY;
	}

	public void setRootTipY(double rootTipY) 
	{
		this.rootTipY = rootTipY;
	}

	public double getOriginX() 
	{
		return originX;
	}

	public double getOriginY() 
	{
		return originY;
	}

	@Override
	public String toString() 
	{
		return "RootNode [diameter=" + diameter + ", length=" + length + ", rootTipX=" + rootTipX + ", rootTipY="
				+ rootTipY + ", rootID=" + rootID + "]";
	}
	
	// global method for RootNode
	@ScheduledMethod(start = 1, interval = 1)
	public void step() 
	{
		ticks++;
//		System.out.println(grid);
		Context context = ContextUtils.getContext(this);
		Parameters params = RunEnvironment.getInstance().getParameters();
//		System.out.println(grid);
		GridPoint pt = grid.getLocation(this); // get the grid location of this rootnode 
		GridCellNgh<Tip> nghCreator = new GridCellNgh<Tip>(grid, pt, Tip.class, 1, 1); // use the GridCellNgh class to create GridCells for the surrounding neighborhood.
		List<GridCell<Tip>> gridCells = nghCreator.getNeighborhood(true);
		SimUtilities.shuffle(gridCells, RandomHelper.getUniform());
		
		
//		addAttractionToTip();
//		GridPoint attractionPoint=lookForAttractionAtTip();
		
		waterFound();//look for water
		
	}
	
	private boolean waterFound() 
	{
		GridPoint pt = grid.getLocation(this);
		NdPoint tip_location = space.getLocation(this);
		//Tip nearest_tip = null;
		RootNode nearest_tip = null;
		double nearest_tip_distance = -1;
	
		int[] this_coordinates  = pt.toIntArray(null);
		
		
		//This code works for the first time or
		//when water is outside the stem
//		if(tip_location==null || 
//				(tip_location!=null && grid.getDistance(pt, new GridPoint((int) this.getRootTipX(), (int) ) > sensing_radius)) 
//		{
		
		
//			System.out.println(tip_location.toString());
		
		sensing_radius+=1; //making the sensing radius for rootips bigger than usual sensing radius.
			//Check all around the water's radius for tip
			for (int i=(-1*sensing_radius); i<=sensing_radius; i++) 
			{
				for (int j=(-1*sensing_radius); j<=sensing_radius; j++) {
					int[] neighbour = {this_coordinates[0]+i, this_coordinates[1]+j};
					if (!withinBounds(neighbour)){continue;} 
					for (Object obj : grid.getObjectsAt(neighbour[0], neighbour[1])) 
					{
//						System.out.println("neighbour found"+obj);
						if (obj instanceof Water) 	
						{
							int[] water_coords= ((Water) obj).getCoords();
//							System.out.println((grid.getDistance(pt, new GridPoint(water_coords[0], water_coords[1] ))));
							if(grid.getDistance(pt, new GridPoint(water_coords[0], water_coords[1] )) < sensing_radius)
							{
//								System.out.println("water found "+obj);
								waterFound = true;
								if(!((Water)obj).tracingRoot)
								{
									((Water)obj).traceRoot(this);
								}
								
								
								
								
//								if ((double)( Math.abs(i)+Math.abs(j)) < nearest_tip_distance || nearest_tip_distance == -1) 
//								{
//									//store information about the nearest tip found
//									nearest_tip_distance = (double)( Math.abs(i)+Math.abs(j));
//									//nearest_tip = (Tip) obj;
//									//nearest_tip = (RootNode) obj;
//								}
//								return true;
								
							}
//							else
//							{
////								System.out.println((grid.getDistance(pt, new GridPoint(water_coords[0], water_coords[1] ))));
//							}
							
						}
						
					}
				}
			}
			if(waterFound) return true;
//		}
		
		return false;
					
	}	
	
	
//	public boolean isAttraction_found() 
//	{
//		return Attraction_found;
//	}
//
//	public void setAttraction_found(boolean attraction_found) 
//	{
//		Attraction_found = attraction_found;
//	}
//	
	private boolean withinBounds(int[] coordinates ) 
	{
		Parameters params = RunEnvironment.getInstance().getParameters();
		int grid_size = (Integer) params.getValue("grid_size");
		//Returns true if the coordinates being tested are within the bounds of the grid.
		return !(coordinates[0] < 0 || coordinates[1] < 0 || coordinates[0] > grid_size || coordinates[1] > grid_size || (coordinates[0] == 0 && coordinates[1] == 0 ));		
	}
//	
//	// Function to verify weather there is a Attraction in the surroundings of the tip (max distance 1)
//	// If more than one Attraction is found with the same intensity, which is the max intensity at that moment,
//	// run a lottery to pick any one of them
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
//		
//	// Function that verifies whether there is already attraction or a new one has to be created
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
