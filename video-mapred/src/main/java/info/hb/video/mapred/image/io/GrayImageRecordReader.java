package info.hb.video.mapred.image.io;

import info.hb.video.mapred.image.writable.GrayImageWritable;

import java.io.InputStream;

import org.apache.hadoop.fs.FSDataInputStream;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;

/**
 * 灰度图像读操作类
 *
 * @author wanggang
 *
 */
public class GrayImageRecordReader extends ImageRecordReader<GrayImageWritable> {

	@Override
	protected GrayImageWritable createImageWritable() {
		return new GrayImageWritable();
	}

	@Override
	protected GrayImageWritable readImage(FSDataInputStream fsDataInputStream) {
		GrayImageWritable fiw = new GrayImageWritable();
		FImage fi;
		try {
			fi = ImageUtilities.readF((InputStream) fsDataInputStream);
		} catch (Exception e) {
			fi = null;
		}
		fiw.setImage(fi);
		return fiw;
	}

}
