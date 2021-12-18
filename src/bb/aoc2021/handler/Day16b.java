package bb.aoc2021.handler;

import org.apache.log4j.Logger;

import bb.aoc2021.UsefulBitSet;
import bb.aoc2021.packet16.Packet;

public class Day16b extends Day16 {
	
	static private Logger logger = Logger.getLogger(Day16.class.getName());
	
	@Override
	public void handleInput(String line) {
		UsefulBitSet packetBits = UsefulBitSet.hexToBits(line);
		logger.info(packetBits);
		Packet p1 = Packet.parsePacket(packetBits);
		logger.info("Packet Value: "+p1.getValue());	
	}
}
