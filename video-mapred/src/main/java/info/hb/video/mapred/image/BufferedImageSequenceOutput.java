package info.hb.video.mapred.image;

import info.hb.video.mapred.image.io.BufferedImageCombineInputFormat;
import info.hb.video.mapred.image.writable.BufferedImageWritable;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * 缓冲图像序列化输出作业
 *
 * 运行命令：
 * bin/hadoop jar video-mapred-jar-with-dependencies.jar bufferedImageSequenceOutput hdfs_image_folder hdfs_output_folder
 *
 * @author wanggang
 *
 */
public class BufferedImageSequenceOutput extends Configured implements Tool {

	@Override
	public int run(String[] args) throws Exception {

		if (args.length != 2) {
			System.err.printf("Usage: %s [generic options] <input> <output>\n", getClass().getSimpleName());
			ToolRunner.printGenericCommandUsage(System.err);
			return -1;
		}

		Job job = Job.getInstance(super.getConf(), "BufferedImageSequenceOutput");
		job.setJarByClass(getClass());
		job.setInputFormatClass(BufferedImageCombineInputFormat.class);
		job.setOutputFormatClass(SequenceFileOutputFormat.class);
		job.setMapperClass(BufferedImageSequenceOutputMapper.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		job.setNumReduceTasks(0);
		job.setOutputKeyClass(NullWritable.class);
		job.setOutputValueClass(BufferedImageWritable.class);
		return job.waitForCompletion(true) ? 0 : 1;
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		int exitCode = ToolRunner.run(conf, new BufferedImageSequenceOutput(), args);
		System.exit(exitCode);
	}

	public static class BufferedImageSequenceOutputMapper extends
			Mapper<NullWritable, BufferedImageWritable, NullWritable, BufferedImageWritable> {

		@Override
		public void map(NullWritable key, BufferedImageWritable value, Context context) throws IOException,
				InterruptedException {

			if (value.getImage() != null) {
				context.write(NullWritable.get(), value);
			}

		}

	}

}
