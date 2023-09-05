package waterSimulation;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;

public class Leafs {
	private ContinuousSpace < Object > space;
	private Grid < Object > grid;
	
	public Leafs(ContinuousSpace<Object> space, Grid<Object> grid) {
		this.space = space;
		this.grid = grid;
	}
}

