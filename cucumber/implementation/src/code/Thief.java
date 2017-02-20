package code;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import com.sun.corba.se.impl.orbutil.graph.Node;

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

class PQsortValue implements Comparator<globe> {
	public int compare(globe one, globe two) {
		if (one.getValue() > two.getValue()) {
			return -1;
		} else if (one.getValue() < two.getValue()) {
			return 1;
		}
		return 0;
	}
}

class PQsortCost implements Comparator<globe> {
	public int compare(globe one, globe two) {
		if (one.getCost() > two.getCost()) {
			return 1;
		} else if (one.getCost() < two.getCost()) {
			return -1;
		}
		return 0;
	}
}

class PQsortRatio implements Comparator<globe> {
	public int compare(globe one, globe two) {
		if (one.getRatio() > two.getRatio()) {
			return -1;
		} else if (one.getRatio() < two.getRatio()) {
			return 1;
		}
		return 0;
	}
}

class sack {
	
	private double lowerBoundValue;
	private double lowerBoundCost;
	private double upperBoundValue;
	private double upperBoundCost;
	private Vector<globe> lowerBoundItems;
	private Vector<globe> upperBoundItems;
	private double capacity;
	
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
	
	public void setLowerBound(Vector<globe> list) {
		PriorityQueue<globe> value = new PriorityQueue<globe>(1, new PQsortValue());
		PriorityQueue<globe> cost = new PriorityQueue<globe>(1, new PQsortCost());
		PriorityQueue<globe> ratio = new PriorityQueue<globe>(1, new PQsortRatio());
		
		for (globe g : list) {
			value.add(g);
			cost.add(g);
			ratio.add(g);
		}
		
		do {
			String itemName = value.peek().getName();
			double tempVal = value.peek().getValue();
			double tempRatio = value.peek().getRatio();
			double tempCost = value.poll().getCost();
			if (lowerBoundCost + tempCost <= capacity) {
				lowerBoundValue += tempVal;
				lowerBoundCost += tempCost;
				lowerBoundItems.add(new globe(itemName, tempVal, tempCost));
			}
		} while (!value.isEmpty());
		
		double totalFromCost = 0;
		double capacityFromCost = 0;
		Vector<globe> itemsTakenByCost = new Vector<>();
		while (totalFromCost + cost.peek().getCost() <= capacity) {
			String itemName = cost.peek().getName();
			double tempVal = cost.peek().getValue();
			double tempRatio = cost.peek().getRatio();
			double tempCost = cost.poll().getCost();
			itemsTakenByCost.add(new globe(itemName, tempVal, tempCost));
			totalFromCost += tempVal;
			capacityFromCost += tempCost;
		}
		
		if (totalFromCost > lowerBoundValue) {
			lowerBoundValue = totalFromCost;
			lowerBoundCost = capacityFromCost;
			lowerBoundItems = itemsTakenByCost;
		}
		
		double totalFromRatio = 0;
		double capacityFromRatio = 0;
		Vector<globe> itemsTakenByRatio = new Vector<>();
		while (totalFromRatio + ratio.peek().getCost() <= capacity) {
			String itemName = ratio.peek().getName();
			double tempVal = ratio.peek().getValue();
			double tempCost = ratio.poll().getCost();
			itemsTakenByRatio.add(new globe(itemName, tempVal, tempCost));
			totalFromRatio += tempVal;
			capacityFromRatio += tempCost;
		}
		
		if (totalFromRatio > lowerBoundValue) {
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
	
	public void setUpperBound(Vector<globe> list) {
		PriorityQueue<globe> ratio = new PriorityQueue<globe>(1, new PQsortRatio());
		
		for (globe g : list) {
			ratio.add(g);
		}
		
		
		while (upperBoundCost <= capacity && !ratio.isEmpty()) {
			String itemName = ratio.peek().getName();
			double tempVal = ratio.peek().getValue();
			double tempCost = ratio.poll().getCost();
			upperBoundItems.add(new globe(itemName, tempVal, tempCost));
			upperBoundValue += tempVal;
			upperBoundCost += tempCost;
		}
	}
	
	public double getUpperBoundValue() {
		return upperBoundValue;
	}
	
	public double getUpperBoundCost() {
		return upperBoundCost;
	}
	
	public void printBounds() {
		System.out.println("Capacity: " + capacity);
		
		System.out.println("\nLower bound: " + lowerBoundValue);
		System.out.println("Cost: " + lowerBoundCost);
		for (globe g : lowerBoundItems) {
			System.out.println(g.getName());
		}
		
		System.out.println("\nUpper bound: " + upperBoundValue);
		System.out.println("Cost: " + lowerBoundValue);
		for (globe g : upperBoundItems) {
			System.out.println(g.getName());
		}
	}
}

class TreeNode {
	protected int self;	//# of node in TreeManager.tree (map)
	private globe data;	//actual globe
	protected Integer parent;	//# of parent node in TreeManager.tree
	private Integer left;	//# of child not taken in tree
	private Integer right;	//# of child taken in tree
	//private boolean exceedsCap;	//if adding this node exceeds capacity
	private boolean taken;	//if this node was taken or not (will be right child of parent)
	//private boolean canBeOptimal;	//if taking all other nodes below this would exceed TreeManager.bestValue
	protected boolean trueLeaf;	//if this is node will never have children (is last in queue of globes)
	protected int depth;	//how many nodes from root corresponds to placement in allGlobes Vector
	
	public TreeNode() { /*do nothing*/ }
	
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
		
		//exceedsCap = false;
		//canBeOptimal = false;
	}
	
	public int getSelf() {
		return self;
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
	//private sack bag;	//sack of all globes currently being considered
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
	
	protected boolean checkNode(TreeNode node) {
		if (node.getDepth() == allGlobes.size()) {
			node.setTrueLeaf(true);
		}
		
		if (node.getTaken())
			return node.getCost() + currCost <= bagCap && !node.isTrueLeaf();
		return !node.isTrueLeaf();
		
		//return true;
	}
	
	protected void saveBestPath() {
		if (currVal <= bestVal || currCost > bagCap) {
			if (tree.get(currentNode).getTaken()) {
				currVal -= tree.get(currentNode).getValue();
				currCost -= tree.get(currentNode).getCost();
			}
			return;
		}
		TreeNode temp = tree.get(currentNode);
		Vector<Integer> v = new Vector<>();
		while (temp.getSelf() != 0) {
			v.addElement(temp.getSelf());
			temp = tree.get(temp.getParent());
		}
		bestPath = new Vector<>(v);
		bestVal = currVal;
		bestCost = currCost;
		
		if (tree.get(currentNode).getTaken()) {
			currVal -= tree.get(currentNode).getValue();
			currCost -= tree.get(currentNode).getCost();
		}
		
		/*System.out.println("new best val: " + bestVal);
		System.out.println("new best cost: " + bestCost);*/
	}
	
	public void FindOptimalSack() {
		startTime = System.nanoTime();
		boolean cont = true;
		//while (!(currentNode == 0) || cont) {
		while (currentNode != 0 || cont || (tree.get(0).getLeft() == null)) {
			TreeNode tempCurr = tree.get(currentNode);
			/*System.out.println("current: " + tempCurr.getSelf());
			System.out.println("taken: " + tempCurr.getTaken());
			System.out.println("currVal: " + currVal);
			System.out.println("currCost: " + currCost);
			if (tempCurr.getSelf() != 0) {
				System.out.println("name: " + tempCurr.getName());
				System.out.println("parent: " + tempCurr.getParent());
			}
			System.out.println("left: " + tempCurr.getLeft());
			System.out.println("right: " + tempCurr.getRight());
			System.out.println("depth: " + tempCurr.getDepth());*/
			//System.out.println("");
			
			//there is a more efficient way to write this if..elif and method calls, but I am following my algorithm for homework efficiency's sake
			if (tempCurr.getRight() == null) {
				//create a new TreeNode to insert if needed with incremented newestNode identifier, data from allGlobes of its parent's depth (its depth - 1), the parent identifier, and the depth of parent + 1
				TreeNode temp = new TreeNode(++newestNode, allGlobes.elementAt((tempCurr.getDepth())), tempCurr.getSelf(), true, tempCurr.getDepth() + 1);
				//System.out.println("entered right");
				//if (temp.getCost() + currCost <= bagCap) {
					//System.out.println("less than cap");
					tree.put(temp.getSelf(), temp);
					tempCurr.setRight(temp.getSelf());
					currVal += temp.getValue();
					currCost += temp.getCost();
					currentNode = temp.getSelf();
					//System.out.println(tree.get(currentNode).getSelf());
				/*} else {
					tree.put(temp.getSelf(), temp);
					tempCurr.setRight(temp.getSelf());
				}*/
				
				/*System.out.println("new: " + temp.getSelf());
				System.out.println("taken: " + temp.getTaken());
				System.out.println("left: " + temp.getLeft());
				System.out.println("right: " + temp.getRight());*/
				
				/*temp.setTaken(true);
				cont = true;
				cont = checkNode(temp);*/
			} else if (tempCurr.getLeft() == null) {
				//System.out.println("entered left");
				TreeNode temp = new TreeNode(++newestNode, allGlobes.elementAt((tempCurr.getDepth())), tempCurr.getSelf(), false, tempCurr.getDepth() + 1);
				tree.put(temp.getSelf(), temp);
				tempCurr.setLeft(temp.getSelf());
				currentNode = temp.getSelf();
				
				/*System.out.println("new: " + temp.getSelf());
				System.out.println("taken: " + temp.getTaken());
				System.out.println("left: " + temp.getLeft());
				System.out.println("right: " + temp.getRight());*/
				
				/*temp.setTaken(false);	//unnecessary, but explicit
				cont = true;
				cont = checkNode(temp);*/
			} else {
				if (tempCurr.getTaken()) {
					/*System.out.println("current drop: " + tempCurr.getSelf());
					System.out.println("current val: " + currVal);
					System.out.println("current cost: " + currCost);
					System.out.println("sub val: " + tempCurr.getValue());
					System.out.println("sub cost: " + tempCurr.getCost());*/
					
					currVal -= tempCurr.getValue();
					currCost -= tempCurr.getCost();
				}
				if (!bestPath.contains(tempCurr.getRight())) {
					tree.remove(tempCurr.getRight());
				}
				
				if (!bestPath.contains(tempCurr.getLeft())) {
					tree.remove(tempCurr.getLeft());
				}
				
				currentNode = tempCurr.getParent();
				/*cont = false;
				currVal -= tempCurr.getValue();
				currCost -= tempCurr.getCost();
				currentNode = tempCurr.getParent();
				continue;*/
			}
			
			if (tree.get(currentNode).getDepth() == allGlobes.size()) {
				//System.out.println("re-eval");
				saveBestPath();
				
				/*for (int i : bestPath) {
					System.out.println("best path: " + i);
				}*/
				
				//go up 1 to avoid conflict
				/*if (tempCurr.getRight() == currentNode) {
					currVal -= tempCurr.getValue();
					currCost -= tempCurr.getCost();
				}*/
				
				if (!bestPath.contains(currentNode)) {
					tree.remove(currentNode);
				}
				
				currentNode = tempCurr.getSelf();
				//System.out.println("new current: " + currentNode);
			}
			
			cont = tree.get(currentNode).getLeft() == null && tree.get(currentNode) == null;
			
			//System.out.println(" ------------ \n");
		}
		
		endTime = System.nanoTime();

		//System.out.println(tree.size());
		System.out.println("\ntotal created: " + newestNode);
	}
	
	public void getOptimalSack() {
		for (int i = bestPath.size() - 1; i >= 0; i--) {
			TreeNode temp = tree.get(bestPath.elementAt(i));
			if (temp.getTaken())
				System.out.println(temp.getName());
		}
		System.out.println("Value: " + bestVal);
		System.out.println("Cost: " + bestCost);
		
//		System.out.println("Calculation time (nanoseconds): " + (endTime - startTime));
		System.out.println("solution Time : " + getFormattedTime(endTime - startTime));
	}
	
	private String getFormattedTime(Long total) {
		Long oneMinute = new Long("60000000000");
		String time = "";
		long seconds = 0;	//the way I find seconds is totally unnecessary, but I was finding minutes first and this works, so I am leaving it for now
		if (total.compareTo(oneMinute) >= 0) {	//nine 0's before
			seconds = total / oneMinute * 60;
			total %= oneMinute;
			//time += ((seconds * 60) + ":");
		}
		
		if (total.compareTo((long) 1000000000) >= 0) {
			seconds += total / 1000000000;
			total %= 1000000000;
			//time += (seconds);
		}
		
		time += (seconds + "." + total + " seconds");
		
		return time;
	}
}

class SmartManager extends TreeManager {
	
	protected Vector<Double> optimalRemainingValue;	//for any globe index i in allGlobes with n globes, holds the value of all globes of i+1..n
	private double maxVal;
	private double maxCost;
	
	public SmartManager(Vector globes, double e) {
		super(globes, e);
		
		optimalRemainingValue = new Vector<Double>();
		optimalRemainingValue.setSize(allGlobes.size());
		setOptimalRemainingValues();
		
		sack s = new sack(e);
		s.setLowerBound(globes);
		bestVal = s.getLowerBoundValue() - 1;	//sub 1 because we know we can get it but we do not know what path through the tree it is
		bestCost = s.getLowerBoundCost();
		
		maxVal = s.getUpperBoundValue();
		maxCost = s.getUpperBoundCost();
	}
	
	private void setOptimalRemainingValues() {
		optimalRemainingValue.setElementAt(allGlobes.elementAt(allGlobes.size() - 1).getValue(), allGlobes.size() - 1);
		/*System.out.println("aG size: " + allGlobes.size());
		System.out.println("oRV size: " + optimalRemainingValue.size());*/
		for (int i = allGlobes.size() - 2; i >= 0; i--) {
			optimalRemainingValue.setElementAt(allGlobes.elementAt(i).getValue() + optimalRemainingValue.get(i + 1), i);
		}
	}
	
	public void printOptimalRemain() {
		for (globe g : allGlobes) {
			System.out.println(g.getName() + ":" + g.getValue());
		}
		for (Double e : optimalRemainingValue) {
			System.out.println("optimal: " + e);
		}
	}
	
	public void FindOptimalSack() {
		startTime = System.nanoTime();
		boolean cont = true;
		while (currentNode != 0 || cont || (tree.get(0).getLeft() == null)) {
			TreeNode tempCurr = tree.get(currentNode);
			/*System.out.println("current: " + tempCurr.getSelf());
			System.out.println("taken: " + tempCurr.getTaken());
			System.out.println("currVal: " + currVal);
			System.out.println("currCost: " + currCost);
			if (tempCurr.getSelf() != 0) {
				System.out.println("name: " + tempCurr.getName());
				System.out.println("parent: " + tempCurr.getParent());
			}
			System.out.println("left: " + tempCurr.getLeft());
			System.out.println("right: " + tempCurr.getRight());
			System.out.println("depth: " + tempCurr.getDepth());*/
			//System.out.println("");
			
			//there is a more efficient way to write this if..elif and method calls, but I am following my algorithm for homework efficiency's sake
			if (tempCurr.getRight() == null) {
				//create a new TreeNode to insert if needed with incremented newestNode identifier, data from allGlobes of its parent's depth (its depth - 1), the parent identifier, and the depth of parent + 1
				TreeNode temp = new TreeNode(++newestNode, allGlobes.elementAt((tempCurr.getDepth())), tempCurr.getSelf(), true, tempCurr.getDepth() + 1);
				//System.out.println("entered right");
				if (temp.getCost() + currCost <= bagCap && bestVal < currVal + optimalRemainingValue.elementAt(temp.getDepth() - 1)) {
					//System.out.println("less than cap");
					tree.put(temp.getSelf(), temp);
					tempCurr.setRight(temp.getSelf());
					currVal += temp.getValue();
					currCost += temp.getCost();
					currentNode = temp.getSelf();
					//System.out.println(tree.get(currentNode).getSelf());
				} else {
					tree.put(temp.getSelf(), temp);
					tempCurr.setRight(temp.getSelf());
				}
				
				/*System.out.println("new: " + temp.getSelf());
				System.out.println("taken: " + temp.getTaken());
				System.out.println("left: " + temp.getLeft());
				System.out.println("right: " + temp.getRight());*/
				
				/*temp.setTaken(true);
				cont = true;
				cont = checkNode(temp);*/
			} else if (tempCurr.getLeft() == null) {
				//System.out.println("entered left");
				TreeNode temp = new TreeNode(++newestNode, allGlobes.elementAt((tempCurr.getDepth())), tempCurr.getSelf(), false, tempCurr.getDepth() + 1);
				tree.put(temp.getSelf(), temp);
				tempCurr.setLeft(temp.getSelf());
				
				if (bestVal < currVal + optimalRemainingValue.elementAt(temp.getDepth() - 1) - temp.getValue()) {
					currentNode = temp.getSelf();
				}
				
				/*System.out.println("new: " + temp.getSelf());
				System.out.println("taken: " + temp.getTaken());
				System.out.println("left: " + temp.getLeft());
				System.out.println("right: " + temp.getRight());*/
				
				/*temp.setTaken(false);	//unnecessary, but explicit
				cont = true;
				cont = checkNode(temp);*/
			} else {
				if (tempCurr.getTaken()) {
					/*System.out.println("current drop: " + tempCurr.getSelf());
					System.out.println("current val: " + currVal);
					System.out.println("current cost: " + currCost);
					System.out.println("sub val: " + tempCurr.getValue());
					System.out.println("sub cost: " + tempCurr.getCost());*/
					
					currVal -= tempCurr.getValue();
					currCost -= tempCurr.getCost();
				}
				if (!bestPath.contains(tempCurr.getRight())) {
					tree.remove(tempCurr.getRight());
				}
				
				if (!bestPath.contains(tempCurr.getLeft())) {
					tree.remove(tempCurr.getLeft());
				}
				
				currentNode = tempCurr.getParent();
				/*cont = false;
				currVal -= tempCurr.getValue();
				currCost -= tempCurr.getCost();
				currentNode = tempCurr.getParent();
				continue;*/
			}
			
			if (tree.get(currentNode).getDepth() == allGlobes.size()) {
				//System.out.println("re-eval");
				saveBestPath();
				
				/*for (int i : bestPath) {
					System.out.println("best path: " + i);
				}*/
				
				//go up 1 to avoid conflict
				/*if (tempCurr.getRight() == currentNode) {
					currVal -= tempCurr.getValue();
					currCost -= tempCurr.getCost();
				}*/
				
				if (!bestPath.contains(currentNode)) {
					tree.remove(currentNode);
				}
				
				currentNode = tempCurr.getSelf();
				//System.out.println("new current: " + currentNode);
			}
			
			cont = tree.get(currentNode).getLeft() == null && tree.get(currentNode) == null;
			
			//System.out.println(" ------------ \n");
		}
		
		endTime = System.nanoTime();
		
		//System.out.println(tree.size());
		System.out.println("\ntotal created: " + newestNode);
	}
}

class DoubleTreeNode {
	
	int self;
	Integer parent;
	int depth;
	boolean smallTaken, largeTaken;
	
	TreeNode small, large;
	double[] optimalBelow;	//total value if all proceeding TreeNodes are taken and (O: neither, 1: smaller, 2: larger, 3: both)
	
	public DoubleTreeNode(int s, globe smallG, globe largeG, Integer p, boolean takeS, boolean takeL, int d) {
		
		self = s;
		//small = smallG;
		//large = largeG;
		parent = p;
		smallTaken = takeS;
		largeTaken = takeL;
		depth = d;
		
		small = new TreeNode(s, smallG, p, takeS, d);
		large = new TreeNode(s, largeG, p, takeL, d);
		
		optimalBelow = new double[4];
		for (int i = 0; i < 4; i++) optimalBelow[i] = 0;
		
	}
	
	
	public TreeNode getSmaller() {
		return small;
	}
	
	public TreeNode getLarger() {
		return large;
	}
	
	public void setOptimal(double val, int index) {
		optimalBelow[index] = val;
	}
	
	public double getOptimal(int index) {
		return optimalBelow[index];
	}
}

class DoubleTreeManager {
	Map<Integer, DoubleTreeNode> tree;
	double capacity;
	Vector<globe> allGlobes;
	Vector<DoubleTreeNode> doubleGlobes;
	
	public DoubleTreeManager(Vector globes, double cap) {
		tree = new HashMap<Integer, DoubleTreeNode>();
		
		allGlobes = new Vector<globe>(globes);
		
		PriorityQueue<globe> ratio = new PriorityQueue<globe>(new PQsortRatio());
		for (globe g : allGlobes) {
			ratio.add(g);
		}
		
		createDoubleNodes(ratio);
		
		setOptimalRemainingValues();
		
		capacity = cap;
	}
	
	private void createDoubleNodes(PriorityQueue<globe> ratio) {
		doubleGlobes = new Vector<DoubleTreeNode>();
		int n = 0;
		System.out.println(ratio.size());
		while (ratio.size() > 1) {
			//System.out.println(n);
			globe large = ratio.poll();
			globe small = ratio.poll();
			System.out.println(large.getName());
			System.out.println(small.getName());
			doubleGlobes.addElement(new DoubleTreeNode(n, small, large, n - 1, true, true, n + 1));
			n++;
		}
		
		if (ratio.size() == 1) {
			doubleGlobes.addElement(new DoubleTreeNode(n, new globe(null, 0, 0), ratio.poll(), n - 1, true, true, n + 1));
		}
	}
	
	public void printNodes() {
		for (DoubleTreeNode d : doubleGlobes) {
			TreeNode s = d.getSmaller();
			TreeNode l = d.getLarger();
			System.out.println(s.getName() + " with " + s.getRatio() + "\tand\t" + l.getName() + " with " + l.getRatio());
		}
	}
	
	private void setOptimalRemainingValues() {
		/*for (int i = 0; i < 4; i++) {
			doubleGlobes.elementAt(doubleGlobes.size() - 1).setOptimal(0, i);
		}*/
		
		//initialize leaf nodes
		double sm = doubleGlobes.elementAt(doubleGlobes.size() - 1).getSmaller().getValue();
		double lg = doubleGlobes.elementAt(doubleGlobes.size() - 1).getLarger().getValue();
		
		doubleGlobes.elementAt(doubleGlobes.size() - 1).setOptimal(0, 0);
		doubleGlobes.elementAt(doubleGlobes.size() - 1).setOptimal(sm, 1);
		doubleGlobes.elementAt(doubleGlobes.size() - 1).setOptimal(lg, 2);
		doubleGlobes.elementAt(doubleGlobes.size() - 1).setOptimal(lg + sm, 3);
		
		//start at parents of leaves, work up
		for (int i = doubleGlobes.size() - 2; i >= 0; i--) {
			
			double prevTotal = 0;
			
			if (doubleGlobes.elementAt(i + 1).getSmaller().getName() != null) {
				prevTotal += doubleGlobes.elementAt(i + 1).getOptimal(3);
			} else {
				prevTotal += doubleGlobes.elementAt(i + 1).getOptimal(2);
			}
			
			double l = doubleGlobes.elementAt(i).getLarger().getValue();
			double s = doubleGlobes.elementAt(i).getSmaller().getValue();
			
			
			doubleGlobes.elementAt(i).setOptimal(prevTotal, 0);
			
			doubleGlobes.elementAt(i).setOptimal(prevTotal + s, 1);
		
			doubleGlobes.elementAt(i).setOptimal(prevTotal + l, 2);
		
			doubleGlobes.elementAt(i).setOptimal(prevTotal + l + s, 3);
		}
	}
	
	public void printOptimalRemain() {
		for (DoubleTreeNode d : doubleGlobes) {
			for (int i = 0; i < 4; i++) {
				System.out.print(d.getOptimal(i) + " ");
			}
			System.out.println("");
		}
	}
}

public class Thief {
	
	sack s;
	TreeManager tm;
	SmartManager sm;
	DoubleTreeManager dm;
	Vector<globe> list;
	
	public Thief(String filename) {
		list = new Vector<>();
		try {
			String currentDir = new File("").getAbsolutePath();
			BufferedReader br = new BufferedReader(new FileReader(currentDir + "\\" + filename));
			String line = "";
			double cap = Double.parseDouble(br.readLine());
			s = new sack(cap);
			while ((line = br.readLine()) != null) {
				String[] itemInfo = line.split(","); // name,cost,value
				System.out.println(itemInfo[0] + ":v:" + itemInfo[2] + ":c:" + itemInfo[1]);
				list.add(new globe(itemInfo[0], Integer.parseInt(itemInfo[2]), Integer.parseInt(itemInfo[1])));
			}
			br.close();
			tm = new TreeManager(list, cap);
			sm = new SmartManager(list, cap);
			dm = new DoubleTreeManager(list, cap);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setBounds() {		
		s.setLowerBound(list);
		s.setUpperBound(list);
	}
	
	public void getBounds() {
		s.printBounds();
	}
	
	public void getOptimalSack() {
		tm.FindOptimalSack();
		tm.getOptimalSack();
	}
	
	public void getOptimalSackSmartly() {
		sm.FindOptimalSack();
		sm.getOptimalSack();
	}
	
	//for debugging
	public void printDoubleNodes() {
		dm.printNodes();
		dm.printOptimalRemain();
	}

	public static void main(String[] args) {
		
		Thief t = new Thief("k05.csv");
		
		t.setBounds();
		t.getBounds();
		
		t.getOptimalSack();
		t.getOptimalSackSmartly();
		
		t.printDoubleNodes();
		
		//TreeManager tm = new TreeManager(list, s.getCapacity());
		//tm.printOptimalRemain();
	}
}