package info.hb.video.mapred.image.writable;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * 基于BufferedImage(缓冲图)的图像序列化表示
 * 
 * @author wanggang
 *
 */
public class BufferedImageWritable extends ImageWritable<BufferedImage> {

	public BufferedImageWritable() {
		//
	}

	public BufferedImageWritable(BufferedImage bi) {
		this.im = bi;
	}

	public BufferedImageWritable(BufferedImage bi, String filename, String format) {
		this.im = bi;
		setFileName(filename);
		setFormat(format);
	}

	@Override
	public BufferedImage getImage() {
		return im;
	}

	@Override
	public void setImage(BufferedImage bi) {
		this.im = bi;
	}

	@Override
	public void write(DataOutput out) throws IOException {
		super.write(out);

		// 写入图像：将图像转换成字节数组
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(im, this.getFormat(), baos);
		baos.flush();
		byte[] bytes = baos.toByteArray();
		baos.close();
		// 写入字节数组大小
		out.writeInt(bytes.length);
		// 写入图像字节数组
		out.write(bytes);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		super.readFields(in);

		// 读取图像字节数组大小
		int bArraySize = in.readInt();
		// 读取图像字节数组
		byte[] bArray = new byte[bArraySize];
		in.readFully(bArray);
		// 将字节数组中读取到图像中
		im = ImageIO.read(new ByteArrayInputStream(bArray));
	}

}
