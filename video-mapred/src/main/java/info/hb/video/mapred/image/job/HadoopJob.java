package info.hb.video.mapred.image.job;

import info.hb.video.mapred.image.io.BufferedImageCombineInputFormat;
import info.hb.video.mapred.image.io.BufferedImageOutputFormat;
import info.hb.video.mapred.image.writable.BufferedImageWritable;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * Hadoop作业
 * 
 * @author wanggang
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class HadoopJob extends Configured implements Tool {

	private final Class mapper;
	private Class reducer;
	private Class IFormat = BufferedImageCombineInputFormat.class;
	private Class OFormat = BufferedImageOutputFormat.class;
	private Class OKey = NullWritable.class;
	private Class OValue = BufferedImageWritable.class;
	private String usage_string = null;
	private int args_length = 0;
	private String info = null;

	public HadoopJob(Class<? extends Mapper> mapper) {
		this.mapper = mapper;
	}

	public HadoopJob(Class<? extends Mapper> mapper, HadoopJobConfiguration conf) {
		this(mapper);
		usage_string = String.format("Usage: %s [generic options]", getClass().getSimpleName()) + conf.getUsageString();
		args_length = conf.getArgsLength();
		info = conf.getJobInfo();
	}

	public HadoopJob(Class<? extends Mapper> mapper, Class<? extends Reducer> reducer, HadoopJobConfiguration conf) {
		this(mapper, conf);
		this.reducer = reducer;
	}

	public void setIFormat(Class<? extends org.apache.hadoop.mapreduce.InputFormat> IFormat) {
		this.IFormat = IFormat;
	}

	public void setOFormat(Class<? extends org.apache.hadoop.mapreduce.OutputFormat> OFormat) {
		this.OFormat = OFormat;
	}

	public void setOKey(Class<? extends Writable> OKey) {
		this.OKey = OKey;
	}

	public void setOValue(Class<? extends Writable> OValue) {
		this.OValue = OValue;
	}

	@Override
	public int run(String[] args) throws Exception {
		if (args.length != args_length) {
			System.err.printf(usage_string);
			ToolRunner.printGenericCommandUsage(System.err);
			return -1;
		}

		Job job = Job.getInstance(super.getConf(), info);
		job.setJarByClass(getClass());
		job.setInputFormatClass(IFormat);
		job.setOutputFormatClass(OFormat);

		if (mapper != null) {
			job.setMapperClass(mapper);
		} else
			throw new NullMapperException();

		if (reducer != null)
			job.setReducerClass(reducer);
		else
			job.setNumReduceTasks(0);

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		preprocess(args, job);
		job.setOutputKeyClass(OKey);
		job.setOutputValueClass(OValue);
		return job.waitForCompletion(true) ? 0 : 1;
	}

	protected void preprocess(String[] args, Job job) {
		//
	}

	public static class NullMapperException extends Exception {

		private static final long serialVersionUID = -955109704894231599L;

		NullMapperException() {
			super("Null mapper exception");
		}

	}

}
