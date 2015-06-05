package info.hb.video.mapred.image;

import info.hb.video.mapred.image.io.BufferedImageInputFormat;
import info.hb.video.mapred.image.io.BufferedImageOutputFormat;
import info.hb.video.mapred.image.process.ImageProcessor;
import info.hb.video.mapred.image.writable.BufferedImageWritable;

import java.awt.image.BufferedImage;
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

/**
 * 基于BufferedImage图像表示来运行只有Map的作业。
 *
 * 每个Map接受一个图像，并基于ImageProcessor（通过命令行指定实现类）来处理，
 * 该作业使用BufferedImageInputFormat输入格式，不适合小文件处理。
 *
 * 运行命令：
 * bin/hadoop jar video-mapred-jar-with-dependencies.jar bufferedImageProcess info.hb.video.mapred.image.process.BI2Gray hdfs_image_folder hdfs_output_folder
 *
 * @author wanggang
 *
 */
public class BufferedImageProcess extends Configured implements Tool {

	public static class BufferedImageProcessMapper extends
			Mapper<NullWritable, BufferedImageWritable, NullWritable, BufferedImageWritable> {

		BufferedImage image, processedImage;
		BufferedImageWritable outImage;

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public void map(NullWritable key, BufferedImageWritable value, Context context) throws IOException,
				InterruptedException {
			image = value.getImage();
			if (image != null) {
				Configuration conf = context.getConfiguration();
				String processorClassName = conf.get("biprocessor.class");
				try {
					Class ipClass = Class.forName(processorClassName);
					ImageProcessor<BufferedImage> ip = (ImageProcessor<BufferedImage>) ipClass.newInstance();
					processedImage = ip.processImage(image);
					outImage = new BufferedImageWritable(processedImage, value.getFileName(), value.getFormat());
					context.write(NullWritable.get(), outImage);
				} catch (ClassNotFoundException e) {
					throw new RuntimeException("Class not found " + processorClassName);
				} catch (InstantiationException e) {
					throw new RuntimeException("Cannot create object of class " + processorClassName);
				} catch (IllegalAccessException e) {
					throw new RuntimeException("Illegal access for class " + processorClassName);
				}

			}
		}
	}

	protected Job basicSetup(String args[]) throws IOException {
		if (args.length != 3) {
			System.err.printf("Usage: %s [generic options] <image_processor_class> <input> <output>\n", getClass()
					.getSimpleName());
			ToolRunner.printGenericCommandUsage(System.err);
			System.exit(-1);
		}

		Configuration conf = super.getConf();
		conf.set("biprocessor.class", args[0]);
		Job job = Job.getInstance(conf);
		job.setJarByClass(getClass());
		job.setOutputFormatClass(BufferedImageOutputFormat.class);
		FileInputFormat.addInputPath(job, new Path(args[1]));
		FileOutputFormat.setOutputPath(job, new Path(args[2]));
		job.setMapperClass(BufferedImageProcessMapper.class);
		job.setNumReduceTasks(0);
		job.setOutputKeyClass(NullWritable.class);
		job.setOutputValueClass(BufferedImageWritable.class);
		return job;
	}

	@Override
	public int run(String[] args) throws Exception {

		Job job = basicSetup(args);

		job.setJobName("BufferedImageProcess");
		job.setInputFormatClass(BufferedImageInputFormat.class);

		return job.waitForCompletion(true) ? 0 : 1;
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		int exitCode = ToolRunner.run(conf, new BufferedImageProcess(), args);
		System.exit(exitCode);
	}

}
