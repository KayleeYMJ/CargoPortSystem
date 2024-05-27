package portsim.util;

/**
 * Exception thrown when an encoded string is not correct according to the appropriate
 * fromString() method.
 *
 * @ass2
 */
public class BadEncodingException extends Exception {
    /**
     * Constructs a new BadEncodingException with no detail message or cause.
     *
     * @see Exception#Exception()
     * @ass2
     */
    public BadEncodingException() {
        super();
    }

    /**
     * Constructs a BadEncodingException that contains a helpful detail message explaining why
     * the exception occurred.
     *
     * @param message detail message
     * @ass2
     * @see Exception#Exception(String)
     */
    public BadEncodingException(String message) {
        super(message);
    }

    /**
     * Constructs a BadEncodingException that stores the underlying cause of the exception.
     *
     * @param cause throwable that caused this exception
     * @ass2
     * @see Exception#Exception(Throwable)
     */
    public BadEncodingException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a BadEncodingException that contains a helpful detail message explaining why
     * the exception occurred and the underlying cause of the exception.
     *
     * @param message detail message
     * @param cause   throwable that caused this exception
     * @ass2
     * @see Exception#Exception(String, Throwable)
     */
    public BadEncodingException(String message, Throwable cause) {
        super(message, cause);
    }
}
