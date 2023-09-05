package waterSimulation;

import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;

public class Abiotic {
	private ContinuousSpace < Object > space;
	private Grid < Object > grid;
	
	public Abiotic(ContinuousSpace<Object> space, Grid<Object> grid) {
		this.space = space;
		this.grid = grid;
	}
}