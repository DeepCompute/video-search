package info.hb.video.shrink.summary;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JFrame;

/**
 * The video player.
 * Used some skeleton code given by the TAs as a basis for this class.
 * @author Christopher Mangus
 * @author Louis Schwartz
 *
 */
public class imageReader implements Runnable {

	/**
	 * Instantiates the play method.
	 */
	@Override
	public void run() {
		play();
	}

	/**
	 * Constructor for imageReader
	 * @param fileName The raw RGB input file name
	 * @param pSound The PlaySound object, used to get the sample rate and current audio position
	 */
	public imageReader(String fileName, PlaySound pSound) {
		this.fileName = fileName;
		this.playSound = pSound;
	}

	/**
	 * Plays the video file to a JFrame.
	 */
	private void play() {

		// Used to output frame number for debugging
		frameCounter = 0;

		img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

		try {
			File file = new File(fileName);
			is = new FileInputStream(file);

			long len = WIDTH * HEIGHT * 3;
			long numFrames = file.length() / len;

			JFrame frame = new JFrame();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setTitle("Wumpi Player");
			frame.setSize(WIDTH, HEIGHT + 22);

			bytes = new byte[(int) len];

			imageReaderComponent component = new imageReaderComponent();

			// audio Samples Per video Frame
			double spf = playSound.getSampleRate() / FPS;

			// Video Frame offsets to sync audio and video
			int offset = 5; // only seems to work for Sample 2

			// Audio ahead of video, roll video forward to catch up
			int j = 0;

			while (j < Math.round(playSound.getPosition() / spf)) {
				readBytes();
				component.setImg(img);
				frame.add(component);
				frame.repaint();
				frame.setVisible(true);
				j++;
			}

			// Video ahead of audio, wait for audio to catch up
			while (j > Math.round(offset + playSound.getPosition() / spf)) {
				// Do Nothing
			}

			for (int i = j; i < numFrames; i++) {
				// Video ahead of audio, wait for audio to catch up
				while (i > Math.round(offset + playSound.getPosition() / spf)) {
					// Do Nothing
				}

				// Audio ahead of video, roll video forward to catch up
				while (i < Math.round(playSound.getPosition() / spf)) {
					readBytes();
					component.setImg(img);
					frame.add(component);
					frame.repaint();
					frame.setVisible(true);
					i++;
				}
				readBytes();
				component.setImg(img);
				frame.add(component);
				frame.repaint();
				frame.setVisible(true);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Reads in the bytes of raw RGB data for a frame.
	 */
	private void readBytes() {
		frameCounter++;

		// Prints frame number every second if necessary.
		/*
		if(frameCounter%32==0) {
		    System.out.println(frameCounter);
		}
		*/

		try {
			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
				offset += numRead;
			}
			int ind = 0;
			for (int y = 0; y < HEIGHT; y++) {
				for (int x = 0; x < WIDTH; x++) {
					byte r = bytes[ind];
					byte g = bytes[ind + HEIGHT * WIDTH];
					byte b = bytes[ind + HEIGHT * WIDTH * 2];

					int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
					img.setRGB(x, y, pix);
					ind++;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	private long frameCounter;
	private PlaySound playSound;
	private String fileName;
	private final int WIDTH = 320;
	private final int HEIGHT = 240;
	//private final double FPS = 23.976; // Frames Per Second
	private final double FPS = 24; // Frames Per Second
	private InputStream is;
	private BufferedImage img;
	private byte[] bytes;

}
