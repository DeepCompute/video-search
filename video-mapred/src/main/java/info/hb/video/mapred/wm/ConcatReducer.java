package info.hb.video.mapred.wm;

import java.io.IOException;

import org.apache.hadoop.mapreduce.Reducer;

/**
 * 
 * @author wgybzb
 *
 */
public class ConcatReducer extends Reducer<Object, Object, Object, Object> {

	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		super.setup(context); // To change body of overridden methods use File | Settings | File Templates.
	}

	@Override
	protected void reduce(Object key, Iterable<Object> values, Context context) throws IOException,
			InterruptedException {
		super.reduce(key, values, context); // To change body of overridden methods use File | Settings | File Templates.
	}

}
