package bb.aoc2021.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import bb.aoc2021.InputHandler;

public class Day12 implements InputHandler {
	static private Logger logger = Logger.getLogger(Day12.class.getName());
		
	public class Cave {
		String name;
		boolean small;
		Set<String> connections;
		
		public Cave(String n) {
			name = n;
			char c1 = n.charAt(0);
			if (c1 >= 'A' && c1 <= 'Z') {
				small = false;
			} else {
				small = true;
			}
			connections = new TreeSet<>();
		}
		
		public boolean isEnd() {
			return name.equals("end");
		}
		
		public boolean isStart() {
			return name.equals("start");
		}
	}
	
	public class Path {
		List<Cave> path;
		Cave smallTwice; // Single small cave that we can visit twice on this path
		
		public Path(Cave start) {
			path = new ArrayList<Cave>();
			path.add(start);
			smallTwice = null;
		}
		
		public Path(Path p1) {
			path = new ArrayList<Cave>();
			for (Cave p : p1.path) {
				path.add(p);
			}
			smallTwice = p1.smallTwice;
		}
		
		public boolean isEnd() {
			return path.get(path.size() - 1).isEnd();
		}
		
		public boolean visitedSmallTwice() {
			Set<String> visited = new HashSet<String>();
			boolean visitedSmallTwice = false;
			for (Cave c : path) {
				if (c.small && visited.contains(c.name)) {
					if (allowSmallTwice() && !c.isEnd() && !c.isStart()) {
						if (smallTwice == null) {
							// We haven't visited any small twice yet
							smallTwice = c;
						} else if (smallTwice.name.equals(c.name) && !visitedSmallTwice) {
							// This is the second time we visited this one, still good
							visitedSmallTwice = true;
						} else {
							return true;
						}
					} else {
						return true;
					}
				} else if (c.small) {
					visited.add(c.name);
				}
			}
			return false;
		}
		
		public Cave getLast() {
			return path.get(path.size() - 1);
		}
		
		public boolean expandTo(String c) {
			Cave c1 = caves.get(c);
			if (c1 != null) {
				path.add(c1);
				return true;
			}
			return false;
		}
		
		public String toString() {
			StringBuffer sb = new StringBuffer();
			boolean first = true;
			for (Cave c : path) {
				if (first) {
					first = false;
				} else {
					sb.append(",");
				}
				sb.append(c.name);
			}
			return sb.toString();
		}
	}
		
	protected boolean allowSmallTwice() {
		return false;
	}
	
	Map<String, Cave> caves = new HashMap<String, Cave>();

	@Override
	public void handleInput(String line) {
		String[] caveInput = line.split("-");
		if (caveInput.length != 2) {
			logger.error("Can't parse line: "+line);
			return;
		}
		
		String c1 = caveInput[0];
		String c2 = caveInput[1];
		
		Cave cave1 = caves.get(c1);
		if (cave1 == null) {
			cave1 = new Cave(c1);
			caves.put(c1, cave1);

			logger.info("Adding cave "+c1);
		}
		
		Cave cave2 = caves.get(c2);
		if (cave2 == null) {
			cave2 = new Cave(c2);			
			caves.put(c2, cave2);

			logger.info("Adding cave "+c2);
		}
		
		cave1.connections.add(c2);
		cave2.connections.add(c1);
		
		logger.info("Adding path between "+c1+" and "+c2);
	}
	
	// DFS:  Start at start, end at end, visit small caves only once
	//   Return all paths
	protected int depthFirstSearch() {
		Stack<Path> pathStack = new Stack<>();
		
		Cave start = caves.get("start");
		if (start == null) {
			logger.error("No start cave!");
			return 0;
		}
		Cave end = caves.get("end");
		if (end == null) {
			logger.error("No end cave!");
			return 0;
		}
		
		Path startPath = new Path(start);
		pathStack.add(startPath);
		
		int pathCount = 0;
		while (!pathStack.isEmpty()) {
			Path curPath = pathStack.pop();
			if (curPath.isEnd()) {
				// Valid path
				pathCount++;
				logger.info("Path: "+curPath.toString());
			} else if (curPath.visitedSmallTwice()) {
				// invalid, skip
			} else {
				// continue path 
				Cave last = curPath.getLast();
				for (String nxt : last.connections) {
					Path nPath = new Path(curPath);
					nPath.expandTo(nxt);
					pathStack.add(nPath);
				}
			}			
		}
		
		return pathCount;
	}

	@Override
	public void output() {
		int paths = depthFirstSearch();
		logger.info("Paths found: "+paths);
	}

}
