package bb.aoc2021.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.log4j.Logger;

public class Day9b extends Day9 {
	
	static private Logger logger = Logger.getLogger(Day9b.class.getName());
	
	protected class Location  {
		int x;
		int y;
		
		public Location(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		public String toString() {
			return x+","+y;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getEnclosingInstance().hashCode();
			result = prime * result + Objects.hash(x, y);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Location other = (Location) obj;
			if (!getEnclosingInstance().equals(other.getEnclosingInstance()))
				return false;
			return x == other.x && y == other.y;
		}

		private Day9b getEnclosingInstance() {
			return Day9b.this;
		}
		
	}
	
	Map<Location, Integer> basins = new HashMap<Location, Integer>();
	
	protected void getLowPoints() {
		for (int y=0; y<map.size(); ++y) {
			int[] row = map.get(y); // allow for a non uniform grid...
			for (int x=0; x<row.length; ++x) {
				if (isLow(x,y)) {
					basins.put(new Location(x,y), 0);
				}
			}
		}
	}
	
	protected Location findLowerPoint(Location loc) {
		// Try up
		int val = getCell(loc.x, loc.y);
		// up
		int y1 = loc.y - 1;
		if (y1 > -1) {
			int nVal = getCell(loc.x, y1);
			if (nVal < val) {
				return new Location(loc.x, y1);
			}
		}
		
		// down
		y1 = loc.y + 1;
		if (y1 < map.size()) {
			int nVal = getCell(loc.x, y1);
			if (nVal < val) {
				return new Location(loc.x, y1);
			}
		}
	
		// left
		y1 = loc.y;
		int x1 = loc.x - 1;
		if (x1 > -1) {
			int nVal = getCell(x1, loc.y);
			if (nVal < val) {
				return new Location(x1, loc.y);
			}
		}
		
		// right
		x1 = loc.x + 1;
		int[] row = map.get(loc.y);
		if (x1 < row.length) {
			int nVal = getCell(x1, loc.y);
			if (nVal < val) {
				return new Location(x1, loc.y);
			}
		}
		
		// At a basin low point
		return null;				
	}
	
	protected void addToBasin(int x, int y) {
		// Add this cell to a basin
		int val = getCell(x,y);
		if (val == 9) {
			return; // 9's aren't part of any basin
		}
		// Find a lower location
		int x1 = x;
		int y1 = y;
		Location curLoc = new Location(x1,y1);
		
		// Avoid infinite loop with a giveup timeout
		int giveUp = 1000; // More than 1000 away and we messed something up
		while (curLoc != null && !basins.containsKey(curLoc) && giveUp > 0) {
			Location l2 = findLowerPoint(curLoc);
			curLoc = l2;
			giveUp--;
		}
		
		if (curLoc == null) {
			logger.error("Found another basin? "+x+","+y);
			return;
		}
		
		if (giveUp == 0) {
			logger.error("Probably were in an inifinite loop for "+x+","+y);
			return;
		}
		
		Integer basinCount = basins.get(curLoc);
		if (basinCount == null) {
			logger.error("Unable to find basin for "+x1+","+y1);
		} else {
			basins.put(curLoc, basinCount+1);
		}
	}
	
	protected void computeBasins() {
		for (int y=0; y<map.size(); ++y) {
			int[] row = map.get(y); // allow for a non uniform grid...
			for (int x=0; x<row.length; ++x) {
				addToBasin(x,y);
			}
		}		
	}
	
	protected Location getLargestBasin() {
		int maxSize = 0;
		Location biggest = null;
		for (Location basin : basins.keySet()) {
			Integer size = basins.get(basin);
			if (size > maxSize) {
				maxSize = size;
				biggest = basin; 
			}
		}
		return biggest;
	}
	
	protected int largestThreeBasinSize() {
		int size = 1;
		for (int i=0; i<3; ++i) {
			Location b1 = getLargestBasin();
			size *= basins.get(b1);
			basins.remove(b1);
		}
		return size;
	}
	
	@Override
	public void output() {
		getLowPoints();
		logger.info("Low Points: "+basins.size());
		for (Location basin : basins.keySet()) {
			logger.info("Basin: "+basin.x+","+basin.y);
		}
		computeBasins();
		logger.info("Largest 3: "+largestThreeBasinSize());
	}
	
}
