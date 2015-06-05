package info.hb.video.mapred.image.io;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.CombineFileSplit;

/**
 * BufferedImageRecordReader的子类
 * 
 * 从多个文件中读取图像
 * 
 * @author wanggang
 *
 */
public class BufferedImageCombineRecordReader extends BufferedImageRecordReader {

	public BufferedImageCombineRecordReader(CombineFileSplit split, TaskAttemptContext context, Integer index)
			throws IOException {
		Configuration job = context.getConfiguration();
		final Path file = split.getPath(index);
		fileName = file.getName();
		FileSystem fs = file.getFileSystem(job);
		fileStream = fs.open(file);
	}

	@Override
	public void initialize(InputSplit inputSplit, TaskAttemptContext taskAttemptContext) throws IOException,
			InterruptedException {
		//
	}

}
