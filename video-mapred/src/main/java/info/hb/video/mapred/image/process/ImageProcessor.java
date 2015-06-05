package info.hb.video.mapred.image.process;

/**
 * 图像处理接口
 * 
 * @author wanggang
 *
 * @param <ImageType> 图像类型
 */
public interface ImageProcessor<ImageType> {

	/**
	 * 处理原始图像
	 * 
	 * @param image     原始图像
	 * @return          处理后的图像
	 */
	public ImageType processImage(ImageType image);

}
