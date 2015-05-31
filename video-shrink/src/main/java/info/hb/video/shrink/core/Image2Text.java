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
			"一个中年男子在持刀抢劫", //
			"3辆蓝色的宝马车在行驶", //
			"一辆奥迪和一辆兰博基尼撞上了，穿白色衣服的男司机和穿红色衣服的女司机正在理论", //
			"两个男子在打架", //
			"一辆自行车横穿马路", //
			"4个学生闯红灯", //
			"一个老人被一辆宝马车撞了，躺在地上", //
			"两个传白色衣服的男子经过", //
			"一个穿黑色衣服的男子在一辆自行车旁边" };

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
