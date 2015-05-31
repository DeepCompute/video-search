package info.hb.video.model.name;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

@SuppressWarnings("deprecation")
public class VideoNameTest {

	@Test
	public void testVideoName1() {
		VideoName vn = new VideoName("A003-G昭亭路-文鼎路3文鼎路东-1-20150326205000-20150326205939-195056832.ts");
		assertEquals("ts", vn.getVideo_type());
		assertEquals(195056832L, vn.getVideo_id());
		assertEquals("2015-3-26 20:59:39", new Date(vn.getVideo_time_end()).toLocaleString());
		assertEquals("2015-3-26 20:50:00", new Date(vn.getVideo_time_start()).toLocaleString());
		assertEquals("A003-G", vn.getRoad_id());
		assertEquals("昭亭路-文鼎路3文鼎路东", vn.getRoad_name());
		assertEquals("昭亭路", vn.getRoad_name_start());
		assertEquals("文鼎路3文鼎路东", vn.getRoad_name_end());
		assertEquals(1, vn.getRoad_type());
	}

	@Test
	public void testVideoName2() {
		VideoName vn = new VideoName("A050敬亭路-文鼎路3敬亭路南-1-20150326205000-20150326205947-195125067.ts");
		assertEquals("ts", vn.getVideo_type());
		assertEquals(195125067L, vn.getVideo_id());
		assertEquals("2015-3-26 20:59:47", new Date(vn.getVideo_time_end()).toLocaleString());
		assertEquals("2015-3-26 20:50:00", new Date(vn.getVideo_time_start()).toLocaleString());
		assertEquals("A050", vn.getRoad_id());
		assertEquals("敬亭路-文鼎路3敬亭路南", vn.getRoad_name());
		assertEquals("敬亭路", vn.getRoad_name_start());
		assertEquals("文鼎路3敬亭路南", vn.getRoad_name_end());
		assertEquals(1, vn.getRoad_type());
	}

	@Test
	public void testVideoName3() {
		VideoName vn = new VideoName("B010锦城路-宣中后门路出口-1-20150326085000-20150326085934-195311270.ts");
		assertEquals("ts", vn.getVideo_type());
		assertEquals(195311270L, vn.getVideo_id());
		assertEquals("2015-3-26 8:59:34", new Date(vn.getVideo_time_end()).toLocaleString());
		assertEquals("2015-3-26 8:50:00", new Date(vn.getVideo_time_start()).toLocaleString());
		assertEquals("B010", vn.getRoad_id());
		assertEquals("锦城路-宣中后门路出口", vn.getRoad_name());
		assertEquals("锦城路", vn.getRoad_name_start());
		assertEquals("宣中后门路出口", vn.getRoad_name_end());
		assertEquals(1, vn.getRoad_type());
	}

	@Test
	public void testVideoName4() {
		VideoName vn = new VideoName("B014-G鳌峰路-陵西路1陵西路北-2-20150326085000-20150326085935-195403888.ts");
		assertEquals("ts", vn.getVideo_type());
		assertEquals(195403888L, vn.getVideo_id());
		assertEquals("2015-3-26 8:59:35", new Date(vn.getVideo_time_end()).toLocaleString());
		assertEquals("2015-3-26 8:50:00", new Date(vn.getVideo_time_start()).toLocaleString());
		assertEquals("B014-G", vn.getRoad_id());
		assertEquals("鳌峰路-陵西路1陵西路北", vn.getRoad_name());
		assertEquals("鳌峰路", vn.getRoad_name_start());
		assertEquals("陵西路1陵西路北", vn.getRoad_name_end());
		assertEquals(2, vn.getRoad_type());
	}

	@Test
	public void testVideoName5() {
		VideoName vn = new VideoName("B016-G鳌峰路-陵西路3鳌峰路东-1-20150326085000-20150326085940-195378148.ts");
		assertEquals("ts", vn.getVideo_type());
		assertEquals(195378148L, vn.getVideo_id());
		assertEquals("2015-3-26 8:59:40", new Date(vn.getVideo_time_end()).toLocaleString());
		assertEquals("2015-3-26 8:50:00", new Date(vn.getVideo_time_start()).toLocaleString());
		assertEquals("B016-G", vn.getRoad_id());
		assertEquals("鳌峰路-陵西路3鳌峰路东", vn.getRoad_name());
		assertEquals("鳌峰路", vn.getRoad_name_start());
		assertEquals("陵西路3鳌峰路东", vn.getRoad_name_end());
		assertEquals(1, vn.getRoad_type());
	}

	@Test
	public void testVideoName6() {
		VideoName vn = new VideoName("C025庙埠村-村部-1-20150326205000-20150326205938-195258979.ts");
		assertEquals("ts", vn.getVideo_type());
		assertEquals(195258979L, vn.getVideo_id());
		assertEquals("2015-3-26 20:59:38", new Date(vn.getVideo_time_end()).toLocaleString());
		assertEquals("2015-3-26 20:50:00", new Date(vn.getVideo_time_start()).toLocaleString());
		assertEquals("C025", vn.getRoad_id());
		assertEquals("庙埠村-村部", vn.getRoad_name());
		assertEquals("庙埠村", vn.getRoad_name_start());
		assertEquals("村部", vn.getRoad_name_end());
		assertEquals(1, vn.getRoad_type());
	}

	@Test
	public void testVideoName7() {
		VideoName vn = new VideoName("C028金苹果幼儿园-1-20150326205000-20150326205930-195190416.ts");
		assertEquals("ts", vn.getVideo_type());
		assertEquals(195190416L, vn.getVideo_id());
		assertEquals("2015-3-26 20:59:30", new Date(vn.getVideo_time_end()).toLocaleString());
		assertEquals("2015-3-26 20:50:00", new Date(vn.getVideo_time_start()).toLocaleString());
		assertEquals("C028", vn.getRoad_id());
		assertEquals("金苹果幼儿园", vn.getRoad_name());
		assertEquals("金苹果幼儿园", vn.getRoad_name_start());
		assertEquals("", vn.getRoad_name_end());
		assertEquals(1, vn.getRoad_type());
	}
}
