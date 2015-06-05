package info.hb.video.mapred.image.writable;

import info.hb.video.mapred.image.utils.ImageConvertUtils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.openimaj.image.FImage;

/**
 * 基于灰度图像序列化表示
 *
 * @author wanggang
 *
 */
public class GrayImageWritable extends ImageWritable<FImage> {

	@Override
	public FImage getImage() {
		return im;
	}

	@Override
	public void setImage(FImage fi) {
		this.im = fi;
	}

	@Override
	public void write(DataOutput out) throws IOException {
		super.write(out);

		// 获取图像宽度和高度
		int width = im.getWidth();
		int height = im.getHeight();

		// 写入图像宽度和高度
		out.writeInt(width);
		out.writeInt(height);

		// 写入图像的所有像素值
		out.write(ImageConvertUtils.grayImagePixelsToByteArray(im));
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		super.readFields(in);

		// 读取图像宽度和高度
		int width = in.readInt();
		int height = in.readInt();

		// 创建像素缓冲区
		byte[] bPixels = new byte[4 * width * height];

		// 读取像素数据
		in.readFully(bPixels);

		im = new FImage(width, height);
		ImageConvertUtils.setGrayImagePixelsFromByteArray(im, bPixels);
	}

}
