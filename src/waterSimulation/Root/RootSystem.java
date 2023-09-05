package waterSimulation.Root;

import java.util.*;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;

public class RootSystem 
{
	private LinkedList<Groot> rootCollar = new LinkedList<Groot>(); //collar is the intersection between the shroot and root
	private Grid < Object > grid;
	private ContinuousSpace < Object > space;
	
	
	Parameters params = RunEnvironment.getInstance().getParameters();
	int grid_size = (Integer) params.getValue("grid_size");
	
	public RootSystem(ContinuousSpace <Object> space, Grid <Object> grid)
	{
		this.space = space;
		this.grid = grid;
	}

	public LinkedList<Groot> getRootCollar() 
	{
		return rootCollar;
	}

	public void setRootCollar(LinkedList<Groot> rootCollar) 
	{
		this.rootCollar = rootCollar;
	}
	
	public void createRootSystem()
	{
		Parameters params = RunEnvironment.getInstance().getParameters();
		int tor = (Integer) params.getValue("type_of_root");
		int stage = (Integer) params.getValue("plant_stage");
		//right side
		// ContinuousSpace <Object> space, Grid <Object> grid, double baseRoot , ...
		// double baseRootLength, double rootOriginX, double rootOriginY, double rootAngle, double lr_flag, int maxNode
		if(stage==1)
		{
			if(tor==2)
			{
			rootCollar.add(new Groot(space, grid, 8, 20, grid_size/2, grid_size/2,-80, 1, 10)); //double diameter, double length, double originX, double originY, int rootID
//			rootCollar.add(new Groot(space, grid, 8, 20, grid_size/2, grid_size/2,-80, 1,8));
//			rootCollar.add(new Groot(space, grid, 2, 20, grid_size/2, grid_size/3,-100,1, 6));
//			rootCollar.add(new Groot(space, grid, 2, 15, grid_size/2, grid_size/3,-100,1, 4));
			//rootCollar.add(new Groot(space, grid, 1, 15, grid_size/2, grid_size/3,-100,1, 2));
//			//left side
			rootCollar.add(new Groot(space, grid, 8, 20, grid_size/2, grid_size/2,-80, 1, 10)); //double diameter, double length, double originX, double originY, int rootID
//			rootCollar.add(new Groot(space, grid, 8, 20, grid_size/2, grid_size/2,-80, 1,8));
//			rootCollar.add(new Groot(space, grid, 2, 20, grid_size/2, grid_size/3,-100,1, 7));
//			rootCollar.add(new Groot(space, grid, 2, 15, grid_size/2, grid_size/3,-100 ,1, 5));
			}
			else
			{
			
			//right side
					// ContinuousSpace <Object> space, Grid <Object> grid, double baseRoot , ...
					// double baseRootLength, double rootOriginX, double rootOriginY, double rootAngle, double lr_flag, int maxNode
					rootCollar.add(new Groot(space, grid, 10, 10, grid_size/2, grid_size/2,-60, 1, 8)); //double diameter, double length, double originX, double originY, int rootID
//					rootCollar.add(new Groot(space, grid, 5, 25, grid_size/2, grid_size/2,-25, 1,8));
//					rootCollar.add(new Groot(space, grid, 2, 20, grid_size/2, grid_size/3,-25,1, 6));
//					rootCollar.add(new Groot(space, grid, 2, 15, grid_size/2, grid_size/3,-25,1, 4));
					//rootCollar.add(new Groot(space, grid, 1, 15, grid_size/2, grid_size/3,-100,1, 2));
//					//left side
					rootCollar.add(new Groot(space, grid, 10, 10, grid_size/2, grid_size/2,-60, -1, 8)); //double diameter, double length, double originX, double originY, int rootID
//					rootCollar.add(new Groot(space, grid, 5, 25, grid_size/2, grid_size/2,-25, -1,4));
//					rootCollar.add(new Groot(space, grid, 2, 20, grid_size/2, grid_size/3,-25,-1, 7));
//					rootCollar.add(new Groot(space, grid, 2, 15, grid_size/2, grid_size/3,-25 ,-1, 5));
			}
		}
		else
		{
			if(tor==2)
			{
			rootCollar.add(new Groot(space, grid, 10, 30, grid_size/2, grid_size/2,-100, 1, 10)); //double diameter, double length, double originX, double originY, int rootID
			rootCollar.add(new Groot(space, grid, 5, 25, grid_size/2, grid_size/2,-100, 1,9));
			rootCollar.add(new Groot(space, grid, 2, 20, grid_size/2, grid_size/3,-100,1, 6));
			rootCollar.add(new Groot(space, grid, 2, 15, grid_size/2, grid_size/3,-100,1, 4));
			//rootCollar.add(new Groot(space, grid, 1, 15, grid_size/2, grid_size/3,-100,1, 2));
//			//left side
			rootCollar.add(new Groot(space, grid, 10, 30, grid_size/2, grid_size/2,-100, 1, 9)); //double diameter, double length, double originX, double originY, int rootID
			rootCollar.add(new Groot(space, grid, 5, 25, grid_size/2, grid_size/2,-100, 1,8));
			rootCollar.add(new Groot(space, grid, 2, 20, grid_size/2, grid_size/3,-100,1, 7));
			rootCollar.add(new Groot(space, grid, 2, 15, grid_size/2, grid_size/3,-100 ,1, 5));
			}
			else
			{
			
			//right side
					// ContinuousSpace <Object> space, Grid <Object> grid, double baseRoot , ...
					// double baseRootLength, double rootOriginX, double rootOriginY, double rootAngle, double lr_flag, int maxNode
					rootCollar.add(new Groot(space, grid, 10, 30, grid_size/2, grid_size/2,-25, 1, 10)); //double diameter, double length, double originX, double originY, int rootID
					rootCollar.add(new Groot(space, grid, 5, 25, grid_size/2, grid_size/2,-25, 1,8));
					rootCollar.add(new Groot(space, grid, 2, 20, grid_size/2, grid_size/3,-25,1, 6));
					rootCollar.add(new Groot(space, grid, 2, 15, grid_size/2, grid_size/3,-25,1, 4));
					//rootCollar.add(new Groot(space, grid, 1, 15, grid_size/2, grid_size/3,-100,1, 2));
//					//left side
					rootCollar.add(new Groot(space, grid, 10, 30, grid_size/2, grid_size/2,-25, -1, 9)); //double diameter, double length, double originX, double originY, int rootID
					rootCollar.add(new Groot(space, grid, 5, 25, grid_size/2, grid_size/2,-25, -1,4));
					rootCollar.add(new Groot(space, grid, 2, 20, grid_size/2, grid_size/3,-25,-1, 7));
					rootCollar.add(new Groot(space, grid, 2, 15, grid_size/2, grid_size/3,-25 ,-1, 5));
			}
		}
		
	}

	
}
