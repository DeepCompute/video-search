package info.hb.video.mapred.image.io;

import info.hb.video.mapred.image.writable.GrayImageWritable;

import java.io.IOException;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * 灰度图像输出格式
 * 数据写入到HDFS中的格式，但是每个图像都是作为一个独立文件写入的
 *
 * @author wanggang
 *
 */
public class GrayImageOutputFormat extends FileOutputFormat<NullWritable, GrayImageWritable> {

	@Override
	public RecordWriter<NullWritable, GrayImageWritable> getRecordWriter(TaskAttemptContext taskAttemptContext)
			throws IOException, InterruptedException {
		return new GrayImageRecordWriter(taskAttemptContext);
	}

}
