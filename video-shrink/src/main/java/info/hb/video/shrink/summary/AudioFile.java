package info.hb.video.shrink.summary;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

public class AudioFile {

	private static String filepath;

	private static RandomAccessFile audioRandomAccessFile;

	private static final double AUDIO_PER_FRAME = 918;

	private static final int audioInfoSize = 2; //audio info is in bytes

	private static final int waveFileHeaderOffset = 44;//wave file has header informaion priior to this

	AudioFile(String filename) throws FileNotFoundException {

		this.filepath = filename;
		//File audiofile
		audioRandomAccessFile = new RandomAccessFile(filename, "r");
		//System.out.println("audio file setup..");
	}

	public String getAudioFilename() {
		return filepath;
	}

	public static void readAudioData() {
		int totalFramesRead = 0;
		File fileIn = new File(filepath);
		//System.out.println("file length is "+fileIn.length());
		// somePathName is a pre-existing string whose value was
		// based on a user selection.
		try {
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(fileIn);
			int bytesPerFrame = audioInputStream.getFormat().getFrameSize();
			System.out.println("bytesPerFrame is " + bytesPerFrame);
			System.out.println("frame length is " + audioInputStream.getFrameLength());
			//Frames per sec in vdeo = 24 : Here it is 22050 ; So corresponding point frame number is 22050/24
			int corrFrameInAudio = (int) (audioInputStream.getFormat().getFrameRate() / VideoFile.VIDEO_FPS);
			System.out.println("frame rate is " + audioInputStream.getFormat().getFrameRate());
			System.out.println("getSampleSizeInBits is " + audioInputStream.getFormat().getSampleSizeInBits());
			System.out.println("channels are " + audioInputStream.getFormat().getChannels());
			System.out.println("encoding is " + audioInputStream.getFormat().getEncoding());
			//System.out.println("property bitrate is "+audioInputStream.getFormat().getProperty(bitrate));
			if (bytesPerFrame == AudioSystem.NOT_SPECIFIED) {
				// some audio formats may have unspecified frame size
				// in that case we may read any amount of bytes
				bytesPerFrame = 1;
			}
			// Set an arbitrary buffer size of 1024 frames.
			int numBytes = 1024 * bytesPerFrame;
			byte[] audioBytes = new byte[numBytes];
			try {
				int numBytesRead = 0;
				int numFramesRead = 0;
				// Try to read numBytes bytes from the file.
				while ((numBytesRead = audioInputStream.read(audioBytes)) != -1) {
					// Calculate the number of frames actually read.
					numFramesRead = numBytesRead / bytesPerFrame;
					totalFramesRead += numFramesRead;
					// Here, do something useful with the audio data that's 
					// now in the audioBytes array...
				}
				System.out.println("totalFramesRead " + totalFramesRead);
			} catch (Exception ex) {
				// Handle the error...
			}
		} catch (Exception e) {
			// Handle the error...
		}

	}

	public static void ComputeAudioVolume() throws IOException {
		double audioAmplitude = 0.0;
		byte[] bytes = new byte[audioInfoSize];
		//System.out.println("Data read in bytes: "+bytes.toString());

		long offset = 0L;
		long offset2 = 0L;

		RandomAccessFile raf = new RandomAccessFile(filepath, "r");

		int iteration = 0;
		long videoFrames = 0;
		int count75 = 0;
		int count75_i = 0;

		//  System.out.println("file length is "+raf.length());
		for (offset = waveFileHeaderOffset; ((offset < raf.length()) && (videoFrames < VideoFile.MAX_POSSIBLE_FRAMES)); offset += 2 * AUDIO_PER_FRAME) {
			//for (offset=waveFileHeaderOffset; offset <  1000; offset+=2) {
			//if (iteration % 1000 == 0) offset+=1; //leap year style correction for frame size being 918.75
			//System.out.println( "current offset is :" + raf.getFilePointer());
			//System.out.println("this is iteration number #"+(iteration++));

			audioAmplitude = 0.0;

			if ((offset2 - 2 + offset - (2 * AUDIO_PER_FRAME)) == offset)
				System.err.println("loop error..");

			for (offset2 = 0; offset2 < 2 * AUDIO_PER_FRAME; offset2 += 2) {
				double temp = 0.0;
				raf.seek(offset + offset2);
				raf.read(bytes);
				temp = Math.abs(((bytes[1] << 8) | (bytes[0] & 0xff)) / 32768.0);
				audioAmplitude += temp;
				//audioAmplitude += (double) Math.abs((double)( ( ( bytes[1]  << 8 ) | ( bytes[0] & 0xff ) ) / 32768.0 ));
				if (temp >= 0.20)
					count75_i++;
				iteration++;
			}
			audioAmplitude /= AUDIO_PER_FRAME;//we are taking average of all frames corresponding to video frame
			//System.out.println("audio amplitude was -- "+audioAmplitude);

			ArrayList<Double> tempFrameData = new ArrayList<Double>();
			//System.out.println("videoFrameNumber is "+videoFrames);
			//tempFrameData = (ArrayList<Double>) (VideoFile.frameHashMap.get(videoFrames).clone());
			tempFrameData.add(audioAmplitude);

			VideoFile.frameHashMap.put(videoFrames, tempFrameData);

			videoFrames++;

			if (audioAmplitude >= 0.25)
				count75++;
		}

		//System.out.println("Total bytes read ="+(2*iteration)+": Amplitude was higher than 0.25, "+count75_i+ " times individulally and "+count75+"mean wise");
		//return audioAmplitude;
		//System.out.println("audio length is :"+(double)videoFrames/24);

	}

}
