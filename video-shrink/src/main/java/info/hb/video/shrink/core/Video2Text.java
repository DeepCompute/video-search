package info.hb.video.shrink.core;

import info.hb.video.model.frame.FrameRecord;
import info.hb.video.model.name.VideoName;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

	private static final String IMAGE_SUFFIX = ".png";

	/**
	 * 测试函数
	 */
	public static void main(String[] args) {

		String videoFile = "/home/wanggang/develop/deeplearning/sample-videos/A003-G昭亭路-文鼎路3文鼎路东-1-20150326205000-20150326205939-195056832.ts";
		List<FrameRecord> frames = Video2Text.video2Text(videoFile);
		System.out.println(frames.size());
		//		System.out.println(JsonUtils.toJson(frames));
	}

	public static List<FrameRecord> video2Text(String videoFile) {
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
				// id是根据videoName+frame_index进行MD5得到的
				String id = CheckSumUtils.getMD5(vname + index);
				// 视频帧缓存的服务器IP，待完善
				// 视频帧的存储地址，待完善
				// 视频所在存储服务器IP，待完善
				// 视频所在存储服务器目录，待完善
				// 视频设备所在的经度，待完善
				// 视频设备所在的维度，待完善
				frameRecord = new FrameRecord.Builder(id, Image2Text.image2Text(mbfImage), index,
						videoName.getVideo_id())
						.setVideo_type(videoName.getVideo_type())
						.setFrame_cache_ip("192.168.31.15")
						.setFrame_url("http://192.168.31.15/video/frames/" + id + IMAGE_SUFFIX)
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
				if (index == 100) {
					break;
				}
			}
		}
		return result;
	}

}
