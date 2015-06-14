package info.hb.video.riak.sample;

import info.hb.video.riak.client.Image2RiakCluster;
import info.hb.video.riak.common.VideoImageRiakConstant;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import zx.soft.utils.json.JsonUtils;

public class ClusterClientDemo {

	/**
	 * 测试函数
	 */
	public static void main(String[] args) throws IOException {
		// http://hefei[08,09,10]:8098/riak/string_data/wgybzb
		Image2RiakCluster cluster = new Image2RiakCluster();
		// 写String数据
		cluster.writeString("string_bucket_type", "string_data", "wgybzb", "测试数据");
		// 读String数据
		System.out.println(JsonUtils.toJson(cluster.readString("string_bucket_type", "string_data", "wgybzb")));
		// 存图片
		// http://hefei[08,09,10]:8098/riak/videos/wgybzb
		cluster.writeImage(VideoImageRiakConstant.IMAGE_BUCKET_TYPE, "videos", "wgybzb",
				ImageIO.read(new File("image/test1.jpg")), "jpg");
		// 删除数据
		cluster.deleteObject("string_bucket_type", "string_data", "wgybzb");
		cluster.deleteObject(VideoImageRiakConstant.IMAGE_BUCKET_TYPE, "videos", "wgybzb");
		// 关闭资源
		cluster.close();
	}

}
