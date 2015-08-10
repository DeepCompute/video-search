package info.hb.video.shrink.sample.video;

import info.hb.video.shrink.keyframes.KeyFramesCore;

import java.io.File;
import java.util.List;

import org.openimaj.image.MBFImage;
import org.openimaj.video.Video;
import org.openimaj.video.xuggle.XuggleVideo;
import org.openimaj.video.xuggle.XuggleVideoWriter;

public class WriteVideoDemo {

	public static void main(String[] args) {
		// 读取视频文件
		Video<MBFImage> video = new XuggleVideo(new File("/home/wanggang/develop/deeplearning/test-videos/test1.ts"));
		System.err.println("读取视频文件完成。");
		// 提取关键帧
		List<MBFImage> frames = KeyFramesCore.extract(video);
		System.err.println("提取关键帧完成。");
		// 保存所有关键帧到MP4文件中
		XuggleVideoWriter xvw = new XuggleVideoWriter("test.mp4", frames.get(0).getWidth(), frames.get(0).getHeight(),
				25);
		for (MBFImage frame : frames) {
			xvw.processFrame(frame);
		}
		xvw.processingComplete();
		System.err.println("保存所有关键帧为MP4文件完成。");

	}

}
