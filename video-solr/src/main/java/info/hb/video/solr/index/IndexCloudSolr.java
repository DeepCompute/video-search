package info.hb.video.solr.index;

import info.hb.video.model.frame.FrameRecord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer.RemoteSolrException;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zx.soft.utils.config.ConfigUtil;
import zx.soft.utils.log.LogbackUtil;

/**
 * 通过HTTP方式索引数据到单台机器上。
 *
 * @author wgybzb
 *
 */
public class IndexCloudSolr {

	private static Logger logger = LoggerFactory.getLogger(IndexCloudSolr.class);

	private final CloudSolrServer cloudServer;

	public IndexCloudSolr() {
		Properties props = ConfigUtil.getProps("solr.properties");
		cloudServer = new CloudSolrServer(props.getProperty("zookeeper_cloud"));
		cloudServer.setDefaultCollection(props.getProperty("collection"));
		cloudServer.setIdField("id");
		cloudServer.setParallelUpdates(true);
		cloudServer.setZkConnectTimeout(Integer.parseInt(props.getProperty("zookeeper_connect_timeout")));
		cloudServer.setZkClientTimeout(Integer.parseInt(props.getProperty("zookeeper_client_timeout")));
		cloudServer.connect();
	}

	public void deleteQuery(String q) {
		try {
			cloudServer.deleteByQuery(q);
			cloudServer.commit();
		} catch (SolrServerException e) {
			logger.error("Exception:{}", LogbackUtil.expection2Str(e));
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("Exception:{}", LogbackUtil.expection2Str(e));
			e.printStackTrace();
		}
	}

	public void addDocsToSolr(List<FrameRecord> records) {
		if (records.size() == 0) {
			return;
		}
		try {
			Collection<SolrInputDocument> docs = new ArrayList<>();
			for (FrameRecord record : records) {
				if (getSentimentDoc(record) != null) {
					docs.add(getSentimentDoc(record));
				}
			}
			cloudServer.add(docs);
			cloudServer.commit();
		} catch (RemoteSolrException | SolrServerException | IOException e) {
			logger.error("Exception:{}", LogbackUtil.expection2Str(e));
		}
	}

	public boolean addFrameDocToSolr(FrameRecord record) {
		SolrInputDocument temp = null;
		try {
			temp = getSentimentDoc(record);
			if (temp != null) {
				cloudServer.add(temp);
			}
			return Boolean.TRUE;
		} catch (RemoteSolrException | SolrServerException | IOException e) {
			logger.error("Index Record:{}", record);
			logger.error("Exception:{}", LogbackUtil.expection2Str(e));
			return Boolean.FALSE;
		}
	}

	public void commitToSolr() {
		try {
			cloudServer.commit();
		} catch (RemoteSolrException | SolrServerException | IOException e) {
			logger.error("Exception:{}", LogbackUtil.expection2Str(e));
		}
	}

	/**
	 * 每次更改字段的时候，这里也需要更改。
	 * 注意：因为我们使用的服务器的时间早8个小时，所以这里面的时间都增加了8个小时
	 */
	public static SolrInputDocument getSentimentDoc(FrameRecord record) {

		if (record.getId() == null || record.getId() == "" || record.getId().length() == 0) {
			logger.error("Record's id is null,{}", record);
			return null;
		}
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField("id", record.getId());
		doc.addField("frame_content", record.getFrame_content());
		doc.addField("frame_index", record.getFrame_index());
		doc.addField("frame_cache_ip", record.getFrame_cache_ip());
		doc.addField("frame_url", record.getFrame_url());
		doc.addField("video_id", record.getVideo_id());
		doc.addField("video_type", record.getVideo_type());
		doc.addField("video_time_start", new Date(transTime(record.getVideo_time_start().getTime())));
		doc.addField("video_time_end", new Date(transTime(record.getVideo_time_end().getTime())));
		doc.addField("video_time_duration", record.getVideo_time_duration());
		doc.addField("video_ip", record.getVideo_ip());
		doc.addField("video_dir", record.getVideo_dir());
		doc.addField("video_name", record.getVideo_name());
		doc.addField("road_id", record.getRoad_id());
		doc.addField("road_name", record.getRoad_name());
		doc.addField("road_name_start", record.getRoad_name_start());
		doc.addField("road_name_end", record.getRoad_name_end());
		doc.addField("road_type", record.getRoad_type());
		doc.addField("longitude", record.getLongitude());
		doc.addField("latitude", record.getLatitude());
		doc.addField("timestamp", new Date(transTime(record.getTimestamp().getTime())));

		return doc;
	}

	private static long transTime(long time) {
		return (time / 1000 + 3600 * 8) * 1000L;
	}

	public void close() {
		cloudServer.shutdown();
	}

}
