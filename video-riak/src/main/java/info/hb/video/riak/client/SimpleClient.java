package info.hb.video.riak.client;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;

import zx.soft.utils.json.JsonUtils;

import com.basho.riak.client.api.RiakClient;
import com.basho.riak.client.api.cap.Quorum;
import com.basho.riak.client.api.commands.datatypes.MapUpdate;
import com.basho.riak.client.api.commands.datatypes.RegisterUpdate;
import com.basho.riak.client.api.commands.datatypes.UpdateMap;
import com.basho.riak.client.api.commands.kv.FetchValue;
import com.basho.riak.client.api.commands.kv.StoreValue;
import com.basho.riak.client.api.commands.kv.StoreValue.Option;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import com.basho.riak.client.core.query.RiakObject;
import com.basho.riak.client.core.util.BinaryValue;

public class SimpleClient {

	RiakClient client;

	public SimpleClient() {
		try {
			client = RiakClient.newClient("hefei08", "hefei09", "hefei10");
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 测试函数
	 */
	public static void main(String[] args) {
		// http://192.168.32.20:8098/riak/some_bucket/some_key/<bucket>,<riaktag>,0|1/...
		// http://192.168.32.20:8098/riak/images/sammy.png
		// http://192.168.32.20:8098/riak/frames/frame_id
		SimpleClient riakClient = new SimpleClient();
		// 读写文本
		//		riakClient.writeData("default", "frames", "frame_id", "frame_value");
		//		riakClient.readData("default", "frames", "frame_id");
		// 写图片
		riakClient.writeDataImage();
		// 关闭资源
		riakClient.close();
	}

	public void writeDataImage() {
		// http://192.168.32.20:8098/riak/frames/test.jpg
		try {
			Namespace ns = new Namespace("default", "frames");
			Location location = new Location(ns, "test1.jpg");
			BufferedImage image = ImageIO.read(new File("image/test1.jpg"));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(image, "jpg", baos);
			byte[] immAsBytes = baos.toByteArray();
			baos.flush();
			baos.close();
			RiakObject riakObject = new RiakObject().setContentType("image/jpg").setValue(
					BinaryValue.create(immAsBytes));
			StoreValue store = new StoreValue.Builder(riakObject).withLocation(location)
					.withOption(Option.W, new Quorum(3)).build();
			client.execute(store);
		} catch (ExecutionException | InterruptedException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void writeData(String bucketType, String bucketName, String key, String data) {
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
		}
	}

	public void readData(String bucketType, String bucketName, String key) {
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
		}
	}

	public void writeDataWithAmpRegister() {
		Namespace ns = new Namespace("my_map_type", "my_map_bucket");
		Location location = new Location(ns, "my_key");
		RegisterUpdate ru1 = new RegisterUpdate(BinaryValue.create("map_value_1"));
		RegisterUpdate ru2 = new RegisterUpdate(BinaryValue.create("map_value_2"));
		MapUpdate mu = new MapUpdate();
		mu.update("map_key_1", ru1);
		mu.update("map_key_2", ru2);
		UpdateMap update = new UpdateMap.Builder(location, mu).build();
		try {
			client.execute(update);
		} catch (ExecutionException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public void close() {
		client.shutdown();
	}

}
