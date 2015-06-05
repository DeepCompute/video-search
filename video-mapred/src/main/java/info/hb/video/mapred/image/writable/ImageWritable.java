package info.hb.video.mapred.image.writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

/**
 * 基本图像序列化表示-抽象类，实现Writable接口，可作为单个key或value来使用
 * 
 * @author wanggang
 *
 * @param <I> 图像对象
 */
public abstract class ImageWritable<I> implements Writable {

	public static final String JPEG_FORMAT = "jpg";

	/**
	 * 图像格式, e.g. jpg, png, etc.
	 */
	private String format = JPEG_FORMAT;

	/**
	 * 图像文件名，不带扩展名
	 */
	private String fileName;

	/**
	 * 图像对象
	 */
	protected I im;

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public void write(DataOutput out) throws IOException {
		// 写入图像类型
		Text.writeString(out, format);
		// 写入图像文件名
		Text.writeString(out, fileName);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		// 读取图像类型
		format = Text.readString(in);
		// 读取图像文件名
		fileName = Text.readString(in);
	}

	public I getImage() {
		return im;
	}

	public void setImage(I im) {
		this.im = im;
	}

}