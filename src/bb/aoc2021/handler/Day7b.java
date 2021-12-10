package bb.aoc2021.handler;

import org.apache.log4j.Logger;

import bb.aoc2021.InputHandler;
import bb.aoc2021.Utilities;

public class Day7b extends Day7 {
	static private Logger logger = Logger.getLogger(Day7b.class.getName());
	
	@Override
	protected int computeFuelUsage(int median) {
		int fuelUsed = 0;
		for (int i=0; i<crabs.length; ++i) {
			int distance = Math.abs(crabs[i] - median);
			int fuel = Math.round((distance / 2.0f) * (1 + distance));
			fuelUsed += fuel;
		}
		return fuelUsed;
	}
	
	protected int gradientDescent(int start) {
		int minFuel = computeFuelUsage(start);
		int current = start + 1; // Could do a longer step size, and more of a binary search, but this is fine for here
		int optimum = start;
		int newFuel = computeFuelUsage(current);
		if (newFuel < minFuel) {
			// optimum is in this direction
			while (newFuel < minFuel) {
				minFuel = newFuel;
				optimum = current;
				current++;			
				newFuel = computeFuelUsage(current);
			}
			return optimum;
		} else {
			// Try the other direction, or we're at the optimum already
			current = start - 1;
			newFuel = computeFuelUsage(current);
			while (newFuel < minFuel) {
				minFuel = newFuel;
				optimum = current;
				current++;
				newFuel = computeFuelUsage(current);
			}
			return optimum;
		}
	}

	@Override
	public void output() {
		int median = Utilities.median(crabs);
		// Median may not be the best anymore, do a quick gradient descent search
		int best = gradientDescent(median);
		int fuel = computeFuelUsage(best);
		logger.info("Optimum range: "+best+" fuelUsage: "+fuel);
	}

}
