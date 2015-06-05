package info.hb.video.mapred.image;

import info.hb.video.mapred.image.job.HadoopJob;
import info.hb.video.mapred.image.job.HadoopJobConfiguration;
import info.hb.video.mapred.image.writable.BufferedImageWritable;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.util.ToolRunner;

/**
 * 缓冲图像灰度化作业
 *
 * 运行命令：
 * bin/hadoop jar kernel.json-jar-with-dependencies.jar bufferedImage2Gray hdfs_image_folder hdfs_output_folder
 *
 * @author wanggang
 *
 */
public class BufferedImage2Gray extends HadoopJob {

	public BufferedImage2Gray() {
		super(BufferedImage2GrayMapper.class, new HadoopJobConfiguration("<input> <output>\n", 2, "BufferedImage2Gray"));
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		int exitCode = ToolRunner.run(conf, new BufferedImage2Gray(), args);
		System.exit(exitCode);
	}

	public static class BufferedImage2GrayMapper extends
			Mapper<NullWritable, BufferedImageWritable, NullWritable, BufferedImageWritable> {

		private Graphics g;
		private BufferedImage grayImage;

		@Override
		protected void map(NullWritable key, BufferedImageWritable value, Context context) throws IOException,
				InterruptedException {

			BufferedImage colorImage = value.getImage();

			if (colorImage != null) {
				grayImage = new BufferedImage(colorImage.getWidth(), colorImage.getHeight(),
						BufferedImage.TYPE_BYTE_GRAY);
				g = grayImage.getGraphics();
				g.drawImage(colorImage, 0, 0, null);
				g.dispose();
				BufferedImageWritable biw = new BufferedImageWritable(grayImage, value.getFileName(), value.getFormat());
				context.write(NullWritable.get(), biw);
			}
		}

	}

}
