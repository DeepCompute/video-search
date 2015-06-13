package info.hb.video.riak.client;

import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import zx.soft.utils.json.JsonUtils;

import com.basho.riak.client.api.RiakClient;
import com.basho.riak.client.api.cap.Quorum;
import com.basho.riak.client.api.commands.kv.FetchValue;
import com.basho.riak.client.api.commands.kv.StoreValue;
import com.basho.riak.client.api.commands.kv.StoreValue.Option;
import com.basho.riak.client.core.RiakCluster;
import com.basho.riak.client.core.RiakNode;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import com.basho.riak.client.core.query.RiakObject;
import com.basho.riak.client.core.util.BinaryValue;

public class ClusterClient {

	private RiakCluster cluster;

	public ClusterClient() {
		RiakNode.Builder builder = new RiakNode.Builder();
		builder.withMinConnections(10);
		builder.withMaxConnections(50);

		List<String> addresses = new LinkedList<String>();
		addresses.add("hefei08");
		addresses.add("hefei09");
		addresses.add("hefei10");
		try {
			List<RiakNode> nodes = RiakNode.Builder.buildNodes(builder, addresses);
			RiakCluster cluster = new RiakCluster.Builder(nodes).build();
			cluster.start();
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 测试函数
	 */
	public static void main(String[] args) throws UnknownHostException {
		// http://192.168.32.20:8098/riak/frames/frame_id
		ClusterClient riakClient = new ClusterClient();
		// 写数据
		riakClient.writeData("default", "frames", "frame_id", "frame_value_cluster");
		// 读数据
		riakClient.readData("default", "frames", "frame_id");
		// 关闭资源
		riakClient.close();
	}

	public void writeData(String bucketType, String bucketName, String key, String data) {
		RiakClient client = getClient();
		Namespace ns = new Namespace(bucketType, bucketName);
		Location location = new Location(ns, key);
		RiakObject riakObject = new RiakObject();
		riakObject.setValue(BinaryValue.create(data));
		StoreValue store = new StoreValue.Builder(riakObject).withLocation(location)
				.withOption(Option.W, new Quorum(3)).build();
		try {
			client.execute(store);
		} catch (ExecutionException | InterruptedException e) {
			throw new RuntimeException(e);
		} finally {
			client.shutdown();
		}
	}

	public void readData(String bucketType, String bucketName, String key) {
		RiakClient client = getClient();
		Namespace ns = new Namespace(bucketType, bucketName);
		Location location = new Location(ns, key);
		FetchValue fv = new FetchValue.Builder(location).build();
		FetchValue.Response response;
		try {
			response = client.execute(fv);
			RiakObject obj = response.getValue(RiakObject.class);
			System.out.println(JsonUtils.toJson(obj));
		} catch (ExecutionException | InterruptedException e) {
			throw new RuntimeException(e);
		} finally {
			client.shutdown();
		}
	}

	private RiakClient getClient() {
		return new RiakClient(cluster);
	}

	public void close() {
		cluster.shutdown();
	}

}
