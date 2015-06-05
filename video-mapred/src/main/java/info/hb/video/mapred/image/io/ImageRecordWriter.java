package info.hb.video.mapred.image.io;

import info.hb.video.mapred.image.writable.ImageWritable;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * 图像记录写操作抽象类
 * 
 * @author wanggang
 *
 * @param <I> 图像对象
 */
public abstract class ImageRecordWriter<I extends ImageWritable<?>> extends RecordWriter<NullWritable, I> {

	protected TaskAttemptContext taskAttemptContext = null;

	ImageRecordWriter(TaskAttemptContext taskAttemptContext) {
		this.taskAttemptContext = taskAttemptContext;
	}

	protected abstract void writeImage(I image, FSDataOutputStream imageFile) throws IOException;

	@Override
	public void write(NullWritable nullWritable, I image) throws IOException, InterruptedException {

		if (image.getImage() != null) {
			FSDataOutputStream imageFile = null;
			Configuration job = taskAttemptContext.getConfiguration();
			Path file = FileOutputFormat.getOutputPath(taskAttemptContext);
			FileSystem fs = file.getFileSystem(job);
			// 使用图像文件名、格式和路径来构造Path
			Path imageFilePath = new Path(file, image.getFileName() + "." + image.getFormat());

			try {
				// 创建文件
				imageFile = fs.create(imageFilePath);
				writeImage(image, imageFile);

				// 使用ImageIO写入图像到文件中
				// ImageIO.write(bufferedImageWritable.getBufferedImage(), bufferedImageWritable.getFormat(), imageFile);
			} finally {
				IOUtils.closeStream(imageFile);
			}
		}

	}

	@Override
	public void close(TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {
		//
	}

}
