package bb.aoc2021.handler;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import bb.aoc2021.InputHandler;

public class Day25 implements InputHandler {
	static private Logger logger = Logger.getLogger(Day25.class.getName());
	
	class Board {
		List<char[]> cells;
		int time;
		boolean didMove = false;
		
		public Board() {
			cells = new ArrayList<>();
			time = 0;
		}
		
		public void addRow(String line) {
			char[] row = new char[line.length()];
			for (int i=0; i<line.length(); ++i) {
				row[i] = line.charAt(i);
			}
			cells.add(row);
		}
		
		public String toString() {
			StringBuffer sb = new StringBuffer("Time: "+time);
			for (char[] row : cells) {
				sb.append(System.lineSeparator());
				sb.append(row);
			}
			return sb.toString();
		}
		
		public Board propagate() {
			Board bNext = new Board();
			bNext.time = time + 1;
			
			//  Copy the row, and put it in the new board			
			// > move right one if they can
			for (char[] row : cells) {
				char[] nRow = new char[row.length];
				for (int i=0; i<row.length; ++i) {
					nRow[i] = row[i];
				}
				for (int i=0; i<row.length; ++i) {
					if (row[i] == '>') {
						int j = i+1;
						// Wrap around to the left
						if (j >= row.length) {
							j = 0;
						}
						if (row[j] == '.') {
							nRow[i] = '.';
							nRow[j] = '>';
							bNext.didMove = true;
						} 
					}
				}
				bNext.cells.add(nRow);
			}
			
			// Now move v down
			int cols = bNext.cells.get(0).length;
			for (int i=0; i<cols; ++i) {
				char[] newCol = new char[cells.size()];				
				for (int j=0; j<newCol.length; ++j) {
					newCol[j] = bNext.cells.get(j)[i];
				}
				for (int j=0; j<bNext.cells.size(); ++j) {
					char[] oRow = bNext.cells.get(j);
					if (oRow[i] == 'v') {
						int k = j + 1;
						if (k >= bNext.cells.size()) {
							k = 0;
						}
						if (bNext.cells.get(k)[i] == '.') {
							newCol[j] = '.';
							newCol[k] = 'v';
							bNext.didMove = true;
						}
					}
				}
				for (int j=0; j<newCol.length; ++j) {
					bNext.cells.get(j)[i] = newCol[j];
				}
			}
			return bNext;
		}
	}
	
	Board initBoard = new Board();

	@Override
	public void handleInput(String line) {
		initBoard.addRow(line);
	}

	@Override
	public void output() {
		logger.info(initBoard);
		Board b = initBoard.propagate();
		while (b.didMove) {
			logger.info(b);
			b = b.propagate();
		}
		logger.info("Stopped moving at time: "+b.time);
	}

}
