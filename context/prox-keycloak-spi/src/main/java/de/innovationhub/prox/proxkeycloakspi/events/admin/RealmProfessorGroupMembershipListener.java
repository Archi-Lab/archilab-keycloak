package de.innovationhub.prox.proxkeycloakspi.events.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.innovationhub.prox.proxkeycloakspi.threads.CreateProfessorMessage;
import de.innovationhub.prox.proxkeycloakspi.threads.CreateProfessorProfileThread;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.jboss.logging.Logger;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;

public class RealmProfessorGroupMembershipListener implements AdminEventListener {

  private final KeycloakSession keycloakSession;
  private final Logger log = Logger.getLogger(RealmProfessorGroupMembershipListener.class);
  private final RealmModel realmModel;
  private final ObjectMapper objectMapper = new ObjectMapper();

  public RealmProfessorGroupMembershipListener(KeycloakSession keycloakSession, RealmModel realmModel) {
    this.keycloakSession = keycloakSession;
    this.realmModel = realmModel;
  }

  @Override
  public void onCreate(AdminEvent event) {
    try {
      var groupId = getGroupId(event.getRepresentation());
      GroupModel affectedGroup = realmModel.getGroupById(groupId);
      var professorRoleModel = realmModel.getRoles().stream().filter(r -> r.getName().equalsIgnoreCase("professor")).findFirst();
      if(affectedGroup != null && professorRoleModel.isPresent() && affectedGroup.hasRole(professorRoleModel.get())) {
        var userId = getUserIdFromResourcePath(event.getResourcePath());
        var userModel = keycloakSession.users().getUserById(userId, realmModel);

        if(userModel != null) {
          //Put UserProfile to queue
          var message = new CreateProfessorMessage(userModel, keycloakSession);
          CreateProfessorProfileThread.putUser(message);
        } else {
          log.error("Could not find user with id '" + userId + "' in realm " + realmModel.getId());
        }
      }
    } catch (Exception e) {
      log.error("Could not process event", e);
    }
  }
  private String getUserIdFromResourcePath(String resourcePath) {
    return resourcePath.split("/")[1];
  }

  private String getGroupId(String representation) throws Exception {
    if (representation != null) {
      JsonReader jsonReader = Json.createReader(new StringReader(representation));
      JsonObject object = jsonReader.readObject();
      return object.getString("id");
    }
    throw new Exception("representation is null");
  }
}
