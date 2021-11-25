package de.innovationhub.prox.proxkeycloakspi.prox.service;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import de.innovationhub.prox.proxkeycloakspi.prox.models.professor.ContactInformation;
import de.innovationhub.prox.proxkeycloakspi.prox.models.professor.Professor;
import java.io.IOException;
import java.util.Collections;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.models.UserModel;

class ProfessorServiceTest {

  WireMockServer wireMockServer;

  @BeforeEach
  public void setup() {
    this.wireMockServer = new WireMockServer(
        options()
            .extensions(new ResponseTemplateTransformer(false))
            .dynamicPort()
    );
  }

  @Test
  void whenCreateProfessor_GivenApiResponse201AndProfessorAndToken_ShouldCallValidApiRequest()
      throws Exception {
    this.wireMockServer
        .stubFor(post(urlPathEqualTo("/professors")).willReturn(aResponse().withStatus(201)));
    this.wireMockServer.start();
    ProfessorService professorService = new ProfessorService(this.wireMockServer.baseUrl());

    Professor professor = new Professor(UUID.randomUUID(), "Test", null, null, null,
        new ContactInformation(null, null, "test@example.org", null, null, null),
        Collections.emptyList(),
        Collections.emptyList());

    String accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

    professorService.createProfessor(professor, accessToken);

    this.wireMockServer.verify(postRequestedFor(urlEqualTo("/professors"))
        .withRequestBody(equalToJson(new ObjectMapper().writeValueAsString(professor)))
        .withHeader("Content-Type", equalTo("application/json"))
        .withHeader("Authorization", equalTo("Bearer " + accessToken))
    );
  }

  @Test
  void profileExists_GivenApiResponse200AndProfessor_ShouldReturnTrue() throws Exception {
    UUID id = UUID.randomUUID();
    this.wireMockServer
        .stubFor(get(urlPathEqualTo("/professors/" + id.toString())).willReturn(aResponse().withStatus(200)));
    this.wireMockServer.start();
    ProfessorService professorService = new ProfessorService(this.wireMockServer.baseUrl());

    Professor professor = new Professor(id, "Test", null, null, null,
        new ContactInformation(null, null, "test@example.org", null, null, null),
        Collections.emptyList(),
        Collections.emptyList());

    String accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

    assertTrue(professorService.profileExists(professor, accessToken));

    this.wireMockServer.verify(getRequestedFor(urlEqualTo("/professors/" + id))
        .withHeader("Authorization", equalTo("Bearer " + accessToken))
    );
  }

  @Test
  void profileExists_GivenApiResponse404AndProfessor_ShouldReturnFalse() throws Exception {
    UUID id = UUID.randomUUID();
    this.wireMockServer
        .stubFor(get(urlPathEqualTo("/professors/" + id.toString())).willReturn(aResponse().withStatus(404)));
    this.wireMockServer.start();
    ProfessorService professorService = new ProfessorService(this.wireMockServer.baseUrl());

    Professor professor = new Professor(id, "Test", null, null, null,
        new ContactInformation(null, null, "test@example.org", null, null, null),
        Collections.emptyList(),
        Collections.emptyList());

    String accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

    assertFalse(professorService.profileExists(professor, accessToken));

    this.wireMockServer.verify(getRequestedFor(urlEqualTo("/professors/" + id))
        .withHeader("Authorization", equalTo("Bearer " + accessToken))
    );
  }

  @Test
  void profileExists_GivenApiResponse500AndProfessor_ShouldThrowException() throws Exception {
    UUID id = UUID.randomUUID();
    this.wireMockServer
        .stubFor(get(urlPathEqualTo("/professors/" + id.toString())).willReturn(aResponse().withStatus(500)));
    this.wireMockServer.start();
    ProfessorService professorService = new ProfessorService(this.wireMockServer.baseUrl());

    Professor professor = new Professor(id, "Test", null, null, null,
        new ContactInformation(null, null, "test@example.org", null, null, null),
        Collections.emptyList(),
        Collections.emptyList());

    String accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

    assertThrows(Exception.class, () -> professorService.profileExists(professor, accessToken));

    this.wireMockServer.verify(getRequestedFor(urlEqualTo("/professors/" + id))
        .withHeader("Authorization", equalTo("Bearer " + accessToken))
    );
  }

  @Test
  void buildProfessorFromUserModel_GivenUserModel_ShouldBeEqual() throws Exception {
    UserModel userModel = mock(UserModel.class);
    UUID id = UUID.randomUUID();
    when(userModel.getId()).thenReturn(id.toString());
    when(userModel.getFirstName()).thenReturn("Max");
    when(userModel.getLastName()).thenReturn("Mustermann");
    when(userModel.getEmail()).thenReturn("example@example.org");

    ProfessorService professorService = new ProfessorService();

    Professor professor = professorService.buildProfessorFromUserModel(userModel);

    Professor professor1 = new Professor(id, "Max Mustermann", null, null, null,
        new ContactInformation(null, null, "example@example.org", null, null, null),
        Collections.emptyList(),
        Collections.emptyList());

    assertEquals(professor1, professor);
  }

  @Test
  void buildProfessorFromUserModel_GivenUserModelWithEmptyFirstName_ShouldThrowException()
      throws Exception {
    UserModel userModel = mock(UserModel.class);
    UUID id = UUID.randomUUID();
    when(userModel.getId()).thenReturn(id.toString());
    when(userModel.getFirstName()).thenReturn("");
    when(userModel.getLastName()).thenReturn("Mustermann");
    when(userModel.getEmail()).thenReturn("example@example.org");

    ProfessorService professorService = new ProfessorService();

    assertThrows(Exception.class, () -> professorService.buildProfessorFromUserModel(userModel));
  }

  @Test
  void buildProfessorFromUserModel_GivenUserModelWithEmptyLastName_ShouldThrowException()
      throws Exception {
    UserModel userModel = mock(UserModel.class);
    UUID id = UUID.randomUUID();
    when(userModel.getId()).thenReturn(id.toString());
    when(userModel.getFirstName()).thenReturn("Max");
    when(userModel.getLastName()).thenReturn("");
    when(userModel.getEmail()).thenReturn("example@example.org");

    ProfessorService professorService = new ProfessorService();

    assertThrows(Exception.class, () -> professorService.buildProfessorFromUserModel(userModel));
  }

  @Test
  void buildProfessorFromUserModel_GivenUserModelWithInvalidUUID_ShouldThrowException()
      throws Exception {
    UserModel userModel = mock(UserModel.class);
    when(userModel.getId()).thenReturn("abcvagjiejaiöozghoasöl");
    when(userModel.getFirstName()).thenReturn("Max");
    when(userModel.getLastName()).thenReturn("Mustermann");
    when(userModel.getEmail()).thenReturn("example@example.org");

    ProfessorService professorService = new ProfessorService();

    assertThrows(Exception.class, () -> professorService.buildProfessorFromUserModel(userModel));
  }
}