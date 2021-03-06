package sk.stuba.fiit.perconik.eclipse.core.runtime;

import javax.annotation.Nullable;

public class RuntimeCoreException extends RuntimeException {
  private static final long serialVersionUID = 0L;

  /**
   * Creates a new instance with no detail message.
   */
  public RuntimeCoreException() {
    super();
  }

  /**
   * Creates a new instance with the given detail message.
   */
  public RuntimeCoreException(@Nullable final String message) {
    super(message);
  }

  /**
   * Creates a new instance with the given detail message and cause.
   */
  public RuntimeCoreException(@Nullable final String message, @Nullable final Throwable cause) {
    super(message, cause);
  }

  /**
   * Creates a new instance with the given cause.
   */
  public RuntimeCoreException(@Nullable final Throwable cause) {
    super(cause);
  }
}
