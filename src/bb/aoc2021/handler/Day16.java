package bb.aoc2021.handler;

import org.apache.log4j.Logger;

import bb.aoc2021.InputHandler;
import bb.aoc2021.UsefulBitSet;
import bb.aoc2021.packet16.Packet;

public class Day16 implements InputHandler {
	
	static private Logger logger = Logger.getLogger(Day16.class.getName());
	
	long versionCount = 0;
	
	@Override
	public void handleInput(String line) {
		versionCount = 0; // per packet version count
		UsefulBitSet packetBits = UsefulBitSet.hexToBits(line);
		logger.info(packetBits);
		
		Packet p1 = Packet.parsePacket(packetBits);
		versionCount += p1.versionCount();
	}

	@Override
	public void output() {
		logger.info("Version count: "+versionCount);
	}

}
