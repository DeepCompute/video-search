package info.hb.video.mapred.wm.io;

import java.io.IOException;

import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

/**
 * 根写操作类
 *
 * @author wgybzb
 *
 */
public class StubWriter extends RecordWriter<Object, Object> {

	@Override
	public void write(Object o, Object o2) throws IOException, InterruptedException {
		// TODO
	}

	@Override
	public void close(TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {
		// TODO
	}

}
