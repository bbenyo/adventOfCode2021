package bb.aoc2021;

import java.util.BitSet;
import java.util.stream.IntStream;

import org.apache.log4j.Logger;

/**
 * A bit set that has the methods that you'd want in one
 * 
 * Assumes BigEndian representation
 * 
 * @author bbenyo
 *
 */
@SuppressWarnings("serial")
public class UsefulBitSet extends BitSet {

	static private Logger logger = Logger.getLogger(UsefulBitSet.class.getName());
	
	// Store current position if we're reading from this bit set across a set of methods
	int pos = 0;
	int realLength = -1;
	
	public UsefulBitSet() {
		super();
	}
	
	public UsefulBitSet(int len) {
		super(len);
	}
	
	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	/**
	 * Straightforward encoding of hex string to bits
	 * 
	 * @param hex
	 * @return
	 */
	static public UsefulBitSet hexToBits(String hex) {
		UsefulBitSet bits = new UsefulBitSet(hex.length() * 4);
		for (int i=0; i<hex.length(); ++i) {
			char c = hex.charAt(i);
			int digit = Character.digit(c, 16);
			if (digit > -1) {
				String str = Integer.toBinaryString(digit);
				if (str.length() > 4) {
					logger.error("String representation for "+digit+" is too large: "+str);
					throw new IllegalArgumentException();
				}
				// 0 pad, 2 = 0010, not 10
				while (str.length() < 4) {
					str = "0"+str;
				}
				for (int j=0;j<4;++j) {
					bits.set((i*4)+j, str.charAt(j) == '0' ? false : true);
				}
			} else {
				logger.error("Invalid character in hex string: "+c);
				throw new IllegalArgumentException();
			}
		}
		return bits;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(length());
		IntStream.range(0, length()).mapToObj(i -> get(i) ? '1' : '0').forEach(sb::append);
		return sb.toString();
	}
	
	/**
	 * Convert the bits from start to end into an int, assume Big Endian
	 * start and end are included, so 0-2 means bits 0,1,2
	 */
	public int toIntRange(int start, int end) {
		int value = 0;
		int sz = end - start;
		if (sz <= 0) {
			return value;
		}
		for (int i = 0; i<=sz; ++i) {
			if (get(start+(sz-i))) {
				value += (1 << i);
			}
		}
		return value;
	}
	
	/**
	 * Convert the bits from start to end into an int, assume Big Endian
	 * start and end are included, so 0-2 means bits 0,1,2
	 */
	public long toLongRange(int start, int end) {
		long value = 0;
		int sz = end - start;
		if (sz <= 0) {
			return value;
		}
		for (int i = 0; i<=sz; ++i) {
			if (get(start+(sz-i))) {
				value += (1 << i);
			}
		}
		return value;
	}
	
	public long toLong() {
		return toLongRange(0, length()-1);
	}
		
	public int length() {
		if (realLength == -1) {
			return super.length();
		} else {
			return realLength;
		}
	}
	
	public void setRealLength(int len) {
		this.realLength = len;
	}
}
