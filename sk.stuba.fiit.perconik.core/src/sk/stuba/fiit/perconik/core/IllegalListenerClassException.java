package sk.stuba.fiit.perconik.core;

import javax.annotation.Nullable;

/**
 * Thrown by an implementation of core services when input parameters
 * are invalid. This may occur either because the class is not an instance
 * of {@link sk.stuba.fiit.perconik.core.Listener Listener} or it does not
 * meet any other service implementation specific requirements.
 *
 * @author Pavol Zbell
 * @since 1.0
 */
public class IllegalListenerClassException extends IllegalArgumentException {
  private static final long serialVersionUID = 0L;

  /**
   * Creates a new instance with no detail message.
   */
  public IllegalListenerClassException() {
    super();
  }

  /**
   * Creates a new instance with the given detail message.
   */
  public IllegalListenerClassException(@Nullable final String message) {
    super(message);
  }

  /**
   * Creates a new instance with the given detail message and cause.
   */
  public IllegalListenerClassException(@Nullable final String message, @Nullable final Throwable cause) {
    super(message, cause);
  }

  /**
   * Creates a new instance with the given cause.
   */
  public IllegalListenerClassException(@Nullable final Throwable cause) {
    super(cause);
  }
}
