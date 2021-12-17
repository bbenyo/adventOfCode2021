package bb.aoc2021.handler;

import bb.aoc2021.Location;

public class Day15b extends Day15 {
	
	// Assume a even grid where each row is the same size
	protected int getGridSizeX() {
		return super.getGridSizeX() * 5;
	}
	
	protected int getGridSizeY() {
		return super.getGridSizeY() * 5;
	}
		
	// All we need to do is compute risk whenever we need it
	//  Find the large square this location is part of, then find the corresponding position in the original grid
	//  Then increment risk by number of large squares to the right and down from the top left
	//    And wrap around from 9 to 1.
	@Override
	protected int getRisk(Location cur) {
		int boxX = (int)(cur.getX() / super.getGridSizeX());
		int boxY = (int)(cur.getY() / super.getGridSizeY());
		
		int baseX = cur.getX() % super.getGridSizeX();
		int baseY = cur.getY() % super.getGridSizeY();
		
		int baseRisk = grid.get(baseX)[baseY];
		int risk = (baseRisk + boxX + boxY) % 9;
		// 9 wraps to 1, not 8 to 0. 
		if (risk == 0) {
			risk = 9;
		}
		return risk;		
	}
	
}
