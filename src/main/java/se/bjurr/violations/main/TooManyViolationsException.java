package se.bjurr.violations.main;

public class TooManyViolationsException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public TooManyViolationsException(final String message) {
    super(message);
  }
}
