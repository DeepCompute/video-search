package info.hb.video.model.name;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class RoadNameTest {

	@Test
	public void testRoadNameParser1() {
		RoadName rn = new RoadName("A003-G昭亭路-文鼎路3文鼎路东-1");
		assertEquals("A003-G", rn.getRoad_id());
		assertEquals("昭亭路-文鼎路3文鼎路东", rn.getRoad_name());
		assertEquals("昭亭路", rn.getRoad_name_start());
		assertEquals("文鼎路3文鼎路东", rn.getRoad_name_end());
		assertEquals(1, rn.getRoad_type());
	}

	@Test
	public void testRoadNameParser2() {
		RoadName rn = new RoadName("A050敬亭路-文鼎路3敬亭路南-1");
		assertEquals("A050", rn.getRoad_id());
		assertEquals("敬亭路-文鼎路3敬亭路南", rn.getRoad_name());
		assertEquals("敬亭路", rn.getRoad_name_start());
		assertEquals("文鼎路3敬亭路南", rn.getRoad_name_end());
		assertEquals(1, rn.getRoad_type());
	}

	@Test
	public void testRoadNameParser3() {
		RoadName rn = new RoadName("B010锦城路-宣中后门路出口-1");
		assertEquals("B010", rn.getRoad_id());
		assertEquals("锦城路-宣中后门路出口", rn.getRoad_name());
		assertEquals("锦城路", rn.getRoad_name_start());
		assertEquals("宣中后门路出口", rn.getRoad_name_end());
		assertEquals(1, rn.getRoad_type());
	}

	@Test
	public void testRoadNameParser4() {
		RoadName rn = new RoadName("B014-G鳌峰路-陵西路1陵西路北-2");
		assertEquals("B014-G", rn.getRoad_id());
		assertEquals("鳌峰路-陵西路1陵西路北", rn.getRoad_name());
		assertEquals("鳌峰路", rn.getRoad_name_start());
		assertEquals("陵西路1陵西路北", rn.getRoad_name_end());
		assertEquals(2, rn.getRoad_type());
	}

	@Test
	public void testRoadNameParser5() {
		RoadName rn = new RoadName("B016-G鳌峰路-陵西路3鳌峰路东-1");
		assertEquals("B016-G", rn.getRoad_id());
		assertEquals("鳌峰路-陵西路3鳌峰路东", rn.getRoad_name());
		assertEquals("鳌峰路", rn.getRoad_name_start());
		assertEquals("陵西路3鳌峰路东", rn.getRoad_name_end());
		assertEquals(1, rn.getRoad_type());
	}

	@Test
	public void testRoadNameParser6() {
		RoadName rn = new RoadName("C025庙埠村-村部-1");
		assertEquals("C025", rn.getRoad_id());
		assertEquals("庙埠村-村部", rn.getRoad_name());
		assertEquals("庙埠村", rn.getRoad_name_start());
		assertEquals("村部", rn.getRoad_name_end());
		assertEquals(1, rn.getRoad_type());
	}

	@Test
	public void testRoadNameParser7() {
		RoadName rn = new RoadName("C028金苹果幼儿园-1");
		assertEquals("C028", rn.getRoad_id());
		assertEquals("金苹果幼儿园", rn.getRoad_name());
		assertEquals("金苹果幼儿园", rn.getRoad_name_start());
		assertEquals("", rn.getRoad_name_end());
		assertEquals(1, rn.getRoad_type());
	}

}
