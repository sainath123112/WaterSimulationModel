package waterSimulation.Root;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
import waterSimulation.Attraction2;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid ;
import java.util.Map.Entry; 
import java.util.*; 

public class Groot //Groot is the entire root structure
{
	private RootNode rootRoot; //root of the binary tree for the roots
	private int maxNode;
	private double baseRootDiameter;
	private double baseRootLength;
	private double rootOriginX;
	private double rootOriginY;
	private ContinuousSpace < Object > space;
	private Grid < Object > grid;
	private boolean Attraction_found;
	private Map<Integer, Integer> map;
	private double lr_flag;
	private double rootAngle;
	
	
	public Groot(ContinuousSpace <Object> space, Grid <Object> grid, double baseRoot , double baseRootLength, double rootOriginX, double rootOriginY, double rootAngle, double lr_flag, int maxNode) // make a new root system with the starting base diameter and base root length
	{
		this.grid = grid;
		this.space = space;
		this.rootRoot=null;
		this.baseRootDiameter=baseRootDiameter;
		this.baseRootLength = baseRootLength;
		this.rootOriginX=rootOriginX;
		this.rootOriginY=rootOriginY;
		this.Attraction_found = false;
		this.lr_flag = lr_flag;
		this.rootAngle=rootAngle;
		this.addGroot(maxNode);
		
	}
	
	public void addGroot(int maxNode) 
	{
		
		for (int i=0; i<maxNode; i++)
		{
//			System.out.println("grr"+this.grid);
			
			if (rootRoot==null) //base case
			{
			    rootRoot= new RootNode (this.space, this.grid, this.baseRootDiameter, this.baseRootLength, this.rootOriginX, this.rootOriginY, rootAngle,lr_flag, 0);  //base case
			    //System.out.println("rootNodePrinted");
			    //System.out.printf("%4.3f , %4.3f\n",rootRoot.getRootTipX(),rootRoot.getRootTipY());
			}
			else
			{
				RootNode added= addRoot(i);
				//System.out.printf("ChildNodePrinted %d\n",i);
				//System.out.println(added.toString());
			    //System.out.printf("%4.3f , %4.3f\n",added.getRootTipX(),added.getRootTipY());
			}
		}
	}
	
	public boolean isAttraction_found() 
	{
		
		return Attraction_found;
	}

	public void setAttraction_found(boolean attraction_found) 
	{
		Attraction_found = attraction_found;
	}
	
	private RootNode addRoot(int rootID)
	{
		//find a place to add the root
		RootNode curr = rootRoot;
        RootNode prev = null;
        while(curr != null) 
        {
            if(Math.random() < 0.5) 
            {
                prev = curr;
                curr = curr.getLeftChild();
            } else 
            {
                prev = curr;
                curr = curr.getRightChild();
            }
        }
		
        RootNode newNode = null; // new node with rootID and the parent node
        
        if(prev.getLeftChild() ==null ) 
        {
        	newNode=new RootNode(this.space, this.grid, rootID, prev,rootAngle,lr_flag,1 ); //tilt left
            prev.setLeftChild(newNode);
//            newNode.setParent(prev);
        } else 
        {
        	newNode=new RootNode(this.space, this.grid, rootID, prev,rootAngle,lr_flag, -1); //tilt right
            prev.setRightChild(newNode);
//            newNode.setParent(prev);
        }
        return newNode;
	}
	
	public boolean containsElement(RootNode item)
	{
		return (this.findElement(item) != -1);
	}
	
	public int findElement(RootNode item) 
	{
        int result = 0;
        if(rootRoot == null)
            return -1;

        Stack<RootNode> s = new Stack<RootNode>();
        RootNode curr = rootRoot;
        while(curr != null) {
            if(curr.getRightChild() != null)
                s.push(curr.getRightChild());
            if(curr.getLeftChild() != null)
                s.push(curr.getLeftChild());
            if(curr.getRootID()==item.getRootID())
                return curr.getRootID();
            if(s.isEmpty())
                curr = null;
            else
                curr = s.pop();
        }

        return result;
    }
	
	public void inOrderTraverse() 
	{
	    printInOrderTraverse(rootRoot);
	}
	
	private void printInOrderTraverse(RootNode t) 
	{
    	if(t != null) 
    	{
    		printInOrderTraverse(t.getLeftChild());
//    		System.out.print(t.getRootID() +" ");

		    System.out.printf("%4.3f , %4.3f\n",t.getRootTipX(),t.getRootTipY());
    		printInOrderTraverse(t.getRightChild());
    	}
    }
	
	public RootNode getRootRoot() 
	{
		return rootRoot;
	}

	public void setRootRoot(RootNode rootRoot) 
	{
		this.rootRoot = rootRoot;
	}
	
//	public void getBottomView()
	public Map<Integer, Integer> getBottomView() // outputs deepest tips of groot
    { 
        if (rootRoot == null) 
            return null;
//        	return;
  
        // Initialize a variable 'hd' with 0 for the root element. 
        int hd = 0; 
  
        // TreeMap which stores key value pair sorted on key value 
        Map<Integer, Integer> map = new TreeMap<>(); 
  
         // Queue to store tree nodes in level order traversal 
        Queue<RootNode> rootqueue = new LinkedList<RootNode>(); 
  
        // Assign initialized horizontal distance value to root node and add it to the queue. 
        rootRoot.hd = hd; 
        rootqueue.add(rootRoot); 
  
        // Loop until the queue is empty (standard level order loop) 
        while (!rootqueue.isEmpty()) 
        { 
            RootNode temp = rootqueue.remove(); 
  
            // Extract the horizontal distance value from the dequeued tree node. 
            hd = temp.hd; 
  
            //check if node is childless
            if (temp.leftChild == null && temp.rightChild == null)
            {
            	// Put the dequeued tree node to TreeMap having key as horizontal distance. Every time we find a node 
            	// having same horizontal distance we need to replace the data in the map. 
            	map.put(hd, temp.rootID); //hd is key and rootID is value
            }
            // If the dequeued node has a left child add it to the queue with a horizontal distance hd-1. 
            if (temp.leftChild != null) 
            { 
                temp.leftChild.hd = hd-1; 
                rootqueue.add(temp.leftChild); 
            } 
            // If the dequeued node has a left child add it to the queue with a horizontal distance hd+1. 
            if (temp.rightChild != null) 
            { 
                temp.rightChild.hd = hd+1; 
                rootqueue.add(temp.rightChild); 
            } 
        } 
        
        // Extract the entries of map into a set to traverse an iterator over that. 
        Set<Entry<Integer, Integer>> set = map.entrySet(); 
        
        return map;        

        //Make an iterator 
//        Iterator<Entry<Integer, Integer>> iterator = set.iterator(); 
//  
//        // Traverse the map elements using the iterator.
//        while (iterator.hasNext()) 
//        {
//            Entry<Integer, Integer> me = iterator.next();
//            System.out.println(me.getKey() + " => " + me.getValue());
//        }
    } 
	
	public RootNode search(int ID, RootNode node)
	{
	    if(node != null)
	    {
	        if(node.getRootID() == ID)
	        {
	           return node;
	        } 
	        else 
	        {
	            RootNode foundNode = search(ID, node.leftChild);
	            if(foundNode == null) 
	            {
	                foundNode = search(ID, node.rightChild);
	            }
	            return foundNode;
	         }
	    } 
	    else 
	    {
	        return null;
	    }
	}
	
	public void addAttractionToTips() //This functions adds an attraction to the bottom tip's location
	{
		map = getBottomView();
		//map has hd is key and rootID is value
		Set<Entry<Integer, Integer>> set = map.entrySet(); //extract entries of map into a set to transverse an iterator over that
		Iterator<Entry<Integer, Integer>> iterator = set.iterator(); // make an iterator
		RootNode temp;
		Entry<Integer, Integer> me = iterator.next();
		
		while (iterator.hasNext()) // adding attraction to the bottom-most tips
		{
			temp = search(me.getValue(), rootRoot); // returning the node that is in the map by ID
			//System.out.println(temp.toString()); System.out.println(me.getValue());
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
		//System.out.println("fred");
		//System.out.println(this);
		int maxIntensity = 0;
		GridPoint pt = grid.getLocation(this);
		
		//System.out.println(pt);
		List<GridPoint> lottery = new ArrayList<GridPoint>();
		int[] this_coordinates  = pt.toIntArray(null);
		//System.out.println("fred is a bitch");
		
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
	
	
	
}
