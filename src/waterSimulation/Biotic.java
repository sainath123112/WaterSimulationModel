package waterSimulation;

import java.util.List;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
import repast.simphony.util.SimUtilities;

public class Biotic {
	private ContinuousSpace < Object > space;
	private Grid < Object > grid;
	
	public Biotic(ContinuousSpace<Object> space, Grid<Object> grid) {
		this.space = space;
		this.grid = grid;
	}
	public void step() 
	{
	
//		System.out.println(grid);
		Context context = ContextUtils.getContext(this);
		Parameters params = RunEnvironment.getInstance().getParameters();
//		System.out.println(grid);
		GridPoint pt = grid.getLocation(this); // get the grid location of this rootnode 
		GridCellNgh<Water> nghCreator = new GridCellNgh<Water>(grid, pt, Water.class, 1, 1); // use the GridCellNgh class to create GridCells for the surrounding neighborhood.
		List<GridCell<Water>> gridCells = nghCreator.getNeighborhood(true);
		SimUtilities.shuffle(gridCells, RandomHelper.getUniform());
	
		
	}

}
