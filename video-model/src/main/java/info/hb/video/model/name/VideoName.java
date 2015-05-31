package info.hb.video.model.name;

import info.hb.video.model.utils.VideoTimeUtils;

/**
 * 视频名称类
 * 组成：road_name-road_type-video_time_start-video_time_end-video_id.video_type
 *
 * @author wanggang
 *
 */
public class VideoName extends RoadName {

	private static final long serialVersionUID = -8806599402219808946L;

	private long video_time_start;
	private long video_time_end;
	private long video_id;
	private String video_type;

	public VideoName() {
		super();
	}

	public VideoName(String videoStr) {
		super(videoStr.split("\\.")[0].substring(0, videoStr.split("\\.")[0].length() - 40));
		String[] strs = videoStr.split("-");
		this.video_time_start = VideoTimeUtils.transTime(strs[strs.length - 3]);
		this.video_time_end = VideoTimeUtils.transTime(strs[strs.length - 2]);
		this.video_id = Long.parseLong(strs[strs.length - 1].split("\\.")[0]);
		this.video_type = strs[strs.length - 1].split("\\.")[1];
	}

	@Override
	public String toString() {
		return "VideoName [video_time_start=" + video_time_start + ", video_time_end=" + video_time_end + ", video_id="
				+ video_id + ", video_type=" + video_type + ", road_name=" + road_name + ", road_id=" + road_id
				+ ", road_name_start=" + road_name_start + ", road_name_end=" + road_name_end + ", road_type="
				+ road_type + "]";
	}

	public long getVideo_time_start() {
		return video_time_start;
	}

	public void setVideo_time_start(long video_time_start) {
		this.video_time_start = video_time_start;
	}

	public long getVideo_time_end() {
		return video_time_end;
	}

	public void setVideo_time_end(long video_time_end) {
		this.video_time_end = video_time_end;
	}

	public long getVideo_id() {
		return video_id;
	}

	public void setVideo_id(long video_id) {
		this.video_id = video_id;
	}

	public String getVideo_type() {
		return video_type;
	}

	public void setVideo_type(String video_type) {
		this.video_type = video_type;
	}

}
