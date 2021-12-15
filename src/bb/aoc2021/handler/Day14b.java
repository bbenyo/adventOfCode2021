package bb.aoc2021.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import bb.aoc2021.Utilities;

public class Day14b extends Day14 {

	// Well that's not going to work, 40 steps will be way too big
	/** We don't need to construct the string, just count the pairs
	 * NNCB -> NN (1) NC (1) CB (1)
	 * To replace, we look for a rule for each pair
	 * NN (1) -> rule NN -> C -> NC (1), CN (1), NN (-1)
	 *   i.e. we're replacing 1 NN with a NC and a CN
	 * If the polymer has 100 NNs, the new polymer will have 100 NCs and 100 CNs (and 0 NNs)
	 * etc.
	 * 
	 * Then we can count the letters in the pairs as before
	 ***/
	
	public Day14b() {
		super();
		steps = 40;
		verbose = false;
	}
	
	Map<String, Long> pairCounts = new HashMap<String, Long>();
	
	protected void initializePairCounts(String polymer) {
		// Add polymer pairs to the base count
		for (int i=0; i<polymer.length() - 1; ++i) {
			String currentPair = polymer.substring(i, i+2);
			Long l1 = pairCounts.get(currentPair);
			if (l1 == null) {
				l1 = 0L;
			}
			pairCounts.put(currentPair, l1+1);
		}
	}
	
	protected void updateCount(String pair, Long c, Map<String, Long> counts) {
		Long p2Count = counts.get(pair);
		if (p2Count == null) {
			p2Count = 0L;
		}
		p2Count += c;
		counts.put(pair, p2Count);
	}
		
	@Override
	protected String growPolymer(String polymer) {
		Map<String, Long> newCounts = new HashMap<String, Long>();
		for (Entry<String, Long> entry : pairCounts.entrySet()) {
			String pair = entry.getKey();
			Long count = entry.getValue();
			
			String replaceElement = rules.get(pair);
			if (replaceElement == null) {
				// Nothing to do, keep the count the same
				updateCount(pair, count, newCounts);
			} else {
				// Add 2 pairs instead
				String p1 = pair.charAt(0) + replaceElement;
				String p2 = replaceElement + pair.charAt(1);
				
				updateCount(p1, count, newCounts);
				updateCount(p2, count, newCounts);				
			}
		}
		
		pairCounts = newCounts;
		return "Polymer stored in pairCounts";
	}
	
	@Override
	public void output() {
		initializePairCounts(polymer);
		for (int i=0; i<steps; ++i) {
			growPolymer("");
		}
		
		Map<Character, Long> counts = new HashMap<>();
		for (Entry<String, Long> entry : pairCounts.entrySet()) {
			String pair = entry.getKey();
			Long count = entry.getValue();
			Character c1 = pair.charAt(0);
			Long c1count = counts.get(c1);
			if (c1count == null) {
				c1count = 0L;
			}
			c1count += count;
			counts.put(c1, c1count);
			
			Character c2 = pair.charAt(1);
			Long c2count = counts.get(c2);
			if (c2count == null) {
				c2count = 0L;
			}
			c2count += count;
			counts.put(c2, c2count);
		}
		
		// We counted each letter twice, so half them
		// Except for the first and last letter
		
		// The first and last letter will always be the same as in the initial polymer
		Character first = polymer.charAt(0);
		Long fCount = counts.get(first);
		counts.put(first, fCount+1); // We're going to divide by 2, so give an additional count here for the first/last
		Character last = polymer.charAt(polymer.length() - 1);
		Long lCount = counts.get(last);
		counts.put(last, lCount+1); // We're going to divide by 2, so give an additional count here for the first/last
		
		Map<Character, Long> correctCounts = new HashMap<>();
		for (Character c : counts.keySet()) {
			correctCounts.put(c, counts.get(c) / 2);
		}
			
		countLargestSmallest(correctCounts);
	}
}
