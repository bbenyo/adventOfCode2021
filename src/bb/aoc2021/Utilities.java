package bb.aoc2021;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

public class Utilities {

	static private Logger logger = Logger.getLogger(Utilities.class.getName());

	private Utilities() {}
	
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
	
	public static int median(int[] list) {
		Arrays.sort(list);
		if (list.length % 2 == 1) {
			int medIndex = (int)Math.floor(list.length / 2);
			return list[medIndex];
		} else {
			int m1 = list.length / 2;
			int m2 = m1 - 1;
			int v1 = list[m1];
			int v2 = list[m2];
			return (int)Math.floor((v1 + v2)/2);
		}
	}

	/**
	 * True if all characters in the set are present in the string
	 * @param input
	 * @param chars
	 * @return
	 */
	static public boolean matchesRight(String input, Set<Character> chars) {
		for (Character c : chars) {
			if (input.indexOf(c) == -1) {
				return false;
			}
		}
		return true;
	}

	/**
	 * True if all characters in the string are present in the set
	 * @param input
	 * @param chars
	 * @return
	 */
	static public boolean matchesLeft(String input, Set<Character> chars) {
		for (int i=0; i<input.length(); ++i) {
			char c = input.charAt(i);
			if (!chars.contains(c)) {
				return false;
			}
		}
		return true;
	}
	
	static public Set<Character> stringToCharList(String s) {
		Set<Character> chars = new HashSet<Character>();
		for (int i=0; i<s.length(); ++i) {
			chars.add(s.charAt(i));
		}
		return chars;
	}
	
}
