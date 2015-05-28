package info.hb.video.mapred.wm.io;

import java.io.IOException;

import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * 视频文件输出格式
 * 
 * @author wgybzb
 *
 */
public class VideoFileOutputFormat extends FileOutputFormat<Object, Object> {

	@Override
	public RecordWriter<Object, Object> getRecordWriter(TaskAttemptContext taskAttemptContext) throws IOException,
			InterruptedException {
		return new StubWriter();
	}

}
