package code;

import java.util.Random;

public class GenerateCentiSack {

	public static void main(String[] args) {
		int sum = 0;
		for (int i = 0; i < 100; i++) { 
			int val = (int) (Math.random() * 200 + 1);
			int cost = (int) (Math.random() * 200 + 1);
			sum += cost;
			System.out.print("(" + val + "," + cost + "),");
		}
		System.out.println("\ncap: " + sum);
	}
}
