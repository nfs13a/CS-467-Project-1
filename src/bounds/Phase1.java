package bounds;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Vector;

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
	
	public void setLowerBound(PriorityQueue<globe> value, PriorityQueue<globe> cost, PriorityQueue<globe> ratio) {
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
	
	public void setUpperBound(PriorityQueue<globe> ratio) {
		while (upperBoundCost <= capacity && !ratio.isEmpty()) {
			String itemName = ratio.peek().getName();
			double tempVal = ratio.peek().getValue();
			double tempCost = ratio.poll().getCost();
			upperBoundItems.add(new globe(itemName, tempVal, tempCost));
			upperBoundValue += tempVal;
			upperBoundCost += tempCost;
		}
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
	int self;	//# of node in TreeManager.tree (map)
	globe data;	//actual globe
	Integer parent;	//# of parent node in TreeManager.tree
	Integer left;	//# of child not taken in tree
	Integer right;	//# of child taken in tree
	//not used yet boolean exceedsCap;	//if adding this node exceeds capacity
	boolean taken;	//if this node was taken or not (will be right child of parent)
	//not used yet boolean canBeOptimal;	//if taking all other nodes below this would exceed TreeManager.bestValue
	boolean trueLeaf;	//if this is node will never have children (is last in queue of globes)
	
	public TreeNode(int s, globe g, Integer p, boolean take) {
		self = s;
		data = g;
		parent = p;
		left = null;
		right = null;
		taken = take;
		trueLeaf = false;
	}
}

class TreeManager {
	private Map<Integer, TreeNode> tree;	//<int identifier of node, TreeNode with globe data etc.>
	private double bestVal;	//the best found value of a complete sack
	private double bestCost;	//the cost of the best complete sack
	private int newestNode;	//# of TreeNode's created (so we can create more when needed)
	private double currVal;	//the value of the current sack
	private double currCost;	//the vost of the current sack
	private int currentNode; // # of the TreeNode we are currently on in tree (different
						// from newestNode, which keeps track of the number of
						// TreeNode's made to keep our identifiers unique)
	private Vector<globe> allGlobes;	//list of all globes
	private Vector<Double> optimalRemainingValue;	//for any globe index i in allGlobes with n globes, holds the value of all globes of i+1..n
	private Vector<Integer> bestPath;	//ordered integer list of self values of optimal sack
	private Vector<Boolean> pathDirections;	//corresponds to taken values of globes in optimal sack
	private sack bag;	//sack of all globes currently being considered
	private Vector<Integer> currentGlobes;	//indexes of all globes in the current sack
	
	public TreeManager(Vector globes, double e) {
		tree = new HashMap<Integer, TreeNode>();
		tree.put(0, new TreeNode(0, null, null, false));
		bestVal = 0;
		bestCost = 0;
		newestNode = 0;
		currVal = 0;
		currCost = 0;
		currentNode = 0;
		allGlobes = new Vector<globe>(globes);
		optimalRemainingValue = new Vector<Double>();
		optimalRemainingValue.setSize(allGlobes.size() - 1);
		setOptimalRemainingValues();
		bestPath = new Vector<Integer>();
		pathDirections = new Vector<Boolean>();
		bag = new sack(e);
		currentGlobes = new Vector<Integer>();
	}
	
	private void setOptimalRemainingValues() {
		optimalRemainingValue.setElementAt(0.0, allGlobes.size() - 2);
		for (int i = allGlobes.size() - 3; i >= 0; i--) {
			optimalRemainingValue.set(i, allGlobes.get(i + 2).getValue() + optimalRemainingValue.get(i + 1));
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
	
	private boolean checkNodes() {
		return false;
	}
	
	private void saveBestPath () {
		
	}
	
	public void FindOptimalSack() {
		
	}
}

public class Phase1 {

	public static void main(String[] args) {
		sack s = null;
		Vector<globe> list = new Vector<>();
		int globeCount = 0;
		PriorityQueue<globe> value = new PriorityQueue<globe>(1, new PQsortValue());
		PriorityQueue<globe> cost = new PriorityQueue<globe>(1, new PQsortCost());
		PriorityQueue<globe> ratio = new PriorityQueue<globe>(1, new PQsortRatio());
		PriorityQueue<globe> ratio2 = new PriorityQueue<globe>(1, new PQsortRatio());

		try {
			String currentDir = new File("").getAbsolutePath();
			BufferedReader br = new BufferedReader(new FileReader(currentDir + "\\k05.csv"));
			String line = "";
			s = new sack(Integer.parseInt(br.readLine()));
			while ((line = br.readLine()) != null) {
				String[] itemInfo = line.split(","); // name,cost,value
				list.add(new globe(itemInfo[0], Integer.parseInt(itemInfo[2]), Integer.parseInt(itemInfo[1])));
				value.add(new globe(itemInfo[0], Integer.parseInt(itemInfo[2]), Integer.parseInt(itemInfo[1])));
				cost.add(new globe(itemInfo[0], Integer.parseInt(itemInfo[2]), Integer.parseInt(itemInfo[1])));
				ratio.add(new globe(itemInfo[0], Integer.parseInt(itemInfo[2]), Integer.parseInt(itemInfo[1])));
				ratio2.add(new globe(itemInfo[0], Integer.parseInt(itemInfo[2]), Integer.parseInt(itemInfo[1])));
				globeCount++;
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		s.setLowerBound(value, cost, ratio);
		s.setUpperBound(ratio2);
		s.printBounds();
		
		
		TreeManager tm = new TreeManager(list, s.getCapacity());
		tm.printOptimalRemain();
	}
}