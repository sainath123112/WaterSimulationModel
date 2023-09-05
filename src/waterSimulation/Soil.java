package waterSimulation;

import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;

public class Soil {
	private ContinuousSpace < Object > space;
	private Grid < Object > grid;
	
	public Soil(ContinuousSpace<Object> space, Grid<Object> grid) {
		this.space = space;
		this.grid = grid;
	}
}
