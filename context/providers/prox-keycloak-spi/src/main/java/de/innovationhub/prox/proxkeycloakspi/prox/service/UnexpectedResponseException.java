package de.innovationhub.prox.proxkeycloakspi.prox.service;

public class UnexpectedResponseException extends Exception{
  public UnexpectedResponseException() {
    super();
  }

  public UnexpectedResponseException(String message) {
    super(message);
  }
}
