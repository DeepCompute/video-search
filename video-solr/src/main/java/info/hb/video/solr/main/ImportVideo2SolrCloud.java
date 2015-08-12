package info.hb.video.solr.main;

import info.hb.video.model.frame.FrameRecord;
import info.hb.video.shrink.core.Video2Text;
import info.hb.video.solr.index.IndexCloudSolr;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImportVideo2SolrCloud {

	private static Logger logger = LoggerFactory.getLogger(ImportVideo2SolrCloud.class);

	// Solr集群客户端
	private IndexCloudSolr ics;
	// 视频转换成文本
	private Video2Text video2Text;

	public ImportVideo2SolrCloud() {
		ics = new IndexCloudSolr();
		video2Text = new Video2Text();
	}

	/**
	 * 主函数
	 */
	public static void main(String[] args) {
		ImportVideo2SolrCloud iv2sc = new ImportVideo2SolrCloud();
		//		iv2sc.flushSolr();
		iv2sc.run("/home/wanggang/develop/deeplearning/sample-videos/mp4");
		iv2sc.close();
	}

	public void run(String dirName) {
		File dir = new File(dirName);
		File[] files = dir.listFiles();
		for (File file : files) {
			logger.info("Tackle video:{}", file.getName());
			// 存储视频到Riak中，然后分析视频帧文件，并存储到Solr中
			List<FrameRecord> records = video2Text.video2Text(file);
			ics.addDocsToSolr(records);
		}
	}

	public void flushSolr() {
		ics.deleteQuery("*:*");
	}

	public void close() {
		ics.close();
		video2Text.close();
		// TS视频关闭不了，进程执行完需要强制关闭
		//		ProcessAnalysis.killPid(ProcessAnalysis.getCurrentPidByLang());
		//		System.err.println("Closed this processor......");
	}

}
