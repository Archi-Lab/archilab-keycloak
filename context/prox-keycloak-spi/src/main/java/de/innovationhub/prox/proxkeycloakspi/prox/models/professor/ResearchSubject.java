package de.innovationhub.prox.proxkeycloakspi.prox.models.professor;

import lombok.Data;

@Data
public class ResearchSubject {
  private String subject;

  public ResearchSubject(String subject) {
    this.subject = subject;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  @Override
  public String toString() {
    return "ResearchSubject{" +
        "subject='" + subject + '\'' +
        '}';
  }
}
