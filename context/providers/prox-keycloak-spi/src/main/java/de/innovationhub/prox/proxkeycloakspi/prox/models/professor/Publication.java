package de.innovationhub.prox.proxkeycloakspi.prox.models.professor;

import lombok.Data;

@Data
public class Publication {
  private String publication;

  public Publication(String publication) {
    this.publication = publication;
  }

  public String getPublication() {
    return publication;
  }

  public void setPublication(String publication) {
    this.publication = publication;
  }

  @Override
  public String toString() {
    return "Publication{" +
        "publication='" + publication + '\'' +
        '}';
  }
}
