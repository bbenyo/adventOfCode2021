package bb.aoc2021.handler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import bb.aoc2021.InputHandler;
import bb.aoc2021.Location;

public class Day13 implements InputHandler {
	
	static private Logger logger = Logger.getLogger(Day13.class.getName());

	Set<Location> dots = new HashSet<Location>();
	int maxX = 0;
	int maxY = 0;
	
	class Fold {
		boolean x; // x = true, y = false
		int val;
	}

	List<Fold> folds = new ArrayList<Fold>();

	@Override
	public void handleInput(String line) {
		if (line.startsWith("fold")) {
			parseFold(line);
		} else if (line.trim().isEmpty()) {
			// No-op, skip blank line
		} else {
			String[] lSplit = line.split(",");
			if (lSplit.length != 2) {
				logger.error("Unable to parse line: "+line);
			}
			
			try {
				String xVal = lSplit[0];
				String yVal = lSplit[1];

				int x = Integer.parseInt(xVal);
				int y = Integer.parseInt(yVal);
				
				if (maxX < x) {
					maxX = x;
				}
				
				if (maxY < y) {
					maxY = y;
				}
				
				Location l1 = new Location(x,y);
				dots.add(l1);
			} catch (NumberFormatException ex) {
				logger.error(ex.toString(), ex);
			}
		}
	}

	protected void parseFold(String line) {
		String[] lSplit = line.split("=");
		try {
			String val = lSplit[1];
			int v = Integer.parseInt(val);
			
			Fold f1 = new Fold();
			f1.val = v;
			if (lSplit[0].endsWith("x")) {
				f1.x = true;
			} else {
				f1.x = false;
			}
			folds.add(f1);
		} catch (NumberFormatException ex) {
			logger.error(ex.toString(), ex);
		}
	}

	protected void drawGrid() {
		boolean[][] grid = new boolean[maxX+1][maxY+1];
		for (Location l1 : dots) {
			grid[l1.getX()][l1.getY()] = true;
		}
		
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<=maxY; ++i) {
			for (int j=0; j<=maxX; ++j) {
				if (grid[j][i]) {
					sb.append("#");
				} else {
					sb.append(".");
				}
			}
			sb.append(System.lineSeparator());
		}
		
		logger.info("Grid: "+System.lineSeparator()+sb.toString());
	}
	
	protected void fold(Fold f1) {
		if (f1.x) {
			foldLeft(f1);
		} else {
			foldUp(f1);
		}
	}
	
	protected void foldLeft(Fold f1) {
		// Fold Left at f1.val
		// Everything to the right of f1.val gets reflected to the left
		
		// We store dots as a list of locations, not as a grid
		//  Which should work better for sparse grids. So we have to check each location to see if we reflect it
		
		Set<Location> newDots = new HashSet<>();
		for (Location d : dots) {
			if (d.getX() > f1.val) {
				// Dot is on the right side of the fold, so we fold it left
				int newX = f1.val - (d.getX() - f1.val); 
				d.setX(newX);
				newDots.add(d);
			} else {
				// Dot is on the left already, keep it
				newDots.add(d);
			}
		}

		dots.clear();
		dots.addAll(newDots);
		recomputeGridSize();
	}
	
	protected void foldUp(Fold f1) {
		// Fold Up at f1.val
		// Everything to below f1.val gets reflected up
		
		// We store dots as a list of locations, not as a grid
		//  Which should work better for sparse grids. So we have to check each location to see if we reflect it
		
		Set<Location> newDots = new HashSet<>();
		for (Location d : dots) {
			if (d.getY() > f1.val) {
				// Dot is on the bottom side of the fold, so we fold it up
				int newY = f1.val - (d.getY() - f1.val); 
				d.setY(newY);
				newDots.add(d);
			} else {
				// Dot is on the top already, keep it
				newDots.add(d);
			}
		}

		dots.clear();
		dots.addAll(newDots);
		recomputeGridSize();
	}
	
	protected void recomputeGridSize() {
		maxX = 0;
		maxY = 0;
		for (Location d : dots) {
			if (d.getX() > maxX) {
				maxX = d.getX();
			}
			if (d.getY() > maxY) {
				maxY = d.getY();
			}
		}
	}
	
	
	@Override
	public void output() {
		drawGrid();

		logger.info("Fold: "+folds.get(0));
		this.fold(folds.get(0));
		
		drawGrid();
		
		logger.info("Dots visible: "+dots.size());
	}

}
