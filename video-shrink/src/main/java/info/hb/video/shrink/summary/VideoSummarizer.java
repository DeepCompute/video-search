package info.hb.video.shrink.summary;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

public class VideoSummarizer {

	/**
	 * @param args the command line arguments
	 */

	//The frameHashMap is scoring the different scores on each metric for eevery frame in the video
	//public static HashMap <Long, ArrayList<Double>> frameHashMap1 = new HashMap<Long,ArrayList<Double>>();

	//The shotHashMap is scoring the shot number and an array list which has keyframe numbers and then finally the overall score number
	//public static HashMap <Integer, ArrayList<Double>> shotHashMap1 = new HashMap<Integer,ArrayList<Double>>();

	public static AudioFile audiofile;

	public static long framesDisplayedThreshold = 0L;
	public static double framesDisplayedThresholdPercent = 0.04;

	public static void main(String[] args) throws IOException, InterruptedException, VideoException,
			FileNotFoundException, UnsupportedAudioFileException, SaveWaveException {

		String audiofileName = args[1];
		String videofileName = args[0];
		double shorteningFactor = Double.parseDouble(args[2]);

		if (shorteningFactor <= 0.0 || shorteningFactor > 1) {
			System.err.println("Invalid shortening factor (must be more than 0, but not more than 1) - returning..");
			return;
		}

		//ColorHistogram.setBins(32);

		VideoFile videofile = new VideoFile(videofileName, shorteningFactor, audiofileName);
		audiofile = new AudioFile(audiofileName);
		if (shorteningFactor == 1) {
			videofile.dumpEverything();
		}
		System.out.println("Analyzing input video...");
		videofile.initializeFrames();

		audiofile.ComputeAudioVolume();
		//System.out.println(videofile.frameHashMap);

		videofile.AnalyzeRGBVideoFile();

		//System.out.println("FramesAfterSummary = "+videofile.getFramesAfterSummary());

		videofile.printHashMaps();
		videofile.populateSortedShotsArrayList();
		videofile.sortShotsByScore();
		framesDisplayedThreshold = Math.round(videofile.getFramesNeeded() * framesDisplayedThresholdPercent);

		if (Math.abs(videofile.getFramesNeeded() - videofile.getFramesAfterSummary()) <= framesDisplayedThreshold) {
			System.out.print("Initializing process to write new reduced file..");
			videofile.populateFramesDumpList();
			videofile.dumpFrames();
		} else if (Math.round(videofile.getFramesNeeded() - videofile.getFramesAfterSummary()) > framesDisplayedThreshold) {
			// System.out.print("Need to increase frames..");
			//System.out.println("videofile.getFramesNeeded() :"+videofile.getFramesNeeded()+" videofile.getFramesAfterSummary() : "+videofile.getFramesAfterSummary());
			//System.out.println("We need to display more frames than currently being generated...");
			videofile.setFramesIncreasedTo(videofile.getFramesNeeded() - framesDisplayedThreshold);
			videofile.setFramesIncreasedTo(0);
			//System.out.println("videofile.getframesIncreasedTo "+videofile.getFramesIncreasedTo());
			videofile.setIncreaseTimeFactor((double) (videofile.getFramesNeeded()) / videofile.getFramesAfterSummary());
			videofile.increaseShots();
			//videofile.populateFramesDumpList();
			System.out.print("Initializing process to write new reduced file..");
			videofile.dumpFrames();
		} else if (videofile.getFramesAfterSummary() - videofile.getFramesNeeded() > framesDisplayedThreshold) {
			//System.out.print("Need to reduce frames..");
			//System.out.println("videofile.getFramesNeeded() :"+videofile.getFramesNeeded()+" videofile.getFramesAfterSummary() : "+videofile.getFramesAfterSummary());

			videofile.reduceShots(framesDisplayedThreshold);
			videofile.populateFramesDumpList();
			System.out.println("Initializing process to write new reduced file..");
			videofile.dumpFrames();
		}

		System.out.print("done");

	}

}
