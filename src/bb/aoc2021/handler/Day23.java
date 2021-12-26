package bb.aoc2021.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.log4j.Logger;

import bb.aoc2021.InputHandler;
import bb.aoc2021.Location;

public class Day23 implements InputHandler {
	static private Logger logger = Logger.getLogger(Day23.class.getName());
	// Could parse the board in from the input too, but for now we'll just parse in the locations of the aPods
	
	public class GameBoard {
		int rows = 5;
		int cols = 13;
		
		char[][] board = new char[cols][rows];
		
		public GameBoard() {
			for (int i=0; i<cols; ++i) {
				board[i][0] = '#';
			}
			board[0][1] = '#';
			for (int i=1; i<cols-1; ++i) {
				board[i][1] = '.';
			}
			board[12][1] = '#';
			for (int i=0; i<cols; ++i) {
				for (int j=2; j<5; ++j) {
					board[i][j] = '#';
				}
			}
			// This means the ending positions
			// Current position of each apod is stored in the apod struct
			board[3][2] = '.';
			board[3][3] = '.';
			board[5][2] = '.';
			board[5][3] = '.';
			board[7][2] = '.';
			board[7][3] = '.';
			board[9][2] = '.';
			board[9][3] = '.';
		}
		
		public boolean isHallway(int x, int y) {
			return y == 1;
		}	
		
		public boolean isEnd(int x, int y, char type) {
			if (y < 2 || y > 3) {
				return false;
			}
			switch (type) {
			case 'A' :
				return x == 3;
			case 'B' : 
				return x == 5;
			case 'C' : 
				return x == 7;
			case 'D' : 
				return x == 9;
			default :
				return false;
			}
		}
		
		public int getEndCol(char type) {
			switch(type) {
			case 'A' : return 3;
			case 'B' : return 5;
			case 'C' : return 7;
			case 'D' : return 9;
			default: 
				logger.error("Unknown type: "+type);
				return 3;
			}
		}
		
		public char get(int x, int y) {
			if (x < 0 || x >= cols || y < 0 || y >= rows) {
				return '\0';
			}
			return board[x][y];
		}
		
		// Get the best path from fx,fy to tx,ty
		// Could do a pathplanning a* here, but we can encode the logic directly, it's simple
		public List<Location> getPath(int fx, int fy, int tx, int ty, GameState state, boolean ignoreBlockers) {
			List<Location> path = new ArrayList<>();
			if (fx == tx && fy == ty) {
				return null;
			}
			if (isHallway(fx, fy)) {
				// Move left or right towards tx first
				int cx = fx;
				while (cx != tx) {
					if (tx > cx) {
						cx++; 
					} else {
						cx--;
					}
					if (!ignoreBlockers && state.occupied(cx, fy)) {
						// We're blocked, can't get to tx/ty
						return null;
					}
					Location pLoc = new Location(cx,fy);
					path.add(pLoc);					
				}
				// Now move down
				int cy = fy;
				while (cy < ty) {
					cy++;
					if (!ignoreBlockers && state.occupied(cx, cy)) {
						// We're blocked, can't get to tx/ty
						return null;
					}
					Location pLoc = new Location(cx,cy);
					path.add(pLoc);	
				}
				return path;
			} else {
				// Not in the hallway, I'm in a room. Go to the hallway and recurse
				int cy = fy;
				if (cy < 1) {
					// We're off the map!
					logger.error("We're not on the board: "+fx+","+fy);
					return null;
				}
				while (cy > 1) {
					cy--;
					if (!ignoreBlockers && state.occupied(fx, cy)) {
						// We're blocked, can't get to tx/ty
						return null;
					}
					Location pLoc = new Location(fx,cy);
					path.add(pLoc);	
				}
				List<Location> hPath = getPath(fx, cy, tx, ty, state, ignoreBlockers);
				if (hPath == null) {
					return null;
				}
				path.addAll(hPath);
				return path;
			}
		}
	}
	
	GameBoard gameBoard = new GameBoard();
	
	public int getCost(char a) {
		switch (a) {
		case 'A' : return 1;
		case 'B' : return 10;
		case 'C' : return 100;
		case 'D' : return 1000;
		default :
			logger.error("Unknown APod type: "+a);
			return 0;
		}
	}
	
	public class APod {
		int x;
		int y;
		
		int energyCost;
		char type;
		int index = 0;
		
		public APod(int x, int y, char t, int index) {
			this.x = x;
			this.y = y;
			this.energyCost = getCost(t);
			this.type = t;
			this.index = index;
		}
		
		public APod(APod o) {
			this.x = o.x;
			this.y = o.y;
			this.energyCost = o.energyCost;
			this.type = o.type;
			this.index = o.index;
		}
		
		public boolean isAtEndPos(GameState s) {
			if (!gameBoard.isEnd(x, y, type)) {
				return false;
			}
			// also check that both spots (both apods of the same type) are in their end positions
			// Else we may have to move out
			if (y == 2) {
				APod x3 = s.whoIsAt(x, 3);
				if (x3 != null && x3.type == this.type) {
					return true;
				} else {
					// We're blocking someone in x,3, we'll have to move, we're not done
					return false;
				}
			} 
			return true;
		}
		
		public boolean inHallway() {
			return gameBoard.isHallway(x, y);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getEnclosingInstance().hashCode();
			result = prime * result + index;
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
			APod other = (APod) obj;
			if (!getEnclosingInstance().equals(other.getEnclosingInstance()))
				return false;
			if (index != other.index)
				return false;
			return true;
		}

		private Day23 getEnclosingInstance() {
			return Day23.this;
		}
		
		@Override
		public String toString() {
			return String.valueOf(type)+" at "+x+","+y;
		}
	
	}
	
	public class Move {
		GameState state;
		APod mover;
		
		public Move(List<Location> path, APod mover, GameState state) {
			// Mover moves along path
			state = new GameState(state);
			state.move(mover, path);
			this.mover = mover;
			this.state = state;
		}		
	}

	public class GameState {
		List<APod> apods = new ArrayList<APod>();
		long score;
		// To avoid wasted search going back and forth between two identical states
		List<GameState> priorStates;
		int stateHash;
		APod lastMover = null;
		
		public GameState(List<APod> apods) {
			this.apods = apods;
			this.score = 0;
			priorStates = new ArrayList<GameState>();
			setStateHash();
		}
		
		public GameState(GameState oState) {
			apods = new ArrayList<APod>();
			for (APod a : oState.apods) {
				apods.add(new APod(a));
			}
			setStateHash();
			score = oState.score;
			priorStates = new ArrayList<GameState>();
			priorStates.addAll(oState.priorStates);
			priorStates.add(oState);
		}
		
		public GameState(GameState oState, Move m) {
			this(oState);
			this.move(m.mover, null);
			this.lastMover = m.mover;
			setStateHash();
		}
		
		protected void setStateHash() {
			StringBuffer sb = new StringBuffer();
			for (APod a : apods) {
				sb.append(a);
			}
			String hash = sb.toString();
			stateHash = hash.hashCode();
		}
		
		public String toString() {
			char[][] b = new char[gameBoard.cols][gameBoard.rows];
			// Initial board
			for (int i=0; i<gameBoard.cols; ++i) {
				for (int j=0; j<gameBoard.rows; ++j) {
					b[i][j] = gameBoard.get(i,j);
				}
			}
			for (APod a : apods) {
				b[a.x][a.y] = a.type; 
			}
			StringBuffer sb = new StringBuffer(System.lineSeparator());
			for (int j=0; j<gameBoard.rows; ++j) {
				for (int i=0; i<gameBoard.cols; ++i) {
					sb.append(b[i][j]);
				}
				sb.append(System.lineSeparator());
			}
			return sb.toString();
		}
		
		public void move(APod mover, List<Location> path) {
			Location lastLoc = path.get(path.size() - 1);
			for (APod a : apods) {
				if (a.equals(mover)) {
					a.x = lastLoc.getX();
					a.y = lastLoc.getY();
					score += (path.size() * a.energyCost);
					setStateHash();
					return;
				}
			}
			logger.error("Unable to move "+mover.type+"-"+mover.index+" can't find it!");
		}
		
		public APod whoIsAt(int x, int y) {
			for (APod a : apods) {
				if (a.x == x && a.y == y) {
					return a;
				}
			}
			return null;
		}
		
		public boolean win() {
			for (APod a : apods) {
				if (!a.isAtEndPos(this)) {
					return false;
				}
			}
			return true;
		}
		
		public long getScore() {
			return score;
		}
		
		public boolean occupied(int x, int y) {
			for (APod a : apods) {
				if (a.x == x && a.y == y) {
					return true;
				}
			}
			return false;
		}
		
		public List<Move> generatePossibleMoves() {
			List<Move> nextStates = new ArrayList<>();

			// Let's order the next states to speed up the search
			// Find the highest energy pod that isn't in its end pos
			//   And prioritize moving anyone in its way, then it
			APod highestOutOfPosition = null;
			for (APod a : apods) {
				// Can't move if we're at the end
				if (a.isAtEndPos(this)) {
					continue;
				}
				if (highestOutOfPosition == null || highestOutOfPosition.energyCost < a.energyCost) {
					highestOutOfPosition = a;
				}
			}
			
			if (highestOutOfPosition != null) {
				List<APod> blockers = getBlockers(highestOutOfPosition);
				apods.remove(highestOutOfPosition);
				apods.add(0, highestOutOfPosition);
				if (blockers != null) {
					for (APod b : blockers) {
						apods.remove(b);
						apods.add(0, b);
					}
				}
			}
			
			for (APod a : apods) {
				// Can't move if we're at the end
				if (a.isAtEndPos(this)) {
					continue;
				}
				if (this.lastMover != null && this.lastMover.index == a.index) {
					// Don't need to try and move the same pod again
					continue;
				}
				// Could be more general here, but we'll hardcode the logic for this specific game for now
				
				// We can always try to move to another room
				// Try to move to the end first
				List<Integer> rooms = new ArrayList<Integer>();
				for (int i=3; i<=9; i+=2) {
					rooms.add(i);
				}
				
				int endCol = gameBoard.getEndCol(a.type);
				rooms.remove(Integer.valueOf(endCol));
				rooms.add(0, endCol);
				boolean myEnd = false;
				for (Integer i : rooms) {
					if (i == a.x) {
						// Don't try to move to the room you're in
						continue;
					}
					List<Location> p1 = gameBoard.getPath(a.x, a.y, i, 2, this, false);
					if (p1 != null && p1.size() > 0) {
						// Small optimization, if we're a, and 3,3 is open, we'd always want to move there instead
						APod i3 = whoIsAt(i,3);
						if (i3 == null && gameBoard.isEnd(i,3,a.type)) {
							Location l2 = new Location(i,3);
							p1.add(l2);
							myEnd = true;
						} else if (i3 != null && i3.type == a.type) {
							myEnd = true;
						}
						nextStates.add(new Move(p1, a, this));
						// If we can move to our end room, always do that
						if (myEnd) {
							break;
						}
					}
				} 
				if (myEnd) {
					continue;
				}
				// If we're not in the hallway now, we can move to the hallway
				if (!gameBoard.isHallway(a.x, a.y)) {
					// Shorter moves first
					List<Integer> hCols = new ArrayList<Integer>();
					for (int i=1; i<9; ++i) {
						int cx = a.x - i;
						if (cx >= 1 && cx <= 11) {
							if (cx != 3 && cx != 5 && cx != 7 && cx != 9) {
								hCols.add(cx);
							}
						}
						cx = a.x + 1;
						if (cx >= 1 && cx <= 11) {
							if (cx != 3 && cx != 5 && cx != 7 && cx != 9) {
								hCols.add(cx);
							}
						}
					}
					
					// We can also try to move to anywhere in the hallway
					// Since the hallway is small, no need for optimization really, we could do better here
					for (Integer i : hCols) {
						List<Location> p1 = gameBoard.getPath(a.x, a.y, i, 1, this, false);
						if (p1 != null && p1.size() > 0) {
							nextStates.add(new Move(p1, a, this));
						}
					}
				}				
			}
						
			
			return nextStates;
		}
		
		public APod getApod(APod o) {
			for (APod a : apods) {
				if (a.index == o.index) {
					return a;
				}
			}
			return null;
		}
		
		// Not calling this .equals since it doesn't meet the equals contract 
		// Should hash the state to make this faster, hash would just be each position
		public boolean sameState(GameState o) {
			if (stateHash == o.stateHash) {
				return true;
			}
			return false;
		}
		
		public boolean seenBefore(GameState o) {
			for (GameState p : priorStates) {
				if (p.sameState(o)) {
					return true;
				}
			}
			return false;
		}
		
		// Get the list of pods that are blocking A from getting to its final spot
		//  Used for optimization
		public List<APod> getBlockers(APod a) {
			List<APod> blockers = new ArrayList<>();
			// Get a's final spot
			if (a.isAtEndPos(this)) {
				return blockers;
			}
			int x = gameBoard.getEndCol(a.type);
			int y = 3;
			APod x3 = whoIsAt(x, y);
			if (x3 != null && x3.type == a.type) {
				// The other pod of my type is in the 3 pos, 2 pos is good for us
				y = 2;				
			}
			
			List<Location> path = gameBoard.getPath(a.x, a.y, x, y, this, true);
			if (path == null) {
				logger.error("Unable to find a path from "+a.x+","+a.y+" to "+x+","+y);
				return blockers;
			}
			for (Location l : path) {
				APod b = whoIsAt(l.getX(), l.getY());
				if (b != null) {
					blockers.add(b);
				}
			}
			
			return blockers;			
		}
		
		// Get the lowest possible score for this state
		// Assume everyone can move straight towards their goal spot, add all that energy to the cur score
		public long getMinScore() {
			long minScore = score;
			Map<Character, Boolean> inRow3 = new HashMap<>();
			APod a3 = whoIsAt(3,3);
			APod b3 = whoIsAt(5,3);
			APod c3 = whoIsAt(7,3);
			APod d3 = whoIsAt(9,3);
			inRow3.put('A', a3 != null && a3.type == 'A' ? true : false);
			inRow3.put('B', b3 != null && b3.type == 'B' ? true : false);
			inRow3.put('C', c3 != null && c3.type == 'C' ? true : false);
			inRow3.put('D', d3 != null && d3.type == 'D' ? true : false);
			for (APod a : apods) {
				if (a.isAtEndPos(this)) {
					continue;
				}
				int x = gameBoard.getEndCol(a.type);
				int y = 3;
				if (inRow3.get(a.type)) {
					y = 2;
				} else {
					inRow3.put(a.type, true);
				}
				// Our end pos is x,y, moving straight there would add
				List<Location> path = gameBoard.getPath(a.x, a.y, x, y, this, true);
				if (path != null) {
					minScore += (path.size() * a.energyCost);
				}
			}
			return minScore;
		}
	}
	
	int curRow = 0;
	List<APod> initPods = new ArrayList<APod>();
	
	@Override
	public void handleInput(String line) {
		// Could read in the entire gameboard here and construct it dynamically
		// But the problem would need to define what is a "hallway", etc. 
		// So instead, we'll just look for the starting locations of the apods
		for (int i=0; i<line.length(); ++i) {
			char c = line.charAt(i);
			if (c >= 'A' && c <= 'D') {
				APod p1 = new APod(i, curRow, c, initPods.size());
				initPods.add(p1);
				logger.info("Adding initial aPod: "+p1);
			}
		}
		curRow++;
	}
	
	Stack<GameState> workList = new Stack<>();
	
	public GameState dfs(GameState initialState) {
		int index = 0;
		workList.clear();
		
		// States that we've already searched, and their score
		// If we get to the same state again, but a better score, we still search it
		Map<Integer, Long> alreadySearched = new HashMap<>();
		workList.push(initialState);
		GameState winner = null;
		while (!workList.isEmpty()) {
			index++;
			GameState state = workList.pop();
			alreadySearched.put(state.stateHash, state.score);
			if (state.win()
				&& (winner == null || (state.score < winner.score))) {
				winner = state;
				continue;
			}
			
			if (winner != null && winner.score < state.score) {
				// Winner is already better than this, we're done on this branch
				continue;
			}
			
			long lScore = state.score;
			List<Move> moves = state.generatePossibleMoves();
			for (Move m : moves) {
				GameState nState = m.state;
				if (winner != null && winner.score < nState.getMinScore()) {
					// Current winner is already better that a win from this state could be
					continue;
				}
				Long oScore = alreadySearched.get(nState.stateHash);
				if (oScore != null && oScore.longValue() <= nState.score) {
					continue;
				}
				workList.add(nState);
				alreadySearched.put(nState.stateHash, nState.score);
				if (nState.score < lScore) {
					lScore = nState.score;
				}
			}
			logger.info("Search step: "+index+" worklist size: "+workList.size()+" lowest score: "+lScore+" found win: "+(winner != null));
			if (winner != null) {
				logger.info("\tWinner score: "+winner.score);
			}
		}
		return winner;
	}

	@Override
	public void output() {
		GameState initState = new GameState(initPods);
		logger.info("Initial State: "+initState);
		GameState endState = dfs(initState);
		
		if (endState == null) {
			logger.error("Unable to find a winning state!");
		} else {
			logger.info("Winning path");
			for (GameState g : endState.priorStates) {
				logger.info(g);
			}
			logger.info("Winning state: "+endState);
			logger.info("Final min end state cost: "+endState.score);
		}
	}

}
