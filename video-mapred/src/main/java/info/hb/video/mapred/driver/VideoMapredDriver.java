package info.hb.video.mapred.driver;

import info.hb.video.mapred.image.BufferedImage2Gray;
import info.hb.video.mapred.image.BufferedImageEdgeDetection;
import info.hb.video.mapred.image.BufferedImageFormatChange;
import info.hb.video.mapred.image.BufferedImageProcess;
import info.hb.video.mapred.image.BufferedImageSequenceInput;
import info.hb.video.mapred.image.BufferedImageSequenceOutput;
import info.hb.video.mapred.image.ColorImage2Gray;
import info.hb.video.mapred.image.CombineBufferedImageProcess;

import org.apache.hadoop.util.ProgramDriver;

/**
 * 驱动类
 *
 * @author wanggang
 *
 */
public class VideoMapredDriver {

	/**
	 * 主函数
	 */
	public static void main(String[] args) {

		int exitCode = -1;
		org.apache.hadoop.util.ProgramDriver pgd = new ProgramDriver();
		try {
			// 基本图像分布式处理
			pgd.addClass("bufferedImageProcess", BufferedImageProcess.class,
					"BufferedImage图像处理(小图像)，通过ImageProcessor实现类指定处理算法");
			pgd.addClass("combineBufferedImageProcess", CombineBufferedImageProcess.class,
					"BufferedImage图像处理(大图像)，通过ImageProcessor实现类指定处理算法");
			pgd.addClass("bufferedImageFormatChange", BufferedImageFormatChange.class, "图片格式转换");
			pgd.addClass("bufferedImageEdgeDetection", BufferedImageEdgeDetection.class, "缓冲图像边缘检测");
			pgd.addClass("bufferedImageSequenceOutput", BufferedImageSequenceOutput.class, "缓冲图像序列化输出");
			pgd.addClass("bufferedImageSequenceInput", BufferedImageSequenceInput.class, "缓冲图像序列化输入");
			pgd.addClass("bufferedImage2Gray", BufferedImage2Gray.class, "缓冲图像灰度化");
			pgd.addClass("colorImage2Gray", ColorImage2Gray.class, "彩色图像灰度化");
			pgd.driver(args);
			// Success
			exitCode = 0;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}

		System.exit(exitCode);

	}

}
