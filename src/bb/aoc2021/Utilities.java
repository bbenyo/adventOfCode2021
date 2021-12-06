package bb.aoc2021;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class Utilities {

	static private Logger logger = Logger.getLogger(Utilities.class.getName());
	/** 
	 * String representing a bit value (e.g. 01101) to the long it represents
	 * @param s
	 * @return
	 */
	static public long bitStringToLong(String s) {
		long val = 0;
		for (int i=0; i<s.length(); ++i) {
			int valPos = s.length() - i - 1;
			if (s.charAt(i) == '1') {
				val += Math.pow(2, valPos);
			}
		}
		return val;
	}
	
	static public int[] stringListToInts(String line, String sep) {
		String[] split = line.split(sep);
		List<Integer> ints = new ArrayList<Integer>();
		for (String s : split) {
			if (s.trim().length() == 0) {
				continue;
			}
			try {
				int i = Integer.parseInt(s.trim());
				ints.add(i);
			} catch (NumberFormatException ex) {
				logger.error(ex.toString(), ex);
			}
		}
		int[] iArray = ints.stream().mapToInt(i -> i).toArray();
		return iArray;
	}
}
