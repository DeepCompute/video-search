package info.hb.video.shrink.core;

import java.util.Random;

import org.openimaj.image.MBFImage;

/**
 * 图片文本化
 *
 * @TODO 待完善
 *
 * @author wanggang
 *
 */
public class Image2Text {

	//	private static Logger logger = LoggerFactory.getLogger(Image2Text.class);

	private static final String[] SAMPLE_TEXTS = { "3个穿白衣服的男子正在斗殴",//
			"一个穿红衣服的中年男子在行走，多辆车经过", //
			"有黑色的轿车和多辆电动车经过，天气疑似阴雨", //
			"有白色的轿车和三轮车经过", //
			"多辆出租车和公交车经过", //
			"一辆白色的轿车和4个成年人在附近", //
			"分别停靠一辆黑色和白色的轿车，一个穿黑衣服的青年男子经过", //
			"视频模糊，疑似阴雨天气或黑暗拍摄", //
			"三个中年人手提物品经过", //
			"停靠两辆白色中巴，天气阴暗，无人经过" };

	private static final Random RANDOM = new Random();

	/**
	 * 测试函数
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static String image2Text(MBFImage mbfImage) {
		// TODO
		return SAMPLE_TEXTS[RANDOM.nextInt(SAMPLE_TEXTS.length)];
	}

}
