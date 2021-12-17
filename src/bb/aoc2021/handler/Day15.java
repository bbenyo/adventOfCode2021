package bb.aoc2021.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import bb.aoc2021.InputHandler;
import bb.aoc2021.Location;
import bb.aoc2021.Utilities;

/**
 * A* search algorithm
 * @author bbenyo
 */
public class Day15 implements InputHandler {
	
	static private Logger logger = Logger.getLogger(Day15.class.getName());
	
	// Don't know how big the grid will be
	List<int[]> grid = new ArrayList<>();
	
	// Assume a even grid where each row is the same size
	protected int getGridSizeX() {
		if (grid.size() > 0) {
			return grid.get(0).length;
		}
		return 0;
	}
	
	protected int getGridSizeY() {
		return grid.size();
	}
	
	protected int getRightDownDistanceToEnd(int x, int y) {
		int right = getGridSizeX() - x;
		int down = getGridSizeY() - y;
		return right + down;
	}
	
	public class Node extends Location {
		String label;
		int gScore = 10 * (getGridSizeY() * 2);  // Worst possible value for risk
		// If we went from top left to bottom right at 10 risk per step, can't be worse than this
		
		int hScore = 0;
		
		Set<Node> neighbors;
		Node backPath;
		
		public Node(Location l1) {
			super(l1.getX(), l1.getY());
			this.label = l1.toString();
			neighbors = new HashSet<Node>();
		}
		
		public void setNeighbors(Set<Node> neighbors) {
			this.neighbors = neighbors;
		}
		
		public void addNeighbor(Node neighbor) {
			this.neighbors.add(neighbor);
		}
		
		// Heuristic, guess on the risk score for the path to the end
		//   We'll just do a right/down distance, and assume average risk of 5
		protected void computeHScore() {
			int dist = getRightDownDistanceToEnd(getX(), getY());
			// Could calculate the actual risk for going this way, but this is fine, it's just a heuristic
			//   and we don't care about optimizing this search for speed today
			int h = (dist * 5);
			hScore = h;
		}
		
		public int getF() {
			return gScore + hScore;
		}
		
		// We allow varying length rows, so we need to pass in the grid to figure out the neighbors
		public void gatherNeighbors() {
			int x = getX() - 1;  //left
			int y = getY();
			if (x > -1) {
				Location lup = new Location(x, y);
				addNeighbor(lup);
			}
			x = getX();
			y = getY() - 1; //up
			if (y > -1) {
				Location lup = new Location(x, y);
				addNeighbor(lup);
			}
			
			x = getX() + 1;
			y = getY(); // right
			if (x < getGridSizeX()) {
				Location lup = new Location(x, y);
				addNeighbor(lup);
			}
			
			x = getX();
			y = getY() + 1; // down
			if (y < getGridSizeY()) {
				Location lup = new Location(x, y);
				addNeighbor(lup);
			}			
		}
		
		protected void addNeighbor(Location lup) {
			Node uNode = nodes.get(lup.toString());
			if (uNode != null) {
				neighbors.add(uNode);
			} else {
				uNode = new Node(lup);
				neighbors.add(uNode);
				nodes.put(lup.toString(), uNode);				
			}
		}
	}
	
	protected int getRisk(Location cur) {
		return grid.get(cur.getY())[cur.getX()];
	}
	
	// Bottom right
	public boolean isEnd(Location loc) {
		if ((loc.getY() == getGridSizeY() - 1) &&
			(loc.getX() == getGridSizeX() - 1)) {
			return true;
		}
		return false;
	}
	
	Map<String, Node> nodes = new HashMap<String, Node>();

	// A*

	List<Node> openSet = new ArrayList<Node>();
	
	protected Node search() {
		Location startLoc = new Location(0,0);
		Node start = new Node(startLoc);
		openSet.add(start);
		start.gScore = 0;
		
		while (!openSet.isEmpty()) {
			// Get the lowest f score
			Collections.sort(openSet, new Comparator<Node>() {

				@Override
				public int compare(Node o1, Node o2) {
					int f1 = o1.getF();
					int f2 = o2.getF();
					if (f1 < f2) {
						return -1;
					} else if (f1 == f2) {
						return 0;
					}
					return 1;
				}
			});
			
			Node next = openSet.remove(0);
			next.gatherNeighbors();
			next.computeHScore();
			
			logger.info("Searching: "+next);
			if (isEnd(next)) {
				return next;
			}
			for (Node n1 : next.neighbors) {
				int g = next.gScore + getRisk(n1);
				// If the path from next to n1 is better than any other path we've found to n1:
				if (g < n1.gScore) {
					n1.backPath = next;
					n1.gScore = g;
					if (!openSet.contains(n1)) {
						openSet.add(n1);
					}
				}
			}
		}
		
		logger.error("Unable to find a path to the end!");
		return null;
	}
	
	@Override
	public void handleInput(String line) {
		int[] row = Utilities.stringListToInts(line, "");
		grid.add(row);
	}
	
	protected int constructPath(Node n1) {
		StringBuffer sb = new StringBuffer();
		Node cur = n1;
		int risk = 0;
		while (cur.backPath != null) {
			sb.append(System.lineSeparator());
			sb.append(cur.toString()+": "+getRisk(cur));
			risk += getRisk(cur);
			cur = cur.backPath;
		}
		logger.info("Path: "+sb.toString());
		return risk;
	}

	@Override
	public void output() {
		for (int i=0; i<10; ++i) {
			Location l1 = new Location(0,i);
			logger.info("Risk for "+l1+": "+getRisk(l1));
		}
		
		Node end = search();
		int risk = 0;
		if (end != null) {
			risk = constructPath(end);
		}
		logger.info("Risk: "+risk);
	}

}
