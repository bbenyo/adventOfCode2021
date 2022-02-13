package bb.aoc2021.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import bb.aoc2021.InputHandler;

public class Day24 implements InputHandler {
	static private Logger logger = Logger.getLogger(Day24.class.getName());
	
	public enum OpCode {INP, ADD, MUL, DIV, MOD, EQL};
	
	abstract class Instruction {
		OpCode opcode;
		String op1;
		String op2;
		
		abstract public void execute(MONAD m);

		public String toString() {
			return opcode + " " + op1 + " " +op2;
		}
	}
	
	class Inp extends Instruction {
		public Inp(String o1) {
			opcode = OpCode.INP;
			op1 = o1;
		}

		@Override
		public void execute(MONAD m) {
			if (m.inputs.isEmpty()) {
				if (m.verbose) logger.error("Invalid opcode: INP with no inputs!");
				m.setErrorCondition();
				return;
			}
			Integer inp = m.inputs.remove(0);
			m.setRegister(op1, inp);
		}

		@Override
		public String toString() {
			return opcode + " " + op1;
		}
	}
	
	class Add extends Instruction {
		public Add(String o1, String o2) {
			opcode = OpCode.ADD;
			op1 = o1;
			op2 = o2;
		}

		@Override
		public void execute(MONAD m) {			
			Integer o1 = m.getValue(op1);
			Integer o2 = m.getValue(op2);
			m.setRegister(op1, o1 + o2);
		}
	}
	
	class Mul extends Instruction {
		public Mul(String o1, String o2) {
			opcode = OpCode.MUL;
			op1 = o1;
			op2 = o2;
		}

		@Override
		public void execute(MONAD m) {			
			Integer o1 = m.getValue(op1);
			Integer o2 = m.getValue(op2);
			m.setRegister(op1, o1 * o2);
		}
	}
	
	class Div extends Instruction {
		public Div(String o1, String o2) {
			opcode = OpCode.DIV;
			op1 = o1;
			op2 = o2;
		}

		@Override
		public void execute(MONAD m) {			
			Integer o1 = m.getValue(op1);
			Integer o2 = m.getValue(op2);
			if (o2 == 0) {
				if (m.verbose) logger.error("DIV BY 0: "+toString());
				m.setErrorCondition();
				return;
			}
			float f1 = (float)o1 / (float)o2;
			Integer val = (int)Math.floor(f1);
			m.setRegister(op1, val);
		}
	}
	
	class Mod extends Instruction {
		public Mod(String o1, String o2) {
			opcode = OpCode.MOD;
			op1 = o1;
			op2 = o2;
		}

		@Override
		public void execute(MONAD m) {			
			Integer o1 = m.getValue(op1);
			Integer o2 = m.getValue(op2);
			if (o1 < 0) {
				if (m.verbose) logger.error("MOD NEG value: "+toString());
				m.setErrorCondition();
				return;
			}
			if (o2 <= 0) {
				if (m.verbose) logger.error("MOD NEG0 second: "+toString());
				m.setErrorCondition();
				return;
			}
			m.setRegister(op1, o1 % o2);
		}
	}
	
	class Eql extends Instruction {
		public Eql(String o1, String o2) {
			opcode = OpCode.EQL;
			op1 = o1;
			op2 = o2;
		}

		@Override
		public void execute(MONAD m) {			
			Integer o1 = m.getValue(op1);
			Integer o2 = m.getValue(op2);
			if (o1.equals(o2)) {
				m.setRegister(op1, 1);
			} else {
				m.setRegister(op1, 0);
			}
		}
	}

	class MONAD {
		int w;
		int x;
		int y;
		int z;
		
		int pc; // Program counter, how many instructions we've executed
		
		List<Integer> inputs;
		
		boolean error = false;
		boolean verbose = false;
		
		int inputsProcessed = 0;
		int startAtInput = 0;
		int stopAfterInputs = 7;
		
		public MONAD() {
			w = 0;
			x = 0;
			y = 0;
			z = 0;
			
			pc = 0;
			
			inputs = new ArrayList<>();
		}
		
		public String toString() {
			StringBuffer sb = new StringBuffer(pc+": ");
			if (error) {
				sb.append("*ERROR* ");
			}
			sb.append("W = "+w);
			sb.append(" X = "+x);
			sb.append(" Y = "+y);
			sb.append(" Z = "+z);
			sb.append(" Input size: "+inputs.size());
			return sb.toString();
		}
		
		public void addInput(Integer inp) {
			inputs.add(inp);
		}
		
		public void setInputs(List<Integer> is) {
			inputs.clear();
			for (Integer i : is) {
				inputs.add(i);
			}
		}
		
		public void setErrorCondition() {
			error = true;
		}
		
		public void setRegister(String reg, Integer v) {
			switch (reg) {
			case "w" :
			case "W" :
				w = v;
				break;

			case "x" :
			case "X" :
				x = v;
				break;

			case "y" :
			case "Y" :
				y = v;
				break;

			case "z" :
			case "Z" :
				z = v;
				break;
				
			default:
				logger.error("Unknown register: "+reg);
				error = true;
			}			
		}
		
		public Integer getValue(String val) {
			switch (val) {
			case "w" :
			case "W" :
				return w;

			case "x" :
			case "X" :
				return x;

			case "y" :
			case "Y" :
				return y;

			case "z" :
			case "Z" :
				return z;
			default:
				try {
					return Integer.parseInt(val);
				} catch (NumberFormatException ex) {
					ex.printStackTrace();
					error = true;
				}
			}
			return 0;
		}
		
		public void execute(List<Instruction> program, boolean verbose) {
			pc = 0;
			this.verbose = verbose;
			for (Instruction i : program) {
				if (i.opcode == OpCode.INP) {
					inputsProcessed++;
				}
				if (inputsProcessed < startAtInput) {
					continue;
				}
				i.execute(this);
				if (verbose) {
					logger.info("Executing "+i);
					logger.info("\t"+this.toString());
				}
				pc++;
				if (stopAfterInputs > 0 && inputsProcessed >= stopAfterInputs) {
					return;
				}
			}
		}
	}
	
	List<Instruction> program = new ArrayList<>();
		
	@Override
	public void handleInput(String line) {
		if (line.length() == 0) {
			return;
		}
		String[] args = line.split(" ");
		String opcode = args[0];
		try {
			OpCode opc = OpCode.valueOf(opcode.toUpperCase());
			String op1 = null;
			String op2 = null;
			if (args.length > 1) {
				op1 = args[1];
			}
			if (args.length > 2) {
				op2 = args[2];
			}
			Instruction i = null;
			switch (opc) {
			case ADD:
				i = new Add(op1, op2);
				break;
			case DIV:
				i = new Div(op1, op2);
				break;
			case EQL:
				i = new Eql(op1, op2);
				break;
			case INP:
				i = new Inp(op1);
				break;
			case MOD:
				i = new Mod(op1, op2);
				break;
			case MUL:
				i = new Mul(op1, op2);
				break;
			default:
				logger.error("Unrecognized opcode: "+opcode);
				break;			
			}
			program.add(i);
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
		}
	}
	
	public boolean nextInput(List<Integer> inputs, int endAt) {
		for (int i=inputs.size() - 1; i >= endAt; --i) {
			Integer i1 = inputs.get(i);
			i1--;
			if (i1 > 0) {
				inputs.set(i, i1);
				return false;
			} else {
				inputs.set(i, 9);
			}
		}
		return true;
	}
	

	// Create a lookup table, if Z = key, then the maximum number for the remainder 
	//    of the input digits = value.
	
	/**
	 * The insight here is that the program consists of 14 chunks that are almost the same
	 * and the only thing that carries over from one chunk to the next is z.
	 *   W is immediately overwritten by the next input
	 *   X and Y are multiplied by 0 before being used, so their values don't carry over.
	 * Thus the only state that carries from chunk to chunk is z.
	 * So we start at the end, the 14th input, and try all z values and see if we can get any
	 *   valid input (final z = 0 after the last chunk).
	 * If we find one, then we put that input value in the table for z
	 * Then when we're running the program, we don't need to run the last chunk, we just
	 * lookup the current value of z in our table, and skip to the end.
	 * 
	 * We can then iterate, and create the table for the second last, etc.
	 * Since we try inputs largest to smallest, we just don't overwrite if we already have
	 *  an entry in the table.
	 **/
	
	// We'll try by only considering this many possible z values, if this doesn't work, we can increase the search area
	int maxZ = 10000;
	
	public Map<Integer, Long> createLookupTable(int i, Map<Integer, Long> fTable) {
		Map<Integer, Long> zInputs = new HashMap<>();
		for (int z=0; z<maxZ; ++z) {
			for (int w=1; w<=9; ++w) {
				MONAD m1 = new MONAD();
				m1.addInput(w);
				// Run the program from input i to input i+1
				m1.startAtInput = i;
				m1.stopAfterInputs = i+1;
				// To run the program starting at input i, all we need to know for state is what z should b
				//  Nothing else carries over, and we're looping through all possible z values
				m1.z = z;
				m1.execute(program, false);
				if (fTable == null) {
					// This is for the last digit, we want z to be 0 for a valid number
					if (m1.z == 0 && !zInputs.containsKey(z)) {
						zInputs.put(z, (long)w);
					}					
				} else {
					Long rest = fTable.get(m1.z);
					if (rest != null) {
						// We have an entry for this z that we ended up with.
						//  This means we know that the rest of the digits should be this value in the table
						//  to get a valid number, and this is the largest "rest of the digits"
						String r1 = String.valueOf(w)+rest.toString();
						Long r2 = Long.parseLong(r1);
						zInputs.put(z, r2);
					} 
				}
			}
		}
		
		// This means that after we handle input # i, we look at the value of z
		//  And can look it up in our table zInputs.
		//  If there's an entry in the table, that number is the largest value for the 
		//  remainder of the inputs to get a valid input (final z = 0)
		//  If there's no entry, then we can't get a valid number from here.
		return zInputs;
	}
	
	@Override
	public void output() {
		Map<Integer, Long> zTable = null;
		for (int i=14; i>=1; --i) {
			zTable = createLookupTable(i, zTable);
			logger.info(zTable);
		}
		// Since z starts at 0, if we have a value for 0 in our first table, that's it!
		if (zTable != null) {
			logger.info("Max valid value: "+zTable.get(0));
		}
		
		
	}

}
