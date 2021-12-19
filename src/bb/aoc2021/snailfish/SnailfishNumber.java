package bb.aoc2021.snailfish;

import org.apache.log4j.Logger;

public class SnailfishNumber extends SnailfishElement {
	static private Logger logger = Logger.getLogger(SnailfishNumber.class.getName());
	
	SnailfishElement left;
	SnailfishElement right;
	
	public SnailfishNumber(SnailfishNumber parent) {
		super(parent);
	}

	public SnailfishElement getLeft() {
		return left;
	}

	public void setLeft(SnailfishElement left) {
		this.left = left;
		left.setParent(this);
	}

	public SnailfishElement getRight() {
		return right;
	}

	public void setRight(SnailfishElement right) {
		this.right = right;
		right.setParent(this);
	}

	public SnailfishLiteral getRightmostLiteral() {
		return right.getRightmostLiteral();
	}

	public SnailfishLiteral getLeftmostLiteral() {
		return left.getLeftmostLiteral();
	}
	
	@Override
	public String toString() {
		return "[" + left.toString()+","+right.toString()+"]";
	}
	
	protected SnailfishElement clone() {
		SnailfishNumber c1 = new SnailfishNumber(null);
		SnailfishElement l1 = left.clone();
		c1.setLeft(l1);
		SnailfishElement r1 = right.clone();
		c1.setRight(r1);
		return c1;
	}
	
	public SnailfishNumber add(SnailfishNumber other) {
		try {
			SnailfishNumber addition = new SnailfishNumber(null);
			SnailfishElement lClone = (SnailfishElement)this.clone();
			addition.setLeft(lClone);
			SnailfishElement rClone = (SnailfishElement)other.clone();
			addition.setRight(rClone);
			addition.reduce();
			logger.info("Addition result: "+addition);
			return addition;
		} catch (Exception ex) {
			logger.error(ex.toString(), ex);
			return null;
		}
	}
	
	public void reduce() {
		logger.info("Reducing "+this);
		while (true) {
			if (tryExplode(0)) {
				logger.info("After explode: "+this);
				continue;
			} else if (trySplit()) {
				logger.info("After split: "+this);
				continue;
			}
			return;
		}
	}
	
	/**
	 * If any pair is nested inside four pairs, the leftmost such pair explodes.
	 */
	public boolean tryExplode(int level) {
		if (level >= 4 && left instanceof SnailfishLiteral && right instanceof SnailfishLiteral) {
			// Nested inside 4 pairs, explode
			explode();
			return true;
		} else {
			if (left.tryExplode(level + 1)) {
				return true;
			}
			if (right.tryExplode(level + 1)) {
				return true;
			}			
		}
		return false;
	}
	
	/**
	 * To explode a pair, the pair's left value is added to the first regular number to the left of the exploding pair (if any), 
	 * and the pair's right value is added to the first regular number to the right of the exploding pair (if any). 
	 * Exploding pairs will always consist of two regular numbers. 
	 * Then, the entire exploding pair is replaced with the regular number 0.
	 */
	public void explode() {
		logger.info("Exploding: "+toString());
		parent.explodeLeft(this, ((SnailfishLiteral)left).getValue());
		parent.explodeRight(this, ((SnailfishLiteral)right).getValue());
		parent.replace(this, new SnailfishLiteral(0));
	}

	protected void explodeLeft(SnailfishNumber exploder, long val) {
		logger.info("Exploding left: "+val);
		
		// If we exploded up from the right, try the left
		if (right.equals(exploder)) {
			// Try the left
			if (left instanceof SnailfishLiteral) {
				SnailfishLiteral lLeft = (SnailfishLiteral)left;
				lLeft.value += val;
				logger.info("Exploded "+val+" at "+this);
			} else {
				SnailfishNumber nLeft = (SnailfishNumber)left;
				SnailfishLiteral rMost = nLeft.getRightmostLiteral();
				rMost.value += val;
				logger.info("Exploded "+val+" at "+this);				
			}
		} else if (parent != null) {  // we exploded up from the left, try the parent
			parent.explodeLeft(this, val);
		}
	}
	
	protected void explodeRight(SnailfishNumber exploder, long val) {
		logger.info("Exploding right: "+val);
		if (left.equals(exploder)) {
			// we exploded up from the left, try the right
			if (right instanceof SnailfishLiteral) {
				SnailfishLiteral lRight = (SnailfishLiteral)right;
				lRight.value += val;
				logger.info("Exploded "+val+" at "+this);
			} else {
				SnailfishNumber nRight = (SnailfishNumber)right;
				SnailfishLiteral lMost = nRight.getLeftmostLiteral();
				lMost.value += val;
				logger.info("Exploded "+val+" at "+this);							
			}
		} else if (parent != null) {
			parent.explodeRight(this, val);
		}
	}
	
	public void replace(SnailfishElement kid, SnailfishElement newKid) {
		if (left.equals(kid)) {
			setLeft(newKid);
		} else if (right.equals(kid)) {
			setRight(newKid);			
		} else {
			logger.error("Trying to replace an orphan: "+kid+" left: "+left+" right: "+right);
		}
	}
	
	/**
	 * If any regular number is 10 or greater, the leftmost such regular number splits.
	 * @return
	 */
	public boolean trySplit() {
		if (left.trySplit()) {
			return true;
		}
		if (right.trySplit()) {
			return true;
		}
		return false;
	}
	
	public long getMagnitude() {
		return (3 * left.getMagnitude()) + (2 * right.getMagnitude());
	}
}
