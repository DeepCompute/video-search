package info.hb.video.shrink.keyframes;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.GrayFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zx.soft.utils.log.LogbackUtil;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageDecoder;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * 图片相似度比较
 *
 * @author wanggang
 *
 */
@SuppressWarnings("restriction")
public class ImageCompare {

	private static Logger logger = LoggerFactory.getLogger(ImageCompare.class);

	protected BufferedImage img1 = null;
	protected BufferedImage img2 = null;
	protected BufferedImage imgc = null;
	// 垂直区域个数
	protected int comparex = 0;
	// 水平区域个数
	protected int comparey = 0;
	// 敏感值（sensitivity）
	protected int factorA = 0;
	// 稳定值（stabilizer）
	protected int factorD = 10;
	protected boolean match = Boolean.FALSE;
	// 0：默认不调试，1: 纹理迹象改变（textual indication of change）, 2: 不同区域（difference of factors）
	protected int debugMode = 0;
	private int diffMax = 0;

	/**
	 * 对比两幅图片是否相似，静态方法
	 */
	public static boolean matchImage(BufferedImage image1, BufferedImage image2) {
		ImageCompare ic = new ImageCompare(image1, image2);
		// 设置比较时参数
		ic.setParameters(80, 50, 5, 100);
		// 设置Debug模式
		ic.setDebugMode(0);
		// 比较操作
		ic.compare();
		// 返回比较结果
		return ic.match();
	}

	// 构造函数：传入文件名
	public ImageCompare(String file1, String file2) {
		this(loadJPG(file1), loadJPG(file2));
	}

	// 构造函数：传入Image
	public ImageCompare(Image img1, Image img2) {
		this(imageToBufferedImage(img1), imageToBufferedImage(img2));
	}

	// 构造函数：传入BI
	public ImageCompare(BufferedImage img1, BufferedImage img2) {
		this.img1 = img1;
		this.img2 = img2;
		autoSetParameters();
	}

	// 默认参数
	protected void autoSetParameters() {
		comparex = 10;
		comparey = 10;
		factorA = 10;
		factorD = 10;
	}

	// 在异同检测过程中使用的参数
	public void setParameters(int x, int y, int factorA, int factorD) {
		this.comparex = x;
		this.comparey = y;
		this.factorA = factorA;
		this.factorD = factorD;
	}

	// 进行比较过程时，设置Debug模式来观察输出情况，了解比较过程
	public void setDebugMode(int m) {
		this.debugMode = m;
	}

	// 对比图片
	public void compare() {
		// 转换成灰度图
		img1 = imageToBufferedImage(GrayFilter.createDisabledImage(img1));
		img2 = imageToBufferedImage(GrayFilter.createDisabledImage(img2));
		// x,y每块（段）的大小
		int blocksx = img1.getWidth() / comparex;
		int blocksy = img1.getHeight() / comparey;
		// 设置默认为匹配，如果找到变换区域则修改为不匹配
		this.match = Boolean.TRUE;
		// 循环整个图片，对比两幅图片的各自区域块
		for (int y = 0; y < comparey; y++) {
			if (debugMode > 0) {
				System.out.print("|");
			}
			for (int x = 0; x < comparex; x++) {
				int b1 = getAverageBrightness(img1.getSubimage(x * blocksx, y * blocksy, blocksx - 1, blocksy - 1));
				int b2 = getAverageBrightness(img2.getSubimage(x * blocksx, y * blocksy, blocksx - 1, blocksy - 1));
				int diff = Math.abs(b1 - b2);
				// 在确定区域的异同，通过factorA阈值来筛选
				if (diff > factorA) {
					this.diffMax = this.diffMax > diff ? this.diffMax : diff;
					this.match = false;
				}
				if (debugMode == 1) {
					System.out.print((diff > factorA ? "X" : " "));
				}
				if (debugMode == 2) {
					System.out.print(diff + (x < comparex - 1 ? "," : ""));
				}
			}
			if (debugMode > 0) {
				System.out.println("|");
			}
		}
	}

	// 返回含有检测到区域变化图片
	public BufferedImage getChangeIndicator() {
		return imgc;
	}

	// 获取指定的平均亮度值
	protected int getAverageBrightness(BufferedImage img) {
		Raster r = img.getData();
		int total = 0;
		for (int y = 0; y < r.getHeight(); y++) {
			for (int x = 0; x < r.getWidth(); x++) {
				total += r.getSample(r.getMinX() + x, r.getMinY() + y, 0);
			}
		}
		return total / ((r.getWidth() / factorD) * (r.getHeight() / factorD));
	}

	// 返回两幅副片是否相互匹配
	public boolean match() {
		return this.match;
	}

	// 将Image转换为Buffered Image
	protected static BufferedImage imageToBufferedImage(Image img) {
		BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = bi.createGraphics();
		g2.drawImage(img, null, null);
		return bi;
	}

	// 将Buffered Image写到一个JPEG文件中
	protected static void saveJPG(Image img, String filename) {
		BufferedImage bi = imageToBufferedImage(img);
		try (FileOutputStream out = new FileOutputStream(filename);) {
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
			JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bi);
			param.setQuality(0.8f, false);
			encoder.setJPEGEncodeParam(param);
			encoder.encode(bi);
		} catch (FileNotFoundException e) {
			logger.error("File Not Found");
		} catch (IOException e) {
			logger.error("Exception:{}", LogbackUtil.expection2Str(e));
		}
	}

	// 读取JPEG文件为Buffered Image
	protected static Image loadJPG(String filename) {
		BufferedImage bi = null;
		try (FileInputStream in = new FileInputStream(filename);) {
			JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder(in);
			bi = decoder.decodeAsBufferedImage();
		} catch (FileNotFoundException e) {
			logger.error("File Not Found");
		} catch (IOException e) {
			logger.error("Exception:{}", LogbackUtil.expection2Str(e));
		}
		return bi;
	}

}
