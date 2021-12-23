package bb.aoc2021.handler;

import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

public class Day21b extends Day21 {
	static private Logger logger = Logger.getLogger(Day21b.class.getName());
	// Number of universes created when we roll the dice 3x
	//   There are 27 different possibilities (3^3), but only 7 different states of the world
	//   sum: 3, 4, 5, 6, 7, 8, 9
	//   Each state has a different number of universes, only 1 way to get a 3, more ways to get a 7
	Map<Integer, Integer> diceWorlds = new TreeMap<>();
	
	public Day21b() {
		diceWorlds.put(3, 1); // 1,1,1
		diceWorlds.put(4, 3); // 1,1,2, 1,2,1, 2,1,1
		diceWorlds.put(5, 6); // 1,2,2, 2,1,2, 2,2,1, 3,1,1, 1,3,1, 1,1,3
		diceWorlds.put(6, 7); // 2,2,2, 1,3,2, 1,2,3, 3,1,2, 3,2,1, 2,1,3, 2,3,1
		diceWorlds.put(7, 6); // 2,2,3, 2,3,2, 3,2,2, 3,3,1, 3,1,3, 1,3,3
		diceWorlds.put(8, 3); // 3,3,2, 3,2,3, 2,3,3
		diceWorlds.put(9, 1); // 3,3,3		
	}
		
	// Assume 2 players
	protected Player computeGameUniverses(Player p, long curUniverses, int curRound) {
		// p's turn
		// For rolling 3x gets us 27 possible universes, but only 7 different states

		Player p1 = players.get(0);
		Player p2 = players.get(1);
		long p1OrigScore = p1.score;
		int p1OrigPos = p1.pos;
		long p2OrigScore = p2.score;
		int p2OrigPos = p2.pos;
		for (int state : diceWorlds.keySet()) {
			// Reset players
			p1.pos = p1OrigPos;
			p1.score = p1OrigScore;
			p2.pos = p2OrigPos;
			p2.score = p2OrigScore;
			
			int nextPos = ((p.pos + state) % maxBoard);
			if (nextPos == 0) {
				nextPos = 10;
			}
			p.score += nextPos;
			p.pos = nextPos;
			
			// For each universe that got us here, we spawn diceWorlds more universes
			long totalUniverses = diceWorlds.get(state) * curUniverses;
						
			// logger.info(curRound+": Player "+p+" rolls "+state+" and moves to space "+nextPos+" for a total score of "+p.score+" in "+totalUniverses+" universes");
			if (p.score >= 21) {
				p.winningUniverses += totalUniverses;
				// logger.info("Winning universes for "+p.name+": "+p.winningUniverses);
			} else {
				Player nextPlayer = p1;
				if (p.equals(p1)) {
					nextPlayer = p2;
				}
				computeGameUniverses(nextPlayer, totalUniverses, curRound+1);
			}
		}
		return null;	
	}
	
	@Override
	public void output() {
		Player p1 = players.get(0);
		Player p2 = players.get(1);
		computeGameUniverses(p1, 1, 1);
		
		logger.info(p1.name+" won in "+p1.winningUniverses+" universes");
		logger.info(p2.name+" won in "+p2.winningUniverses+" universes");
		
		if (p1.winningUniverses > p2.winningUniverses) {
			logger.info("P1 won more: "+p1.winningUniverses);
		} else {
			logger.info("P2 won more: "+p2.winningUniverses);
		}
		
	}
}
