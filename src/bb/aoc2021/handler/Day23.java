package bb.aoc2021.handler;

import java.util.ArrayList;
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
		
		char[][] board;
		
		public GameBoard(int rows) {
			this.rows = rows;
			board = new char[cols][rows];
			for (int i=0; i<cols; ++i) {
				board[i][0] = '#';
			}
			board[0][1] = '#';
			for (int i=1; i<cols-1; ++i) {
				board[i][1] = '.';
			}
			board[12][1] = '#';
			for (int i=0; i<cols; ++i) {
				for (int j=2; j<rows; ++j) {
					board[i][j] = '#';
				}
			}
			// This means the ending positions
			// Current position of each apod is stored in the apod struct
			for (int i=3; i<=9; i+=2) {
				for (int r=2; r<rows-1; ++r) {
					board[i][r] = '.';
				}
			}
		}
		
		public boolean isHallway(int x, int y) {
			return y == 1;
		}
		
		// Rules say that you can't move to a point in the hallway directly above a room
		public boolean validHallwayMove(int cx) {
			if (cx < 1 || cx > 11) {
				return false;
			}
			if (cx != 3 && cx != 5 && cx != 7 && cx != 9) {
				return true;
			}
			return false;
		}
		
		public boolean isEnd(int x, int y, char type) {
			if (y < 2 || y > (rows-2)) {
				return false;
			}
			return x == getEndCol(type);
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
		
		public char getEndType(int col) {
			switch(col) {
			case 3: return 'A';
			case 5: return 'B';
			case 7: return 'C';
			case 9: return 'D';
			default: 
				logger.error("Unknown col: "+col);
				return '\0';
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
		
		public int getApodCountPerType() {
			return rows - 3;
		}
		
		// Is this mover currently blocked?
		//  We're blocked if we're in a room and can't get out because someone is in our way
		public boolean isBlocked(APod a, GameState state) {
			if (this.isHallway(a.x,  a.y)) {
				return false; // Could be blocked in the hallway if people are on our sides.  Ignore that case for now, this is just an opimization
			}
			// Is anyone between us and the hallway
			for (int i=a.y - 1; i>=1; --i) {
				if (state.occupied(a.x, i)) {
					return true;
				}
			}
			return false;
		}
		
		// Does this room only contain pods of the correct type?
		public boolean onlyDestinationPods(int col, GameState state) {
			char endType = this.getEndType(col);
			for (int i=2; i<this.rows - 1; ++i) {
				if (state.occupied(col, i)) {
					APod occ = state.whoIsAt(col, i);
					if (occ.type != endType) {
						return false;
					}
				}
			}
			return true;
		}
	}
	
	GameBoard gameBoard;
	
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
		
		// Where were we last time we moved?
		int movedFrom;
		
		public APod(int x, int y, char t, int index) {
			this.x = x;
			this.y = y;
			this.energyCost = getCost(t);
			this.type = t;
			this.index = index;
			this.movedFrom = y;
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
			// also check that all spots lower than y have pods of the same type
			// Else we may have to move out
			for (int i=(y+1); i<(gameBoard.rows-1); ++i) {
				APod x3 = s.whoIsAt(x, i);
				if (x3 == null || x3.type != this.type) {
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
		Location destination;
		
		public Move(List<Location> path, APod mover, GameState state) {
			// Mover moves along path
			state = new GameState(state);
			state.move(mover, path);
			destination = path.get(path.size() - 1);
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
			// TODO: we could and should make a much faster state hash function
			//   We just want this to produce the same hash if the game board state is the same
			//  Printing out the gameboard as a string will work, but this could be sped up immensely
			String hash = toString();
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
					a.movedFrom = a.x;
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
		
		public List<Integer> getOrderedHallwayColumns(int col) {
			List<Integer> hCols = new ArrayList<>();
			// Get as far out of the way as possible first
			if (col > 7) {
				hCols.add(11);
				hCols.add(10);
				hCols.add(1);
				hCols.add(2);
				hCols.add(4);
				hCols.add(6);
				hCols.add(8);
			} else {
				hCols.add(1);
				hCols.add(2);
				hCols.add(11);
				hCols.add(10);
				hCols.add(8);
				hCols.add(6);
				hCols.add(4);
			}
			return hCols;
		}
		
		public List<Move> generatePossibleMoves() {
			List<Move> nextStates = new ArrayList<>();
			List<Move> nextBlockingStates = new ArrayList<>();

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
			
			List<APod> blockers = new ArrayList<>();
			if (highestOutOfPosition != null) {
				blockers = getBlockers(highestOutOfPosition);
				apods.remove(highestOutOfPosition);
				apods.add(0, highestOutOfPosition);
				if (blockers != null) {
					for (APod b : blockers) {
						apods.remove(b);
						apods.add(0, b);
					}
				}
			}
			
			List<Integer> allRooms = new ArrayList<Integer>();
			for (int i=3; i<=9; i+=2) {
				allRooms.add(i);
			}
			
			for (APod a : apods) {
				boolean amBlocker = blockers.contains(a);
				List<Move> aMoves = new ArrayList<>();
				// Can't move if we're at the end
				if (a.isAtEndPos(this)) {
					continue;
				}
				if (this.lastMover != null && this.lastMover.index == a.index) {
					// Don't need to try and move the same pod again
					continue;
				}
				// Could be more general here, but we'll hardcode the logic for this specific game for now
				
				// If we're blocked, just move on
				if (gameBoard.isBlocked(a, this)) {
					continue;
				}
				
				// We can only move to our destination room
				//  If it's unblocked
				// Try to move to the end first
				int endCol = gameBoard.getEndCol(a.type);
				boolean myEnd = false;
				
				// Is this room empty of other pods?
				if (endCol != a.x && gameBoard.onlyDestinationPods(endCol, this)) {
					int ty = 2;
					while (ty < gameBoard.rows - 1) {
						if (this.occupied(endCol, ty)) {
							// can't go any farther
							break;
						}
						ty++;
					}
					ty--;
					List<Location> p1 = gameBoard.getPath(a.x, a.y, endCol, ty, this, false);
					if (p1 != null && p1.size() > 0) {
						Move m1 = new Move(p1, a, this);
						aMoves.add(m1);
						myEnd = true;
					}
				}
			
				if (myEnd) {
					// No need to move anywhere else, we're done
					nextStates.addAll(aMoves);
					continue;
				}
				
				// If we're not in the hallway now, we can move to the hallway
				if (!gameBoard.isHallway(a.x, a.y)) {
					// Get as far out of the way as we can first
					List<Integer> hCols = getOrderedHallwayColumns(a.x);
					
					// We can also try to move to anywhere in the hallway
					// Since the hallway is small, no need for optimization really, we could do better here
					for (Integer i : hCols) {
						if (!gameBoard.validHallwayMove(i)) {
							continue;
						}
						List<Location> p1 = gameBoard.getPath(a.x, a.y, i, 1, this, false);
						if (p1 != null && p1.size() > 0) {
							Move m1 = new Move(p1, a, this);
							if (amBlocker && this.amIBlocking(highestOutOfPosition, m1.destination)) {
								nextBlockingStates.add(m1);
							} else {
								aMoves.add(m1);
							}
						}
					}
				}
				
				Move lastMove = null;
				for (Move m : aMoves) {
					// This is a move back to where we were last time, deprioritize it
					if (m.destination.getX() == a.movedFrom) {
						lastMove = m;
					} else {
						nextStates.add(m);
					}
				}
				if (lastMove != null) {
					nextStates.add(lastMove);
				}
			}						
			
			nextStates.addAll(nextBlockingStates);
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
		
		public Location getFinalSpot(APod a) {
			if (a.isAtEndPos(this)) {
				return new Location(a.x, a.y);
			}
			int x = gameBoard.getEndCol(a.type);
			int y = gameBoard.rows - 2;
			APod x3 = whoIsAt(x, y);
			// Look for where we want this APod to end up. 
			//   Get the destination column, and find the destination row by finding the lowest
			//   spot not occupied by an APod of this type.
			while (y > 1 && (x3 != null && x3.type == a.type)) {
				y--;
				x3 = whoIsAt(x,y);
			}
			Location loc = new Location(x, y);
			return loc;
		}
		
		// Is blocker blocking mover from getting to its final spot? Used for ordering moves
		public boolean amIBlocking(APod mover, Location blocker) {
			if (mover.isAtEndPos(this)) {
				return false;
			}
			Location finalSpot = getFinalSpot(mover);
			List<Location> path = gameBoard.getPath(mover.x, mover.y, finalSpot.getX(), 
					finalSpot.getY(), this, true);
			for (Location l : path) {
				if (l.equals(blocker)) {
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
			
			Location finalSpot = getFinalSpot(a);
			int x = finalSpot.getX();
			int y = finalSpot.getY();
			
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
			
			// Number of APods of this type that are in their destination column
			Map<Character, Integer> inDest = new HashMap<>();
			for (int i=3; i<=9; i=i+2) {
				char endType = gameBoard.getEndType(i);
				int count = 0;
				for (int j=gameBoard.rows - 2; j>1; j--) {
					APod a = whoIsAt(i,j);
					if (a == null || a.type != endType) {
						// Empty space here or an APod of the wrong type
						break;
					}
					count++;
				}
				inDest.put(endType, count);
			}
			
			// For each apod, assume we could move straight to the destination
			for (APod a : apods) {
				if (a.isAtEndPos(this)) {
					continue;
				}
				int x = gameBoard.getEndCol(a.type);
				int typeCountInDest = inDest.get(a.type);
				int y = 1 + gameBoard.getApodCountPerType() - typeCountInDest;
				// Our end pos is x,y, moving straight there would add
				List<Location> path = gameBoard.getPath(a.x, a.y, x, y, this, true);
				if (path != null) {
					minScore += (path.size() * a.energyCost);
					inDest.put(a.type, typeCountInDest + 1);
				}
			}
			return minScore;
		}
	}
	
	int curRow = 0;
	List<APod> initPods = new ArrayList<APod>();
	
	protected void initGameboard() {
		gameBoard = new GameBoard(5);
	}
	
	@Override
	public void handleInput(String line) {
		if (gameBoard == null) {
			initGameboard();
		}
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
			
			List<Move> moves = state.generatePossibleMoves();
			
			for (int m1 = 0; m1 < moves.size(); ++m1) {
				// Iterate from the end of the list
				//   The possible moves are ordered heuristically, we want to 
				//   investigate the first one first, so it needs to be last on the stack
				Move m = moves.get(moves.size() - 1 - m1);
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
			}
			logger.info("Search step: "+index+" worklist size: "+workList.size()+" found win: "+(winner != null));
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
