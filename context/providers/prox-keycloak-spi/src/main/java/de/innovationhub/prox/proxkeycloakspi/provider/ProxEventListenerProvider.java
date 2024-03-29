package de.innovationhub.prox.proxkeycloakspi.provider;


import de.innovationhub.prox.proxkeycloakspi.events.EventListener;
import de.innovationhub.prox.proxkeycloakspi.events.VerifyEmailListener;
import de.innovationhub.prox.proxkeycloakspi.events.admin.AdminEventListener;
import de.innovationhub.prox.proxkeycloakspi.utils.KeycloakUtils;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jboss.logging.Logger;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.OperationType;
import org.keycloak.events.admin.ResourceType;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;

public class ProxEventListenerProvider implements EventListenerProvider {

  private final static Logger log = Logger.getLogger(ProxEventListenerProvider.class);
  private final RealmModel realmModel;
  private final KeycloakSession keycloakSession;
  private final static String REALM_ID = "innovation-hub-bergisches-rheinland";

  public ProxEventListenerProvider(KeycloakSession keycloakSession) {
    this.realmModel = keycloakSession.realms().getRealm(REALM_ID);
    this.keycloakSession = keycloakSession;

    if (this.realmModel == null) {
      log.error("Realm ' " + REALM_ID + "' not found");
      throw new IllegalArgumentException("Realm ' " + REALM_ID + "' not found");
    }
  }

  @Override
  public void onEvent(Event event) {
    if (event.getRealmId().equalsIgnoreCase(this.realmModel.getId())) {
      log.debug("Detected event " + event.getType() + " in realm");
      log.debug(KeycloakUtils.eventToString(event));

      EventListener eventListener = null;
      if(event.getType() == EventType.VERIFY_EMAIL) {
        eventListener = new VerifyEmailListener(keycloakSession);
      }
      if (eventListener != null) {
        eventListener.onAction(event);
      } else {
        log.debug("No event listener for event found");
      }
    }
  }

  @Override
  public void close() {

  }

  @Override
  public void onEvent(AdminEvent event, boolean includeRepresentation) {
    // Auto-generated method stub
  }
}
