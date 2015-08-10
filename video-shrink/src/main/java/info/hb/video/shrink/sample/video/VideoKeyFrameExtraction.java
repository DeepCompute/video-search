package info.hb.video.shrink.sample.video;

import java.io.InputStreamReader;
import java.io.LineNumberReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VideoKeyFrameExtraction {
	private static Logger logger = LoggerFactory.getLogger(VideoKeyFrameExtraction.class);

	public static int exec(String cmd) {
		try {
			String[] cmdA = { "/bin/sh", "-c", cmd };
			Process process = Runtime.getRuntime().exec(cmdA);

			LineNumberReader br = new LineNumberReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = br.readLine()) != null) {
				logger.info(line);
			}
			while (process.isAlive()) {
				Thread.sleep(1000);
			}
			return process.exitValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 1;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int pwdString = exec("pwd");
		int netsString = exec(
"ffmpeg -i /home/donglei/test-videos/test1.ts -vf select=\"eq(pict_type\\,PICT_TYPE_I)\" -vsync 2 -f image2 /home/donglei/test-videos/output/thumbnails-%02d.jpeg");

		System.out.println("==========获得值=============");
		System.out.println(pwdString);
		System.out.println(netsString);
	}

}
