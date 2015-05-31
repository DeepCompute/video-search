package info.hb.video.model.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则匹配工具
 *
 * @author wanggang
 *
 */
public class PatternUtils {

	private static final Pattern PATTERN = Pattern.compile("[0-9A-Z\\-]{4,}");

	public static String matchRoadId(String roadStr) {
		Matcher ss = PATTERN.matcher(roadStr);
		if (ss.find()) {
			return ss.group();
		} else {
			return "";
		}
	}

}
