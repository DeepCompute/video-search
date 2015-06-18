package info.hb.video.riak.client;

import info.hb.video.riak.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zx.soft.utils.config.ConfigUtil;
import zx.soft.utils.log.LogbackUtil;

import com.basho.riak.client.api.RiakClient;
import com.basho.riak.client.api.cap.Quorum;
import com.basho.riak.client.api.commands.kv.DeleteValue;
import com.basho.riak.client.api.commands.kv.FetchValue;
import com.basho.riak.client.api.commands.kv.StoreValue;
import com.basho.riak.client.api.commands.kv.StoreValue.Option;
import com.basho.riak.client.core.RiakCluster;
import com.basho.riak.client.core.RiakNode;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import com.basho.riak.client.core.query.RiakObject;
import com.basho.riak.client.core.util.BinaryValue;

/**
 * 图片存储Riak集群客户端
 *
 * @author wanggang
 *
 */
public class Image2RiakCluster {

	private static Logger logger = LoggerFactory.getLogger(Image2RiakCluster.class);

	private RiakCluster cluster;
	private RiakClient client;

	// 根据需求设置，确认是否是副本数
	private final int QUORUM_SIZE;

	// IP地址列表
	private String ipsStr;
	private String[] ips;

	public Image2RiakCluster() {
		try {
			Properties props = ConfigUtil.getProps("riak.properties");
			RiakNode.Builder builder = new RiakNode.Builder()
					.withMinConnections(Integer.parseInt(props.getProperty("riak.min.connections")))
					.withMaxConnections(Integer.parseInt(props.getProperty("riak.max.connections")))
					.withRemotePort(Integer.parseInt(props.getProperty("riak.port")));
			//			builder.withAuth(props.getProperty("riak.username"), props.getProperty("riak.password"), null);
			ipsStr = props.getProperty("riak.cluster");
			ips = ipsStr.split(",");
			List<RiakNode> nodes = RiakNode.Builder.buildNodes(builder, Arrays.asList(ips));
			// 法定数要多于一半的节点
			QUORUM_SIZE = nodes.size() / 2 + 1;
			cluster = new RiakCluster.Builder(nodes).build();
			cluster.start();
			client = new RiakClient(cluster);
		} catch (Exception e) {
			logger.error("Exception:{}", LogbackUtil.expection2Str(e));
			throw new RuntimeException(e);
		}
	}

	public String getIpsStr() {
		return ipsStr;
	}

	public String[] getIps() {
		return ips;
	}

	/**
	 * 参考配置文件：http://riak.com.cn/riak/latest/ops/advanced/configs/configuration-files/
	 * 注意：对于我们的视频帧存储Riak场景，要求写速度非常快，查询很少，那么W=1,R=N更合适，不需要使用法定值
	 *
	 * @param bucketType 任意的字符串，主要用于管理统一类数据的配置信息
	 * @param bucketName 相当于DBMS中的数据表名字
	 * @param key  键名
	 */
	public void writeImage(String bucketType, String bucketName, String key, BufferedImage bi, String format) {
		try {
			Location location = new Location(new Namespace(bucketType, bucketName), key);
			RiakObject riakObject = new RiakObject().setContentType("image/" + format).setValue(
					BinaryValue.create(ImageUtils.transBI2BytesBytes(bi, format)));
			/*
			n_val - 保存的副本数。注意：详细讨论参见“CAP 控制”一文。
			读、写和删除请求的法定值。可选值包括数字（例如，{r, 2}），以及下面列出的值：
			quorum： 大多数副本要响应，等同于 n_val / 2 + 1
			all： 所有副本都要响应
			r - 读请求的法定值（成功的 GET 请求必须返回结果的 Riak 节点数）默认值：quorum
			pr - 主读取请求的法定值（成功的 GET 请求必须返回结果的 Riak 主节点（非备用节点）数）默认值：0 注意：关于主节点的说明请阅读“最终一致性”一文
			w - 写请求的法定值（必须接受 PUT 请求的 Riak 节点数）默认值：quorum
			dw - 持续写请求的法定值（接受从存储后台发出的写请求的 Riak 节点数）默认值：quorum
			pw - 主写入请求的法定值（必须接受 PUT 请求的 Riak 主节点（非备用节点）数）默认值：0
			rw - 删除请求的法定值。默认值：quorum
			*/
			StoreValue store = new StoreValue.Builder(riakObject).withLocation(location)
					.withOption(Option.W, new Quorum(QUORUM_SIZE)).build();
			client.execute(store);
		} catch (ExecutionException | InterruptedException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void writeString(String bucketType, String bucketName, String key, String data) {
		try {
			Location location = new Location(new Namespace(bucketType, bucketName), key);
			RiakObject riakObject = new RiakObject().setValue(BinaryValue.create(data));
			StoreValue store = new StoreValue.Builder(riakObject).withLocation(location)
					.withOption(Option.W, new Quorum(QUORUM_SIZE)).build();
			client.execute(store);
		} catch (ExecutionException | InterruptedException e) {
			logger.error("Exception:{}", LogbackUtil.expection2Str(e));
			// 注意：项目稳定时，需要把抛出异常去掉，防止因个别异常线程停止
			throw new RuntimeException(e);
		}
	}

	public RiakObject readString(String bucketType, String bucketName, String key) {
		try {
			Location location = new Location(new Namespace(bucketType, bucketName), key);
			FetchValue fv = new FetchValue.Builder(location).build();
			FetchValue.Response response = client.execute(fv);
			return response.getValue(RiakObject.class);
		} catch (ExecutionException | InterruptedException e) {
			logger.error("Exception:{}", LogbackUtil.expection2Str(e));
			// 注意：项目稳定时，需要把抛出异常去掉，防止因个别异常线程停止
			throw new RuntimeException(e);
		}
	}

	public void deleteObject(String bucketType, String bucketName, String key) {
		try {
			Location location = new Location(new Namespace(bucketType, bucketName), key);
			DeleteValue dv = new DeleteValue.Builder(location).build();
			client.execute(dv);
		} catch (ExecutionException | InterruptedException e) {
			logger.error("Exception:{}", LogbackUtil.expection2Str(e));
			// 注意：项目稳定时，需要把抛出异常去掉，防止因个别异常线程停止
			throw new RuntimeException(e);
		}
	}

	public void close() {
		client.shutdown();
		// 不能关闭
		//		cluster.shutdown();
	}

}
