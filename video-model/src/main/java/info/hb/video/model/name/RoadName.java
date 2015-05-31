package info.hb.video.model.name;

import info.hb.video.model.utils.PatternUtils;

import java.io.Serializable;

/**
 * 道路名称信息
 * 组成：road_id-road_name_start-road_name_end-road_type
 *
 * @author wanggang
 *
 */
public class RoadName implements Serializable {

	private static final long serialVersionUID = 7369498614025652277L;

	// 视频设备所在道路名称
	protected String road_name;
	// 视频设备所在道路ID
	protected String road_id;
	// 视频设备所在道路起始地址
	protected String road_name_start;
	// 视频设备所在道路结束地址，可能没有
	protected String road_name_end = "";
	// 视频设备所在道路的类型
	protected int road_type;

	public RoadName() {
		super();
	}

	public RoadName(String roadStr) {
		super();
		this.road_type = Integer.parseInt(roadStr.substring(roadStr.length() - 1));
		String roadStrPre = roadStr.substring(0, roadStr.length() - 2);
		this.road_id = PatternUtils.matchRoadId(roadStrPre);
		this.road_name = roadStrPre.substring(road_id.length());
		if (road_name.contains("-")) {
			this.road_name_start = road_name.split("-")[0];
			this.road_name_end = road_name.split("-")[1];
		} else {
			this.road_name_start = road_name;
		}
	}

	public String getRoad_name() {
		return road_name;
	}

	public void setRoad_name(String road_name) {
		this.road_name = road_name;
	}

	public String getRoad_id() {
		return road_id;
	}

	public void setRoad_id(String road_id) {
		this.road_id = road_id;
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

}
