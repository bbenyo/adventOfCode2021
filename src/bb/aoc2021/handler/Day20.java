package bb.aoc2021.handler;

import org.apache.log4j.Logger;

import bb.aoc2021.BitImage;
import bb.aoc2021.InputHandler;
import bb.aoc2021.UsefulBitSet;

public class Day20 implements InputHandler {
	static private Logger logger = Logger.getLogger(Day20.class.getName());
	
	UsefulBitSet imageEnhancementAlgorithm = new UsefulBitSet(512);
	int curAlgSize = 0;
	boolean doneWithIEA = false;
	
	BitImage image = new BitImage();
	BitImage enhanced = null;
	
	@Override
	public void handleInput(String line) {
		if (line.trim().length() == 0) {
			doneWithIEA = true;
			if (imageEnhancementAlgorithm.length() < 512) {
				logger.warn("Image Enhacement Algorithm is too small: "+imageEnhancementAlgorithm.length());
			}
			return;
		}
		if (!doneWithIEA) {
			parseAlgorithm(line.trim());
		} else {
			parseInputImage(line.trim());
		}
	}
	
	protected void parseAlgorithm(String line) {
		for (int i=0; i < line.length(); ++i) {
			char c = line.charAt(i);
			if (c == '#') {
				imageEnhancementAlgorithm.set(curAlgSize + i);
			}
		}
		curAlgSize += line.length();		
	}
	
	protected void parseInputImage(String line) {
		image.readLine(line);
	}
	
	protected BitImage enhance() {
		// Extend the image by 1 row at the top/bottom, and 1 col at the right/left at most
		//   Anything further out is all dark pixels, no need to store anything
		image.extendImage();
		enhanced = new BitImage(image.getMaxRowLen(), image.getRowCount());
		enhanced.infiniteOn = image.infiniteOn;
		for (int i=0; i<enhanced.getMaxRowLen(); ++i) {
			for (int j=0; j<enhanced.getRowCount(); ++j) {
				int ieaIndex = image.getEncoding3x3(i, j);
				if (ieaIndex < 0 || ieaIndex > 512) {
					logger.error("Invalid image enhancement index: "+ieaIndex);
				}
				if (imageEnhancementAlgorithm.get(ieaIndex)) {
					enhanced.set(i, j);
				}
			}
		}
		if (imageEnhancementAlgorithm.get(0) && !image.infiniteOn) {
			enhanced.infiniteOn = true;
		} 
		if (!imageEnhancementAlgorithm.get(511) && image.infiniteOn) {
			enhanced.infiniteOn = false;
		}
		return enhanced;
	}

	@Override
	public void output() {
		logger.info("Input image: "+System.lineSeparator()+image);
		logger.info("Pixels On: "+image.getOnPixels());
		image = this.enhance();
		logger.info("Input image, enhanced once: "+System.lineSeparator()+image);
		logger.info("Pixels On: "+image.getOnPixels());
		image = this.enhance();
		logger.info("Input image, enhanced twice: "+System.lineSeparator()+image);
		logger.info("On Pixels: "+image.getOnPixels());
	}

}
