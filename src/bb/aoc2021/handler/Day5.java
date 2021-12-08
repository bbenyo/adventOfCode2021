package bb.aoc2021.handler;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import bb.aoc2021.InputHandler;

public class Day5 implements InputHandler {
	
	static private Logger logger = Logger.getLogger(Day5.class.getName());
		
	protected class Line {
		int x1;
		int x2;
		int y1;
		int y2;
		
		public Line(int x1, int y1, int x2, int y2) {
			this.x1 = x1;
			this.x2 = x2;
			this.y1 = y1;
			this.y2 = y2;
		}
		
		public void addToWorldHorV(int[][] world) throws Exception {
			if (x1 == x2) {
				int yStart = y1;
				int yEnd = y2;
				if (y1 >= y2) {
					yStart = y2;
					yEnd = y1;
				}
				for (int i=yStart; i<=yEnd; ++i) {
					world[x1][i] += 1;
				}
			} else if (y1 == y2) {
				int xStart = x1;
				int xEnd = x2;
				if (x1 >= x2) {
					xStart = x2;
					xEnd = x1;
				}
				for (int i=xStart; i<=xEnd; ++i) {
					world[i][y1] += 1;
				}
			}
		}
		
		public void addToWorldDiag45(int[][] world) {
			if (x1 != x2 && y1 != y2) {
				// Assume diagonal at 45 degreesc, we won't bother checking now
				int xCurrent = x1;
				int yCurrent = y1;
				while (xCurrent != x2) {
					world[xCurrent][yCurrent] += 1;
					if (xCurrent < x2) {
						xCurrent++;
					} else {
						xCurrent--;
					}
					if (yCurrent < y2) {
						yCurrent++;
					} else {
						yCurrent--;
					}
				}
				world[xCurrent][yCurrent] += 1;
			}
		}
	}
	
	// Don't know the size of the world yet, so we need to read in all lines first to discover it
	//  Could do a double pass to avoid memory usage here, tradeoff between time/memory
	List<Line> lines = new ArrayList<Line>();
	
	int[][] world;
	
	@Override
	public void handleInput(String line) {
		String[] split = line.split(" ");
		if (split.length != 3) {
			logger.error("Unable to parse: "+line+" split size: "+split.length);
			return;
		}
		
		String p1 = split[0];
		String p2 = split[2];
		int[] p1Coords = parsePoint(p1);
		int[] p2Coords = parsePoint(p2);
		if (p1Coords == null || p2Coords == null) {
			return;
		}
		
		Line l1 = new Line(p1Coords[0], p1Coords[1], p2Coords[0], p2Coords[1]);
		lines.add(l1);
	}
	
	protected int[] parsePoint(String p) {
		String[] pSplit = p.split(",");
		if (pSplit.length != 2) {
			logger.error("Unable to parse point: "+p);
			return null;
		}
		try {
			int[] coords = new int[2];
			coords[0] = Integer.parseInt(pSplit[0]);
			coords[1] = Integer.parseInt(pSplit[1]);
			return coords;
		} catch (NumberFormatException ex) {
			logger.error(ex.toString(), ex);
		}
		return null;
	}
	
	protected void createGrid() throws Exception {
		int maxX = 0;
		int maxY = 0;
		for (Line l : lines) {
			if (l.x1 > maxX) {
				maxX = l.x1;
			}
			if (l.x2 > maxX) {
				maxX = l.x2;
			}
			if (l.y1 > maxY) {
				maxY = l.y1;
			}
			if (l.y2 > maxY) {
				maxY = l.y2;
			}
		}
		maxX++;
		maxY++;
		
		world = new int[maxX][maxY];
		for (int i=0; i<maxX; ++i) {
			for (int j=1; j<maxY; ++j) {
				world[i][j] = 0;
			}
		}
	}
	
	protected void addLinesToWorld() throws Exception {
		for (Line l : lines) {
			l.addToWorldHorV(world);
		}
	}
	
	protected int getOverlapPoints(int[][] world) {
		int count = 0;
		for (int i=0; i<world.length; ++i) {
			for (int j=0; j<world[i].length; ++j) {
				if (world[i][j] > 1) {
					count++;
					logger.info("Overlap point: "+j+","+i+": "+world[i][j]);
				}
			}
		}
		return count;
	}

	@Override
	public void output() {
		// Figure out how big the world is, add lins (H or V) to the world
		try {
			createGrid();
			addLinesToWorld();
			logger.info("Points: "+getOverlapPoints(world));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
