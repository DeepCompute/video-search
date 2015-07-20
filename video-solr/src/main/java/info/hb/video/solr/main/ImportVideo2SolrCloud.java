package info.hb.video.solr.main;

import info.hb.video.model.frame.FrameRecord;
import info.hb.video.shrink.core.Video2Text;
import info.hb.video.solr.index.IndexCloudSolr;

import java.util.List;

import zx.soft.utils.threads.ApplyThreadPool;

public class ImportVideo2SolrCloud {

	public static void main(String[] args) {
		String[] videoFiles = { //
		//		"A003-G昭亭路-文鼎路3文鼎路东-1-20150326205000-20150326205939-195056832.ts", //
		//				"A050敬亭路-文鼎路3敬亭路南-1-20150326205000-20150326205947-195125067.ts", //
		//				"B010锦城路-宣中后门路出口-1-20150326085000-20150326085934-195311270.ts", //
		//				"B014-G鳌峰路-陵西路1陵西路北-2-20150326085000-20150326085935-195403888.ts", //
		//				"B016-G鳌峰路-陵西路3鳌峰路东-1-20150326085000-20150326085940-195378148.ts", //
		//				"C025庙埠村-村部-1-20150326205000-20150326205938-195258979.ts", //
		"C028金苹果幼儿园-1-20150326205000-20150326205930-195190416.ts" };
		IndexCloudSolr ics = new IndexCloudSolr();
		//		ics.deleteQuery("*:*");
		Video2Text video2Text = new Video2Text();
		for (String videoFile : videoFiles) {
			List<FrameRecord> records = video2Text.video2Text("/home/wanggang/develop/deeplearning/sample-videos/"
					+ videoFile);
			ics.addDocsToSolr(records);
		}
		video2Text.close();
		ics.close();
		ApplyThreadPool.stop(4);
	}

}
