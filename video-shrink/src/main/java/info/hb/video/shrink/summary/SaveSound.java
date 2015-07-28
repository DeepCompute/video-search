package info.hb.video.shrink.summary;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SaveSound {

	private InputStream waveStream;
	private String inputfile;

	private BufferedInputStream bufferedWaveStream;

	//private final int EXTERNAL_BUFFER_SIZE = 524288; // 128Kb
	private final int EXTERNAL_BUFFER_SIZE = 524288;

	private static File audioOut;
	private static final double AUDIO_PER_FRAME = 22050 / 24;

	private static final double AUDIO_BYTES_PER_SAMPLE = 2;

	//private InputStream waveStream;

	//private BufferedInputStream bufferedWaveStream;

	private static long outFileFrames = 0L;

	private static FileOutputStream outFileStream;

	private static final int waveFileHeaderOffset = 44;

	//private final int EXTERNAL_BUFFER_SIZE = 1837; 

	/**
	 * CONSTRUCTOR
	 */
	public SaveSound(InputStream waveStream, String infile) throws FileNotFoundException, IOException {
		this.bufferedWaveStream = new BufferedInputStream(waveStream);
		inputfile = infile;
		//this.waveStream = waveStream;
		//initializeDumpAudioStuff("../savedAudio",args);
		initializeDumpAudioStuff("../savedAudio", infile);

	}

	public void save() throws SaveWaveException {

		AudioInputStream audioInputStream = null;
		try {
			//audioInputStream = AudioSystem.getAudioInputStream(this.waveStream);
			audioInputStream = AudioSystem.getAudioInputStream(this.bufferedWaveStream);
		} catch (UnsupportedAudioFileException e1) {
			throw new SaveWaveException(e1);
		} catch (IOException e1) {
			throw new SaveWaveException(e1);
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
			throw new SaveWaveException(e1);
		}

		// Starts the music :P
		dataLine.start();

		int readBytes = 0;
		byte[] audioBuffer = new byte[this.EXTERNAL_BUFFER_SIZE];

		try {
			while (readBytes != -1) {
				readBytes = audioInputStream.read(audioBuffer, 0, audioBuffer.length);
				if (readBytes >= 0) {
					dataLine.write(audioBuffer, 0, readBytes);
				}
			}
		} catch (IOException e1) {
			throw new SaveWaveException(e1);
		} finally {
			// plays what's left and and closes the audioChannel
			dataLine.drain();
			dataLine.close();
		}

	}

	public void saveSound(InputStream wavestream, String args) throws FileNotFoundException, IOException,
			UnsupportedAudioFileException, SaveWaveException {

		this.bufferedWaveStream = new BufferedInputStream(wavestream);
		//this.waveStream = waveStream;

		initializeDumpAudioStuff("../savedAudio", args);

		dumpSound(0, 575);

		System.out.println(" File length finally is: " + audioOut.length());

		saveAsWaveFile();
		audioOut.delete();

	}

	private static void initializeDumpAudioStuff(String outfile, String infile) throws FileNotFoundException,
			IOException {

		//rafOut = new RandomAccessFile(outfile,"rw");
		audioOut = new File(outfile);

		boolean success = audioOut.createNewFile();

		if (!success)
			audioOut.delete();
		audioOut.createNewFile();

		if (!(audioOut.canWrite()))
			System.err.println("Cannot create output audio file..");
		//rafOut.seek(0);

		outFileStream = new FileOutputStream(audioOut, true);
	}

	public void dumpSound(long startFrameNum, long stopFrameNum) throws IOException, SaveWaveException,
			FileNotFoundException, UnsupportedAudioFileException {
		System.out.println("will save audio sound from frame#" + startFrameNum + " to frame#" + stopFrameNum);

		//We first get the bytes from the input file
		double audioTime = (double) (stopFrameNum - startFrameNum + 1) / VideoFile.VIDEO_FPS;//time in secs
		double audioDataBytesLength = audioTime * VideoFile.AUDIO_FPS * AUDIO_BYTES_PER_SAMPLE; //samples of audio needed with 22050 sample per sec

		double startTime = (double) startFrameNum / VideoFile.VIDEO_FPS;//the audio dump must start after these many seconds
		double audioStartOffset = startTime * VideoFile.AUDIO_FPS;

		byte[] bytes = new byte[(int) Math.round(audioDataBytesLength)];

		AudioInputStream audioInputStream = null;

		try {
			//audioInputStream = AudioSystem.getAudioInputStream(this.waveStream);
			audioInputStream = AudioSystem.getAudioInputStream(this.bufferedWaveStream);
		} catch (UnsupportedAudioFileException e1) {
			throw new SaveWaveException(e1);
		} catch (IOException e1) {
			throw new SaveWaveException(e1);
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
			throw new SaveWaveException(e1);
		}

		// Starts the music :P
		dataLine.start();

		int readBytes = 0;
		//byte[] audioBuffer = new byte[this.EXTERNAL_BUFFER_SIZE];

		try {
			while (readBytes != -1) {
				readBytes = audioInputStream.read(bytes, (int) audioStartOffset, bytes.length);
				if (readBytes >= 0) {
					outFileStream.write(bytes);
					//outFileStream.flush();
				}
			}
		} catch (IOException e1) {
			throw new SaveWaveException(e1);
		} finally {
			// plays what's left and and closes the audioChannel
			dataLine.drain();
			dataLine.close();
		}
		//if (last)
		//saveAsWaveFile();

	}

	public void saveAsWaveFile() throws IOException, FileNotFoundException, IOException, UnsupportedAudioFileException {
		//outFileStream.flush();
		//outFileStream.close();

		File savedTempFile = new File(audioOut.getAbsolutePath());

		FileInputStream fis = new FileInputStream(audioOut);

		AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(
				new FileInputStream(new File(inputfile))));

		AudioInputStream audioStream = new AudioInputStream(fis, audioInputStream.getFormat(), audioOut.length()
				/ audioInputStream.getFormat().getFrameSize());
		//AudioInputStream stream = new AudioInputStream(fis,AudioFileFormat.Type.WAVE,audioOut.length()/2);

		//AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, new File("../savedAudio.wav"));

		File audioOutWave = new File("../audioOutput.wav");

		boolean success = audioOutWave.createNewFile();

		if (!success) {
			audioOutWave.delete();
			audioOutWave.createNewFile();
		}

		AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, audioOutWave);
		audioOut.delete();
	}

}
