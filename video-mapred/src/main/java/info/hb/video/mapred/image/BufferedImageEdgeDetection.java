package info.hb.video.mapred.image;

import info.hb.video.mapred.image.job.HadoopJob;
import info.hb.video.mapred.image.job.HadoopJobConfiguration;
import info.hb.video.mapred.image.writable.BufferedImageWritable;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.util.ToolRunner;

/**
 * 缓冲图像边缘检测作业
 *
 * 运行命令：
 * bin/hadoop jar video-mapred-jar-with-dependencies.jar bufferedImageEdgeDetection hdfs_image_folder hdfs_output_folder
 *
 * @author wanggang
 *
 */
public class BufferedImageEdgeDetection extends HadoopJob {

	public BufferedImageEdgeDetection() {
		super(BufferedImageEdgeDetectionMapper.class, new HadoopJobConfiguration("<input> <output>\n", 2,
				"BufferedImageEdgeDetection"));
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		int exitCode = ToolRunner.run(conf, new BufferedImageEdgeDetection(), args);
		System.exit(exitCode);
	}

	public static class BufferedImageEdgeDetectionMapper extends
			Mapper<NullWritable, BufferedImageWritable, NullWritable, BufferedImageWritable> {

		private static final int DIMENSION = 3;
		private static final float[] KERNEL = { 0.0f, -1.0f, 0.0f, -1.0f, 4.0f, -1.0f, 0.0f, -1.0f, 0.0f };

		@Override
		protected void setup(Context context) throws IOException, InterruptedException {
			super.setup(context);
			/*File file = new File("kernel.json");
			String str = FileUtils.readFileToString(file, "utf-8");
			JSONObject json = new JSONObject(str);
			JSONArray jsonker = json.getJSONArray("kernel");

			float[] ker = new float[jsonker.length()];
			for (int i = 0; i < jsonker.length(); i++) {
				ker[i] = Float.parseFloat(jsonker.get(i).toString());
			}

			dimension = (int) json.get("dim");
			kernel = ker;*/
		}

		@Override
		public void map(NullWritable key, BufferedImageWritable value, Context context) throws IOException,
				InterruptedException {
			BufferedImage sourceImg = value.getImage();

			if (sourceImg != null) {
				BufferedImageOp kernelFilter = new ConvolveOp(new Kernel(DIMENSION, DIMENSION, KERNEL));
				BufferedImage destinationImg = kernelFilter.filter(sourceImg, null);
				BufferedImageWritable biw = new BufferedImageWritable(destinationImg, value.getFileName(),
						value.getFormat());
				context.write(NullWritable.get(), biw);
			}

		}

	}

}
