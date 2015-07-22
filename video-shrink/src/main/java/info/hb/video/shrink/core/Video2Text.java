package info.hb.video.shrink.core;

import info.hb.riak.cluster.client.HBRiakClient;
import info.hb.riak.cluster.client.HBRiakClusterImpl;
import info.hb.video.model.frame.FrameRecord;
import info.hb.video.model.name.VideoName;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.video.Video;
import org.openimaj.video.xuggle.XuggleVideo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zx.soft.utils.checksum.CheckSumUtils;

/**
 * 视频转文本化
 *
 * @author wanggang
 *
 */
public class Video2Text {

	private static Logger logger = LoggerFactory.getLogger(Video2Text.class);

	private static final String IMAGE_SUFFIX = "png";

	private static final String BUCKET_NAME = "frames";

	public static final String IMAGE_BUCKET_TYPE = "default";

	public static final int HTTP_PORT = 8098;

	// Nginx代理服务器
	public static final String NGINX_PROXY = "192.168.32.23:1888";

	// 帧率
	public static final int FRAME_RATE = 30;

	private HBRiakClient cluster;

	public Video2Text() {
		cluster = new HBRiakClusterImpl();
	}

	public List<FrameRecord> video2Text(String videoFile) {
		List<FrameRecord> result = new ArrayList<>();
		try (Video<MBFImage> frames = new XuggleVideo(new File(videoFile));) {
			long current = System.currentTimeMillis();
			String vname = videoFile.substring(videoFile.lastIndexOf("/") + 1);
			VideoName videoName = new VideoName(vname);
			logger.info("Video:{} has {} frmaes.", vname, frames.countFrames());
			// 帧索引
			int index = 1;
			FrameRecord frameRecord = null;
			/*
			 *  封装当前视频帧
			 */
			for (MBFImage mbfImage : frames) {
				System.err.println(index);
				// 没帧率取1帧
				if ((index - 1) % FRAME_RATE != 0) {
					index++;
					continue;
				}
				// id是根据videoName+frame_index进行MD5得到的
				String id = CheckSumUtils.getMD5(vname + index);
				/**
				 * 存储关键帧到Riak中
				 */
				// TODO 需要图片压缩
				//
				// 存储PNG到Riak
				cluster.writeImage(IMAGE_BUCKET_TYPE, BUCKET_NAME, id + "." + IMAGE_SUFFIX,
						ImageUtilities.createBufferedImageForDisplay(mbfImage), IMAGE_SUFFIX);
				// 视频帧缓存的服务器IP，待完善
				// 视频帧的存储地址，待完善
				// 视频所在存储服务器IP，待完善
				// 视频所在存储服务器目录，待完善
				// 视频设备所在的经度，待完善
				// 视频设备所在的维度，待完善
				frameRecord = new FrameRecord.Builder(id, Image2Text.image2Text(mbfImage), index,
						videoName.getVideo_id())
						.setVideo_type(videoName.getVideo_type())
						.setFrame_cache_ip(cluster.getIpsStr())
						.setFrame_url(getFrameUrl(id))
						.setVideo_time_start(new Date(videoName.getVideo_time_start()))
						.setVideo_time_end(new Date(videoName.getVideo_time_end()))
						.setVideo_time_duration(
								(int) (videoName.getVideo_time_end() - videoName.getVideo_time_start()) / 1000)
						.setVideo_ip("192.168.31.15").setVideo_dir("/home/videos/samples-videos").setVideo_name(vname)
						.setRoad_id(videoName.getRoad_id()).setRoad_name(videoName.getRoad_name())
						.setRoad_name_start(videoName.getRoad_name_start())
						.setRoad_name_end(videoName.getRoad_name_end()).setRoad_type(videoName.getRoad_type())
						.setLongitude(-120.36365d).setLatitude(10.23365d).setTimestamp(new Date(current))

						.build();
				// 添加到列表中
				result.add(frameRecord);
				index++;
				//				if (index == 100) {
				//					break;
				//				}
			}
		}
		return result;
	}

	private String getFrameUrl(String id) {
		return "http://" + NGINX_PROXY + "/riak/" + BUCKET_NAME + "/" + id + "." + IMAGE_SUFFIX;
	}

	public void close() {
		cluster.close();
	}

}
