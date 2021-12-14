package bb.aoc2021.handler;

import org.apache.log4j.Logger;

import bb.aoc2021.InputHandler;

public class Day11 implements InputHandler {
	static private Logger logger = Logger.getLogger(Day11.class.getName());
	
	int[][] grid = new int[10][10];
	int curIndex = 0;
	int flashes = 0;
	
	@Override
	public void handleInput(String line) {
		if (line.trim().length() != 10) {
			logger.error("Input line is not length 10: "+line);
			return;
		}
		for (int i=0; i<10; ++i) {
			char val = line.charAt(i);
			try {
				int octopusE = Integer.parseInt(String.valueOf(val));
				grid[curIndex][i] = octopusE;
			} catch (NumberFormatException ex) {
				logger.error("Improper value at index "+i+" of "+line);
			}
		}
		curIndex++;
	}
	
	protected void simulate(int steps) {
		for (int i=0; i<steps; ++i) {
			simulateNextStep();
			logger.info("After "+i+" steps, flashes: "+flashes);
		}		
		printGrid(steps);
	}
	
	protected void simulateNextStep() {
		// First, the energy level of each octopus increases by 1.
		for (int i=0; i<10; ++i) {
			for (int j=0; j<10; ++j) {
				grid[i][j]++;
			}
		}
		/**
		 * Then, any octopus with an energy level greater than 9 flashes. 
		 * This increases the energy level of all adjacent octopuses by 1, 
		 * including octopuses that are diagonally adjacent. 
		 * If this causes an octopus to have an energy level greater than 9, 
		 * it also flashes. This process continues as long as new octopuses 
		 * keep having their energy level increased beyond 9. 
		 * (An octopus can only flash at most once per step.)
		 */
		
		boolean keepChecking = true;
		int giveUp = 0;
		
		while (keepChecking && giveUp < 1000) {
			keepChecking = false;
			giveUp++;
			for (int i=0; i<10; ++i) {
				for (int j=0; j<10; ++j) {
					if (grid[i][j] > 9) {
						flash(i, j);
						// If someone flashes, we'll go back and check everyone again to see if anyone else flashes
						keepChecking = true;
					}
				}
			}
		}
		
		/**
		 * Finally, any octopus that flashed during this step has its energy level set to 0, 
		 * as it used all of its energy to flash.
		 */
		for (int i=0; i<10; ++i) {
			for (int j=0; j<10; ++j) {
				if (grid[i][j] == -1) {
					grid[i][j] = 0;
				}
			}
		}
	}
	
	protected void flash(int i, int j) {
		grid[i][j] = -1; // -1 means we're flashing now
		flashes++;
		for (int i1 = i - 1; i1 <= i + 1; ++i1) {
			for (int j1 = j - 1; j1 <= j + 1; ++j1) {
				if (i1 > -1 && i1 < 10 && j1 > -1 && j1 < 10) {
					// If we're not flashing, add to our lvl
					if (grid[i1][j1] > -1) {
						grid[i1][j1] ++;
					}
				}
			}
		}
	}
	
	protected void printGrid(int step) {
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<10; ++i) {
			for (int j=0; j<10; ++j) {
				sb.append(grid[i][j]);
			}
			sb.append(System.lineSeparator());
		}
		logger.info("Grid at "+step+System.lineSeparator()+sb.toString());
	}

	@Override
	public void output() {
		simulate(100);
		logger.info("Flashes: "+flashes);
	}

}
