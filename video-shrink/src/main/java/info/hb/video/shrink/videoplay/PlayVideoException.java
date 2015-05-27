package info.hb.video.shrink.videoplay;

public class PlayVideoException extends Exception {

	private static final long serialVersionUID = -5789176294602134159L;

	public PlayVideoException() {
		System.out.println("Cant render video stream..");
	}

	public PlayVideoException(String message) {
		super(message);
	}

	public PlayVideoException(Throwable cause) {
		super(cause);
	}

	public PlayVideoException(String message, Throwable cause) {
		super(message, cause);
	}

}
