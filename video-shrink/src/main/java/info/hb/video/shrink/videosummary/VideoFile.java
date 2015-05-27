package info.hb.video.shrink.videosummary;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import javax.sound.sampled.UnsupportedAudioFileException;

public class VideoFile {

	private String videoFilename;
	private String audioFilename;
	private long fileLength;

	public static final int WIDTH = 320;
	public static final int HEIGHT = 240;

	private RandomAccessFile rafForMotionVector;

	private ArrayList<Double> mvValueCheckTemp = new ArrayList<Double>();

	public static long MAX_POSSIBLE_FRAMES;

	//Each frame will give us atleast these many seconds of corresponding audio and video
	public static final int SEC_PER_FRAME = 5;
	public static final int VIDEO_FPS = 24;
	public static final double time_per_KF = VIDEO_FPS * SEC_PER_FRAME;

	public static long framesNeeded = 0L; //we need to shroten the video to these many seconds

	public static HashMap<Long, ArrayList<Double>> frameHashMap = new HashMap<Long, ArrayList<Double>>();

	//The shotHashMap is scoring the shot number and an array list which has keyframe numbers and then finally the overall score number
	public static HashMap<Long, ArrayList<Double>> shotHashMap = new HashMap<Long, ArrayList<Double>>();

	private static final long FRAMESIZE = WIDTH * HEIGHT * (24 / 8);//size of each frame in bytes

	//----FOr color and edge detection
	private static long frameNumber = 0;
	private static long shotNumber = 0;

	private static double sameShotNewSceneThreshold = 0.5;
	private static double newShotThreshold = 0.9;
	private ArrayList<Double> frameMetrics = new ArrayList<Double>();

	private static int newShotNeeded = 0;
	//    private ArrayList<Double> shotDetails = new ArrayList<Double>();

	private static ArrayList<Double> shotDetails = new ArrayList<Double>();

	private static double entropy = 1.0;
	private static int entropyScore = 0;
	private static double lastKeyEntropy = 1.0;

	private static double shotEntropyScore = 0.0;
	private static double entropyDiff = -1.0;

	private static int entropyFlag = 1;
	private static double lastEntropyZeroThreshold = 0.1;
	private static double lastEntropyZeroThresholdNewShot = 0.2;

	private static int newKFConditionsCorrect = 0;
	private static int newKFConditionsNeeded = 2;

	//for motion
	private static double mv = 0.0;
	private static int mvScore = 0;
	private static double mvThreshold = 200;
	private static double shotMVscore = 0;
	private static int mvFlag = 1;

	//------For audio
	private static long audioFramePosition = 0L;
	private static double audioVolume = 0.0;

	private static double highVolumeThreshold = 0.25;
	private static double volumeChangeThreshold = 2.00;//Percentage change for us to clarify volume to have changed too much

	public static final int AUDIO_FPS = 22050;

	private static double audio = 1.0;
	private static int audioScore = 0;
	private static double lastKeyAudio = 1.0;

	private static double shotAudioScore = 0.0;

	private static int isNewKF = 0;
	//For time estimation
	private static long keyFrameNumber = 0;
	private static long lastKeyFrameNumber = 0;
	private static long framesFromShot = 0;
	private static long framesFromKF = 0;

	private static double audioDiff = -1.0;

	private static int audioFlag = 1;
	private static double lastAudioZeroThreshold = 0.1;

	//For generating final stuff
	private static long framesAfterSummary = 0;
	public static ArrayList<ArrayList> sortedShotsByScore = new ArrayList<ArrayList>();
	private static long framesIncreasedTo = -1;

	private static double increaseTimeFactor = 1.0;
	private static final int Max_Frame_ArrayList_Size = 4;//this is max entries the arrayist in frae hashap can have

	private static final int minShotTimeThreshold = (int) Math.round(time_per_KF * 0.5);
	private static final int minFrameTimeThreshold = (int) Math.round(time_per_KF / 6.0);

	private static final int KFRemoveScoreUpperThreshold = 3;
	private static final int KFRemoveScoreLowerThreshold = 2;

	public static ArrayList<ArrayList> frameDumpList = new ArrayList<ArrayList>();//in this first element in inside arraylist has frame to be dumped and next has the no. of frames thereafter (including that)

	/*
	private JFrame outFrame;
	private JLabel label;

	private BufferedImage outImg = new BufferedImage (WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	*
	* 
	*/

	public VideoFile(String filename, double shortenRatio, String afilename) throws FileNotFoundException {

		this.videoFilename = filename;
		this.audioFilename = afilename;
		File file = new File(filename);
		this.fileLength = file.length();

		rafForMotionVector = new RandomAccessFile(this.videoFilename, "r");

		MAX_POSSIBLE_FRAMES = (this.fileLength / this.FRAMESIZE);
		framesNeeded = Math.round(MAX_POSSIBLE_FRAMES * shortenRatio);
		// System.out.println(framesNeeded);

		if (shortenRatio >= 0.3 && shortenRatio <= 0.55) {
			// ColorHistogram.setBins(64);
			sameShotNewSceneThreshold = 0.3;
			newShotThreshold = 0.5;
			lastEntropyZeroThreshold = 0.06;
			lastEntropyZeroThresholdNewShot = 0.15;
			highVolumeThreshold = 0.25;
			volumeChangeThreshold = 2.00;
		} else if (shortenRatio > 0.55 && shortenRatio <= 0.75) {
			// ColorHistogram.setBins(64);
			sameShotNewSceneThreshold = 0.3;
			newShotThreshold = 0.5;
			lastEntropyZeroThreshold = 0.06;
			lastEntropyZeroThresholdNewShot = 0.15;
			highVolumeThreshold = 0.25;
			volumeChangeThreshold = 2.00;

		} else if (shortenRatio > 0.75 && shortenRatio < 1) {
			System.out.println("shortenRatio > 0.75 ..");
			//ColorHistogram.setBins(32);
			sameShotNewSceneThreshold = 0.1;
			newShotThreshold = 0.2;
			lastEntropyZeroThreshold = 0.05;
			lastEntropyZeroThresholdNewShot = 0.1;
			highVolumeThreshold = 0.5;
			volumeChangeThreshold = 2.00;
			mvThreshold = 150;
		}

	}

	public void setFramesIncreasedTo(long x) {
		framesIncreasedTo = x;
		framesIncreasedTo = framesNeeded - x;

	}

	public long getFramesIncreasedTo() {
		return (framesIncreasedTo);
	}

	public static long getFramesNeeded() {
		return framesNeeded;
	}

	public static void calcFramesGenerated() {
		//start with shot of lowest score : (we do this because in case of extra time being left we want that to go the highest)

		double framesFromShot = 0;
		double totalFrames = 0.0;
		//for (long i=sortedShotsByScore.size()-1; i>=0;i-=sortedShotsByScore.size()-1)
		double framesAddedInShot = 0;
		double shotNumber = 0.0;

		//System.out.println("shotHashMap.size() = "+(shotHashMap.size())+"sortedShotsByScore.size()-1 = "+(sortedShotsByScore.size()-1));

		for (long i = sortedShotsByScore.size() - 1; i >= 0; i--) {
			shotNumber = 0.0;
			framesAddedInShot = 0;

			ArrayList<Double> shotDetailsTemp = new ArrayList<Double>();
			shotDetailsTemp = (ArrayList<Double>) sortedShotsByScore.get((int) i).clone();
			shotNumber = shotDetailsTemp.get(0);//this is shot number

			//System.out.println("shotNumber = "+shotNumber);
			//we have least imptt shot now.. - now we get shot length and find frames one-by-one
			ArrayList<Double> shotTempInfo = new ArrayList<Double>();
			shotTempInfo = (ArrayList<Double>) (shotHashMap.get((long) shotNumber).clone());
			framesFromShot = shotTempInfo.get(shotTempInfo.size() - 1);//the last value in arraylist was my shot length..  
			totalFrames += framesFromShot;

		}
		//System.out.println("total frames in VIdeo : "+(totalFrames));

	}

	public static long getFramesAfterSummary() {
		return framesAfterSummary;
	}

	public static void setIncreaseTimeFactor(double d) {
		increaseTimeFactor = d;
		//System.out.println("increaseTimeFactor set to: "+increaseTimeFactor);
	}

	public void initializeFrames() {

		for (long i = 0, j = 0; i < this.fileLength; i += FRAMESIZE, j++) {

			ArrayList<Double> tempFrameList = new ArrayList<Double>();

			//tempFrameList.add((double) i);
			//tempFrameList.add(-1.0);//entropy score
			//tempFrameList.add(-1.0);//audio score
			//tempFrameList.add(0.0);//motion score
			//tempFrameList.add(0.0);
			frameHashMap.put(j, tempFrameList);

			//frameNumber++ ;
			//frameMetrics.clear();

		}

		//ArrayList<Double> tempShotFrames = new ArrayList<Double>();

		//tempShotFrames.add(0.0);

		//shotDetails.add(0.0);

		//shotHashMap.put((long)0, tempShotFrames);
		//shotHashMap.put((long)0, shotDetails);

	}

	public void getFrameInfo(byte bytes[]) throws InterruptedException, IOException, VideoException {

		double rr = 0.0;
		double gg = 0.0;
		double bb = 0.0;
		double yAtPixel[][] = new double[WIDTH][HEIGHT];

		//double yValues[][] = new double [WIDTH][HEIGHT];
		//yValues = obtainYatPixel(bytes);

		int ind = 0;
		for (int y = 0; y < HEIGHT; y++) {

			for (int x = 0; x < WIDTH; x++) {

				byte a = 0;//This is opacity value
				byte r = bytes[ind];
				byte g = bytes[ind + HEIGHT * WIDTH];
				byte b = bytes[ind + HEIGHT * WIDTH * 2];

				rr = r & 0xff;
				gg = g & 0xff;
				bb = b & 0xff;

				int pix = (0xff000000) | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);//Set transparency factor as zero

				//outImg.setRGB(x,y,pix);

				ind++;

				yAtPixel[x][y] = 0.299 * rr + 0.587 * gg + 0.114 * bb;

				if (yAtPixel[x][y] < 0.0)
					yAtPixel[x][y] = 0.0;

				if (yAtPixel[x][y] > 255.0)
					yAtPixel[x][y] = 255.0;

				//System.out.println("yAtPixel["+x+"]["+y+"] is "+yAtPixel[x][y]);

			}
		}

		ColorHistogram yHistogram = new ColorHistogram();

		//double shotScore = 1.0;
		newKFConditionsCorrect = 0;
		audioFlag = 0;
		entropyFlag = 0;
		entropy = ColorHistogram.ComputeScore(yAtPixel);

		//System.out.println("frameNumber ="+frameNumber);
		//System.out.println(frameHashMap);
		ArrayList<Double> frameAudioTemp = new ArrayList<Double>();
		frameAudioTemp = ((ArrayList<Double>) frameHashMap.get(frameNumber).clone());
		//double frameAudioScore = frameDataTemp.get(0);
		//audio score is 1st entry
		//since this is 1st ever frame, this is qualified to be keyframe as  per audio as well
		audio = frameAudioTemp.get(0);
		//System.out.println("audio = "+audio);
		//audio=0.0;

		if (frameNumber != 0)//this aint first frame
		{
			entropyDiff = Math.abs(entropy - lastKeyEntropy);
			//System.out.println("entropy is"+entropy+" and lastKeyEntropy was "+lastKeyEntropy);
			audioDiff = Math.abs(audio - lastKeyAudio);
			if (lastKeyEntropy == 0.0) {
				if (entropyDiff > lastEntropyZeroThresholdNewShot) {
					entropyFlag = 2;
					entropyScore = 1;
					newKFConditionsCorrect++;
				}//we have new shot
				else if (entropyDiff > lastEntropyZeroThreshold) {
					entropyFlag = 1;
					entropyScore = 1;
					newKFConditionsCorrect++;
				}//this is same shot, new KF
				else {
					entropyFlag = 0;
					entropyScore = 0;
				}
			} else {
				if (entropyDiff > newShotThreshold * lastKeyEntropy) {
					entropyFlag = 2;
					newKFConditionsCorrect++;
					entropyScore = 1;
					//System.out.println("entropyScore ="+entropyScore);
				}//we have a new shot
				else if (entropyDiff > sameShotNewSceneThreshold * lastKeyEntropy) {
					entropyFlag = 1;
					newKFConditionsCorrect++;
					entropyScore = 1;
					//System.out.println("entropyScore ="+entropyScore);
				}//this is same shot, new KF
				else {
					entropyFlag = 0;
					entropyScore = 0;
					//System.out.println("entropyScore ="+entropyScore);
				}
			}

			if (lastKeyAudio == 0.0) {
				if (audioDiff > lastAudioZeroThreshold || audio > highVolumeThreshold) {
					audioFlag = 1;
					newKFConditionsCorrect++;
					audioScore = 1;
					//System.out.println("audioScore ="+audioScore);
				} else {
					audioFlag = 0;
					audioScore = 0;
					//System.out.println("audioScore ="+audioScore);
				}
			} else {
				if (audioDiff > volumeChangeThreshold * lastKeyAudio || audio > highVolumeThreshold) {
					audioFlag = 1;
					audioScore = 1;
					//  System.out.println("audioScore ="+audioScore);
				} else {
					audioFlag = 0;
					audioScore = 0;
					//   System.out.println("audioScore ="+audioScore);
				}
			}

		}
		newKFConditionsCorrect = audioScore + entropyScore;

		//we will include motion only if one of other is present - to speed up work
		//  System.out.println("newKFConditionsCorrect "+newKFConditionsCorrect);

		if (newKFConditionsCorrect > 0) {

			mv = motionVectorComputation(frameNumber, lastKeyFrameNumber);
			if (mv > mvThreshold) {
				mvFlag = 1;
				mvScore = 1;
				newKFConditionsCorrect++;
			} else {
				mvFlag = 0;
				mvScore = 0;
			}

		}

		newKFConditionsCorrect = audioScore + entropyScore + mvScore;

		//  System.out.println("newKFConditionsCorrect "+newKFConditionsCorrect);

		//System.out.println("FrameHashMap has info: "+frameHashMap);

		//audioVolume = VideoSummarizer.audiofile.ComputeAudioVolume(audioFramePosition);
		//System.out.println("frame number is "+frameNumber);
		//ArrayList<Double> shotDetails = new ArrayList<Double>();

		//shotDetails.clear();
		//shotNumber++;// We start with a shot number = 0

		//shotEntropyScore+=entropy; 

		newShotNeeded = 0;
		//audioScore=0;
		//entropyScore=0;
		isNewKF = 0;

		//If we have the first frame or cross a threshold of our video, we shall create a new shot                  
		//if current frame's entropy value is ,more than newShotThreshold than we add more shots

		if (frameNumber == 0) {
			keyFrameNumber = frameNumber;
			lastKeyFrameNumber = keyFrameNumber;

			newShotNeeded = 1;
			lastKeyEntropy = entropy;

			entropyScore = 1;//this is keyframe
			shotEntropyScore = 1;
			framesFromShot = 0;

			//System.out.println("audio value for frame#"+frameNumber+" ="+audio);
			lastKeyAudio = audio;

			audioScore = 1;
			shotAudioScore = 1;

			mvScore = 1;
			shotMVscore = 1;
			newKFConditionsCorrect = 3;

			framesFromShot = 0;
			framesFromKF = 0;

		} else if (entropyFlag == 2) {
			newShotNeeded = 1;
			keyFrameNumber = frameNumber;

			if ((keyFrameNumber - lastKeyFrameNumber) >= time_per_KF) {
				framesFromKF = keyFrameNumber - lastKeyFrameNumber;
				framesFromShot += time_per_KF;
			} else {
				framesFromKF = keyFrameNumber - lastKeyFrameNumber;
				framesFromShot += framesFromKF;
			}

			//System.out.println(" KF and shot change : frames from last KF#"+lastKeyFrameNumber+" ="+framesFromKF);
			//System.out.println(" KF and shot change : frames from last shot = "+framesFromShot+"in shot#"+shotNumber+"\n\n\n");

			ArrayList<Double> tempFrameData = new ArrayList<Double>();

			//System.out.println("videoFrameNumber is "+videoFrames);

			tempFrameData = (ArrayList<Double>) (frameHashMap.get(lastKeyFrameNumber).clone());
			tempFrameData.add((double) framesFromKF);
			frameHashMap.put(lastKeyFrameNumber, tempFrameData);

			lastKeyFrameNumber = keyFrameNumber;

			framesAfterSummary += framesFromShot;
			lastKeyEntropy = entropy;
			//System.out.println("inside new shot creation code with frameNumber = "+frameNumber);
			//we'll see if audio score qualifies this frame to be key frame as well

			//newKFConditionsCorrect++;
			ArrayList<Double> shotDataTemp = new ArrayList<Double>();
			shotDataTemp = (ArrayList<Double>) shotHashMap.get(shotNumber).clone();

			//shotDataTemp.add((double)(frameNumber-1));//we save last frame in our list
			shotDataTemp.add(shotEntropyScore + shotAudioScore + shotMVscore); //last thing we score is shot entropy score
			shotDataTemp.add((double) framesFromShot); //last thing we score is shot entropy score

			//shotDataTemp.add((double)framesFromShot);
			//System.out.println("shotEntropyScore for shot#"+shotNumber+" finally is: "+shotEntropyScore);
			shotHashMap.put(shotNumber, shotDataTemp);
			//  System.out.println("shotHashMap inside function ="+shotHashMap);
			shotNumber++;
			framesFromShot = 0;
			framesFromKF = 0;
			shotAudioScore = 0;
			//shotEntropyScore=1;

			//ArrayList<Double> frameDataTemp = new ArrayList<Double>();
			//frameDataTemp = ((ArrayList<Double>) frameHashMap.get(frameNumber).clone());
			//double frameAudioScore = frameDataTemp.get(0);//audio score is 1st entry
			//audio = frameDataTemp.get(0);
			if (audioFlag == 1) { //this is now also an important frame by our audio criteria
				lastKeyAudio = audio;
				//audioScore=1;
				shotAudioScore = 1;
				//newKFConditionsCorrect++;
			}
			if (mvFlag == 1) {
				shotMVscore = 1;
			}
			//System.out.println("Shot #"+shotNumber+" finally had --"+(shotHashMap.get(shotNumber).size()-1)+"-- keyframes :Overall frame number is"+frameNumber);    
			//ArrayList<Double> shotDetails = new ArrayList<Double>();
			//Tests

			//------ArrayList<Double> shotData = new ArrayList<Double>();
			//------shotData.add((double)frameNumber);

			//shotEntropyScore+=entropy;
			//shotData = shotHashMap.get(shotNumber);
			//System.out.println("shot Hashmap now has data = "+shotHashMap);

			//------shotHashMap.put(shotNumber, shotData);

			//entropyScore=1;
			shotEntropyScore = 1;

		}

		if (newShotNeeded == 1) {

			ArrayList<Double> shotData = new ArrayList<Double>();

			//We are starting a new shot so we add this frame Number

			shotData.add((double) frameNumber);
			//framesFromShot=0;
			shotHashMap.put(shotNumber, shotData);

			//System.out.println("this got data ");
		} else {
			//if current frame's entropy value is ,more than sameShotNewSceneThreshold than we add more keyframes to the list
			//shotAudioScore =0;
			//ArrayList<Double> frameDataTemp = new ArrayList<Double>();
			//frameDataTemp = ((ArrayList<Double>) frameHashMap.get(frameNumber).clone());
			//double frameAudioScore = frameDataTemp.get(0);//audio score is 1st entry
			//audio = frameDataTemp.get(0);

			if (audioFlag == 1) { //this is now also an important frame by our audio criteria
				lastKeyAudio = audio;
				//audioScore=1;
				shotAudioScore++;
				//isNewKF =1;
			}

			if (entropyFlag == 1) {
				//shotEntropyScore++; 
				lastKeyEntropy = entropy;
				//entropyScore=1;
				shotEntropyScore++;

			}

			if (mvFlag == 1) {
				shotMVscore++;
			}

			//Boundary condition - if this is last possible frame (and not a KF), then we must simply add up and store up our entropy                 

			if ((frameNumber == MAX_POSSIBLE_FRAMES - 1) && (newKFConditionsCorrect < newKFConditionsNeeded)) {
				ArrayList<Double> shotDataTemp = new ArrayList<Double>();
				shotDataTemp = (ArrayList<Double>) shotHashMap.get(shotNumber).clone();
				if ((frameNumber - lastKeyFrameNumber + 1) >= time_per_KF) {
					framesFromKF = frameNumber - lastKeyFrameNumber + 1;
					framesFromShot += time_per_KF;
				} else {
					framesFromKF = frameNumber - lastKeyFrameNumber + 1;
					framesFromShot += framesFromKF;
				}

				//framesFromShot+=framesFromKF;
				//shotDataTemp.add((double)(frameNumber-1));//we save last frame in our list
				shotDataTemp.add(shotEntropyScore + shotAudioScore); //last thing we score is shot entropy score
				shotDataTemp.add((double) framesFromShot);
				framesAfterSummary += framesFromShot;
				//System.out.println("shotEntropyScore for shot#"+shotNumber+" finally is: "+shotEntropyScore);
				shotHashMap.put(shotNumber, shotDataTemp);

				ArrayList<Double> tempFrameData = new ArrayList<Double>();

				//System.out.println("videoFrameNumber is "+videoFrames);

				tempFrameData = (ArrayList<Double>) (frameHashMap.get(lastKeyFrameNumber).clone());
				tempFrameData.add((double) framesFromKF);
				frameHashMap.put(lastKeyFrameNumber, tempFrameData);
				lastKeyFrameNumber = frameNumber;

				//shotEntropyScore=0.0;

				//return;
			}

			if (newKFConditionsCorrect >= newKFConditionsNeeded) {
				ArrayList<Double> shotDataTemp = new ArrayList<Double>();
				shotDataTemp = (ArrayList<Double>) shotHashMap.get(shotNumber).clone();
				shotDataTemp.add((double) frameNumber);
				shotHashMap.put(shotNumber, shotDataTemp);
				keyFrameNumber = frameNumber;

				if ((keyFrameNumber - lastKeyFrameNumber) >= time_per_KF) {
					framesFromKF = keyFrameNumber - lastKeyFrameNumber;
					framesFromShot += time_per_KF;
				} else {
					framesFromKF = keyFrameNumber - lastKeyFrameNumber;
					framesFromShot += framesFromKF;
				}

				//System.out.println(" KF was generated, but no shot change : frames from last KF ="+framesFromKF);
				//System.out.println(" KF was generated, but no shot change : frames from shot thus far ="+framesFromShot);
				//framesFromShot+=framesFromKF;
				ArrayList<Double> tempFrameData = new ArrayList<Double>();

				//System.out.println("videoFrameNumber is "+videoFrames);

				tempFrameData = (ArrayList<Double>) (frameHashMap.get(lastKeyFrameNumber).clone());
				tempFrameData.add((double) framesFromKF);
				frameHashMap.put(lastKeyFrameNumber, tempFrameData);
				lastKeyFrameNumber = keyFrameNumber;

			}
			//shotEntropyScore+=entropyScore;
			//shotAudioScore+=audioScore;

		}

		ArrayList<Double> tempFrameData = new ArrayList<Double>();

		//System.out.println("videoFrameNumber is "+videoFrames);

		if (!(entropyScore == 0 || entropyScore == 1)) {
			//System.err.println("entropyScore is out of range for frame#"+frameNumber+" .. it is "+entropyScore);
		}

		if (!(audioScore == 0 || audioScore == 1)) {
			//System.err.println("audioScore is out of range for frame#"+frameNumber+" .. it is "+audioScore);
		}

		if (!(newKFConditionsCorrect == 0 || newKFConditionsCorrect == 1 || newKFConditionsCorrect == 2 || newKFConditionsCorrect == 3)) {
			//System.err.println("newKFConditionsCorrect is out of range for frame#"+frameNumber+" .. it is "+newKFConditionsCorrect);
		}

		tempFrameData = (ArrayList<Double>) (frameHashMap.get(frameNumber).clone());

		tempFrameData.add(entropy);
		tempFrameData.add((double) (newKFConditionsCorrect));//we score final overall score as well
		//tempFrameData.add((double)(framesFromKF));//we score frames from KF -1 if not KF
		frameHashMap.put(frameNumber, tempFrameData);

		//ArrayList<Double> tempFrameInfo = new ArrayList<Double>();
		//tempFrameInfo.add(entropy);
		//tempFrameInfo.add(-1.0);
		//tempFrameInfo.add(videoScore);
		//frameHashMap.put(frameNumber, tempFrameInfo);
		frameNumber++;
		//frameMetrics.clear();

	}

	public double[][] obtainYatPixel(byte bytes[]) throws InterruptedException {

		double rr = 0.0;
		double gg = 0.0;
		double bb = 0.0;
		double yAtPixel[][] = new double[WIDTH][HEIGHT];

		int ind = 0;
		for (int y = 0; y < HEIGHT; y++) {

			for (int x = 0; x < WIDTH; x++) {

				byte a = 0;//This is opacity value
				byte r = bytes[ind];
				byte g = bytes[ind + HEIGHT * WIDTH];
				byte b = bytes[ind + HEIGHT * WIDTH * 2];

				rr = r & 0xff;
				gg = g & 0xff;
				bb = b & 0xff;

				int pix = (0xff000000) | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);//Set transparency factor as zero

				//outImg.setRGB(x,y,pix);

				ind++;

				yAtPixel[x][y] = 0.299 * rr + 0.587 * gg + 0.114 * bb;

				if (yAtPixel[x][y] < 0.0)
					yAtPixel[x][y] = 0.0;

				if (yAtPixel[x][y] > 255.0)
					yAtPixel[x][y] = 255.0;

			}
		}
		return yAtPixel;

	}

	public double motionVectorComputation(long frameNumber1, long frameNumber2) throws IOException, VideoException,
			InterruptedException {
		double motionVector = 0.0;
		try {

			int len = (int) FRAMESIZE;
			byte[] bytes = new byte[len];

			double[][] yAtFrame1 = new double[WIDTH][HEIGHT];
			double[][] yAtFrame2 = new double[WIDTH][HEIGHT];

			//FileInputStream;
			//System.out.println("this.fileLength "+this.fileLength);
			long offset1 = 0L, offset2 = 0L;

			//audioRandomAccessFile.seek(waveFileHeaderOffset + seekPosition); 
			offset1 = frameNumber1 * len;
			offset2 = frameNumber2 * len;
			if (offset1 < rafForMotionVector.length() && offset2 < rafForMotionVector.length()) {
				rafForMotionVector.seek(offset1);
				rafForMotionVector.read(bytes);
				yAtFrame1 = obtainYatPixel(bytes);

				rafForMotionVector.seek(offset2);

				rafForMotionVector.read(bytes);
				yAtFrame2 = obtainYatPixel(bytes);
				motionVector = motionCalculate(yAtFrame1, yAtFrame2);

				//System.out.println("Motion Vector between frame: "+frameNumber1+" and frame :"+frameNumber2+" is "+motionVector);

			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return -1.0;
		}

		return motionVector;

	}

	public double motionCalculate(double[][] frame1, double[][] frame2) {
		int valOfBlock[] = new int[9];
		int centerX = 152, centerY = 112;
		int hops = 4, length = 64;
		int dx = 152, dy = 112;
		for (int d = 0; d <= 8; d++) {
			valOfBlock[d] = 0;
		}
		while (hops >= 1 && length >= 16) {

			for (int y = 0; y < 16; y++) {

				for (int x = 0; x < 16; x++) {

					valOfBlock[0] += Math.abs(frame1[x + 152][y + 112] - frame2[centerX + x + length][centerY + y]);

					valOfBlock[1] += Math.abs(frame1[x + 152][y + 112]
							- frame2[centerX + length + x][centerY + y + length]);

					valOfBlock[2] += Math.abs(frame1[x + 152][y + 112]
							- frame2[centerX + length + x][centerY + y - length]);

					valOfBlock[3] += Math.abs(frame1[x + 152][y + 112] - frame2[centerX + x - length][centerY + y]);

					valOfBlock[4] += Math.abs(frame1[x + 152][y + 112]
							- frame2[centerX + x - length][centerY + y + length]);

					valOfBlock[5] += Math.abs(frame1[x + 152][y + 112]
							- frame2[centerX + x - length][centerY + y - length]);

					valOfBlock[6] += Math.abs(frame1[x + 152][y + 112] - frame2[centerX + x][centerY + y + length]);

					valOfBlock[7] += Math.abs(frame1[x + 152][y + 112] - frame2[centerX + x][centerY + y - length]);

					valOfBlock[8] += Math.abs(frame1[x + 152][y + 112] - frame2[centerX + x][centerY + y]);

				}
			}
			int min = valOfBlock[0];
			int indx = 0;

			for (int d = 0; d <= 8; d++) {
				if (valOfBlock[d] < min) {

					min = valOfBlock[d];
					indx = d;
				}
			}
			//System.out.println("min is "+valOfBlock[indx]);
			//System.out.println("+++++++++++++++++++++++++++++++++++++++++++");
			//System.out.println("dx is "+dx);
			//System.out.println("dy is "+dy);
			//System.out.println("-------------------------------------------");

			if (indx == 0) {
				dx = dx + length;
				centerX = centerX + length;
			} else if (indx == 1) {
				dx = dx + length;
				dy = dy + length;
				centerX = centerX + length;
				centerY = centerY + length;
			}

			else if (indx == 2) {
				dx = dx + length;
				dy = dy - length;
				centerX = centerX + length;
				centerY = centerY - length;
			}

			else if (indx == 3) {
				dx = dx - length;
				centerX = centerX - length;
			} else if (indx == 4) {
				dx = dx - length;
				dy = dy + length;
				centerX = centerX - length;
				centerY = centerY + length;
			} else if (indx == 5) {
				dx = dx - length;
				dy = dy - length;
				centerX = centerX - length;
				centerY = centerY - length;
			}

			else if (indx == 6) {
				dy = dy + length;
				centerY = centerY + length;
			}

			else if (indx == 7) {
				dy = dy - length;
				centerY = centerY - length;
			}

			hops -= 1;
			length /= 2;

		}

		//System.out.println("dx is "+dx);
		//System.out.println("dy is "+dy);
		long motionVector = (((dx - 152) * (dx - 152)) + ((dy - 112) * (dy - 112)));
		double mv = Math.sqrt(motionVector);

		return mv;
	}

	public void AnalyzeRGBVideoFile() throws IOException, VideoException, InterruptedException {

		FileInputStream inputStream;
		try {
			inputStream = new FileInputStream(this.videoFilename);
			BufferedInputStream bufferedVideoStream = new BufferedInputStream(inputStream);

			int len = (int) FRAMESIZE;
			byte[] bytes = new byte[len];

			int pixelAtFrame, count = 0;

			//System.out.println("this.fileLength "+this.fileLength);
			//System.out.println("Total video frames ="+MAX_POSSIBLE_FRAMES);
			//MAX_POSSIBLE_FRAMES = 500;
			//for (long stream=0;stream<(this.FRAMESIZE)*MAX_POSSIBLE_FRAMES; stream+= this.FRAMESIZE)
			for (long stream = 0; stream < this.fileLength; stream += this.FRAMESIZE) {

				int offset = 0;
				int numRead = 0;

				bufferedVideoStream.read(bytes);

				getFrameInfo(bytes);

				//System.out.println("offset is "+offset+" and numRead is "+numRead);
				count++;
				//System.out.println("Displaying for "+ (count)+" time ");

			}//All video scores for frames have been set up
				// System.out.println(frameHashMap);
				// System.out.println("shotHashMap "+shotHashMap);

			//System.out.println("framesAfterSummary : "+framesAfterSummary);
			//framesReducedTo = framesAfterSummary;

			//System.out.println("Total frames = "+(frameNumber+1)+" were divided into total shots = "+(shotNumber+1));
			//System.out.println("Total frames = "+(frameHashMap.size())+" were divided into total shots = "+shotHashMap.size());

			//System.out.println("@%@%!@(!@ contents of shotHashMap is "+shotHashMap);

			/*
			 * We now have scores for all our frames and have filled 
			 * up the datastructures for frame Info and shot info
			 * 
			 * Now, we allow SEC_PER_FRAME seconds to each keyframe
			 * 
			 */
			//int keyFrameNumber = 1;
			//int nextKeyFrameNumber = 2;
			final int stdFramesShown = SEC_PER_FRAME * VIDEO_FPS; //these many following frames from each key frame

			double totalDisplayTime = 0.0;
			double timeForShot = 0.0;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}

	}

	public static void printHashMaps() {

		//System.out.println("FrameHashMap has info: ");

		//for(long ii=0; ii<MAX_POSSIBLE_FRAMES; ii++)
		//     System.out.println("at"+ii+" - "+frameHashMap.get(ii).clone());

		//System.out.println("shotHashMap has info: ");

		for (long ii = 0; ii < shotHashMap.size(); ii++) {
			//System.out.println("at"+ii+" - "+shotHashMap.get(ii).clone());

			//ArrayList<Double> tempKFData = new ArrayList<Double>();
			//tempKFData = (ArrayList<Double>)shotHashMap.get(ii).clone();
			//if (tempKFData.get(tempKFData.size()-2) > 2*(tempKFData.size()-2))
			// System.err.println("We have a problem with shot#"+ii);

		}

	}

	public static void populateSortedShotsArrayList() {
		//Here we traverse all shots and go on to have our sorted list to get shotNumber and their score
		for (long i = 0; i < shotHashMap.size(); i++) {
			ArrayList<Double> tempShotData = new ArrayList<Double>();
			tempShotData = (ArrayList<Double>) (shotHashMap.get(i).clone());
			//Second-to-last index here has shot score and last index has shot length
			ArrayList<Double> tempSortedShotData = new ArrayList<Double>();
			tempSortedShotData.add((double) i);
			int ii = tempShotData.size() - 2;
			//System.out.println("at"+i+": tempShotData.size()-2  = "+(tempShotData.size()-2)+ "tempShotData.get("+ii+") is "+tempShotData.get(ii));
			tempSortedShotData.add(tempShotData.get(ii));
			sortedShotsByScore.add((ArrayList) (tempSortedShotData.clone()));
			//sortedShotsByScore.add(tempSortedShotData);

		}

		//System.out.println("\n\n\nthe arraylist <<sortedShotsByScore>> has arraylists: "+sortedShotsByScore);

	}

	public static void sortShotsByScore() {

		Collections.sort(sortedShotsByScore, new ShotsCompare());

		//System.out.println("\n\n\nthe arraylist <<sortedShotsByScore>> now is: "+sortedShotsByScore);

	}

	public static void reduceShots(double frameThreshold) {
		//System.out.println("inside reduceShots");
		double needToReduceFramesBy = framesAfterSummary - framesNeeded;
		//System.out.println("needToReduceFramesBy ="+needToReduceFramesBy);
		int completed = 0;
		double shotNum = 0.0;

		//System.out.println("First Pass is starting..");

		//First Pass.. We remove shots less than minShotTimeThreshold
		for (long i = sortedShotsByScore.size() - 1; i >= 0; i--) {
			if (completed == 1) {
				break;
			}

			shotNum = 0.0;

			ArrayList<Double> shotDetailsTemp = new ArrayList<Double>();
			shotDetailsTemp = (ArrayList<Double>) sortedShotsByScore.get((int) i).clone();
			shotNum = shotDetailsTemp.get(0);//this is shot number
			//System.out.println("shotNumber is "+shotNumber);
			ArrayList<Double> shotTempInfo = new ArrayList<Double>();
			shotTempInfo = (ArrayList<Double>) (shotHashMap.get((long) shotNum).clone());
			//System.out.println("shotTempInfo :"+shotTempInfo);
			int indexOfShotLength = shotTempInfo.size() - 1;
			double shotLength = shotTempInfo.get(indexOfShotLength);

			if ((long) shotLength <= minShotTimeThreshold) {
				//this shot is too small in length, we get rid of this
				//ArrayList<Double> emptyShot = new ArrayList<Double>();
				//emptyShot.clear();
				shotHashMap.get((long) shotNum).clear();
				needToReduceFramesBy -= shotLength;
				//System.out.println("shot#"+shotNum+"removed (was "+shotLength+" frames long).. VideoLength is now "+(framesNeeded+needToReduceFramesBy));
			}

			if (needToReduceFramesBy - frameThreshold <= 0) {
				completed = 1;
			}

		}
		//System.out.println("First Pass is over..");

		if (completed == 1) {
			//we are done; we ought to start dumping frames
			return;
		}

		// KFRemoveScoreUpperThreshold
		//System.out.println("Second Pass is starting..");
		//We remove the KF with length less than minFrameTime and also those shots whose time is now less than minShotTime

		for (long i = sortedShotsByScore.size() - 1; i >= 0; i--) {
			if (completed == 1) {
				break;
			}

			shotNum = 0.0;

			ArrayList<Double> shotDetailsTemp = new ArrayList<Double>();
			shotDetailsTemp = (ArrayList<Double>) sortedShotsByScore.get((int) i).clone();
			shotNum = shotDetailsTemp.get(0);//this is shot number
			//System.out.println("shotNumber is "+shotNumber);
			ArrayList<Double> shotTempInfo = new ArrayList<Double>();
			shotTempInfo = (ArrayList<Double>) (shotHashMap.get((long) shotNum).clone());
			//System.out.println("shotTempInfo :"+shotTempInfo);

			if (shotTempInfo.isEmpty()) {
				continue;
			}

			int indexOfShotLength = shotTempInfo.size() - 1;
			double shotLength = shotTempInfo.get(indexOfShotLength);

			long count = 0;
			double framesGivenByKF = 0.0;
			for (long ii = 0; ii < shotTempInfo.size() - 2; ii++, count++) //this is to read list of KF in shot one-by-one we remove if we get one of length less than 20 frame
			{
				if (completed == 1) {
					break;
				}

				ArrayList<Double> frameInfoTemp = new ArrayList<Double>();
				double frameNum = 0.0;
				frameNum = shotTempInfo.get((int) ii);
				frameInfoTemp = (ArrayList<Double>) (frameHashMap.get((long) frameNum).clone());

				if (frameInfoTemp.size() < Max_Frame_ArrayList_Size) //we have no KF length attribute for this frame anymore 
				{
					//System.out.println("frame was useless..");
					continue;
				}

				framesGivenByKF = frameInfoTemp.get(frameInfoTemp.size() - 1);
				//System.out.println("inside frame#"+frameNum+" which is inside shot#"+shotNum);
				if (framesGivenByKF <= minFrameTimeThreshold) {//we will get rid of this KF

					//ArrayList<Double> frameDataTemp = new ArrayList<Double>();
					frameInfoTemp.remove(frameInfoTemp.size() - 1);//remove entry for KF length from frameHashMap
					frameHashMap.put((long) frameNum, frameInfoTemp);
					//needToReduceFramesBy-=framesGivenByKF;

					//also if the shot is now too small, we will let that go too
					if ((shotLength - framesGivenByKF) <= minShotTimeThreshold) {
						//shotHashMap.put((long)shotNum, null);
						shotHashMap.get((long) shotNum).clear();
						needToReduceFramesBy -= shotLength;
						//System.out.println("shot#"+shotNum+"removed (was "+shotLength+" frames long).. VideoLength is now "+(framesNeeded+needToReduceFramesBy));
						break;//we now look at next shot
					} else {//update shot length in shotHashMap
							//System.out.println("shotTempInfo was :"+shotTempInfo);
							//shotTempInfo.remove(indexOfShotLength);
						shotTempInfo.set(indexOfShotLength, (shotLength - framesGivenByKF));
						//System.out.println("shotTempInfo is now:"+shotTempInfo);
						shotHashMap.put((long) shotNum, shotTempInfo);
						needToReduceFramesBy -= framesGivenByKF;
						//System.out.println("frame#"+frameNum+"removed (was "+framesGivenByKF+" frames long).. VideoLength is now "+(framesNeeded+needToReduceFramesBy));
					}

				}

				if (needToReduceFramesBy - frameThreshold <= 0) {
					completed = 1;
				}
			}

		}

		if (completed == 1) {
			//we are done; we ought to start dumping frames
			return;
		}
		//System.out.println("Second Pass is over..");

		// KFRemoveScoreUpperThreshold
		//System.out.println("Third and Final Pass is starting.. we need to reduce by "+needToReduceFramesBy);
		//we start from bottom again and start removing shots with KF score = 1 - one by one
		// we keep on doing that till we reach required shots; if we cant remove any frame in 3 runs we quit and output

		int uselessPassCount = 0;
		long frameReductionNeededBeforePass = 0;
		double scoreOfKF = 0.0;

		do {

			//System.out.println("uselessPassCount :"+uselessPassCount);
			frameReductionNeededBeforePass = (long) needToReduceFramesBy;

			for (long i = sortedShotsByScore.size() - 1; i >= 0; i--) //least imptt shot first
			{
				if (completed == 1) {
					break;
				}

				shotNum = 0.0;

				ArrayList<Double> shotDetailsTemp = new ArrayList<Double>();
				shotDetailsTemp = (ArrayList<Double>) sortedShotsByScore.get((int) i).clone();
				shotNum = shotDetailsTemp.get(0);//this is shot number

				if (!(shotHashMap.containsKey(shotNum))) {
					continue;
				}
				ArrayList<Double> shotTempInfo = new ArrayList<Double>();
				shotTempInfo = (ArrayList<Double>) (shotHashMap.get((long) shotNum).clone());

				if (shotTempInfo.isEmpty()) //this shot is gone
				{
					continue;
				}
				int foundKFtoRemove = 0;

				for (int iii = KFRemoveScoreLowerThreshold; iii < KFRemoveScoreUpperThreshold; iii++) {//System.out.println("counter - "+(iii-KFRemoveScoreLowerThreshold));
																										//we look inside the list of KF and remove the one with the first one with the lowest score possible, with the lowest possible shot score as well
					if (completed == 1 || foundKFtoRemove == 1) {
						break;
					}
					int indexOfShotLength = shotTempInfo.size() - 1;
					double shotLength = shotTempInfo.get(indexOfShotLength);

					double framesGivenByKF = 0.0;

					for (long ii = 0; ii < shotTempInfo.size() - 2; ii++) //this is to read list of KF in shot one-by-one 
					{
						if (completed == 1) {
							break;
						}

						ArrayList<Double> frameInfoTemp = new ArrayList<Double>();
						double frameNum = 0.0;
						frameNum = shotTempInfo.get((int) ii);
						//System.out.println("inside frame#"+frameNum+" which is inside shot#"+shotNum);
						frameInfoTemp = (ArrayList<Double>) (frameHashMap.get((long) frameNum).clone());

						if (frameInfoTemp.size() < Max_Frame_ArrayList_Size) //we have no KF length attribute for this frame anymore 
						{
							continue;
						}

						framesGivenByKF = frameInfoTemp.get(frameInfoTemp.size() - 1);
						scoreOfKF = frameInfoTemp.get(frameInfoTemp.size() - 2);

						//System.out.println("inside frame#"+frameNum+" which is inside shot#"+shotNum);

						if (scoreOfKF == ii) {//we will get rid of this KF
							foundKFtoRemove = 1;
							//ArrayList<Double> frameDataTemp = new ArrayList<Double>();
							frameInfoTemp.remove(frameInfoTemp.size() - 1);//remove entry for KF length from frameHashMap
							frameHashMap.put((long) frameNum, frameInfoTemp);

							//also if the shot is now too small, we will let that go too
							if ((shotLength - framesGivenByKF) <= minShotTimeThreshold) {
								shotHashMap.put((long) shotNum, null);
								needToReduceFramesBy -= shotLength;
								break;//we now look at next shot
							} else {//update shot length in shotHashMap
									//System.out.println("shotTempInfo was :"+shotTempInfo);
								shotTempInfo.remove(indexOfShotLength);
								shotTempInfo.add(shotLength - framesGivenByKF);
								//System.out.println("shotTempInfo is now:"+shotTempInfo);
								shotHashMap.put((long) shotNum, shotTempInfo);
								needToReduceFramesBy -= framesGivenByKF;
							}

						}
					}

					if (needToReduceFramesBy - frameThreshold <= 0) {
						completed = 1;
					}
				}

			}

			if (needToReduceFramesBy == frameReductionNeededBeforePass) //no reduction happened
			{
				uselessPassCount++;
			}

		} while (completed != 1 && uselessPassCount < 3);

		//System.out.println("Third and final Pass is over..");

		//we are done; we ought to start dumping frames
		return;

		/*
		 * 
		 *           long count=0;
		         for (long ii=0; ii<shotTempInfo.size()-2; ii++,count++)
		         {       
		         ArrayList<Double> frameInfoTemp = new ArrayList<Double>();
		         double frameNumber =0.0;
		         frameNumber = shotTempInfo.get((int)ii);
		         frameInfoTemp = (ArrayList<Double>) (frameHashMap.get((long)frameNumber).clone());
		         //since frameInfoTemp is deffo refering to a KF, we can see its length as las value in arraylist..
		         //System.out.println("frameInfoTemp :"+frameInfoTemp);
		         framesGivenByKF = frameInfoTemp.get(frameInfoTemp.size()-1);
		         //System.out.println("framesFromKF :"+framesGivenByKF);

		         if (framesGivenByKF > time_per_KF)
		         {
		             framesGivenByKF=(long) time_per_KF;
		         }
		                 ArrayList dumpTemp = new ArrayList();
		                 dumpTemp.add(frameNumber);
		                 dumpTemp.add(framesGivenByKF);
		                 frameDumpList.add(dumpTemp);

		                 //System.out.println("frameDumpList right now :"+frameDumpList);
		           
		         }
		 */

	}

	public void populateFramesDumpList() {
		double shotNum = 0.0;
		double framesGivenByKF = 0;

		for (long i = sortedShotsByScore.size() - 1; i >= 0; i--) {
			shotNum = 0.0;

			ArrayList<Double> shotDetailsTemp = new ArrayList<Double>();
			shotDetailsTemp = (ArrayList<Double>) sortedShotsByScore.get((int) i).clone();
			shotNum = shotDetailsTemp.get(0);//this is shot number

			ArrayList<Double> shotTempInfo = new ArrayList<Double>();
			shotTempInfo = (ArrayList<Double>) (shotHashMap.get((long) shotNum).clone());

			if (shotTempInfo == null) {
				continue; //this shot was removed, we look for some other..
			}

			long count = 0;
			for (long ii = 0; ii < shotTempInfo.size() - 2; ii++, count++) {
				ArrayList<Double> frameInfoTemp = new ArrayList<Double>();
				double frameNum = 0.0;
				frameNum = shotTempInfo.get((int) ii);
				frameInfoTemp = (ArrayList<Double>) (frameHashMap.get((long) frameNum).clone());
				//since frameInfoTemp is deffo refering to a KF, we can see its length as las value in arraylist..

				//if its no longer a KF, we will have to look for another frame
				if (frameInfoTemp.size() < Max_Frame_ArrayList_Size) //we have no KF length attribute for this frame anymore 
				{
					continue;
				}

				//System.out.println("frameInfoTemp :"+frameInfoTemp);
				framesGivenByKF = frameInfoTemp.get(frameInfoTemp.size() - 1);
				//System.out.println("framesFromKF :"+framesGivenByKF);

				if (framesGivenByKF > time_per_KF) {
					framesGivenByKF = (long) time_per_KF;
				}
				ArrayList dumpTemp = new ArrayList();
				dumpTemp.add(frameNum);
				dumpTemp.add(framesGivenByKF);
				frameDumpList.add(dumpTemp);

				//System.out.println("frameDumpList right now :"+frameDumpList);

			}

		}

		//System.out.println("frameDumpList finally is :"+frameDumpList);

		//first we order dumped videos in chronological frames

		Collections.sort(frameDumpList, new ShotsDumpCompare());

		//System.out.println("\n\n\nthe arraylist <<frameDumpList>> finally is: "+frameDumpList);
	}

	public static void increaseShots() {
		//start with shot of lowest score : (we do this because in case of extra time being left we want that to go the highest)

		double shotLength = 0.0;
		double neededShotLengthIncrease = 0.0;
		double neededVideoLengthIncrease = framesNeeded - framesAfterSummary;
		double framesFromKF = 0;
		double increasePendingFromLastShot = 0.0;
		double totalFramesAddedSoFar = 0.0;
		//for (long i=sortedShotsByScore.size()-1; i>=0;i-=sortedShotsByScore.size()-1)
		double framesAddedInShot = 0;
		double shotNumber = 0.0;

		//System.out.println("neededVideoLengthIncrease is initially "+neededVideoLengthIncrease);

		for (long i = sortedShotsByScore.size() - 1; i >= 0; i--) {
			shotNumber = 0.0;
			framesAddedInShot = 0;
			shotLength = 0.0;

			ArrayList<Double> shotDetailsTemp = new ArrayList<Double>();
			shotDetailsTemp = (ArrayList<Double>) sortedShotsByScore.get((int) i).clone();
			shotNumber = shotDetailsTemp.get(0);//this is shot number
			//As we traverse we keep KF number and more possible KF if we can do from that shot in our data
			//printHashMaps();
			//System.out.println("\n\n\n\nshotHashMap has info: ");

			for (long ii = 0; ii < shotHashMap.size(); ii++) {
				//System.out.println("at"+ii+" - "+shotHashMap.get(ii).clone());   
			}

			//System.out.println("shotNumber = "+shotNumber);
			//we have least imptt shot now.. - now we get shot length and find frames one-by-one
			ArrayList<Double> shotTempInfo = new ArrayList<Double>();
			shotTempInfo = (ArrayList<Double>) (shotHashMap.get((long) shotNumber).clone());
			shotLength = shotTempInfo.get(shotTempInfo.size() - 1);//the last value in arraylist was my shot length..  
			//System.out.println("shotLength :"+shotLength); 
			//System.out.println("increaseTimeFactor "+increaseTimeFactor);
			neededShotLengthIncrease = (Math.round(increaseTimeFactor * shotLength - shotLength))
					+ increasePendingFromLastShot;
			//System.out.println("neededShotLengthIncrease :"+neededShotLengthIncrease); 

			//System.out.println(" shotDetailsTemp = "+shotDetailsTemp);
			//System.out.println("shotTempInfo = "+shotTempInfo);

			long count = 0;
			//ArrayList shotHasKF = new ArrayList();
			for (long ii = 0; ii < shotTempInfo.size() - 2; ii++, count++) {//barring last two (score and duration) rest info in shotHashMap are KF numbers
																			//System.out.println("inside shot iterated : "+(++count));
																			//System.out.println(" inside for loop .."+ii+"times.. for shot#"+shotNumber);
				ArrayList<Double> frameInfoTemp = new ArrayList<Double>();
				double frameNumber = 0.0;
				frameNumber = shotTempInfo.get((int) ii);
				frameInfoTemp = (ArrayList<Double>) (frameHashMap.get((long) frameNumber).clone());
				//since frameInfoTemp is deffo refering to a KF, we can see its length as las value in arraylist..
				//System.out.println("frameInfoTemp :"+frameInfoTemp);
				if (neededShotLengthIncrease == 0)
					break;
				//System.out.println("frameNumber :"+frameNumber);
				framesFromKF = frameInfoTemp.get(frameInfoTemp.size() - 1);
				//System.out.println("framesFromKF :"+framesFromKF);
				if (framesFromKF <= time_per_KF) {
					//skip this KF we cant get more out of this frame
					ArrayList dumpTemp = new ArrayList();
					dumpTemp.add(frameNumber);
					dumpTemp.add(framesFromKF);
					frameDumpList.add(dumpTemp);
					//System.out.println("frameDumpList right now :"+frameDumpList);
					continue;
				} else {
					//we change the frames from this to be more, as much as we can take - if we can take more means its imptt for some time

					//framesFromKF = framesFromKF;
					if (neededShotLengthIncrease >= (framesFromKF - time_per_KF)) {
						framesAddedInShot = framesFromKF - time_per_KF;
						//System.out.println(framesAddedInShot+" frames Added In Shot#"+shotNumber+" by frame#"+frameNumber);
						totalFramesAddedSoFar += framesAddedInShot;
						neededShotLengthIncrease -= framesAddedInShot;
						//neededVideoLengthIncrease-=neededShotLengthIncrease;
						//ArrayList dumpTemp = new ArrayList();
						//dumpTemp.add(ii);
						//dumpTemp.add(framesFromKF);
						//frameDumpList.add(dumpTemp);
					} else if (neededShotLengthIncrease == 0) {
						break;
					} else //neededShotLengthIncrease is less than available -- coverable by single shot
					{
						framesFromKF = time_per_KF + neededShotLengthIncrease;
						framesAddedInShot = neededShotLengthIncrease;
						System.out.println(framesAddedInShot + " frames Added In Shot#" + shotNumber + " by frame#"
								+ frameNumber);
						totalFramesAddedSoFar += framesAddedInShot;
						neededShotLengthIncrease = 0;
						break;
						//neededVideoLengthIncrease-=neededShotLengthIncrease;
						//ArrayList dumpTemp = new ArrayList();
						//dumpTemp.add(ii);
						//dumpTemp.add(framesFromKF);
						//frameDumpList.add(dumpTemp);
					}
					/*
					if (Math.round(time_per_KF*increaseTimeFactor) <= framesFromKF)
					{
					    framesFromKF*=increaseTimeFactor;
					    neededShotLengthIncrease-=(time_per_KF*increaseTimeFactor);//we have space so we increase this KF by some value
					    framesAddedInShot=(time_per_KF*increaseTimeFactor)-(time_per_KF);
					}
					else
					{
					    //framesFromKF = framesFromKF; //we cant extend this beyond this point
					    neededShotLengthIncrease-=(framesFromKF-(time_per_KF*increaseTimeFactor));
					    framesAddedInShot = framesFromKF-time_per_KF;
					}
					 * 
					 */
				}

				ArrayList dumpTemp = new ArrayList();
				dumpTemp.add(frameNumber);
				dumpTemp.add(framesFromKF);
				frameDumpList.add(dumpTemp);
				System.out.println("frameDumpList right now :" + frameDumpList);

				neededVideoLengthIncrease = framesNeeded - (framesAfterSummary + totalFramesAddedSoFar);
				if (neededVideoLengthIncrease <= 0) {
					System.out.println("neededVideoLengthIncrease is now zero..");
					break;
				}

			}

			if (neededVideoLengthIncrease <= 0) {
				System.out.println("neededVideoLengthIncrease is now zero..");
				break;
			}

			if (neededShotLengthIncrease > 0) {

				increasePendingFromLastShot = neededShotLengthIncrease;
			}

			System.out.println("neededVideoLengthIncrease is now " + neededVideoLengthIncrease);
			System.out.println("framesAddedInShot#" + shotNumber + " is now " + framesAddedInShot);
			System.out.println("totalFramesAddedSoFar by iterations#" + count + " " + totalFramesAddedSoFar);
			System.out.println("neededVideoLengthIncrease is now " + neededVideoLengthIncrease);

		}
		System.out.println("newVideoLength is now " + (totalFramesAddedSoFar + framesAfterSummary));
		System.out.println("frameDumpList finally is :" + frameDumpList);

		//first we order dumped videos in chronological frames

		Collections.sort(frameDumpList, new ShotsDumpCompare());

		System.out.println("\n\n\nthe arraylist <<frameDumpList>> now is: " + frameDumpList);

	}

	public void dumpFrames() throws FileNotFoundException, IOException, UnsupportedAudioFileException,
			SaveWaveException {

		double totalFramesDumped = 0.0;

		dumpVideo saveVid = new dumpVideo("videoOutput.rgb", this.videoFilename);
		//saveVid.dump(0,100);
		SaveWaveFile saveAud = new SaveWaveFile(audioFilename);
		//saveAu
		for (long ii = 0; ii < frameDumpList.size(); ii++) {
			ArrayList<Double> tempDumpInfo = new ArrayList<Double>();
			tempDumpInfo = (ArrayList<Double>) (frameDumpList.get((int) ii).clone());

			double startFrame = 0L, stopFrame = 0L;

			startFrame = Math.round(tempDumpInfo.get(0));
			stopFrame = Math.round(tempDumpInfo.get(0) + tempDumpInfo.get(1) - 1.0);

			totalFramesDumped += tempDumpInfo.get(1);
			saveVid.dump(startFrame, stopFrame);

			if (startFrame != -1) {
				saveAud.dumpSound((long) startFrame, (long) stopFrame);
				//saveAud.SaveAudioFile((long)startFrame,(long)stopFrame);
			}

		}

		saveAud.saveAsWaveFile();

		//   System.out.println("totalFramesDumped :"+totalFramesDumped);

		//throw new UnsupportedOperationException("Not yet implemented");
	}

	public void dumpEverything() throws FileNotFoundException, IOException, UnsupportedAudioFileException,
			SaveWaveException {
		dumpVideo saveVid = new dumpVideo("videoOutput.rgb", this.videoFilename);
		SaveWaveFile saveAud = new SaveWaveFile(audioFilename);

		saveVid.dump(0, MAX_POSSIBLE_FRAMES - 1);

		saveAud.dumpSound(0, MAX_POSSIBLE_FRAMES - 1);
		saveAud.saveAsWaveFile();

	}

	public void populateDumpList() throws FileNotFoundException, IOException {
		//System.out.println("inside populateDumpList");
		double shotNum = 0.0;
		double framesGivenByKF = 0;

		for (long i = sortedShotsByScore.size() - 1; i >= 0; i--) {
			shotNum = 0.0;

			ArrayList<Double> shotDetailsTemp = new ArrayList<Double>();
			shotDetailsTemp = (ArrayList<Double>) sortedShotsByScore.get((int) i).clone();
			shotNum = shotDetailsTemp.get(0);//this is shot number

			ArrayList<Double> shotTempInfo = new ArrayList<Double>();
			shotTempInfo = (ArrayList<Double>) (shotHashMap.get((long) shotNum).clone());

			long count = 0;
			for (long ii = 0; ii < shotTempInfo.size() - 2; ii++, count++) {
				ArrayList<Double> frameInfoTemp = new ArrayList<Double>();
				double frameNumber = 0.0;
				frameNumber = shotTempInfo.get((int) ii);
				frameInfoTemp = (ArrayList<Double>) (frameHashMap.get((long) frameNumber).clone());
				//since frameInfoTemp is deffo refering to a KF, we can see its length as las value in arraylist..
				//System.out.println("frameInfoTemp :"+frameInfoTemp);
				framesGivenByKF = frameInfoTemp.get(frameInfoTemp.size() - 1);
				//System.out.println("framesFromKF :"+framesGivenByKF);

				if (framesGivenByKF > time_per_KF) {
					framesGivenByKF = (long) time_per_KF;
				}
				ArrayList dumpTemp = new ArrayList();
				dumpTemp.add(frameNumber);
				dumpTemp.add(framesGivenByKF);
				frameDumpList.add(dumpTemp);

				//System.out.println("frameDumpList right now :"+frameDumpList);

			}

		}

		//System.out.println("frameDumpList finally is :"+frameDumpList);

		//first we order dumped videos in chronological frames

		Collections.sort(frameDumpList, new ShotsDumpCompare());

		//System.out.println("\n\n\nthe arraylist <<frameDumpList>> finally is: "+frameDumpList);

	}

	/**
	 * @return the mvValueCheckTemp
	 */
	public ArrayList<Double> getMvValueCheckTemp() {
		return mvValueCheckTemp;
	}

	/**
	 * @param mvValueCheckTemp the mvValueCheckTemp to set
	 */
	public void setMvValueCheckTemp(ArrayList<Double> mvValueCheckTemp) {
		this.mvValueCheckTemp = mvValueCheckTemp;
	}

	private static class ShotsCompare implements Comparator {

		@Override
		public int compare(Object o1, Object o2) {
			if (!((o1 instanceof ArrayList) && (o2 instanceof ArrayList))) {
				throw new ClassCastException(" must compare two arraylists only..");
			}

			Double value1 = (Double) ((ArrayList) o1).get(1);
			Double value2 = (Double) ((ArrayList) o2).get(1);

			return value2.compareTo(value1);
		}

	}

	private static class ShotsDumpCompare implements Comparator {

		@Override
		public int compare(Object o1, Object o2) {
			if (!((o1 instanceof ArrayList) && (o2 instanceof ArrayList))) {
				throw new ClassCastException(" must compare two arraylists only..");
			}

			Double value1 = (Double) ((ArrayList) o1).get(0);
			Double value2 = (Double) ((ArrayList) o2).get(0);

			return value1.compareTo(value2);
		}

	}

}
