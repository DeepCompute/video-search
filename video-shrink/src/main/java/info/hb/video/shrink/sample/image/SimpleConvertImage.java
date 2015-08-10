package info.hb.video.shrink.sample.image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

/**
 * The following is a simpler version which does the same thing.
 * It uses the BufferedImage class, which is a proved more efficient way.
 * 效率更高
 *
 * @author wanggang
 *
 */
public class SimpleConvertImage {

	public static void main(String[] args) throws IOException {
		String dirName = "image";
		ByteArrayOutputStream baos = new ByteArrayOutputStream(1000);
		BufferedImage img = ImageIO.read(new File(dirName, "test1.jpg"));
		ImageIO.write(img, "jpg", baos);
		baos.flush();

		String base64String = Base64.encode(baos.toByteArray());
		baos.close();
		// 获取图片字节数组
		byte[] bytearray = Base64.decode(base64String);
		// 将字节数组写到图片中
		BufferedImage imag = ImageIO.read(new ByteArrayInputStream(bytearray));
		ImageIO.write(imag, "jpg", new File(dirName, "snap.jpg"));
	}

}
