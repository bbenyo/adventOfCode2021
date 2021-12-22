package bb.aoc2021;

import java.util.ArrayList;
import java.util.List;

public class BitImage {
	
	// Image of boolean 'pixels', on/off
	// We'll allow non-square images
	// Anything out of bounds is off, rather than an error
	
	List<UsefulBitSet> image;
	char onChar = '#';
	char offChar = '.';
	int maxRowLen = 0;
	
	public boolean infiniteOn = false;

	public BitImage() {
		image = new ArrayList<>();
	}
	
	public BitImage(int maxX, int maxY) {
		this.maxRowLen = maxX;
		image = new ArrayList<UsefulBitSet>();
		for (int i=0; i<maxY; ++i) {
			image.add(new UsefulBitSet(maxX));
		}		
	}
	
	public char getOnChar() {
		return onChar;
	}
	
	public void setOnChar(char onChar) {
		this.onChar = onChar;
	}

	public char getOffChar() {
		return offChar;
	}

	public void setOffChar(char offChar) {
		this.offChar = offChar;
	}
	
	public int getMaxRowLen() {
		return maxRowLen;
	}
	
	public int getRowCount() {
		return image.size();
	}

	public void setMaxRowLen(int maxRowLen) {
		this.maxRowLen = maxRowLen;
	}

	public void readLine(String line) {
		UsefulBitSet row = new UsefulBitSet(line.length());
		for (int i=0; i<line.length(); ++i) {
			char c = line.charAt(i);
			if (c == onChar) {
				row.set(i);
			}
		}
		row.setRealLength(line.length());
		if (maxRowLen < line.length()) {
			maxRowLen = line.length();
		}
		image.add(row);
	}
	
	public boolean get(int i, int j) {
		// Out of bounds = false, (dark pixels go to infinity)
		if (i < 0 || j < 0) {
			return infiniteOn;
		}
		if (j >= image.size()) {
			return infiniteOn;
		}
		UsefulBitSet row = image.get(j);
		if (i >= row.realLength) {
			return infiniteOn;
		}
		return row.get(i);
	}
	
	public void set(int i, int j) {
		if (i < 0 || j < 0) {
			return;
		}
		if (j >= image.size()) {
			return;
		}
		UsefulBitSet row = image.get(j);
		row.set(i);
	}
	
	/**
	 * For a 3x3 square around pixel i,j, convert to a single binary number:
	 * Each pixel of the output image is determined by looking at a 3x3 square 
	 * of pixels centered on the corresponding input image pixel. So, to determine
	 *  the value of the pixel at (5,10) in the output image, nine pixels from the
     *  input image need to be considered: (4,9), (4,10), (4,11), (5,9), (5,10), 
     *  (5,11), (6,9), (6,10), and (6,11). 
     *  These nine input pixels are combined into a single binary number that is used 
     *  as an index in the image enhancement algorithm string.
     *  
	 * @param i
	 * @param j
	 * @return
	 */
	public int getEncoding3x3(int i, int j) {
		UsefulBitSet num = new UsefulBitSet(9);
		int idx = 0;
		for (int j1 = j - 1; j1 <= j + 1; ++j1) {
			for (int i1 = i - 1; i1 <= i + 1; ++i1) {
				if (get(i1, j1)) {
					num.set(idx);
				}
				idx++;
			}
		}
		return num.toIntRange(0, 8);
	}
	
	/** 
	 * Extend the image by adding a 2 rows of infinite pixels at the top/bottom/left/right
	 */
	public void extendImage() {
		// Top
		for (int i=0; i<2; ++i) {
			UsefulBitSet top = new UsefulBitSet(maxRowLen);
			image.add(0, top);
			if (infiniteOn) {
				top.setAllOn();
			}
			// bottom
			UsefulBitSet bottom = new UsefulBitSet(maxRowLen);
			image.add(bottom);
			if (infiniteOn) {
				bottom.setAllOn();
			}
		}

		// left
		for (UsefulBitSet row : image) {
			row.shift1();
			row.shift1();
			row.setRealLength(row.length() + 2);
			if (infiniteOn) {
				row.set(0);
				row.set(1);
				row.set(row.length() - 1);
				row.set(row.length() - 2);
			}
		}
		this.maxRowLen += 4;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (UsefulBitSet row : image) {
			for (int i=0; i<row.length(); ++i) {
				if (row.get(i)) {
					sb.append(onChar);
				} else {
					sb.append(offChar);
				}
			}
			sb.append(System.lineSeparator());
		}
		return sb.toString();
	}
	
	public long getOnPixels() {
		long count = 0;
		for (UsefulBitSet row : image) {
			for (int i=0; i<row.realLength; ++i) {
				if (row.get(i)) {
					count++;
				}
			}
		}
		return count;
	}
}
