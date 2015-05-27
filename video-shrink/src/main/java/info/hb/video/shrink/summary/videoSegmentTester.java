package info.hb.video.shrink.summary;

public class videoSegmentTester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		if (args.length < 1) {
			System.err.println("usage: java videoSegment file.");
			return;
		}

		videoSegment vs = new videoSegment(args[0]);
		vs.analyze();
	}

}
