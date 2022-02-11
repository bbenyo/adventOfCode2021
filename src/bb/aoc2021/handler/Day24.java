package bb.aoc2021.handler;

import java.util.ArrayList;
import java.util.List;

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
				logger.error("Invalid opcode: INP with no inputs!");
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
				logger.error("DIV BY 0: "+toString());
				m.setErrorCondition();
				return;
			}
			float f1 = o1 / o2;
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
				logger.error("MOD NEG value: "+toString());
				m.setErrorCondition();
				return;
			}
			if (o2 <= 0) {
				logger.error("MOD NEG0 second: "+toString());
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
			for (Instruction i : program) {
				logger.info("Executing "+i);
				i.execute(this);
				if (verbose) {
					logger.info("\t"+this.toString());
				}
				pc++;
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

	@Override
	public void output() {
		// Execute the program
		MONAD m1 = new MONAD();
		m1.addInput(9);
		m1.addInput(8);
		m1.addInput(7);
		m1.addInput(6);
		m1.addInput(5);
		m1.addInput(4);
		m1.addInput(3);
		m1.addInput(2);
		m1.addInput(1);
		m1.addInput(1);
		m1.addInput(1);
		m1.addInput(1);
		m1.addInput(1);
		m1.addInput(1);
		m1.execute(program, true);
		logger.info("FINAL Output: "+m1);
	}

}
