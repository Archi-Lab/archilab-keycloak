package de.innovationhub.prox.proxkeycloakspi.prox.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.innovationhub.prox.proxkeycloakspi.prox.models.professor.ContactInformation;
import de.innovationhub.prox.proxkeycloakspi.prox.models.professor.Professor;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.UUID;
import org.jboss.logging.Logger;
import org.keycloak.models.UserModel;

public class ProfessorService {

  private final Logger logger = Logger.getLogger(ProfessorService.class);
  private final String apiUrl;
  private static final String RESOURCE = "/professors";
  private final ObjectMapper objectMapper = new ObjectMapper();

  public ProfessorService() {
    this.apiUrl = "https://api.dev.prox.innovation-hub.de" + RESOURCE;
  }

  public ProfessorService(String apiUrl) {
    this.apiUrl = apiUrl + RESOURCE;
  }

  public boolean profileExists(Professor professor, String accessToken)
      throws IllegalArgumentException, UnexpectedResponseException, IOException {
    if (professor == null || accessToken == null || accessToken.isBlank()
        || professor.getId() == null) {
      throw new IllegalArgumentException();
    }
    var url = new URL(this.apiUrl + "/" + professor.getId());
    this.logger.info(accessToken);
    var conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("GET");
    conn.setRequestProperty("Authorization", "Bearer " + accessToken);
    conn.setUseCaches(false);

    var code = conn.getResponseCode();

    if (code == 200) {
      return true;
    } else if (code == 404) {
      return false;
    } else {
      throw new UnexpectedResponseException("Unexpected Response code " + code);
    }
  }

  public boolean createProfessor(Professor professor, String accessToken)
      throws IOException, IllegalArgumentException, UnexpectedResponseException {

    if (professor == null || accessToken == null || accessToken.isBlank()
        || professor.getId() == null || professor.getName() == null || professor.getName()
        .isBlank()) {
      throw new IllegalArgumentException();
    }

    var postData = this.objectMapper.writeValueAsBytes(professor);
    var postDataLength = postData.length;
    var url = new URL(this.apiUrl);
    var conn = (HttpURLConnection) url.openConnection();
    conn.setDoOutput(true);
    conn.setInstanceFollowRedirects(false);
    conn.setRequestMethod("POST");
    conn.setRequestProperty("Content-Type", "application/json");
    conn.setRequestProperty("charset", "utf-8");
    conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
    conn.setRequestProperty("Authorization", "Bearer " + accessToken);
    conn.setUseCaches(false);
    try (var wr = new DataOutputStream(conn.getOutputStream())) {
      wr.write(postData);
    }

    var code = conn.getResponseCode();

    if (code == 201) {
      try (var is = conn.getInputStream()) {
        var out = inputStreamToString(is);
        this.logger.info("Successfully created professor resource :" + out);
        return true;
      } catch (IOException e) {
        this.logger.error("Unable to read response", e);
        throw e;
      }
    } else {
      try (var is = conn.getInputStream()) {
        var out = inputStreamToString(is);
        this.logger.error("Unexpected HTTP Status Code (" + code + ") with response: " + out);
        throw new UnexpectedResponseException("Unexpected HTTP Status code");
      } catch (IOException e) {
        this.logger.error("Unable to read response", e);
        throw e;
      }
    }
  }

  public Professor buildProfessorFromUserModel(UserModel userModel)
      throws IllegalArgumentException {
    var uuid = UUID.fromString(userModel.getId());
    if (userModel.getFirstName() == null || userModel.getFirstName().trim().isEmpty()) {
      throw new IllegalArgumentException("First name cannot be null or empty");
    }
    if (userModel.getLastName() == null || userModel.getLastName().trim().isEmpty()) {
      throw new IllegalArgumentException("Last name cannot be null or empty");
    }
    var name = userModel.getFirstName() + " " + userModel.getLastName();
    var email = userModel.getEmail();
    return new Professor(uuid, name, null, null, null,
        new ContactInformation(null, null, email, null, null, null),
        Collections.emptyList(),
        Collections.emptyList());
  }

  private String inputStreamToString(InputStream is) throws IOException {
    var bufferSize = 1024;
    var buffer = new char[bufferSize];
    var out = new StringBuilder();
    var in = new InputStreamReader(is, StandardCharsets.UTF_8);
    for (int numRead; (numRead = in.read(buffer, 0, buffer.length)) > 0; ) {
      out.append(buffer, 0, numRead);
    }
    return out.toString();
  }
}
