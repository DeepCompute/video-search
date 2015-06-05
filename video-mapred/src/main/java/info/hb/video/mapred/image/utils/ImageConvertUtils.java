package info.hb.video.mapred.image.utils;

import java.nio.ByteBuffer;

import org.openimaj.image.FImage;
import org.openimaj.image.MBFImage;

/**
 * 几种图像格式的工具类
 *
 * @author wanggang
 *
 */
public class ImageConvertUtils {

	// 将灰度图像的像素转换成字节数组
	public static byte[] grayImagePixelsToByteArray(FImage im) {
		int width = im.getWidth();
		int height = im.getHeight();
		ByteBuffer buffer = ByteBuffer.allocate(4 * width * height);
		for (int i = 0; i < height; i++)
			for (int j = 0; j < width; j++)
				buffer.putFloat(im.pixels[i][j]);

		return buffer.array();
	}

	// 将彩色图像的像素转换成字节数组
	public static byte[] colorImagePixelsToByteArray(MBFImage im) {
		int width = im.getWidth();
		int height = im.getHeight();
		int bands = im.numBands();
		ByteBuffer buffer = ByteBuffer.allocate(4 * width * height * bands);
		for (int b = 0; b < bands; b++) {
			FImage fim = im.bands.get(b);
			for (int i = 0; i < height; i++)
				for (int j = 0; j < width; j++)
					buffer.putFloat(fim.pixels[i][j]);
		}
		return buffer.array();
	}

	// 将字节数组转换成灰度图像的像素
	public static void setGrayImagePixelsFromByteArray(FImage im, byte[] bPixels) {
		ByteBuffer buffer = ByteBuffer.wrap(bPixels);
		for (int i = 0; i < im.height; i++)
			for (int j = 0; j < im.width; j++)
				im.pixels[i][j] = buffer.getFloat();
	}

	// 将字节数组转换成彩色图像的像素
	public static void setColorImagePixelsFromByteArray(MBFImage im, byte[] bPixels) {
		ByteBuffer buffer = ByteBuffer.wrap(bPixels);
		int bands = im.numBands();
		for (int b = 0; b < bands; b++) {
			FImage fim = im.bands.get(b);
			for (int i = 0; i < fim.height; i++)
				for (int j = 0; j < fim.width; j++)
					fim.pixels[i][j] = buffer.getFloat();
		}
	}

}
