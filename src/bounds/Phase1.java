package bounds;

import java.util.Comparator;

class globe {
	
	private double value;
	private double cost;
	
	public globe(double v, double c) {
		value = v;
		cost = c;
	}
	
	double getValue() {
		return value;
	}
	
	double getCost() {
		return cost;
	}
	
	double getRatio() {
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
		System.out.println("Hello World!");
	}
}