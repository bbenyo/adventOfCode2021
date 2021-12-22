package bb.aoc2021.dirac;

public class DeterministicDice extends Dice {

	int sides;
	int lastVal = 0;
	
	public DeterministicDice(int s) {
		this.sides = s;
	}
	
	@Override
	public int sides() {
		return sides;
	}

	@Override
	public int roll() {
		super.roll();
		lastVal++;
		if (lastVal > sides) {
			lastVal = 1;
		}
		return lastVal;
	}
	
}
