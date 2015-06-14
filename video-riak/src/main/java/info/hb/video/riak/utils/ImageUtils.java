package info.hb.video.riak.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * 图片处理工具类
 *
 * @author wanggang
 *
 */
public class ImageUtils {

	public static byte[] transBI2BytesBytes(BufferedImage bi, String format) throws IOException {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();) {
			ImageIO.write(bi, format, baos);
			baos.flush();
			return baos.toByteArray();
		}
	}

}
