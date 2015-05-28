package info.hb.video.mapred.wm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author wgybzb
 *
 */
public class Test {

	public static void main(String[] args) throws IOException, InterruptedException {
		String inputPath = "/home/wanggang/develop/deeplearning/sample-videos/test.ts";
		Process process = Runtime.getRuntime().exec(
				new String[] { "bash", "-c",
						"/usr/local/bin/ffmpeg -i " + inputPath + " 2>&1 | grep Duration | awk '{print($2)}'" });
		int result = process.waitFor();
		System.out.println(result);
		InputStream stream = process.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

		while (true) {
			String duration = reader.readLine();
			if (duration == null) {
				break;
			}
			System.out.println(duration);
		}
	}

}