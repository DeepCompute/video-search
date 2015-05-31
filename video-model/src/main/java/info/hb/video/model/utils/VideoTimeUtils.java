package info.hb.video.model.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zx.soft.utils.log.LogbackUtil;

public class VideoTimeUtils {

	private static Logger logger = LoggerFactory.getLogger(VideoTimeUtils.class);

	private static final SimpleDateFormat SIMPLE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");

	/**
	 * 处理20150326205000格式的时间字符串，即：2015-03-26,20:50:00
	 */
	public static long transTime(String timeStr) {
		try {
			return SIMPLE_FORMAT.parse(timeStr).getTime();
		} catch (ParseException e) {
			// 出现错误抛出异常
			logger.error("Exception:{}", LogbackUtil.expection2Str(e));
			throw new RuntimeException(e);
		}
	}

}
