package info.hb.video.mapred.image.io;

import info.hb.video.mapred.image.writable.ColorImageWritable;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.openimaj.image.ImageUtilities;

/**
 * 彩色图像记录写操作类
 *
 * @author wanggang
 *
 */
public class ColorImageRecordWriter extends ImageRecordWriter<ColorImageWritable> {

	ColorImageRecordWriter(TaskAttemptContext taskAttemptContext) {
		super(taskAttemptContext);
	}

	@Override
	protected void writeImage(ColorImageWritable image, FSDataOutputStream imageFile) throws IOException {
		ImageUtilities.write(image.getImage(), image.getFormat(), (OutputStream) imageFile);
	}

}
