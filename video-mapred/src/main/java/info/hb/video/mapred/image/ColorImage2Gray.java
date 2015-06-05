package info.hb.video.mapred.image;

import info.hb.video.mapred.image.io.ColorImageInputFormat;
import info.hb.video.mapred.image.io.GrayImageOutputFormat;
import info.hb.video.mapred.image.writable.ColorImageWritable;
import info.hb.video.mapred.image.writable.GrayImageWritable;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.openimaj.image.FImage;
import org.openimaj.image.MBFImage;

/**
 * 彩色图像灰度化作业
 *
 * 运行命令：
 * bin/hadoop jar video-mapred-jar-with-dependencies.jar colorImage2Gray hdfs_image_folder hdfs_output_folder
 *
 * @author wanggang
 *
 */
public class ColorImage2Gray extends Configured implements Tool {

	@Override
	public int run(String[] args) throws Exception {

		if (args.length != 2) {
			System.err.printf("Usage: %s [generic options] <input> <output>\n", getClass().getSimpleName());
			ToolRunner.printGenericCommandUsage(System.err);
			return -1;
		}

		Job job = Job.getInstance(super.getConf(), "ColorImage2Gray");
		job.setJarByClass(getClass());
		job.setInputFormatClass(ColorImageInputFormat.class);
		job.setOutputFormatClass(GrayImageOutputFormat.class);
		job.setMapperClass(ColorImage2GrayMapper.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		job.setNumReduceTasks(0);
		job.setOutputKeyClass(NullWritable.class);
		job.setOutputValueClass(GrayImageWritable.class);
		return job.waitForCompletion(true) ? 0 : 1;
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		int exitCode = ToolRunner.run(conf, new ColorImage2Gray(), args);
		System.exit(exitCode);
	}

	public static class ColorImage2GrayMapper extends
			Mapper<NullWritable, ColorImageWritable, NullWritable, GrayImageWritable> {

		private FImage gray_image;
		private MBFImage color_image;
		private final GrayImageWritable fiw = new GrayImageWritable();

		@Override
		public void map(NullWritable key, ColorImageWritable value, Context context) throws IOException,
				InterruptedException {
			color_image = value.getImage();

			if (color_image != null) {
				gray_image = color_image.flatten();
				fiw.setFormat(value.getFormat());
				fiw.setFileName(value.getFileName());
				fiw.setImage(gray_image);
				context.write(NullWritable.get(), fiw);
			}

		}

	}

}
