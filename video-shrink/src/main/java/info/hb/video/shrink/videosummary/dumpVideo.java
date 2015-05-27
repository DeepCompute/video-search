package info.hb.video.shrink.videosummary;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

public class dumpVideo {

	/**
	*
	* This function attempts to dump the video frames taken as input startFrameNum 
	* and endFrameNum (these numbers correspond to location in original video file)
	* save it in a new video file - videoOutput.rgb
	* 
	*/

	/**
	 * @param args the command line arguments
	 */

	//private static RandomAccessFile rafOut;
	private static RandomAccessFile rafIn;
	private static File videoOut;
	private static final double AUDIO_PER_FRAME = 22050 / 24;
	private static final int VIDEO_FRAMESIZE = 320 * 240 * 3;
	private static long outFileFrames = 0L;

	private static FileOutputStream outFileStream;

	//private static final int waveFileHeaderOffset = 44;

	public static long getFinalVidLength() {
		return videoOut.length();
	}

	public dumpVideo(String outfile, String infile) throws FileNotFoundException, IOException {

		rafIn = new RandomAccessFile(infile, "r");
		videoOut = new File(outfile);

		boolean success = videoOut.createNewFile();

		if (!success)
			videoOut.delete();
		videoOut.createNewFile();

		if (!(videoOut.canWrite()))
			System.err.println("Cannot create output video file..");
		//rafOut.seek(0);

		rafIn.seek(0);

		outFileStream = new FileOutputStream(videoOut, true);
	}

	public static void dump(double startFrameNum, double stopFrameNum) throws IOException {

		//We first get the bytes from the input file

		//System.out.println("will print from frame#"+startFrameNum+" to frame#"+stopFrameNum); 

		long seekPos = (long) (startFrameNum * VIDEO_FRAMESIZE);
		byte[] bytes = new byte[VIDEO_FRAMESIZE];

		for (long i = 0; i <= (stopFrameNum - startFrameNum); i++, seekPos += VIDEO_FRAMESIZE) {

			rafIn.seek(seekPos);
			rafIn.read(bytes);
			//if (seekPos > rafIn.length()) return;
			outFileStream.write(bytes); // we have automaticallly stored position of last store
			//System.out.println(" File length now is: "+audioOut.length());

		}

	}

}
