package bb.aoc2021.handler;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import bb.aoc2021.InputHandler;
import bb.aoc2021.Utilities;

public class Day4 implements InputHandler {

	static private Logger logger = Logger.getLogger(Day4.class.getName());
	
	int[] bingoNumbers = null;
	int winningNumber = -1;
	
	class BingoGrid {
		int index = 0;
		int[][] grid;
		int nextRow = 0;
		boolean won = false;
		
		public BingoGrid(int idx) {
			index = idx;
			grid = new int[5][5];
			nextRow = 0;
		}
		
		public boolean isFull() {
			return nextRow == 5;
		}
		
		public boolean addRow(String row) {
			if (nextRow < 5) {
				int[] nr = Utilities.stringListToInts(row, " ");
				if (nr.length != 5) {
					logger.error("Improper row, not 5 ints: "+nr.length+": "+row);
					return false;
				}
				grid[nextRow] = nr;
				nextRow++;
				return true;
			}
			logger.error("Tried to add a row to a full grid!");
			return false;
		}
		
		public void playNumber(int num) {
			for (int i=0; i<grid.length; ++i) {
				for (int j=0; j<grid[i].length; ++j) {
					if (grid[i][j] == num) {
						grid[i][j] = -1; // -1 means played
					}
				}
			}
		}
		
		public boolean bingoRow() {
			// rows
			for (int i=0; i<grid.length; ++i) {
				boolean bingo = true;
				for (int j=0;j<grid[i].length; ++j) {
					if (grid[i][j] != -1) {
						bingo = false;
						break;
					}
				}
				if (bingo) {
					logger.info("Bingo "+index+" row "+i);
					return true;
				}
			}
			return false;
		}
		
		public boolean bingoColumn() {			
			// columns
			for (int i=0; i<grid.length; ++i) {
				boolean bingo = true;
				for (int j=0;j<grid[i].length; ++j) {
					if (grid[j][i] != -1) {
						bingo = false;
						break;
					}
				}
				if (bingo) {
					logger.info("Bingo "+index+" column "+i);
					return true;
				}
			}
			return false;
		}
		
		public boolean bingo() {
			if (bingoRow()) {
				this.won = true;
				return true;
			}
			if (bingoColumn()) {
				this.won = true;
				return true;
			}
			// No diagonals in this version
			return false;
		}
		
		public long computeScore() {
			long sum = 0;
			for (int i=0; i<grid.length; ++i) {
				for (int j=0; j<grid[i].length; ++j) {
					if (grid[i][j] > -1) {
						sum += grid[i][j];
					}
				}
			}
			return sum;
		}
	}
	
	List<BingoGrid> grids = new ArrayList<BingoGrid>();
	BingoGrid curGrid = null;
	
	@Override
	public void handleInput(String line) {
		if (bingoNumbers == null) {
			bingoNumbers = Utilities.stringListToInts(line, ",");
			logger.info("Loaded "+bingoNumbers.length+" bingo numbers");
			return;
		}
		if (line.trim().length() == 0) {
			return;
		}
		if (curGrid == null) {
			curGrid = new BingoGrid(grids.size());
		}
		boolean valid = curGrid.addRow(line);
		if (!valid) {
			throw new IllegalArgumentException(line);
		}
		if (curGrid.isFull()) {
			grids.add(curGrid);
			curGrid = null;
		}
	}
	
	public BingoGrid playGame() {
		for (int i=0; i<bingoNumbers.length; ++i) {
			int call = bingoNumbers[i];
			for (BingoGrid grid : grids) {
				grid.playNumber(call);
				if (grid.bingo()) {
					winningNumber = call;
					return grid;
				}
			}
		}
		logger.error("Played all numbers with no winner!");
		return null;
	}
	
	@Override
	public void output() {
		logger.info("Loaded "+grids.size()+" grids");
		BingoGrid winningGrid = playGame();
		if (winningGrid != null) {
			long score = winningGrid.computeScore();
			logger.info("Winning Grid: "+winningGrid.index+" score: "+score);
			logger.info("Winning Number: "+winningNumber);
			logger.info("Product: "+(score * winningNumber));
		} else {
			logger.error("No winner =(");
		}
	}

}
