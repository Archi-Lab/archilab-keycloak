package de.innovationhub.prox.proxkeycloakspi.prox.models.professor;

import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public class Professor {
  private UUID id;
  private String name;
  private String affiliation;
  private String mainSubject;
  private String vita;
  private ContactInformation contactInformation;
  private List<ResearchSubject> researchSubjects;
  private List<Publication> publications;

  public Professor(UUID id, String name, String affiliation, String mainSubject,
      String vita,
      ContactInformation contactInformation,
      List<ResearchSubject> researchSubjects,
      List<Publication> publications) {
    this.id = id;
    this.name = name;
    this.affiliation = affiliation;
    this.mainSubject = mainSubject;
    this.vita = vita;
    this.contactInformation = contactInformation;
    this.researchSubjects = researchSubjects;
    this.publications = publications;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAffiliation() {
    return affiliation;
  }

  public void setAffiliation(String affiliation) {
    this.affiliation = affiliation;
  }

  public String getMainSubject() {
    return mainSubject;
  }

  public void setMainSubject(String mainSubject) {
    this.mainSubject = mainSubject;
  }

  public String getVita() {
    return vita;
  }

  public void setVita(String vita) {
    this.vita = vita;
  }

  public ContactInformation getContactInformation() {
    return contactInformation;
  }

  public void setContactInformation(
      ContactInformation contactInformation) {
    this.contactInformation = contactInformation;
  }

  public List<ResearchSubject> getResearchSubjects() {
    return researchSubjects;
  }

  public void setResearchSubjects(
      List<ResearchSubject> researchSubjects) {
    this.researchSubjects = researchSubjects;
  }

  public List<Publication> getPublications() {
    return publications;
  }

  public void setPublications(
      List<Publication> publications) {
    this.publications = publications;
  }

  @Override
  public String toString() {
    return "Professor{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", affiliation='" + affiliation + '\'' +
        ", mainSubject='" + mainSubject + '\'' +
        ", vita='" + vita + '\'' +
        ", contactInformation=" + contactInformation +
        ", researchSubjects=" + researchSubjects +
        ", publications=" + publications +
        '}';
  }
}
