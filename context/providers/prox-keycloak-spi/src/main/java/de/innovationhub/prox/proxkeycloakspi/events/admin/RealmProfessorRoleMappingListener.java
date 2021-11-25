package de.innovationhub.prox.proxkeycloakspi.events.admin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.innovationhub.prox.proxkeycloakspi.threads.CreateProfessorMessage;
import de.innovationhub.prox.proxkeycloakspi.threads.CreateProfessorProfileThread;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.jboss.logging.Logger;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;

public class RealmProfessorRoleMappingListener implements AdminEventListener {

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final RealmModel realmModel;
  private final KeycloakSession keycloakSession;
  private final Logger log = Logger.getLogger(RealmProfessorRoleMappingListener.class);

  public RealmProfessorRoleMappingListener(KeycloakSession keycloakSession, RealmModel realmModel) {
    this.keycloakSession = keycloakSession;
    this.realmModel = realmModel;
  }

  @Override
  public void onCreate(AdminEvent event) {
    try {
      //Get roles that were affected by this event
      var affectedRoles = getRolesByIds(realmModel,
          getRoleIds(event.getRepresentation()));


      var professorRoleModel = realmModel.getRoles().stream().filter(r -> r.getName().equalsIgnoreCase("professor")).findFirst();

      if(professorRoleModel.isEmpty()) {
        log.error("Could not find professor role in realm " + realmModel.getId());
      } else {
        //If the professor role was created
        if (affectedRoles.stream().anyMatch(r -> affectsRole(r, professorRoleModel.get()))) {
          var userId = getUserIdFromResourcePath(event.getResourcePath());
          var userModel = keycloakSession.users().getUserById(userId, realmModel);

          if (userModel != null) {
            //Put UserProfile to queue
            var message = new CreateProfessorMessage(userModel, keycloakSession);
            CreateProfessorProfileThread.putUser(message);
          } else {
            log.error(
                "Could not find user with id '" + userId + "' in realm " + realmModel.getId());
          }
        }
      }
    } catch (Exception e) {
      log.error("Could not process event", e);
    }
  }

  private String getUserIdFromResourcePath(String resourcePath) {
    return resourcePath.split("/")[1];
  }

  private List<String> getRoleIds(String representation) throws Exception {
    if (representation != null) {
      var roles = objectMapper
          .readValue(representation, new TypeReference<List<Map<String, String>>>() {
          });
      return roles.stream().map(r -> r.get("id")).collect(Collectors.toList());
    }
    throw new Exception("representation is null");
  }

  private List<RoleModel> getRolesByIds(RealmModel realmModel, Iterable<String> ids) {
    return StreamSupport.stream(ids.spliterator(), false)
        .map(id -> realmModel.getRoleById(id)).collect(
            Collectors.toList());
  }

  private boolean affectsRole(RoleModel roleModel, RoleModel roleModel1) {
    if(roleModel.equals(roleModel1)) {
      return true;
    }
    return roleModel.getComposites().stream().anyMatch(r -> r.equals(roleModel1));
  }
}
