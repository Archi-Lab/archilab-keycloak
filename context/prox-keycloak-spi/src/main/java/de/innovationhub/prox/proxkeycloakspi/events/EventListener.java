package de.innovationhub.prox.proxkeycloakspi.events;

import org.keycloak.events.Event;
import org.keycloak.events.admin.AdminEvent;

public interface EventListener {
  default void onAction(Event event) {

  }
}
