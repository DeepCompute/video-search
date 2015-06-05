package info.hb.video.mapred.image.io;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;

/**
 * 图像输入格式抽象类
 * 
 * @author wanggang
 *
 * @param <K>
 * @param <V>
 */
public abstract class ImageInputFormat<K, V> extends FileInputFormat<Object, Object> {

	/**
	 * 因为图像数据作为一个整体不能被分片计算，所以返回false
	 */
	@Override
	protected boolean isSplitable(JobContext context, Path file) {
		return Boolean.FALSE;
	}

}
