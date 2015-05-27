package info.hb.video.shrink.videoplay;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class PlayAudioVideo {

	public static void main(String[] args) throws IOException, PlayVideoException, InterruptedException {
		Buffer buf = new Buffer();
		String audiofileName = args[1];
		String videofileName = args[0];

		//System.out.println(audiofileName);

		Thread playWaveFile = new PlayWaveFile(audiofileName, buf);

		Thread playVideoFile = new PlayVideoFile(videofileName, buf);

		// playWaveFile.start();

		// create new threads

		// starting threads
		playWaveFile.start();
		playVideoFile.start();

		// Wait for the threads to finish
		try {
			playWaveFile.join();
			playVideoFile.join();
		} catch (InterruptedException e) {
			return;
		}
	}

}

class Buffer {

	//	private int video = 0, audio = 0;
	private boolean empty = true;
	PlayVideoFile p = new PlayVideoFile();

	public synchronized void put(byte[] audioBuffer, int readBytes, SourceDataLine dataLine)
			throws InterruptedException {
		while (empty == false) { //wait till the buffer becomes empty
			try {
				wait();
			} catch (InterruptedException e) {
				throw e;
			}
		}

		empty = false;
		if (readBytes >= 0) {
			dataLine.write(audioBuffer, 0, readBytes);
			// System.out.println("in the audio :"+c++);
		}
		//System.out.println("Audio no:" + (++audio));
		notify();
	}

	public synchronized void get(byte[] bytes, BufferedInputStream bufferedVideoStream, int WIDTH, int HEIGHT,
			JFrame outFrame, BufferedImage outImg) throws InterruptedException {
		while (empty == true) { //wait till something appears in the buffer
			try {
				wait();
			} catch (InterruptedException e) {
				throw e;
			}
		}
		empty = true;
		try {
			bufferedVideoStream.read(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}

		double rAtPixel[][] = new double[WIDTH][HEIGHT];
		double gAtPixel[][] = new double[WIDTH][HEIGHT];
		double bAtPixel[][] = new double[WIDTH][HEIGHT];

		int ind = 0;
		for (int y = 0; y < HEIGHT; y++) {

			for (int x = 0; x < WIDTH; x++) {

				//				byte a = 0;//This is opacity value
				byte r = bytes[ind];
				byte g = bytes[ind + HEIGHT * WIDTH];
				byte b = bytes[ind + HEIGHT * WIDTH * 2];

				rAtPixel[x][y] = r & 0xff;
				gAtPixel[x][y] = g & 0xff;
				bAtPixel[x][y] = b & 0xff;

				int pix = (0xff000000) | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);//Set transparency factor as zero

				outImg.setRGB(x, y, pix);

				ind++;

			}
		}

		outFrame.repaint();
		outFrame.pack();
		//System.out.println("Video no:" + (++video));
		notify();
	}

}

class PlayWaveFile extends Thread {

	//	private int n;
	private Buffer prodBuf;
	private String audioFilename;
	//	private InputStream waveStream;

	private BufferedInputStream bufferedWaveStream;

	//private final int EXTERNAL_BUFFER_SIZE = 524288; // 128Kb//2400
	private final int EXTERNAL_BUFFER_SIZE = 1837;
	FileInputStream inputStream;

	public PlayWaveFile(String filename, Buffer buf) {
		this.audioFilename = filename;
		prodBuf = buf;
	}

	@Override
	public void run() {
		try {
			inputStream = new FileInputStream(this.audioFilename);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		bufferedWaveStream = new BufferedInputStream(inputStream);
		AudioInputStream audioInputStream = null;
		try {
			//audioInputStream = AudioSystem.getAudioInputStream(this.waveStream);
			audioInputStream = AudioSystem.getAudioInputStream(this.bufferedWaveStream);
		} catch (UnsupportedAudioFileException e1) {
			try {
				throw new PlayWaveException(e1);
			} catch (PlayWaveException e) {
				e.printStackTrace();
			}
		} catch (IOException e1) {
			try {
				throw new PlayWaveException(e1);
			} catch (PlayWaveException e) {
				e.printStackTrace();
			}
		}

		// Obtain the information about the AudioInputStream
		AudioFormat audioFormat = audioInputStream.getFormat();
		Info info = new Info(SourceDataLine.class, audioFormat);

		// opens the audio channel
		SourceDataLine dataLine = null;
		try {
			dataLine = (SourceDataLine) AudioSystem.getLine(info);
			dataLine.open(audioFormat, this.EXTERNAL_BUFFER_SIZE);
		} catch (LineUnavailableException e1) {
			try {
				throw new PlayWaveException(e1);
			} catch (PlayWaveException e) {
				e.printStackTrace();
			}
		}

		// Starts the music :P
		dataLine.start();

		int readBytes = 0;

		byte[] audioBuffer = new byte[this.EXTERNAL_BUFFER_SIZE];

		try {
			while (readBytes != -1) {

				readBytes = audioInputStream.read(audioBuffer, 0, audioBuffer.length);

				try {
					prodBuf.put(audioBuffer, readBytes, dataLine); //starting from 1, not 0
				} catch (InterruptedException e) {
					return;
				}

			}
		}

		catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			// plays what's left and and closes the audioChannel
			dataLine.drain();
			dataLine.close();
		}

	}

}

class PlayVideoFile extends Thread {

	private String videoFilename;
	private long fileLength;

	private static final int WIDTH = 320;
	private static final int HEIGHT = 240;
	private final long FRAMESIZE = WIDTH * HEIGHT * (24 / 8);//size of each frame in bytes

	private JFrame outFrame;
	private JLabel label;

	private BufferedImage outImg = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	//	private int n;
	private Buffer consBuf;

	public PlayVideoFile(String filename, Buffer buf) {
		consBuf = buf;
		this.videoFilename = filename;
		File file = new File(filename);
		this.fileLength = file.length();

		outFrame = new JFrame();
		outFrame.setVisible(true);

		label = new JLabel(new ImageIcon(outImg));

		outFrame.getContentPane().add(label, BorderLayout.CENTER);
		outFrame.pack();
		outFrame.setVisible(true);
	}

	public PlayVideoFile() {
		//
	}

	@Override
	public void run() {

		FileInputStream inputStream;
		try {
			inputStream = new FileInputStream(this.videoFilename);
			BufferedInputStream bufferedVideoStream = new BufferedInputStream(inputStream);

			int len = (int) FRAMESIZE;
			byte[] bytes = new byte[len];

			//FileInputStream;
			//System.out.println("this.fileLength "+this.fileLength);
			for (long stream = 0; stream < this.fileLength; stream += this.FRAMESIZE) {

				try {
					consBuf.get(bytes, bufferedVideoStream, WIDTH, HEIGHT, outFrame, outImg);

				} catch (InterruptedException e) {
					return;
				}

			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}

	}

}
