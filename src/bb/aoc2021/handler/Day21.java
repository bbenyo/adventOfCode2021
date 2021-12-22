package bb.aoc2021.handler;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import bb.aoc2021.InputHandler;
import bb.aoc2021.Utilities;
import bb.aoc2021.dirac.DeterministicDice;
import bb.aoc2021.dirac.Dice;

public class Day21 implements InputHandler {
	static private Logger logger = Logger.getLogger(Day21.class.getName());
	
	public class Player {
		int pos;
		long score;
		String name;
		
		boolean won = false;
				
		public Player(String name) {
			this.name = name;
		}
	}
	
	List<Player> players = new ArrayList<Player>();
	int round = 0;
	int minBoard = 1;
	int maxBoard = 10;
		
	@Override
	public void handleInput(String line) {
		String pName = Utilities.getStringBetween(line, "Player ", "starting", 0).trim();
		Player p = new Player(pName);
		
		int pPos = line.lastIndexOf(":");
		if (pPos > -1) {
			String pos = line.substring(pPos+1).trim();
			p.pos = Integer.parseInt(pos);
		} else {
			logger.error("Unable to parse: "+line);
		}
		
		players.add(p);
	}
	
	protected Player playRound(Dice dice) {
		round++;
		for (Player p : players) {
			int r1 = dice.roll();
			int r2 = dice.roll();
			int r3 = dice.roll();
			
			int roll = r1 + r2 + r3;
			int nextPos = ((p.pos + roll) % maxBoard);
			if (nextPos == 0) {
				nextPos = 10;
			}
			p.score += nextPos;
			p.pos = nextPos;
			
			logger.info("Player "+p.name+" rolls "+r1+"+"+r2+"+"+r3+" and moves to space "+nextPos+" for a total score of "+p.score);
			if (p.score >= 1000) {
				p.won = true;
				return p;
			}
		}
		return null;	
	}

	@Override
	public void output() {
		Dice dice = new DeterministicDice(100);
		Player won = null;
		while (won == null) {
			won = playRound(dice);
		}
		
		logger.info(won.name+" won on round "+round+" with a score of "+won.score);
		Player loser = null;
		for (Player p : players) {
			if (p.name.equals(won.name)) {
				continue;
			}
			loser = p;
			break;
		}
		logger.info(loser.name+" lost with a score of "+loser.score);
		logger.info("The dice was rolled "+dice.getRollCount());
		logger.info("Product: "+(loser.score * dice.getRollCount()));
		
	}

}
