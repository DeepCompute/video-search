package info.hb.video.mapred.wm.io;

import info.hb.video.mapred.wm.util.FFMPEGUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.hadoop.conf.Configuration;

/**
 * 分片上传类
 * 
 * @author wgybzb
 *
 */
public class SplitUploader {

	private static final int UPLOADERS_COUNT = 4;
	private final String inputPath;
	private final String jobUUID;
	private final Configuration configuration;

	public SplitUploader(String inputPath, String jobUUID, Configuration configuration) {
		this.inputPath = inputPath;
		this.jobUUID = jobUUID;
		this.configuration = configuration;
	}

	@SuppressWarnings("rawtypes")
	public void splitAndUpload() {
		int splitCount = getSplitCount();

		ExecutorService uploadersPool = Executors.newFixedThreadPool(UPLOADERS_COUNT);
		List<Future> uploads = new ArrayList<>();

		for (int i = 0; i < splitCount; i++) {

			final int finalI = i;
			Future uploadFuture = uploadersPool.submit(new Runnable() {
				@Override
				public void run() {
					FFMPEGUtil.uploadSplit(inputPath, jobUUID, configuration, finalI);
				}
			});

			uploads.add(uploadFuture);
		}

		for (Future upload : uploads) {
			try {
				upload.get();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		uploadersPool.shutdown();
	}

	private int getSplitCount() {
		return FFMPEGUtil.getLength(inputPath);
	}

}
