package se.bjurr.violations.main;

public class TooManyViolationsException extends RuntimeException {

  public TooManyViolationsException(final String message) {
    super(message);
  }
}
