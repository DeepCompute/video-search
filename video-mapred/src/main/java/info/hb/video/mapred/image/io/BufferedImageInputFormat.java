package info.hb.video.mapred.image.io;

import info.hb.video.mapred.image.writable.BufferedImageWritable;

import java.io.IOException;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

/**
 * BufferedImage输入格式
 * 每个Map读取一个图像，所以不适合小图像处理
 * 
 * @author wanggang
 *
 */
public class BufferedImageInputFormat extends ImageInputFormat<NullWritable, BufferedImageWritable> {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public RecordReader createRecordReader(InputSplit inputSplit, TaskAttemptContext taskAttemptContext)
			throws IOException, InterruptedException {
		return new BufferedImageRecordReader();
	}

}
