package info.hb.video.shrink.videosummary;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

public class ColorHistogram {

	private static final int numOfBins = 16; //we devide 256 possible Y values equally into 16 bins

	private static int arrBins[] = new int[numOfBins]; // this array store the number of occurences of a particularly range of Y value at specific bin number

	public static void ColorHistogram() {

	}

	public static double ComputeScore(double yy[][]) {

		//int count = 0;

		for (int i = 0; i < numOfBins; i++)
			arrBins[i] = 0;

		for (int y = 0; y < VideoFile.HEIGHT; y++) {
			for (int x = 0; x < VideoFile.WIDTH; x++) {

				//count++;

				//System.out.println("Pixels covered was "+count);

				int binNumber = 0;

				//System.out.println("yy["+x+"]["+y+"] is "+yy[x][y]);

				//if (Math.round(yy[x][y]) < 0 || Math.round(yy[x][y]) > 255)
				//System.out.println("Error - Math.round(yy[x][y]) is "+Math.round(yy[x][y]));

				binNumber = (int) (Math.round(yy[x][y] + 1.0)) / numOfBins;

				if (binNumber == numOfBins)
					binNumber--;

				arrBins[binNumber]++;

				//if (arrBins[binNumber] > count)
				//  System.out.println(" count for bin was "+arrBins[binNumber]);

			}

		}

		double probOfBins[] = new double[numOfBins];
		double entropy = 0.0;

		for (int i = 0; i < numOfBins; i++) {

			probOfBins[i] = 1.0;

			probOfBins[i] = (double) arrBins[i] / (VideoFile.HEIGHT * VideoFile.WIDTH);

			double temp = 0.0;

			if (probOfBins[i] < 0 || probOfBins[i] > 1)
				System.out.println(" probability for bin was " + probOfBins[i]);

			if (probOfBins[i] != 0)
				temp -= (probOfBins[i]) * ((Math.log(probOfBins[i])) / (Math.log(2)));

			entropy += temp;

		}

		//System.out.println(" Entropy is "+entropy);

		return entropy;
	}

}
