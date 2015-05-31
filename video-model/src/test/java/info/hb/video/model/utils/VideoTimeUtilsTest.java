package info.hb.video.model.utils;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

public class VideoTimeUtilsTest {

	@SuppressWarnings("deprecation")
	@Test
	public void testTransTime() {
		long time = VideoTimeUtils.transTime("20150326205000");
		assertEquals(1427374200000L, time, 0L);
		assertEquals("Thu Mar 26 20:50:00 CST 2015", new Date(time).toString());
		assertEquals("2015-3-26 20:50:00", new Date(time).toLocaleString());
	}

}
