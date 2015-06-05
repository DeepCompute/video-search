package info.hb.video.mapred.image;

import info.hb.video.mapred.image.io.BufferedImageCombineInputFormat;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * 基于BufferedImage图像表示来运行只有Map的作业。
 *
 * 每个Map接受一个图像，并基于ImageProcessor（通过命令行指定实现类）来处理，
 * 该作业使用BufferedImageCombineInputFormat输入格式，适合小文件处理。
 *
 * 运行命令：
 * bin/hadoop jar video-mapred-jar-with-dependencies.jar combineBufferedImageProcess info.hb.video.mapred.image.process.BI2Gray hdfs_image_folder hdfs_output_folder
 *
 * @author wanggang
 *
 */
public class CombineBufferedImageProcess extends BufferedImageProcess implements Tool {

	@Override
	public int run(String[] args) throws Exception {

		Job job = basicSetup(args);

		job.setJobName("CombineBufferedImageProcess");
		job.setInputFormatClass(BufferedImageCombineInputFormat.class);

		return job.waitForCompletion(true) ? 0 : 1;
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		int exitCode = ToolRunner.run(conf, new CombineBufferedImageProcess(), args);
		System.exit(exitCode);
	}

}
