package info.hb.video.model.frame;

import java.io.Serializable;
import java.util.Date;

/**
 * 视频帧索引记录信息
 *
 * @author wanggang
 *
 */
public class FrameRecord implements Serializable {

	private static final long serialVersionUID = 3978546729349108743L;

	// 视频帧的唯一ID
	private String id;
	// 视频帧对应的文本描述内容
	private String frame_content;
	// 视频帧所在视频的索引
	private int frame_index;
	// 视频帧缓存的服务器IP
	private String frame_cache_ip;
	// 视频帧的存储地址
	private String frame_url;
	// 视频ID
	private long video_id;
	// 视频类型
	private String video_type;
	// 视频的记录起始时间
	private Date video_time_start;
	// 视频的记录结束时间
	private Date video_time_end;
	// 视频持续时间，秒
	private int video_time_duration;
	// 视频所在存储服务器IP
	private String video_ip;
	// 视频所在存储服务器目录
	private String video_dir;
	// 视频完整名称
	private String video_name;
	// 视频设备所在道路ID
	private String road_id;
	// 视频设备所在道路名称
	private String road_name;
	// 视频设备所在道路起始地址
	private String road_name_start;
	// 视频设备所在道路结束地址
	private String road_name_end;
	// 视频设备所在道路类型
	private int road_type;
	// 视频设备所在的经度
	private double longitude;
	// 视频设备所在的维度
	private double latitude;
	// 视频帧处理时间
	private Date timestamp;

	public FrameRecord() {
		super();
	}

	public FrameRecord(Builder builder) {
		super();
		this.id = builder.id;
		this.frame_content = builder.frame_content;
		this.frame_index = builder.frame_index;
		this.frame_cache_ip = builder.frame_cache_ip;
		this.frame_url = builder.frame_url;
		this.video_id = builder.video_id;
		this.video_type = builder.video_type;
		this.video_time_start = builder.video_time_start;
		this.video_time_end = builder.video_time_end;
		this.video_time_duration = builder.video_time_duration;
		this.video_ip = builder.video_ip;
		this.video_dir = builder.video_dir;
		this.video_name = builder.video_name;
		this.road_id = builder.road_id;
		this.road_name = builder.road_name;
		this.road_name_start = builder.road_name_start;
		this.road_name_end = builder.road_name_end;
		this.road_type = builder.road_type;
		this.longitude = builder.longitude;
		this.latitude = builder.latitude;
		this.timestamp = builder.timestamp;
	}

	@Override
	public String toString() {
		return "FrameRecord [id=" + id + ", frame_content=" + frame_content + ", frame_index=" + frame_index
				+ ", frame_cache_ip=" + frame_cache_ip + ", frame_url=" + frame_url + ", video_id=" + video_id
				+ ", video_type=" + video_type + ", video_time_start=" + video_time_start + ", video_time_end="
				+ video_time_end + ", video_time_duration=" + video_time_duration + ", video_ip=" + video_ip
				+ ", video_dir=" + video_dir + ", video_name=" + video_name + ", road_id=" + road_id + ", road_name="
				+ road_name + ", road_name_start=" + road_name_start + ", road_name_end=" + road_name_end
				+ ", road_type=" + road_type + ", longitude=" + longitude + ", latitude=" + latitude + ", timestamp="
				+ timestamp + "]";
	}

	public static class Builder {

		private final String id;
		private final String frame_content;
		private final int frame_index;
		private String frame_cache_ip = "";
		private String frame_url = "";
		private final long video_id;
		private String video_type = "ts";
		private Date video_time_start = null;
		private Date video_time_end = null;
		private int video_time_duration;
		private String video_ip = "";
		private String video_dir = "";
		private String video_name = "";
		private String road_id = "";
		private String road_name = "";
		private String road_name_start = "";
		private String road_name_end = "";
		private int road_type = 1;
		private double longitude;
		private double latitude;
		private Date timestamp = new Date();

		public Builder(String id, String frame_content, int frame_index, long video_id) {
			this.id = id;
			this.frame_content = frame_content;
			this.frame_index = frame_index;
			this.video_id = video_id;
		}

		public Builder setFrame_cache_ip(String frame_cache_ip) {
			this.frame_cache_ip = frame_cache_ip;
			return this;
		}

		public Builder setFrame_url(String frame_url) {
			this.frame_url = frame_url;
			return this;
		}

		public Builder setVideo_type(String video_type) {
			this.video_type = video_type;
			return this;
		}

		public Builder setVideo_time_start(Date video_time_start) {
			this.video_time_start = video_time_start;
			return this;
		}

		public Builder setVideo_time_end(Date video_time_end) {
			this.video_time_end = video_time_end;
			return this;
		}

		public Builder setVideo_time_duration(int video_time_duration) {
			this.video_time_duration = video_time_duration;
			return this;
		}

		public Builder setVideo_ip(String video_ip) {
			this.video_ip = video_ip;
			return this;
		}

		public Builder setVideo_dir(String video_dir) {
			this.video_dir = video_dir;
			return this;
		}

		public Builder setVideo_name(String video_name) {
			this.video_name = video_name;
			return this;
		}

		public Builder setRoad_id(String road_id) {
			this.road_id = road_id;
			return this;
		}

		public Builder setRoad_name(String road_name) {
			this.road_name = road_name;
			return this;
		}

		public Builder setRoad_name_start(String road_name_start) {
			this.road_name_start = road_name_start;
			return this;
		}

		public Builder setRoad_name_end(String road_name_end) {
			this.road_name_end = road_name_end;
			return this;
		}

		public Builder setRoad_type(int road_type) {
			this.road_type = road_type;
			return this;
		}

		public Builder setLongitude(double longitude) {
			this.longitude = longitude;
			return this;
		}

		public Builder setLatitude(double latitude) {
			this.latitude = latitude;
			return this;
		}

		public Builder setTimestamp(Date timestamp) {
			this.timestamp = timestamp;
			return this;
		}

		public FrameRecord build() {
			return new FrameRecord(this);
		}

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFrame_content() {
		return frame_content;
	}

	public void setFrame_content(String frame_content) {
		this.frame_content = frame_content;
	}

	public int getFrame_index() {
		return frame_index;
	}

	public void setFrame_index(int frame_index) {
		this.frame_index = frame_index;
	}

	public String getFrame_cache_ip() {
		return frame_cache_ip;
	}

	public void setFrame_cache_ip(String frame_cache_ip) {
		this.frame_cache_ip = frame_cache_ip;
	}

	public String getFrame_url() {
		return frame_url;
	}

	public void setFrame_url(String frame_url) {
		this.frame_url = frame_url;
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

	public Date getVideo_time_start() {
		return video_time_start;
	}

	public void setVideo_time_start(Date video_time_start) {
		this.video_time_start = video_time_start;
	}

	public Date getVideo_time_end() {
		return video_time_end;
	}

	public void setVideo_time_end(Date video_time_end) {
		this.video_time_end = video_time_end;
	}

	public int getVideo_time_duration() {
		return video_time_duration;
	}

	public void setVideo_time_duration(int video_time_duration) {
		this.video_time_duration = video_time_duration;
	}

	public String getVideo_ip() {
		return video_ip;
	}

	public void setVideo_ip(String video_ip) {
		this.video_ip = video_ip;
	}

	public String getVideo_dir() {
		return video_dir;
	}

	public void setVideo_dir(String video_dir) {
		this.video_dir = video_dir;
	}

	public String getVideo_name() {
		return video_name;
	}

	public void setVideo_name(String video_name) {
		this.video_name = video_name;
	}

	public String getRoad_id() {
		return road_id;
	}

	public void setRoad_id(String road_id) {
		this.road_id = road_id;
	}

	public String getRoad_name() {
		return road_name;
	}

	public void setRoad_name(String road_name) {
		this.road_name = road_name;
	}

	public String getRoad_name_start() {
		return road_name_start;
	}

	public void setRoad_name_start(String road_name_start) {
		this.road_name_start = road_name_start;
	}

	public String getRoad_name_end() {
		return road_name_end;
	}

	public void setRoad_name_end(String road_name_end) {
		this.road_name_end = road_name_end;
	}

	public int getRoad_type() {
		return road_type;
	}

	public void setRoad_type(int road_type) {
		this.road_type = road_type;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

}
