package info.hb.video.mapred.wm.util;

import java.util.UUID;

/**
 * 管道
 * 
 * @author wgybzb
 *
 */
public class Pipe {

	private final String path;

	public Pipe() {
		this.path = "/tmp/" + UUID.randomUUID().toString();

		try {
			FFMPEGUtil.runBash("mkfifo " + path);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Pipe(String suffix) {
		this.path = "/tmp/" + UUID.randomUUID().toString() + suffix;

		try {
			FFMPEGUtil.runBash("mkfifo " + path);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public String getPath() {
		return path;
	}

	public void delete() {
		try {
			FFMPEGUtil.runBash("rm " + path);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
