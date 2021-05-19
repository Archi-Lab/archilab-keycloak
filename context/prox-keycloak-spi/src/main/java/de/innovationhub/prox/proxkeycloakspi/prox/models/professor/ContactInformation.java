package de.innovationhub.prox.proxkeycloakspi.prox.models.professor;

import lombok.Data;

@Data
public class ContactInformation {
  private String room;
  private String consultationHour;
  private String email;
  private String telephone;
  private String homepage;
  private String collegePage;

  public ContactInformation(String room, String consultationHour, String email,
      String telephone, String homepage, String collegePage) {
    this.room = room;
    this.consultationHour = consultationHour;
    this.email = email;
    this.telephone = telephone;
    this.homepage = homepage;
    this.collegePage = collegePage;
  }
}
