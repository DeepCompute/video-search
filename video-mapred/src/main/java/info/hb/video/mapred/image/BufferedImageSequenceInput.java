package info.hb.video.mapred.image;

import info.hb.video.mapred.image.io.BufferedImageOutputFormat;
import info.hb.video.mapred.image.writable.BufferedImageWritable;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * 缓冲图像序列化输入作业
 *
 * 运行命令：
 * bin/hadoop jar video-mapred-jar-with-dependencies.jar bufferedImageSequenceInput hdfs_seq_image_folder hdfs_output_folder
 *
 * @author wanggang
 *
 */
public class BufferedImageSequenceInput extends Configured implements Tool {

	@Override
	public int run(String[] args) throws Exception {

		if (args.length != 2) {
			System.err.printf("Usage: %s [generic options] <input> <output>\n", getClass().getSimpleName());
			ToolRunner.printGenericCommandUsage(System.err);
			return -1;
		}

		Job job = Job.getInstance(super.getConf(), "BufferedImageSequenceInput");
		job.setJarByClass(getClass());
		// 输入文件是序列化文件
		job.setInputFormatClass(SequenceFileInputFormat.class);
		job.setOutputFormatClass(BufferedImageOutputFormat.class);
		job.setMapperClass(BufferedImageSequenceInputMapper.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		job.setNumReduceTasks(0);
		job.setOutputKeyClass(NullWritable.class);
		job.setOutputValueClass(BufferedImageWritable.class);
		return job.waitForCompletion(true) ? 0 : 1;
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		int exitCode = ToolRunner.run(conf, new BufferedImageSequenceInput(), args);
		System.exit(exitCode);
	}

	public static class BufferedImageSequenceInputMapper extends
			Mapper<NullWritable, BufferedImageWritable, NullWritable, BufferedImageWritable> {

		/**
		 * key：图像文件名
		 */
		@Override
		public void map(NullWritable key, BufferedImageWritable value, Context context) throws IOException,
				InterruptedException {

			if (value.getImage() != null) {
				context.write(NullWritable.get(), value);
			}

		}

	}

}
