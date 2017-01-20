package bounds;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
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

public class Phase1 {

	public static void main(String[] args) {
		sack s = null;
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
				value.add(new globe(itemInfo[0], Integer.parseInt(itemInfo[2]), Integer.parseInt(itemInfo[1])));
				cost.add(new globe(itemInfo[0], Integer.parseInt(itemInfo[2]), Integer.parseInt(itemInfo[1])));
				ratio.add(new globe(itemInfo[0], Integer.parseInt(itemInfo[2]), Integer.parseInt(itemInfo[1])));
				ratio2.add(new globe(itemInfo[0], Integer.parseInt(itemInfo[2]), Integer.parseInt(itemInfo[1])));
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
	}
}