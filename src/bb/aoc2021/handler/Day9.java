package bb.aoc2021.handler;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import bb.aoc2021.InputHandler;

public class Day9 implements InputHandler {
	static private Logger logger = Logger.getLogger(Day9.class.getName());
	
	// Could think of a simpler data structure for what we need to store, but this is fine for now
	List<int[]> map; // Don't know the size in rows at the start
	int cIndex = 0;
	
	@Override
	public void handleInput(String line) {
		int len = line.length();
		if (map == null) {
			map = new ArrayList<int[]>();
		}
		int[] row = new int[len];
		map.add(row);
		
		try {
			for (int i=0; i<len; ++i) {
				row[i] = Integer.parseInt(String.valueOf(line.charAt(i)));  // ugly, but its Sunday and we're behind =)
			}
		} catch (NumberFormatException ex) {
			logger.error(ex.toString(), ex);
		}
	}
	
	protected int getCell(int x, int y) {
		if (map.size() > y) {
			int[] row = map.get(y);
			if (row.length > x) {
				return row[x];
			}
		}
		logger.error("Invalid cell: "+x+","+y+": size is "+map.size()+" rows "+map.get(0).length+" cols");
		return -1;
	}
	
	// Is this value lower than the value in the x/y cell?
	protected boolean isLowerThan(int val, int x, int y) {
		int oCell = getCell(x, y);
		if (oCell > val) { // Do equal values count? Initial reading of rules says no, low point must be lower
			return true;
		}	
		return false;
	}
	
	// Probably a cleverer algorithm, but this will do
	//  Simply check each direction for a lower or equal cell
	protected boolean isLow(int x, int y) {
		int val = getCell(x,y);
		// up
		int y1 = y - 1;
		if (y1 > -1 && !isLowerThan(val, x, y1)) {
			return false;
		}
		// down
		y1 = y + 1;
		if (y1 < map.size() && !isLowerThan(val, x, y1)) {
			return false;
		}
	
		// left
		y1 = y;
		int x1 = x - 1;
		if (x1 > -1 && !isLowerThan(val, x1, y)) {
			return false;
		}
		
		// right
		x1 = x + 1;
		int[] row = map.get(y);
		if (x1 < row.length && !isLowerThan(val, x1, y)) {
			return false;
		}
		
		return true;
	}

	protected int getRisk() {
		int risk = 0;
		for (int y=0; y<map.size(); ++y) {
			int[] row = map.get(y); // allow for a non uniform grid...
			for (int x=0; x<row.length; ++x) {
				if (isLow(x,y)) {
					int val = getCell(x,y);
					logger.info("Low Point: "+x+","+y+": "+val);
					risk += (val + 1);
				}
			}
		}
		return risk;
	}
	
	@Override
	public void output() {
		logger.info("Risk: "+getRisk());
	}

}
