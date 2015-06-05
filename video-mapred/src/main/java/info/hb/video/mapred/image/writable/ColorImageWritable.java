package info.hb.video.mapred.image.writable;

import info.hb.video.mapred.image.utils.ImageConvertUtils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.openimaj.image.MBFImage;

/**
 * 基于彩色图像序列化表示
 *
 * @author wanggang
 *
 */
public class ColorImageWritable extends ImageWritable<MBFImage> {

	@Override
	public MBFImage getImage() {
		return im;
	}

	@Override
	public void setImage(MBFImage mbfi) {
		this.im = mbfi;
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

		// 写入图像波段数量，注：图像波段/频带主要针对彩色图像
		out.writeInt(im.bands.size());

		// 写入图像的所有像素值
		out.write(ImageConvertUtils.colorImagePixelsToByteArray(im));
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		super.readFields(in);

		// 读取图像宽度和高度
		int width = in.readInt();
		int height = in.readInt();

		// 读取图像的波段数量
		int n = in.readInt();

		// 创建像素缓冲区
		byte[] bPixels = new byte[4 * width * height * n];

		// 读取像素数据
		in.readFully(bPixels);

		im = new MBFImage(width, height, n);

		ImageConvertUtils.setColorImagePixelsFromByteArray(im, bPixels);
	}

}
