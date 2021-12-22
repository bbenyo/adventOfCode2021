package bb.aoc2021.dirac;

abstract public class Dice {
	
	int rollCount = 0;
	
	abstract public int sides();
	
	public int roll() {
		rollCount++;
		return 0;
	}
	
	public int getRollCount() {
		return rollCount;
	}

}
