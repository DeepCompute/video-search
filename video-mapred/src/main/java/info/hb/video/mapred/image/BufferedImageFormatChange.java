package info.hb.video.mapred.image;

import info.hb.video.mapred.image.job.HadoopJob;
import info.hb.video.mapred.image.job.HadoopJobConfiguration;
import info.hb.video.mapred.image.writable.BufferedImageWritable;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.util.ToolRunner;

/**
 * 图片格式转换作业
 *
 * 运行命令：
 * bin/hadoop jar video-mapred-jar-with-dependencies.jar bufferedImageFormatChange hdfs_image_folder hdfs_output_folder image_format
 *
 * @author wanggang
 *
 */
public class BufferedImageFormatChange extends HadoopJob {

	public BufferedImageFormatChange() {
		super(BufferedImageFormatChangeMapper.class, new HadoopJobConfiguration("<input> <output> <format>\n", 3,
				"BufferedImageFormatChange"));
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		int exitCode = ToolRunner.run(conf, new BufferedImageFormatChange(), args);
		System.exit(exitCode);
	}

	@Override
	protected void preprocess(String[] args, Job job) {
		Configuration conf = job.getConfiguration();
		conf.set("format", args[2]);
	}

	public static class BufferedImageFormatChangeMapper extends
			Mapper<NullWritable, BufferedImageWritable, NullWritable, BufferedImageWritable> {

		@Override
		public void map(NullWritable key, BufferedImageWritable value, Context context) throws IOException,
				InterruptedException {
			Configuration conf = context.getConfiguration();
			value.setFormat(conf.get("format"));
			context.write(NullWritable.get(), value);
		}

	}

}
