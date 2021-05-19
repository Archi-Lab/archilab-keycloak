package de.innovationhub.prox.proxkeycloakspi.events.admin;

import org.keycloak.events.admin.AdminEvent;

public interface AdminEventListener {
  default void onCreate(AdminEvent event) {

  }

  default void onDelete(AdminEvent event) {

  }

  default void onUpdate(AdminEvent event) {

  }

  default void onAction(AdminEvent event) {

  }
}
