package de.innovationhub.prox.proxkeycloakspi.events;

import de.innovationhub.prox.proxkeycloakspi.threads.CreateProfessorMessage;
import de.innovationhub.prox.proxkeycloakspi.threads.CreateProfessorProfileThread;
import java.util.UUID;
import org.jboss.logging.Logger;
import org.keycloak.events.Event;
import org.keycloak.models.KeycloakSession;

public class VerifyEmailListener implements EventListener {

  private final KeycloakSession keycloakSession;
  private final Logger log = Logger.getLogger(VerifyEmailListener.class);

  public VerifyEmailListener(KeycloakSession keycloakSession) {
    this.keycloakSession = keycloakSession;
  }

  @Override
  public void onAction(Event event) {
    var realm = keycloakSession.realms().getRealm(event.getRealmId());
    var userId = UUID.fromString(event.getUserId());
    var email = event.getDetails().get("email");

    if(email != null && !email.isBlank()) {
      if(email.trim().endsWith("@th-koeln.de") || email.trim().endsWith("@fh-koeln.de")) {
        log.info("User " + userId + " verified college email account, assigning professor group");
        var user = keycloakSession.users().getUserById(userId.toString(), realm);
        var professorGroup = realm.getGroups().stream().filter(g -> g.getName().equalsIgnoreCase("professor")).findFirst();

        if(user != null && professorGroup.isPresent()) {
          user.joinGroup(professorGroup.get());
          var message = new CreateProfessorMessage(user, keycloakSession);
          try {
            CreateProfessorProfileThread.putUser(message);
          } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
          }
        } else {
          if(user == null) {
            log.error("Could not assign group to user, user is null");
          }
          if(professorGroup.isEmpty()) {
            log.error("Could not assign group to user, group is null");
          }
        }
      }
    }
  }
}
