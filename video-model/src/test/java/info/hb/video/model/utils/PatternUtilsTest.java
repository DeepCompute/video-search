package info.hb.video.model.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PatternUtilsTest {

	@Test
	public void testPatternUtils() {
		assertEquals("A003-G", PatternUtils.matchRoadId("A003-G昭亭路-文鼎路3文鼎路东-1"));
		assertEquals("A050", PatternUtils.matchRoadId("A050敬亭路-文鼎路3敬亭路南-1"));
		assertEquals("B010", PatternUtils.matchRoadId("B010锦城路-宣中后门路出口-1"));
		assertEquals("B014-G", PatternUtils.matchRoadId("B014-G鳌峰路-陵西路1陵西路北-2"));
		assertEquals("B016-G", PatternUtils.matchRoadId("B016-G鳌峰路-陵西路3鳌峰路东-1"));
		assertEquals("C025", PatternUtils.matchRoadId("C025庙埠村-村部-1"));
		assertEquals("C028", PatternUtils.matchRoadId("C028金苹果幼儿园-1"));
	}

}
