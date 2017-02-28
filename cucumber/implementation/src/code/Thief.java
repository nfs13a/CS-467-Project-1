package code;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;
import java.util.Vector;

/**
 * a globe is an item (named such thanks to Dr. Homer)
 * name - String identifier
 * value - double value of the item
 * cost - double cost of the item
 * will be set with data from a file, so only need getters, no setters
 * a sack or a tree build to solve the problem will contain many globes
 */
class globe {
	
	private String name;
	private double value;
	private double cost;
	
	public globe(String n, double v, double c) {
		name = n;
		value = v;
		cost = c;
	}
	
	public String getName() {
		return name;
	}
	
	public double getValue() {
		return value;
	}
	
	public double getCost() {
		return cost;
	}
	
	public double getRatio() {
		return value / cost;
	}
}

//three comparators here to sort globes by highest value, lowest cost, and highest value/cost ratio
//only used with priority queues, which by nature automatically sort things, and will provide the items in order of the sort
class PQsortValue implements Comparator<globe> {
	public int compare(globe one, globe two) {
		if (one.getValue() > two.getValue()) {	//higher value sorted to the front
			return -1;
		} else if (one.getValue() < two.getValue()) {	//lower value sorted to the back
			return 1;
		}
		return 0;	//ties allowed
	}
}

class PQsortCost implements Comparator<globe> {
	public int compare(globe one, globe two) {
		if (one.getCost() > two.getCost()) {	//higher cost sorted to the back
			return 1;
		} else if (one.getCost() < two.getCost()) {	//lower cost sorted to the front
			return -1;
		}
		return 0;	//ties allowed
	}
}

class PQsortRatio implements Comparator<globe> {
	public int compare(globe one, globe two) {
		if (one.getRatio() > two.getRatio()) {	//higher ratio sorted to the front
			return -1;
		} else if (one.getRatio() < two.getRatio()) {	//lower ratio sorted to the back
			return 1;
		}
		return 0;	//ties allowed
	}
}

/**
 * A sack is used to compute the upper and lower bounds of the problem.
 * lowerBoundValue - double value of the lower bound
 * lowerBoundCost - double cost of the lower bound
 * upperBoundValue - double value of the upper bound
 * upperBoundCost - double cost of the upper bound
 * lowerBoundItems - Vector of globes to store all the items that make up the lower bound
 * upperBoundItems - Vector of globes to store all the items that make up the upper bound
 * capacity - double maximum cost the sack can hold
 */
class sack {
	
	private double lowerBoundValue;
	private double lowerBoundCost;
	private double upperBoundValue;
	private double upperBoundCost;
	private Vector<globe> lowerBoundItems;
	private Vector<globe> upperBoundItems;
	private double capacity;
	
	//constructor, capacity passed in
	public sack(double c) {
		lowerBoundValue = 0;
		lowerBoundCost = 0;
		upperBoundValue = 0;
		upperBoundCost = 0;
		lowerBoundItems = new Vector<>();
		upperBoundItems = new Vector<>();
		capacity = c;
	}
	
	public double getCapacity() {
		return capacity;
	}
	
	//pass in list of globes to calculate lower bound
	public void setLowerBound(Vector<globe> list) {
		//use 3 priority queues to sort the globes
		PriorityQueue<globe> value = new PriorityQueue<globe>(1, new PQsortValue());
		PriorityQueue<globe> cost = new PriorityQueue<globe>(1, new PQsortCost());
		PriorityQueue<globe> ratio = new PriorityQueue<globe>(1, new PQsortRatio());
		
		for (globe g : list) {
			value.add(g);
			cost.add(g);
			ratio.add(g);
		}
		
		//form initial lower bound from list sorted by highest value
		do {
			String itemName = value.peek().getName();
			double tempVal = value.peek().getValue();
			double tempCost = value.poll().getCost();
			if (lowerBoundCost + tempCost <= capacity) {	//add head globe to list only if it does not force the set over capacity
				lowerBoundValue += tempVal;
				lowerBoundCost += tempCost;
				lowerBoundItems.add(new globe(itemName, tempVal, tempCost));
			}
		} while (!value.isEmpty());	//look at all globes
		
		//determine a potential lower bound from list sorted by lowest cost
		double totalFromCost = 0;	//value
		double capacityFromCost = 0;	//cost
		Vector<globe> itemsTakenByCost = new Vector<>();
		while (cost.peek() != null && totalFromCost + cost.peek().getCost() <= capacity) {
			String itemName = cost.peek().getName();
			double tempVal = cost.peek().getValue();
			double tempCost = cost.poll().getCost();
			itemsTakenByCost.add(new globe(itemName, tempVal, tempCost));
			totalFromCost += tempVal;
			capacityFromCost += tempCost;
		}
		
		if (totalFromCost > lowerBoundValue) {	//if the lower bound from cost has higher value than the lower bound from value, save the new lower bound
			lowerBoundValue = totalFromCost;
			lowerBoundCost = capacityFromCost;
			lowerBoundItems = itemsTakenByCost;
		}
		
		//determine a potential lower bound from list sorted by highest ratio
		double totalFromRatio = 0;	//value
		double capacityFromRatio = 0;	//cost
		Vector<globe> itemsTakenByRatio = new Vector<>();
		while (ratio.peek() != null && totalFromRatio + ratio.peek().getCost() <= capacity) {
			String itemName = ratio.peek().getName();
			double tempVal = ratio.peek().getValue();
			double tempCost = ratio.poll().getCost();
			itemsTakenByRatio.add(new globe(itemName, tempVal, tempCost));
			totalFromRatio += tempVal;
			capacityFromRatio += tempCost;
		}
		
		if (totalFromRatio > lowerBoundValue) {	//if the lower bound from ratio has higher value than the lower bound from value, save the new lower bound
			lowerBoundValue = totalFromRatio;
			lowerBoundCost = capacityFromRatio;
			lowerBoundItems = itemsTakenByRatio;
		}
	}
	
	public double getLowerBoundCost() {
		return lowerBoundCost;
	}
	
	public double getLowerBoundValue() {
		return lowerBoundValue;
	}
	
	//examine the items sorted by highest ratio, take until the next will go over capacity, then take that one and stop
		public void setUpperBound(Vector<globe> list) {
			//sort with a priority queue
			PriorityQueue<globe> ratio = new PriorityQueue<globe>(1, new PQsortRatio());
			
			for (globe g : list) {
				ratio.add(g);
			}
			
			//loop until sum of globe costs is greater than capacity
			while (upperBoundCost <= capacity && !ratio.isEmpty()) {
				String itemName = ratio.peek().getName();
				double tempVal = ratio.peek().getValue();
				double tempCost = ratio.poll().getCost();
				upperBoundItems.add(new globe(itemName, tempVal, tempCost));
				upperBoundValue += tempVal;
				upperBoundCost += tempCost;
			}
		}
		
		//print the capacity, the bounds with their values, costs, and items
		public void printBounds() {
			System.out.println("Capacity: " + capacity);
			
			//lower bound
			System.out.println("\nLower Bound: " + lowerBoundValue);
			System.out.println("Cost: " + lowerBoundCost);
			
			//using a List because Collections.sort will sort a List but not a Vector (polymorphism yay)
			List<String> lower = new Vector<String>();
			
			for (globe g : lowerBoundItems) {
				lower.add(g.getName());
			}
			
			Collections.sort(lower);
			
			System.out.print("|");
			for (String g : lower) {
				System.out.print(" " + g + " |");
			}
			
			//upper bound
			System.out.println("\n\nUpper Bound: " + upperBoundValue);
			System.out.println("Cost: " + lowerBoundValue);
			System.out.print("|");
			
			List<String> upper = new Vector<String>();
			
			for (globe g : upperBoundItems) {
				upper.add(g.getName());
			}
			
			Collections.sort(upper);
			for (String g : upper) {
				System.out.print(" " + g + " |");
			}
			System.out.println("");
		}
}

/**
 * A node in a tree that holds a globe and defines whether or not the globe was taken
 *
 */
class TreeNode {
	protected int self;	//# of node in TreeManager.tree (map)
	private globe data;	//actual globe
	protected Integer parent;	//# of parent node in TreeManager.tree
	private Integer left;	//# of child not taken in tree
	private Integer right;	//# of child taken in tree
	private boolean taken;	//if this node was taken or not (will be right child of parent)
	protected boolean trueLeaf;	//if this is node will never have children (is last in queue of globes)
	protected int depth;	//how many nodes from root corresponds to placement in allGlobes Vector
	
	/**
	 * @param s - self identifier (int)
	 * @param g - globe data (globe)
	 * @param p - parent identifier (Integer)
	 * @param take - taken flag (boolean)
	 * @param d - depth identifier (int)
	 */
	public TreeNode(int s, globe g, Integer p, boolean take, int d) {
		self = s;
		data = g;
		parent = p;
		left = null;
		right = null;
		taken = take;
		trueLeaf = false;
		depth = d;
	}
	
	public int getSelf() {
		return self;
	}
	
	public globe getData() {
		return data;
	}
	
	public String getName() {
		return data.getName();
	}
	
	public double getValue() {
		return data.getValue();
	}
	
	public double getCost() {
		return data.getCost();
	}
	
	public double getRatio() {
		return data.getRatio();
	}
	
	public int getParent() {
		return parent;
	}
	
	public void setLeft(Integer g) {
		left = g;
	}
	
	public Integer getLeft() {
		return left;
	}
	
	public void setRight(Integer g) {
		right = g;
	}
	
	public Integer getRight() {
		return right;
	}
	
	public void setTaken(boolean b) {
		taken = b;
	}
	
	public boolean getTaken() {
		return taken;
	}
	
	public boolean isTrueLeaf() {
		return trueLeaf;
	}
	
	public void setTrueLeaf(boolean b) {
		trueLeaf = b;
	}
	
	public int getDepth() {
		return depth;
	}
}

/**
 * structure to hold the tree, keep track of current position, best value and cost found
 *
 */
class TreeManager {
	protected Map<Integer, TreeNode> tree;	//<int identifier of node, TreeNode with globe data etc.>
	protected double bestVal;	//the best found value of a complete sack
	protected double bestCost;	//the cost of the best complete sack
	protected int newestNode;	//# of TreeNode's created (so we can create more when needed)
	protected double currVal;	//the value of the current sack
	protected double currCost;	//the vost of the current sack
	protected int currentNode; // # of the TreeNode we are currently on in tree (different
						// from newestNode, which keeps track of the number of
						// TreeNode's made to keep our identifiers unique)
	protected Vector<globe> allGlobes;	//list of all globes
	
	protected Vector<Integer> bestPath;	//ordered integer list of self values of optimal sack
	protected double bagCap;
	protected Vector<Integer> currentGlobes;	//indexes of all globes in the current sack
	
	protected long startTime;
	protected long endTime;
	
	/**
	 * @param globes - list of all globes in house (Vector)
	 * @param e - capacity of sack (double)
	 */
	public TreeManager(Vector globes, double e) {
		tree = new HashMap<Integer, TreeNode>();
		tree.put(0, new TreeNode(0, null, null, false, 0));
		bestVal = 0;
		bestCost = 0;
		newestNode = 0;
		currVal = 0;
		currCost = 0;
		currentNode = 0;
		allGlobes = new Vector<globe>(globes);
		bestPath = new Vector<Integer>();
		//bag = new sack(e);
		bagCap = e;
		currentGlobes = new Vector<Integer>();
		startTime = 0;
		endTime = 0;
	}
	
	//given that a leaf node has been reached, save the best value and cost
	protected void saveBestPath() {
		//if the found value is less than the best value, or the cost is over capacity, do nothing
		if (currVal <= bestVal || currCost > bagCap) {
			//if current node has been taken, remove its value and cost
			if (tree.get(currentNode).getTaken()) {
				currVal -= tree.get(currentNode).getValue();
				currCost -= tree.get(currentNode).getCost();
			}
			return;
		}
		
		//climbs up the tree from the current node
		TreeNode temp = tree.get(currentNode);
		
		//saves taken nodes
		Vector<Integer> v = new Vector<>();
		
		//climbing and saving here
		while (temp.getSelf() != 0) {
			v.addElement(temp.getSelf());
			temp = tree.get(temp.getParent());
		}
		
		//save new path, value, cost
		bestPath = new Vector<>(v);
		bestVal = currVal;
		bestCost = currCost;
		
		//remove current node value and cost if it is taken (for continued searching
		if (tree.get(currentNode).getTaken()) {
			currVal -= tree.get(currentNode).getValue();
			currCost -= tree.get(currentNode).getCost();
		}
	}
	
	//method that creates path in tree of taken nodes to create sacks
	public void FindOptimalSack() {
		System.out.println("\nBrute Force");
		
		//begin timing
		startTime = System.nanoTime();
		
		//when the current node is the root and the both the root's children are not null (have been assigned ever), then the search is done
		while (currentNode != 0 || (tree.get(0).getLeft() == null)) {
			//current TreeNode
			TreeNode tempCurr = tree.get(currentNode);

			//if the next globe has been been taken
			if (tempCurr.getRight() == null) {
				//create a new TreeNode to insert if needed with incremented newestNode identifier, data from allGlobes of its parent's depth (its depth - 1), the parent identifier, and the depth of parent + 1
				TreeNode temp = new TreeNode(++newestNode, allGlobes.elementAt((tempCurr.getDepth())), tempCurr.getSelf(), true, tempCurr.getDepth() + 1);
				
				//put new TreeNode in the tree
				tree.put(temp.getSelf(), temp);
				
				//assign new TreeNode as the right child of the current TreeNode
				tempCurr.setRight(temp.getSelf());
				
				//add new TreeNode value to current value sum
				currVal += temp.getValue();
				
				//add new TreeNode cost to current cost sum
				currCost += temp.getCost();
				
				//the new TreeNode becomes the current TreeNode
				currentNode = temp.getSelf();
			} else if (tempCurr.getLeft() == null) {
				//same as going right, but without taking the globe, so no addition to current sums
				TreeNode temp = new TreeNode(++newestNode, allGlobes.elementAt((tempCurr.getDepth())), tempCurr.getSelf(), false, tempCurr.getDepth() + 1);
				tree.put(temp.getSelf(), temp);
				tempCurr.setLeft(temp.getSelf());
				currentNode = temp.getSelf();
			} else {	//both children have been assigned, so go up a level
				if (tempCurr.getTaken()) {	//remove current values from the current sums
					currVal -= tempCurr.getValue();
					currCost -= tempCurr.getCost();
				}
				
				//remove children from the tree if they are not part of the best collection
				if (!bestPath.contains(tempCurr.getRight())) {
					tree.remove(tempCurr.getRight());
				}
				
				if (!bestPath.contains(tempCurr.getLeft())) {
					tree.remove(tempCurr.getLeft());
				}
				
				currentNode = tempCurr.getParent();
			}
			
			//if we have reached a leaf node, check to see if a new best path has been found
			if (tree.get(currentNode).getDepth() == allGlobes.size()) {
				saveBestPath();
	
				//remove current node from the tree if it does not become part of the best path
				if (!bestPath.contains(currentNode)) {
					tree.remove(currentNode);
				}
				
				//stay put
				currentNode = tempCurr.getSelf();
			}

			//if the search has taken over seven minutes, break
			Long sevenMinutes = new Long("420000000000");
			if (System.nanoTime() - startTime > sevenMinutes) {
				System.out.println("Error: calculation took over 7 minutes, incomplete search.");
				break;
			}
		}
		
		//stop timing here, for best path has been found or the search has taken too long
		endTime = System.nanoTime();
	}
	
	//print best sack values
	public void getOptimalSack() {
		//create List for best path globe names, which can be sorted easily
		List<String> best = new Vector<String>();
		
		//put globe names into List
		for (int i = bestPath.size() - 1; i >= 0; i--) {
			TreeNode temp = tree.get(bestPath.elementAt(i));
			if (temp.getTaken()) 
				best.add(temp.getName());
		}
		
		//sort globe names
		Collections.sort(best);
		
		//output results
		System.out.println("Value: " + bestVal);
		System.out.println("Cost: " + bestCost);
		System.out.print("|");
		for (String g : best) {
			System.out.print(" " + g + " |");
		}
		System.out.println("\nSolution Time: " + getFormattedTime(endTime - startTime));
	}
	
	//format the total # of nanoseconds the program took to find an optimal combination
	private String getFormattedTime(Long total) {
		Long oneMinute = new Long("60000000000");
		String time = "";
		long seconds = 0;	//the way I find seconds is totally unnecessary, but I was finding minutes first and this works, so I am leaving it
		if (total.compareTo(oneMinute) >= 0) {	//nine 0's before
			seconds = total / oneMinute * 60;
			total %= oneMinute;
		}
		
		if (total.compareTo((long) 1000000000) >= 0) {
			seconds += total / 1000000000;
			total %= 1000000000;
		}
		
		String totalS = total + "";
		
		time += (seconds + ".");
		
		for (int i = 0; i < 9 - totalS.length(); i++) {
			time += "0";
		}
		
		time += (total + " seconds");
		
		return time;
	}
}

//for initial optimizations (Phase 4)
class SmartManager extends TreeManager {
	
	protected Vector<Double> optimalRemainingValue;	//for any globe index i in allGlobes with n globes, holds the value of all globes of i+1..n
	
	public SmartManager(Vector globes, double e) {
		super(globes, e);
		
		optimalRemainingValue = new Vector<Double>();
		optimalRemainingValue.setSize(allGlobes.size());
		setOptimalRemainingValues();
	}
	
	//find the optimal value from any given node
	private void setOptimalRemainingValues() {
		//optimal value at the last node is the last node's value
		optimalRemainingValue.setElementAt(allGlobes.elementAt(allGlobes.size() - 1).getValue(), allGlobes.size() - 1);
		//optimal value at any other node is the node's value plus the optimal value of the next node
		for (int i = allGlobes.size() - 2; i >= 0; i--) {
			optimalRemainingValue.setElementAt(allGlobes.elementAt(i).getValue() + optimalRemainingValue.get(i + 1), i);
		}
	}
	
	//same as TreeManager, with optimizations
	public void FindOptimalSack() {
		System.out.println("\nInitial Optimizations");
		startTime = System.nanoTime();
		while (currentNode != 0 || (tree.get(0).getLeft() == null)) {
			TreeNode tempCurr = tree.get(currentNode);
			if (tempCurr.getRight() == null) {
				TreeNode temp = new TreeNode(++newestNode, allGlobes.elementAt((tempCurr.getDepth())), tempCurr.getSelf(), true, tempCurr.getDepth() + 1);
				
				//do not add to sums and follow if not optimal or exceeds capacity
				if (temp.getCost() + currCost <= bagCap && bestVal < currVal + optimalRemainingValue.elementAt(temp.getDepth() - 1)) {
					tree.put(temp.getSelf(), temp);
					tempCurr.setRight(temp.getSelf());
					currVal += temp.getValue();
					currCost += temp.getCost();
					currentNode = temp.getSelf();
				} else {
					tree.put(temp.getSelf(), temp);
					tempCurr.setRight(temp.getSelf());
				}
			} else if (tempCurr.getLeft() == null) {	//same as right but without adding sometimes
				TreeNode temp = new TreeNode(++newestNode, allGlobes.elementAt((tempCurr.getDepth())), tempCurr.getSelf(), false, tempCurr.getDepth() + 1);
				tree.put(temp.getSelf(), temp);
				tempCurr.setLeft(temp.getSelf());
				
				if (bestVal < currVal + optimalRemainingValue.elementAt(temp.getDepth() - 1) - temp.getValue()) {
					currentNode = temp.getSelf();
				}
			} else {	//remove from tree, sums if not part of best path
				if (tempCurr.getTaken()) {					
					currVal -= tempCurr.getValue();
					currCost -= tempCurr.getCost();
				}
				if (!bestPath.contains(tempCurr.getRight())) {
					tree.remove(tempCurr.getRight());
				}
				
				if (!bestPath.contains(tempCurr.getLeft())) {
					tree.remove(tempCurr.getLeft());
				}
				
				currentNode = tempCurr.getParent();	//go up 1 node
			}
			
			//check for new best path
			if (tree.get(currentNode).getDepth() == allGlobes.size()) {
				saveBestPath();
				
				if (!bestPath.contains(currentNode)) {
					tree.remove(currentNode);
				}
				
				currentNode = tempCurr.getSelf();
			}
			
			//cut short at 7 minutes
			Long sevenMinutes = new Long("420000000000");
			if (System.nanoTime() - startTime > sevenMinutes) {
				System.out.println("Error: calculation took over 7 minutes, incomplete search.");
				break;
			}
		}
		
		//end timer
		endTime = System.nanoTime();
	}
}

//Node for storing 2 globes in 1 node
class DoubleTreeNode {
	
	int self;	//identifier of DoubleTreeNode in tree
	Integer parent;	//identifier of DoubleTreeNode 1 level up
	Integer neither, smaller, larger, both;	//identifiers of the child DoubleTreeNodes
	int depth;	//how many nodes away from root
	boolean smallTaken, largeTaken;	//if the smaller ratio, larger ratio globes were taken
	
	TreeNode small, large;	//TreeNode of the smaller and larger ratios
	double[] optimalBelow;	//total value if all proceeding TreeNodes are taken and (O: neither, 1: smaller, 2: larger, 3: both)
	
	//create DoubleTreeNode with 2 globes
	public DoubleTreeNode(int s, globe smallG, globe largeG, Integer p, boolean takeS, boolean takeL, int d) {
		self = s;
		parent = p;
		neither = smaller = larger = both = null;
		smallTaken = takeS;
		largeTaken = takeL;
		depth = d;
		
		small = new TreeNode(s, smallG, p, takeS, d);
		large = new TreeNode(s, largeG, p, takeL, d);
		
		optimalBelow = new double[4];
		for (int i = 0; i < 4; i++) optimalBelow[i] = 0;
		
	}
	
	//create a copy of a DoubleTreeNode
	public DoubleTreeNode(int s, DoubleTreeNode dTN, Integer p, boolean takeS, boolean takeL, int d) {
		self = s;
		parent = p;
		neither = smaller = larger = both = null;
		smallTaken = takeS;
		largeTaken = takeL;
		depth = d;
		
		small = dTN.getSmall();
		large = dTN.getLarge();
		
		optimalBelow = new double[4];
		for (int i = 0; i < 4; i++) optimalBelow[i] = dTN.getOptimal(i);
	}
	
	public int getSelf() {
		return self;
	}
	
	/**
	 * @return TreeNode with the smaller ratio
	 */
	public TreeNode getSmall() {
		return small;
	}
	
	/**
	 * @return TreeNode with the larger ratio
	 */
	public TreeNode getLarge() {
		return large;
	}
	
	//set the identifier of the DoubleTreeNode child where neither TreeNodes were taken
	public void setNeither(Integer n) {
		neither = n;
	}
	
	//get the identifier of the DoubleTreeNode child where neither TreeNodes were taken
	public Integer getNeither() {
		return neither;
	}
	
	/**
	 * set the identifier of the DoubleTreeNode where only the smaller ratio TreeNode was taken
	 */
	public void setSmaller(Integer n) {
		smaller = n;
	}
	
	/**
	 * @return the identifier of the DoubleTreeNode child where only the smaller ratio TreeNode was taken
	 */
	public Integer getSmaller() {
		return smaller;
	}
	
	/**
	 * set the identifier of the DoubleTreeNode where only the larger ratio TreeNode was taken
	 */
	public void setLarger(Integer n) {
		larger = n;
	}
	
	/**
	 * @return the identifier of the DoubleTreeNode child where only the larger ratio TreeNode was taken
	 */
	public Integer getLarger() {
		return larger;
	}
	
	//set the identifier of the DoubleTreeNode child where both TreeNodes were taken
	public void setBoth(Integer n) {
		both = n;
	}
	
	//get the identifier of the DoubleTreeNode child where both TreeNodes were taken
	public Integer getBoth() {
		return both;
	}
	
	//get distance from this DoubleTreeNode to roots
	public int getDepth() {
		return depth;
	}
	
	//set optimal value for taking (0: neither, 1: smaller, 2: larger, 3: both)
	public void setOptimal(double val, int index) {
		optimalBelow[index] = val;
	}
	
	//get optimal value for taking (0: neither, 1: smaller, 2: larger, 3: both)
	public double getOptimal(int index) {
		return optimalBelow[index];
	}
	
	//set if smaller ratio TreeNode of this DoubleTreeNode was taken
	public void takeSmaller(boolean b) {
		smallTaken = b;
	}
	
	//if smaller ratio TreeNode of this DoubleTreeNode was taken
	public boolean smallTaken() {
		return smallTaken;
	}
	
	//set if larger ratio TreeNode of this DoubleTreeNode was taken
	public void takeLarger(boolean b) {
		largeTaken = b;
	}
	
	//if larger ratio TreeNode of this DoubleTreeNode was taken
	public boolean largeTaken() {
		return largeTaken;
	}

	//identifier of parent DoubleTreeNode
	public int getParent() {
		return parent;
	}
}

//as Tree/SmartManager to TreeNode, holds the tree of DoubleTreeNodes, current node, best value/cost/path found
class DoubleTreeManager {
	//lots of parallel to previous managers here because inheritance did not flow well (DoubleNodes and the implications of their functionality)
	Map<Integer, DoubleTreeNode> tree;
	double capacity;
	Vector<globe> allGlobes;
	Vector<DoubleTreeNode> doubleGlobes;
	int newestNode, currentNode, currentNewNode;
	Queue<Integer> deletedNodes;
	double currCost, currVal, bestCost, bestVal;
	Vector<Integer> bestPath;
	
	long startTime, endTime;
	
	public DoubleTreeManager(Vector globes, double cap) {
		//create tree, initialize root
		tree = new HashMap<Integer, DoubleTreeNode>();
		tree.put(0, new DoubleTreeNode(0, null, null, null, false, false, 0));
		
		allGlobes = new Vector<globe>(globes);
		
		//initialize doubleGlobes vector
		PriorityQueue<globe> ratio = new PriorityQueue<globe>(new PQsortRatio());
		for (globe g : allGlobes) {
			ratio.add(g);
		}
		createDoubleNodes(ratio);
		
		setOptimalRemainingValues();
		
		capacity = cap;
		newestNode = currentNode = currentNewNode = 0;
		currCost = currVal = bestCost = bestVal = 0;
		bestPath = new Vector<Integer>();
		
		startTime = endTime = 0;
		
		deletedNodes = new LinkedList<Integer>();
	}
	
	//create double nodes from ordered (by highest ratio) list of globes (TreeNodes)
	private void createDoubleNodes(PriorityQueue<globe> ratio) {
		doubleGlobes = new Vector<DoubleTreeNode>();
		int n = 0;	//# of DoubleTreeNodes created, identifier of next one
		
		//assign in pairs
		while (ratio.size() > 1) {
			globe large = ratio.poll();
			globe small = ratio.poll();
			doubleGlobes.addElement(new DoubleTreeNode(n, small, large, n - 1, true, true, n + 1));
			n++;
		}
		
		//if 1 extra TreeNode, make give its partner null and 0 values for later identification
		if (ratio.size() == 1) {
			doubleGlobes.addElement(new DoubleTreeNode(n, new globe(null, 0, 0), ratio.poll(), n - 1, true, true, n + 1));
		}
	}
	
	//must set optimal values for taking neither, smaller, larger, both
	private void setOptimalRemainingValues() {
		//initialize leaf nodes
		double sm = doubleGlobes.elementAt(doubleGlobes.size() - 1).getSmall().getValue();
		double lg = doubleGlobes.elementAt(doubleGlobes.size() - 1).getLarge().getValue();
		
		int finalIndex = doubleGlobes.size() - 1;
		DoubleTreeNode dLast = doubleGlobes.elementAt(doubleGlobes.size() - 1);
		dLast.setOptimal(0, 0);
		dLast.setOptimal(sm, 1);
		dLast.setOptimal(lg, 2);
		dLast.setOptimal(lg + sm, 3);
		doubleGlobes.setElementAt(dLast, finalIndex);
		
		//start at parents of leaves, work up
		for (int i = doubleGlobes.size() - 2; i >= 0; i--) {
			
			DoubleTreeNode dI = doubleGlobes.elementAt(i);
			
			double prevTotal = 0;
			
			if (doubleGlobes.elementAt(i + 1).getSmall().getName() != null) {
				prevTotal += doubleGlobes.elementAt(i + 1).getOptimal(3);
			} else {
				prevTotal += doubleGlobes.elementAt(i + 1).getOptimal(2);
			}
			
			double l = dI.getLarge().getValue();
			double s = dI.getSmall().getValue();
			
			
			dI.setOptimal(prevTotal, 0);
			dI.setOptimal(prevTotal + s, 1);
			dI.setOptimal(prevTotal + l, 2);
			dI.setOptimal(prevTotal + l + s, 3);
			
			doubleGlobes.setElementAt(dI, i);
			
		}
	}
	
	protected void saveBestPath() {
		//if something went wrong and current cost exceeds capacity or current value is not greater than best value, do nothing
		if (currVal <= bestVal || currCost > capacity) {
			return;
		}
		
		//climb up tree
		DoubleTreeNode temp = tree.get(currentNode);
		
		//save visited nodes
		Vector<Integer> v = new Vector<>();
		while (temp.getSelf() != 0) {
			v.addElement(temp.getSelf());
			temp = tree.get(temp.getParent());
		}
		
		//save new values
		bestPath = new Vector<>(v);
		bestVal = currVal;
		bestCost = currCost;
	}
	
	//new search method for DoubleTreeManager, optimized from previous 2 (fewer redundant lines)
	public void FindOptimalSack() {
		System.out.println("\nPersonal Optimizations");
		
		startTime = System.nanoTime();
		while (currentNode != 0 || (tree.get(0).getSmaller() == null)) {
			DoubleTreeNode tempCurr = tree.get(currentNode);
			
			//we are keeping track of which keys are deleted from the map that, while holding nodes, acts as a tree
			//the associated values are set to null, but the keys are not removed, so they can be reused later because we climb up,
			//not down, the tree when following created nodes when investigating a potential new best path
			int tempID;
			if (!deletedNodes.isEmpty()) {	//use an old key if one exists
				tempID = deletedNodes.poll();
			} else {	//use the next highest key
				tempID = ++newestNode;
			}
			
			DoubleTreeNode temp = new DoubleTreeNode(tempID, doubleGlobes.elementAt(tempCurr.getDepth()), tempCurr.getSelf(), false, false, tempCurr.getDepth() + 1);
			
			//quick variables for value,cost of smaller,larger
			double costL = temp.getLarge().getCost(), costS = temp.getSmall().getCost(), valL = temp.getLarge().getValue(), valS = temp.getSmall().getValue();
			
			//take both first
			if (tempCurr.getBoth() == null) {
				//assign that they were taken
				temp.takeSmaller(true);
				temp.takeLarger(true);
				
				//only add to sums with previous constraints, and if the smaller one does not equal null (this could be true if there were an odd number of TreeNodes)
				if (costL + costS + currCost <= capacity && bestVal < temp.getOptimal(3) + currVal && temp.getSmall().getName() != null) {
					currVal += (valS + valL);
					currCost += (costS + costL);
					currentNode = temp.getSelf();
				}
				
				//assign to tree
				tree.put(temp.getSelf(), temp);
				tempCurr.setBoth(temp.getSelf());
				
				if (tempCurr.getDepth() == doubleGlobes.size() - 1 && currentNode == tempCurr.getSelf()) {
					tree.remove(temp.getSelf());
					continue;
				}
			} else if (tempCurr.getLarger() == null) {	//next only take larger ratio
				temp.takeSmaller(false);
				temp.takeLarger(true);
				
				if (costL + currCost <= capacity && bestVal < temp.getOptimal(2) + currVal) {
					currVal += (valL);
					currCost += (costL);
					currentNode = temp.getSelf();
				}
				
				tree.put(temp.getSelf(), temp);
				tempCurr.setLarger(temp.getSelf());
				
				if (tempCurr.getDepth() == doubleGlobes.size() - 1 && currentNode == tempCurr.getSelf()) {
					tree.remove(temp.getSelf());
					continue;
				}
			} else if (tempCurr.getNeither() == null) {	//then take neither (making an assumption that taking smaller could be less efficient than taking larger)
				//"take" values are correct
				
				if (bestVal < temp.getOptimal(0) + currVal) {
					//currVal += 0;
					//currCost += 0;
					currentNode = temp.getSelf();
				}
				
				tree.put(temp.getSelf(), temp);
				tempCurr.setNeither(temp.getSelf());
				
				if (tempCurr.getDepth() == doubleGlobes.size() - 1 && currentNode == tempCurr.getSelf()) {
					tree.remove(temp.getSelf());
					continue;
				}
			} else if (tempCurr.getSmaller() == null) {	//take smaller
				temp.takeSmaller(true);
				temp.takeLarger(false);
				
				if (costS + currCost <= capacity && bestVal < temp.getOptimal(1) + currVal && temp.getSmall().getName() != null) {
					currVal += (valS);
					currCost += (costS);
					currentNode = temp.getSelf();
				}
				
				tree.put(temp.getSelf(), temp);
				tempCurr.setSmaller(temp.getSelf());
				if (tempCurr.getDepth() == doubleGlobes.size() - 1 && currentNode == tempCurr.getSelf()) {
					tree.remove(temp.getSelf());
					continue;
				}
			} else {	//remove current and go up if all children have been assigned
				if (tempCurr.smallTaken()) {
					currVal -= tempCurr.getSmall().getValue();
					currCost -= tempCurr.getSmall().getCost();
				}
				if (tempCurr.largeTaken()) {
					currVal -= tempCurr.getLarge().getValue();
					currCost -= tempCurr.getLarge().getCost();
				}
				
				currentNode = tempCurr.getParent();
				
				if (!bestPath.contains(tempCurr.getSelf())) {
					tree.remove(tempCurr.getSelf());
				}
				
				continue;	//do not bother checking for best path after this, as it would not enter if
			}
			
			if (tempCurr.getDepth() == doubleGlobes.size() - 1) {	//when at a leaf, check for best path
				saveBestPath();
				
				currentNode = temp.getParent();	//go up 1
				
				//remove from sums if needed
				if (temp.smallTaken()) {
					currVal -= temp.getSmall().getValue();
					currCost -= temp.getSmall().getCost();
				}
				if (temp.largeTaken()) {
					currVal -= temp.getLarge().getValue();
					currCost -= temp.getLarge().getCost();
				}
				
				if (!bestPath.contains(tempCurr.getSelf())) {
					tree.remove(tempCurr.getSelf());
				}
			}
			
			//stop at 7 minutes
			Long sevenMinutes = new Long("420000000000");
			if (System.nanoTime() - startTime > sevenMinutes) {
				System.out.println("Error: calculation took over 7 minutes, incomplete search.");
				break;
			}
		}
		//record final time
		endTime = System.nanoTime();
	}
	
	//print optimal solution found
	public void getOptimalSack() {
		//same method of sorting alphabetically, then printing, only with DoubleTreeNodes
		List<String> best = new Vector<String>();
		
		for (int i = bestPath.size() - 1; i >= 0; i--) {
			DoubleTreeNode temp = tree.get(bestPath.elementAt(i));
			if (temp.largeTaken())
				best.add(temp.getLarge().getName());
			if (temp.smallTaken())
				best.add(temp.getSmall().getName());
		}
		
		Collections.sort(best);
		
		System.out.println("Value: " + bestVal);
		System.out.println("Cost: " + bestCost);
		System.out.print("|");
		for (String g : best) {
			System.out.print(" " + g + " |");
		}
		
		System.out.println("\nSolution Time: " + getFormattedTime(endTime - startTime));
	}
	
	//same formatting time function
	private String getFormattedTime(Long total) {
		Long oneMinute = new Long("60000000000");
		String time = "";
		long seconds = 0;	//the way I find seconds is totally unnecessary, but I was finding minutes first and this works, so I am leaving it for now
		if (total.compareTo(oneMinute) >= 0) {	//nine 0's before
			seconds = total / oneMinute * 60;
			total %= oneMinute;
		}
		
		if (total.compareTo((long) 1000000000) >= 0) {
			seconds += total / 1000000000;
			total %= 1000000000;
		}
		
		String totalS = total + "";
		
		time += (seconds + ".");
		
		for (int i = 0; i < 9 - totalS.length(); i++) {
			time += "0";
		}
		
		time += (total + " seconds");
		
		return time;
	}
}

public class Thief {
	
	sack s;	//for bounds
	TreeManager tm;	//for brute force
	SmartManager sm;	//for initial optimizations
	DoubleTreeManager dm;	//for creative optimization attempt
	Vector<globe> list;	//list of all globes
	
	public Thief(String filename) {
		list = new Vector<>();
		try {
			String currentDir = new File("").getAbsolutePath();
			BufferedReader br = new BufferedReader(new FileReader(currentDir + "\\" + filename));
			String line = "";	//holds each new line
			double cap = Double.parseDouble(br.readLine());	//first line is capacity
			s = new sack(cap);
			while ((line = br.readLine()) != null) {	//loop through lines
				String[] itemInfo = line.split(","); // name,cost,value
				list.add(new globe(itemInfo[0], Integer.parseInt(itemInfo[2]), Integer.parseInt(itemInfo[1])));
			}
			br.close();
			tm = new TreeManager(list, cap);
			sm = new SmartManager(list, cap);
			dm = new DoubleTreeManager(list, cap);
		} catch (FileNotFoundException e) {	//file not found, oops
			e.printStackTrace();
		} catch (IOException e) {	//bad input from file
			e.printStackTrace();
		}
	}
	
	public void setBounds() {		
		s.setLowerBound(list);
		s.setUpperBound(list);
	}
	
	public void getBounds() {
		s.printBounds();
		s = null;	//save memory (less important because much smaller than a tree)
	}
	
	public void getOptimalSack() {
		tm.FindOptimalSack();
		tm.getOptimalSack();
		tm = null;	//save memory
	}
	
	public void getOptimalSackSmartly() {
		sm.FindOptimalSack();
		sm.getOptimalSack();
		sm = null;	//save memory
	}
	
	public void getOptimalSackNewly() {
		dm.FindOptimalSack();
		dm.getOptimalSack();
		dm = null;	//save memory (less important because done at the end)
	}

	public static void main(String[] args) {
		
		//used for running through all sequentially
		//String allFiles[] = {"k05", "k10", "k24", "k30", "A1", "A2", "A3", "A4", "B1", "B2", "B3", "D1", "F1", "F2", "G1", "G2", "G3"};
		
		Scanner input = new Scanner(System.in);
		
		System.out.println("Enter a filename, (\"k05\", \"k10\", \"k24\", \"k30\", \"A1\", \"A2\", \"A3\", \"A4\", \"B1\", \"B2\", \"B3\", \"D1\", \"F1\", \"F2\", \"G1\", \"G2\", \"G3\") excluding extension.");
		String file = input.next();
		
		//formatting, calculating, printing
		System.out.println("###############################################\n###############################################\n" + file + ":");
		Thief t = new Thief("Input Files/" + file + ".csv");
		
		t.setBounds();	//sets lower and upper bounds
		t.getBounds();	//print lower and upper bounds (values, costs, items)
		
		t.getOptimalSack();			//sets and prints brute force optimal sack
		t.getOptimalSackSmartly();	//sets and prints initial optimization optimal sack
			
		t.getOptimalSackNewly();	//sets and prints creative optimization optimal sack
		
		input.close();
	}
}