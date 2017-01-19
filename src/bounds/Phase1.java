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

public class Phase1 {

	public static void main(String[] args) {
		int capacity = 0;
		PriorityQueue<globe> value = new PriorityQueue<globe>(1, new PQsortValue());
		PriorityQueue<globe> cost = new PriorityQueue<globe>(1, new PQsortCost());
		PriorityQueue<globe> ratio = new PriorityQueue<globe>(1, new PQsortRatio());
		
		try {
			// sets up parsing data
			BufferedReader br = null;
			String line = "";
			
			//gets current directory to know where to find csv's
			String currentDir = new File("").getAbsolutePath();
			
			try {
				br = new BufferedReader(new FileReader(currentDir + "\\k05.csv"));
				capacity = Integer.parseInt(br.readLine());
				System.out.println("capacity: " + capacity);
				while ((line = br.readLine()) != null) {
					String[] itemInfo = line.split(",");	//name,cost,value
					System.out.println(itemInfo[0] + ":" + itemInfo[1] + ":" + itemInfo[2]);
					value.add(new globe(itemInfo[0], Integer.parseInt(itemInfo[2]), Integer.parseInt(itemInfo[1])));
					cost.add(new globe(itemInfo[0], Integer.parseInt(itemInfo[2]), Integer.parseInt(itemInfo[1])));
					ratio.add(new globe(itemInfo[0], Integer.parseInt(itemInfo[2]), Integer.parseInt(itemInfo[1])));
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			br.close();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		}
		
		double totalFromValue = 0;
		double capacityFromValue = 0;
		Vector<globe> itemsTakenByValue = new Vector<>();
		do {
			String itemName = value.peek().getName();
			double tempVal = value.peek().getValue();
			double tempRatio = value.peek().getRatio();
			double tempCost = value.poll().getCost();
			System.out.println(itemName + ":" + tempVal + ":" + tempCost + ":" + tempRatio);
			if (capacityFromValue + tempCost <= capacity) {
				totalFromValue += tempVal;
				capacityFromValue += tempCost;
				itemsTakenByValue.add(new globe(itemName, tempVal, tempCost));
				System.out.println("taken");
			}
		} while (!value.isEmpty());
		
		
		System.out.println(totalFromValue);
		System.out.println(capacityFromValue);
		for (globe g : itemsTakenByValue) {
			System.out.println(g.getName());
		}
		
		double totalFromCost = 0;
		double capacityFromCost = 0;
		Vector<globe> itemsTakenByCost = new Vector<>();
		while (totalFromCost + cost.peek().getCost() <= capacity) {
			itemsTakenByCost.add(new globe(cost.peek().getName(), cost.peek().getValue(), cost.peek().getCost()));
			totalFromCost += cost.peek().getValue();
			capacityFromCost += cost.poll().getCost();
		}
		
		System.out.println(totalFromCost);
		System.out.println(capacityFromCost);
		for (globe g : itemsTakenByCost) {
			System.out.println(g.getName());
		}
		
		double upperBound = 0;
		double totalFromRatio = 0;
		double capacityFromRatio = 0;
		Vector<globe> itemsTakenByRatio = new Vector<>();
		while (totalFromRatio + ratio.peek().getCost() <= capacity) {
			itemsTakenByRatio.add(new globe(ratio.peek().getName(), ratio.peek().getValue(), ratio.peek().getCost()));
			totalFromRatio += ratio.peek().getValue();
			capacityFromRatio += ratio.poll().getCost();
		}
		upperBound = totalFromRatio + ratio.poll().getValue();
		
		System.out.println(totalFromRatio);
		System.out.println(capacityFromRatio);
		for (globe g : itemsTakenByRatio) {
			System.out.println(g.getName());
		}
		
		
		double lowerBound;
		if (totalFromValue >= totalFromCost) {
			if (totalFromValue >= totalFromRatio) {
				lowerBound = totalFromValue;
			} else {
				lowerBound = totalFromRatio;
			}
		} else {
			if (totalFromCost >= totalFromRatio) {
				lowerBound = totalFromCost;
			} else {
				lowerBound = totalFromRatio;
			}
		}
		System.out.println("Lower bound: " + lowerBound);
		System.out.println("Upper bound: " + upperBound);
	}
}