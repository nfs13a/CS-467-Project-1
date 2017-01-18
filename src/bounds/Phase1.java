package bounds;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.PriorityQueue;

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
			return 1;
		} else if (one.getValue() < two.getValue()) {
			return -1;
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
			return 1;
		} else if (one.getRatio() < two.getRatio()) {
			return -1;
		}
		return 0;
	}
}

public class Phase1 {

	public static void main(String[] args) {
		int capacity;
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
		
		
	}
}