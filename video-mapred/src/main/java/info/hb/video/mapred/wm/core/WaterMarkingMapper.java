package info.hb.video.mapred.wm.core;

import info.hb.video.mapred.wm.util.FFMPEGUtil;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * 水印Mapper
 *
 * @author wgybzb
 *
 */
public class WaterMarkingMapper extends Mapper<Text, Text, Text, LongWritable> {

	@Override
	protected void map(Text key, Text value, Context context) throws IOException, InterruptedException {

		FFMPEGUtil.init();
		Path partPath = new Path(value.toString());

		String partName = partPath.getName();
		int partIdx = Integer.parseInt(partName.replace("in", ""));

		Configuration configuration = context.getConfiguration();
		String jobUUID = configuration.get("jobUUID");
		FFMPEGUtil.applyWatermark(partIdx, jobUUID, configuration);

		context.write(key, new LongWritable(partIdx));

	}

}
