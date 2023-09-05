package waterSimulation;

public class Stem 
{

	private int pile_of_tips = 0;	
	
	public void insertTip() 
	{
		pile_of_tips += 1;
	}
	
	public int getSizeOfPile() 
	{
		return pile_of_tips;
	}
}
