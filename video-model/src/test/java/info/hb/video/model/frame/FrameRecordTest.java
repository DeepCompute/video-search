package info.hb.video.model.frame;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

public class FrameRecordTest {

	@Test
	public void testFrameRecord() {
		FrameRecord fr = new FrameRecord.Builder("123456789", "一个40多岁的男人汽车摩托车", 123).setFrame_cache_ip("192.168.3.12")
				.setFrame_url("http://192.168.3.100/frame/1000000001.jpg")
				.setVideo_time_start(new Date(1432890201651L)).setVideo_time_end(new Date(1432910201651L))
				.setVideo_time_duration(3000).setVideo_ip("192.168.3.10").setVideo_dir("/home/video/dir")
				.setVideo_name("A003-G昭亭路-文鼎路3文鼎路东-1-20150326205000-20150326205939-195056832.ts").setRoad_id("A003-G")
				.setRoad_name("昭亭路-文鼎路3文鼎路东-1").setRoad_name_start("昭亭路").setRoad_name_end("文鼎路3文鼎路东-1")
				.setLongitude(-120.02336d).setLatitude(10.23665d).setTimestamp(new Date(1432890202651L)).build();
		String frStr = "FrameRecord [id=123456789, frame_content=一个40多岁的男人汽车摩托车, frame_index=123, frame_cache_ip=192.168.3.12, "
				+ "frame_url=http://192.168.3.100/frame/1000000001.jpg, video_time_start=Fri May 29 17:03:21 CST 2015, "
				+ "video_time_end=Fri May 29 22:36:41 CST 2015, video_time_duration=3000, video_ip=192.168.3.10, video_dir=/home/video/dir,"
				+ " video_name=A003-G昭亭路-文鼎路3文鼎路东-1-20150326205000-20150326205939-195056832.ts, road_id=A003-G, road_name=昭亭路-文鼎路3文鼎路东-1, "
				+ "road_name_start=昭亭路, road_name_end=文鼎路3文鼎路东-1, longitude=-120.02336, latitude=10.23665, "
				+ "timestamp=Fri May 29 17:03:22 CST 2015]";
		assertEquals(frStr, fr.toString());
		assertEquals("123456789", fr.getId());
		assertEquals("一个40多岁的男人汽车摩托车", fr.getFrame_content());
		assertEquals(123, fr.getFrame_index());
		assertEquals("192.168.3.12", fr.getFrame_cache_ip());
		assertEquals("http://192.168.3.100/frame/1000000001.jpg", fr.getFrame_url());
		assertEquals("Fri May 29 17:03:21 CST 2015", fr.getVideo_time_start().toString());
		assertEquals("Fri May 29 22:36:41 CST 2015", fr.getVideo_time_end().toString());
		assertEquals(3000, fr.getVideo_time_duration());
		assertEquals("192.168.3.10", fr.getVideo_ip());
		assertEquals("/home/video/dir", fr.getVideo_dir());
		assertEquals("A003-G昭亭路-文鼎路3文鼎路东-1-20150326205000-20150326205939-195056832.ts", fr.getVideo_name());
		assertEquals("A003-G", fr.getRoad_id());
		assertEquals("昭亭路-文鼎路3文鼎路东-1", fr.getRoad_name());
		assertEquals("昭亭路", fr.getRoad_name_start());
		assertEquals("文鼎路3文鼎路东-1", fr.getRoad_name_end());
		assertEquals(-120.02336, fr.getLongitude(), 0.0d);
		assertEquals(10.23665, fr.getLatitude(), 0.0d);
		assertEquals("Fri May 29 17:03:22 CST 2015", fr.getTimestamp().toString());
	}

}
