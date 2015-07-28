package info.hb.video.shrink.summary;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

public class VideoException extends Exception {

	private static final long serialVersionUID = -8569873996804244801L;

	public VideoException() {
		System.out.println("Cant render video stream..");
	}

	public VideoException(String message) {
		super(message);
	}

	public VideoException(Throwable cause) {
		super(cause);
	}

	public VideoException(String message, Throwable cause) {
		super(message, cause);
	}

}
