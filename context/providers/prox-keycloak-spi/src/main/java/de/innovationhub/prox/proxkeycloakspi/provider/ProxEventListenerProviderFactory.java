package de.innovationhub.prox.proxkeycloakspi.provider;

import org.jboss.logging.Logger;
import org.keycloak.Config.Scope;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class ProxEventListenerProviderFactory implements EventListenerProviderFactory {

  private final static Logger log = Logger.getLogger(ProxEventListenerProviderFactory.class);

  @Override
  public EventListenerProvider create(KeycloakSession keycloakSession) {
    return new ProxEventListenerProvider(keycloakSession);
  }

  @Override
  public void init(Scope scope) {

  }

  @Override
  public void postInit(KeycloakSessionFactory keycloakSessionFactory) {

  }

  @Override
  public void close() {
  }

  @Override
  public String getId() {
    return "prox-event-listener";
  }
}
