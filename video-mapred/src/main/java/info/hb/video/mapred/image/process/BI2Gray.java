package info.hb.video.mapred.image.process;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * 将BufferedImage类型彩色图像转换成灰度图
 *
 * @author wanggang
 *
 */
public class BI2Gray implements ImageProcessor<BufferedImage> {

	/**
	 * 缓冲图灰度化处理
	 */
	@Override
	public BufferedImage processImage(BufferedImage image) {
		if (image != null) {
			BufferedImage grayImage = new BufferedImage(image.getWidth(), image.getHeight(),
					BufferedImage.TYPE_BYTE_GRAY);
			Graphics g = grayImage.getGraphics();
			g.drawImage(image, 0, 0, null);
			g.dispose();
			return grayImage;
		}
		return null;
	}

}
