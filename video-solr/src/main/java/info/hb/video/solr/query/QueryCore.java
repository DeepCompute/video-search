package info.hb.video.solr.query;

import java.io.IOException;
import java.util.Properties;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zx.soft.utils.config.ConfigUtil;
import zx.soft.utils.log.LogbackUtil;

/**
 * 搜索舆情数据
 *
 * @author wanggang
 *
 */
public class QueryCore {

	private static Logger logger = LoggerFactory.getLogger(QueryCore.class);

	final CloudSolrServer cloudServer;

	public QueryCore() {
		Properties props = ConfigUtil.getProps("solr.properties");
		cloudServer = new CloudSolrServer(props.getProperty("zookeeper_cloud"));
		cloudServer.setDefaultCollection(props.getProperty("collection"));
		cloudServer.setZkConnectTimeout(Integer.parseInt(props.getProperty("zookeeper_connect_timeout")));
		cloudServer.setZkClientTimeout(Integer.parseInt(props.getProperty("zookeeper_client_timeout")));
		cloudServer.connect();
	}

	/**
	 * 测试函数
	 */
	public static void main(String[] args) {
		QueryCore search = new QueryCore();
		SolrDocumentList result = search.query("宝马车", 0, 20);
		//		System.out.println(JsonUtils.toJson(result));
		System.err.println(result.size());
		search.close();
	}

	/**
	 * 根据关键词查询结果
	 */
	public SolrDocumentList query(String keyword, int start, int rows) {
		SolrQuery solrQuery = new SolrQuery();
		// 忽略版本信息，否则会对分类统计产生影响
		String[] vinfo = null;
		solrQuery.add("version", vinfo);
		// 分片失效忽略
		solrQuery.set("shards.tolerant", true);
		// 设置关键词连接逻辑，默认是AND
		solrQuery.set("q.op", "AND");
		// 根据关键词查询
		solrQuery.setQuery(keyword);
		solrQuery.setStart(start);
		solrQuery.setRows(rows);
		return query(solrQuery);
	}

	/**
	 * 根据多条件查询结果数据
	 */
	public SolrDocumentList query(SolrQuery solrQuery) {
		QueryResponse queryResponse = null;
		try {
			queryResponse = cloudServer.query(solrQuery, METHOD.POST);
			// GET方式的时候所有查询条件都是拼装到url上边的，url过长当然没有响应，必然中断talking了
			//			queryResponse = server.query(query, METHOD.GET);
		} catch (SolrServerException e) {
			logger.error("Exception:{}", LogbackUtil.expection2Str(e));
			throw new RuntimeException(e);
		}
		if (queryResponse == null) {
			logger.error("no response!");
		}

		return queryResponse.getResults();
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

	public void close() {
		cloudServer.shutdown();
	}

}
