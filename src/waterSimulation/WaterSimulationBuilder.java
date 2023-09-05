package waterSimulation;

import waterSimulation.Root.*; //import all the root codes

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;

import repast.simphony.space.graph.RepastEdge;//for lines
import repast.simphony.space.graph.Network;//for lines
import repast.simphony.util.ContextUtils;

/**
 * Class responsible to build the context, with all the nests and crumbs created according to scatter factor
 * 
 *
 */
public class WaterSimulationBuilder implements ContextBuilder<Object> 
{

	@Override
	public Context build(Context<Object> context) 
	{
		context.setId("WaterSimulation");

		NetworkBuilder<Object> netBuilder = new NetworkBuilder<Object>("food network", context, true);
		netBuilder.buildNetwork();
		
		Parameters params = RunEnvironment.getInstance().getParameters();
		int grid_size = (Integer) params.getValue("grid_size");

		// Continuous space (double coordinates)
		ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);
		ContinuousSpace<Object> space = spaceFactory.createContinuousSpace(
				"space", context, new RandomCartesianAdder<Object>(),
				new repast.simphony.space.continuous.WrapAroundBorders(), grid_size, grid_size);

		// Grid space (int coordinates)
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid<Object> grid = gridFactory.createGrid("grid", context,
				new GridBuilderParameters<Object>(new WrapAroundBorders(),
						new SimpleGridAdder<Object>(), true, grid_size, grid_size));
		
		// Creates stems according to parameter "number_of_stems"
		int stem_count = (Integer) params.getValue("number_of_stems");
		int plant_stage = (Integer) params.getValue("plant_stage");
		List<Stem> stems = new ArrayList<Stem>();
		for (int i = 0; i < stem_count; i++) 
		{	
			if (stem_count == 1 ) // when there is one plant
			{	
				if(plant_stage==1)
				{
					for(int x=20; x>=10; x=x-10)
					{
					Stem n = new Stem();
					context.add(n);
					stems.add(n);
				    space.moveTo(n, (int) 100, (int) grid_size/2+x);
				    grid.moveTo(n, (int) 100, (int) grid_size/2+x);
					}
				}
				else
				{
					for(int x=60; x>=10; x=x-10)
					{
					Stem n = new Stem();
					context.add(n);
					stems.add(n);
				    space.moveTo(n, (int) 100, (int) grid_size/2+x);
				    grid.moveTo(n, (int) 100, (int) grid_size/2+x);
					}
				}
				
				
			}
		}

		/////////////////
		///root system placement
			RootSystem rs = new RootSystem(space, grid);
			context.add(rs);
			rs.createRootSystem();
//			int count=0;
			
			for( Groot groot : rs.getRootCollar()) //get the linked list of root system and traverse through each root to add to context
			{
//				System.out.println("groots");
				
				context.add(groot);
				NdPoint pt = space.getLocation(groot); // extract point of each tip and position them in the grid
//				if(count==1)
//				{
					space.moveTo(groot, (int) groot.getRootRoot().getOriginX(), (int) groot.getRootRoot().getOriginY());
					grid.moveTo(groot, (int) groot.getRootRoot().getOriginX(), (int) groot.getRootRoot().getOriginY());
//				}
//				else
//				{
//					space.moveTo(groot, (int) groot.getRootRoot().getOriginX(), (int) groot.getRootRoot().getOriginY()+10);
//					grid.moveTo(groot, (int) groot.getRootRoot().getOriginX(), (int) groot.getRootRoot().getOriginY()+10);
//				}
//				count++;
				
				
			
				RootNode rootNode = groot.getRootRoot(); //root of the groot
				RootNode current=rootNode; //travel left first then right and add to context
				RootNode pre=null;
				
				// Queue to store tree nodes in level order traversal 
		        Queue<RootNode> rootqueue = new LinkedList<RootNode>(); 
		  
		        
		        rootqueue.add(groot.getRootRoot()); 
		  
		        // Loop until the queue is empty (standard level order loop) 
		        while (!rootqueue.isEmpty()) 
		        { 
		            RootNode temp = rootqueue.remove(); 
		  
		            
		            // If the dequeued node has a left child add it to the queue with a horizontal distance hd-1. 
		            if (temp.getLeftChild() != null) 
		            { 
		                rootqueue.add(temp.getLeftChild()); 
		                context.add(temp);
		                pt = space.getLocation(temp); // extract point of each tip and position them in the grid
						space.moveTo(temp, (int) temp.getRootTipX(), (int) temp.getRootTipY());
						grid.moveTo(temp, (int) temp.getRootTipX(), (int) temp.getRootTipY());
		            } 
		            // If the dequeued node has a left child add it to the queue with a horizontal distance hd+1. 
		            if (temp.getRightChild() != null) 
		            { 
		                rootqueue.add(temp.getRightChild()); 
		                context.add(temp);
		                pt = space.getLocation(temp); // extract point of each tip and position them in the grid
						space.moveTo(temp, (int) temp.getRootTipX(), (int) temp.getRootTipY());
						grid.moveTo(temp, (int) temp.getRootTipX(), (int) temp.getRootTipY());
		            } 
		        }  
		        
				groot.getBottomView();
				
			}
			for (Object obj : context) // iterate through all objects that are rootRoot and move them in the correct position
			{ 
			    if(obj instanceof waterSimulation.Root.RootNode)
			    {
			    	RootNode current = (RootNode) obj;
			    	NdPoint pt = space.getLocation(obj); // extract point of each tip and position them in the grid
						space.moveTo(obj, (int) current.getRootTipX(), (int) current.getRootTipY());
						grid.moveTo(obj, (int) current.getRootTipX(), (int) current.getRootTipY());
						

			    }
			}
			
		/////end of root system placement
		////////////////
			
			
		//create soil
		int soilType = (Integer) params.getValue("soil_type");	
		for( int i=0; i<1500; i++)
		{
			context.add(new Soil(space, grid));
		}
		for (Object obj : context) // iterate through all objects that are Water
		{ 
			if(obj instanceof waterSimulation.Soil)
			{
				NdPoint pt = space.getLocation(obj); // extract point of each soil particle 
				if (pt.getY() >= grid_size/2 ) // soil is above the ground
				{
					double newY = RandomHelper.nextIntFromTo(grid_size/3, grid_size/2); // generate newY
					space.moveTo(obj, (int) pt.getX(), (int) newY);
					grid.moveTo(obj, (int) pt.getX(), (int) newY);
				}
			 }
		}	
		
		//adding rocks
		int rock_count = (Integer) params.getValue("rock_count");	
		for( int i=0; i<rock_count; i++)
		{
			context.add(new Abiotic(space, grid));
		}
		for (Object obj : context) // iterate through all objects that are Water
		{ 
			if(obj instanceof waterSimulation.Abiotic)
			{
				NdPoint pt = space.getLocation(obj); // extract point of each rock 
				if (pt.getY() >= grid_size/2 || pt.getY() <= grid_size/3) // soil is above the ground
				{
					double newY = RandomHelper.nextIntFromTo(0, (grid_size/2)-1); // generate newY
					space.moveTo(obj, (int) pt.getX(), (int) newY);
					grid.moveTo(obj, (int) pt.getX(), (int) newY);
				}
			 }
		}	
		
		
		//creating Leafs

		//adding pest and pathogens
				int pest_count = (Integer) params.getValue("pest_count");	
				for( int i=0; i<pest_count; i++)
				{
					context.add(new Biotic(space, grid));
				}
				for (Object obj : context) // iterate through all objects that are Water
				{ 
					if(obj instanceof waterSimulation.Biotic)
					{
						NdPoint pt = space.getLocation(obj); // extract point of each rock 
						if (pt.getY() >= grid_size/2 || pt.getY() <= grid_size/3) // soil is above the ground
						{
							double newY = RandomHelper.nextIntFromTo(0, (grid_size/2)-1); // generate newY
							space.moveTo(obj, (int) pt.getX(), (int) newY);
							grid.moveTo(obj, (int) pt.getX(), (int) newY);
						}
					 }
				}	
				
	
				
				
		// Creates water in random positions in the environment below groundLevel
		int waterCount = (Integer) params.getValue("water_count");
		
		
		for (int i = 0; i < waterCount; i++) 
		{
			double move_angle = RandomHelper.nextIntFromTo(0, 360);
			context.add(new Water(space, grid, move_angle, stems, i, rs)); // create instance of water		
		}
		
		for (Object obj : context) // iterate through all objects that are Water
		{ 
			
			if(obj instanceof waterSimulation.Water)
			{
				NdPoint pt = space.getLocation(obj); // extract point of each water 
				if (pt.getY() >= grid_size/2 ) // water is above the ground
				{
					double newY = RandomHelper.nextIntFromTo(0, grid_size/3); // generate newY
					space.moveTo(obj, (int) pt.getX(), (int) newY);
					grid.moveTo(obj, (int) pt.getX(), (int) newY);
				}
			 }
			}


		// Calculates the initial scatter factor, which has to be close to the intented scatter factor in the parameters
		//float initial_scatter_factor = (1 - (float) (groups + individual) / (float)tipCount); // uncomment this if using tips
		//System.out.println("Initial scatter factor: " + initial_scatter_factor);
		
		// Aligns objects in continuous space with grid 
		for (Object obj : context) 
		{
			NdPoint pt = space.getLocation(obj);
			grid.moveTo(obj, (int) pt.getX(), (int) pt.getY());
		}
		
		//LogWriter eval = new LogWriter(initial_scatter_factor);
		//context.add(eval);
		
		// If this is batch, stop at the tick 2501 (just after the log is created)
		if (RunEnvironment.getInstance().isBatch()) 
		{
			RunEnvironment.getInstance().endAt(2501);
		}

		return context;
	}
	
}
