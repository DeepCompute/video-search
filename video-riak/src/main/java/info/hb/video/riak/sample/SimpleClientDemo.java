package info.hb.video.riak.sample;

import info.hb.video.riak.client.Image2RiakClient;
import info.hb.video.riak.common.VideoImageRiakConstant;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import zx.soft.utils.json.JsonUtils;

public class SimpleClientDemo {

	/**
	 * 测试函数
	 */
	public static void main(String[] args) throws IOException {
		// http://hefei[08,09,10]:8098/riak/string_data/wgybzbsimple
		Image2RiakClient client = new Image2RiakClient();
		// 写String数据
		client.writeString("string_bucket_type", "string_data", "wgybzbsimple", "测试数据，简单的Client");
		// 读String数据
		System.out.println(JsonUtils.toJson(client.readString("string_bucket_type", "string_data", "wgybzb")));
		// 存图片
		// http://hefei[08,09,10]:8098/riak/videos/wgybzbsimple
		client.writeImage(VideoImageRiakConstant.IMAGE_BUCKET_TYPE, "videos", "wgybzbsimple",
				ImageIO.read(new File("image/test1.jpg")), "jpg");
		// 删除数据
		client.deleteObject("string_bucket_type", "string_data", "wgybzbsimple");
		client.deleteObject(VideoImageRiakConstant.IMAGE_BUCKET_TYPE, "videos", "wgybzbsimple");
		// 关闭资源
		client.close();
	}

}
