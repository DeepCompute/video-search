package info.hb.video.mapred.wm;

import java.io.IOException;

import org.apache.hadoop.mapreduce.Reducer;

public class ConcatReducer extends Reducer<Object, Object, Object, Object> {

	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		super.setup(context);
		// TODO
	}

	@Override
	protected void reduce(Object key, Iterable<Object> values, Context context) throws IOException,
			InterruptedException {
		super.reduce(key, values, context);
		// TODO
	}

}
