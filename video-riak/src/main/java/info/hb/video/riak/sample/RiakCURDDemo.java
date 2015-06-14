package info.hb.video.riak.sample;

import java.net.UnknownHostException;

import zx.soft.utils.json.JsonUtils;

import com.basho.riak.client.api.RiakClient;
import com.basho.riak.client.api.commands.kv.DeleteValue;
import com.basho.riak.client.api.commands.kv.FetchValue;
import com.basho.riak.client.api.commands.kv.StoreValue;
import com.basho.riak.client.core.RiakCluster;
import com.basho.riak.client.core.RiakNode;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import com.basho.riak.client.core.query.RiakObject;
import com.basho.riak.client.core.util.BinaryValue;

/**
 * Riak简单CURD示例
 *
 * @author wanggang
 *
 */
public class RiakCURDDemo {

	// 基本的POJO类演示Riak中的类型交换
	public static class Book {

		public String title;
		public String author;
		public String body;
		public String isbn;
		public Integer copiesOwned;

	}

	// 创建Riak客户端对象
	private static RiakCluster setUpCluster() throws UnknownHostException {
		// 监听hefei10:10017
		RiakNode node = new RiakNode.Builder().withRemoteAddress("hefei10").withRemotePort(8087).build();
		// 集群对象使用一个节点作为参数
		RiakCluster cluster = new RiakCluster.Builder(node).build();
		// 启动集群
		cluster.start();
		return cluster;
	}

	/**
	 * 测试函数
	 */
	public static void main(String[] args) {
		try {
			// 创建Riak集群对象
			RiakCluster cluster = setUpCluster();
			RiakClient client = new RiakClient(cluster);
			System.out.println("成功创建Client对象......");
			System.out.println("###########################存储String数据############################");
			// 首先，创建基本的对象存储电影名称quotes
			RiakObject quoteObject = new RiakObject()
			// 告诉Riak需要存储的是纯文本信息，不是JSON, HTML之类的
					.setContentType("text/plain")
					// 最终以二进制存储对象
					.setValue(BinaryValue.create("天下无贼"));
			System.out.println("创建基本的RiakObject对象......");
			// 创建Namespace对象，传入一个bucketName，使用默认的bucketType（default）
			Namespace quotesBucket = new Namespace("quotes");
			// 创建本地对象，键名为Iceman
			Location quoteObjectLocation = new Location(quotesBucket, "Iceman");
			System.out.println("为RiakObject对象创建Location对象......");
			// 创建一个StoreValue存储操作对象
			StoreValue storeOp = new StoreValue.Builder(quoteObject).withLocation(quoteObjectLocation).build();
			System.out.println("StoreValue操作对象创建......");
			// 执行存储
			StoreValue.Response storeOpResp = client.execute(storeOp);
			System.out.println(JsonUtils.toJson(storeOpResp));
			System.out.println("完成String对象存储操作!!!!!!");
			//
			System.out.println("###########################读取String数据############################");
			FetchValue fetchOp = new FetchValue.Builder(quoteObjectLocation).build();
			RiakObject fetchedObject = client.execute(fetchOp).getValue(RiakObject.class);
			System.out.println("存储值为：" + quoteObject.getValue());
			System.out.println("读取值为：" + fetchedObject.getValue());
			System.out.println("读取的值和存储的一样!!!!!!");
			//
			System.out.println("###########################删除String数据############################");
			DeleteValue deleteOp = new DeleteValue.Builder(quoteObjectLocation).build();
			client.execute(deleteOp);
			System.out.println("成功删除!!!!!!");
			//
			System.out.println("###########################读取对象数据############################");
			Book mobyDick = new Book();
			mobyDick.title = "《大数据实战》";
			mobyDick.author = "永不止步";
			mobyDick.body = "预练此功，必先自宫......";
			mobyDick.isbn = "1111979723";
			mobyDick.copiesOwned = 3;
			System.out.println("Book对象创建成功......");
			// 使用default的bucketType和books创建Namesapce
			Namespace booksBucket = new Namespace("books");
			Location mobyDickLocation = new Location(booksBucket, "big_data");
			StoreValue storeBookOp = new StoreValue.Builder(mobyDick).withLocation(mobyDickLocation).build();
			client.execute(storeBookOp);
			System.out.println("Book对象存储成功!!!!");
			FetchValue fetchMobyDickOp = new FetchValue.Builder(mobyDickLocation).build();
			Book fetchedBook = client.execute(fetchMobyDickOp).getValue(Book.class);
			System.out.println("Book对象读取成功!!!!");
			System.out.println(mobyDick.getClass() + "  ,  " + fetchedBook.getClass());
			System.out.println(mobyDick.title + "  ,  " + fetchedBook.title);
			System.out.println(mobyDick.author + "  ,  " + fetchedBook.author);

			System.out.println("所有测试Riak的CURD成功！！！！！！");

			// 关闭集群
			cluster.shutdown();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

}