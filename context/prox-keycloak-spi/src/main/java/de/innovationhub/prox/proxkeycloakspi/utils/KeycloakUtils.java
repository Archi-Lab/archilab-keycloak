package de.innovationhub.prox.proxkeycloakspi.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.jboss.logging.Logger;
import org.keycloak.crypto.AsymmetricSignatureSignerContext;
import org.keycloak.crypto.KeyUse;
import org.keycloak.crypto.KeyWrapper;
import org.keycloak.events.Event;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.AuthDetails;
import org.keycloak.jose.jws.JWSBuilder;
import org.keycloak.models.KeycloakContext;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.AccessToken.Access;
import org.keycloak.services.Urls;

public class KeycloakUtils {

  private static final ObjectMapper objectMapper = new ObjectMapper();
  private static final Logger log = Logger.getLogger(KeycloakUtils.class);

  public String getAccessToken(UserModel userModel, KeycloakSession keycloakSession,
      long lifeTimeInSeconds) {
    KeycloakContext keycloakContext = keycloakSession.getContext();

    AccessToken token = new AccessToken();
    token.subject(userModel.getId());
    token.issuer(Urls.realmIssuer(keycloakContext.getUri().getBaseUri(),
        keycloakContext.getRealm().getName()));
    token.issuedNow();
    token.addAudience("account");
    token.type("Bearer");
    token.setScope("openid email profile");
    token.setRealmAccess(new Access().addRole("professor"));
    token.expiration((int) (token.getIat() + lifeTimeInSeconds));

    KeyWrapper key = keycloakSession.keys()
        .getActiveKey(keycloakContext.getRealm(), KeyUse.SIG, "RS256");

    return new JWSBuilder().kid(key.getKid()).type("JWT").jsonContent(token)
        .sign(new AsymmetricSignatureSignerContext(key));
  }

  public static String eventToString(Event event) {
    try {
      return objectMapper.writeValueAsString(event);
    } catch (JsonProcessingException e) {
      log.warn("Could not serialize event, use fallback", e);
    }

    return String.format(
        "Event (type: %s, realm: %s, client: %s, error: %s, ip: %s, session: %s, time: %d, user: %s, details: %s)",
        event.getType(), event.getRealmId(), event.getClientId(), event.getError(),
        event.getIpAddress(), event.getSessionId(), event.getTime(), event.getUserId(),
        event.getDetails());
  }

  public static String adminEventToString(AdminEvent event) {
    try {
      return objectMapper.writeValueAsString(event);
    } catch (JsonProcessingException e) {
      log.warn("Could not serialize admin event, use fallback method", e);
    }

    return String.format(
        "AdminEvent (type: %s, operation: %s, realm: %s, error: %s, representation: %s, resourcePath: %s, time: %d, authDetails: %s)",
        event.getResourceType(), event.getOperationType(), event.getRealmId(), event.getError(),
        event.getRepresentation(), event.getResourcePath(), event.getTime(),
        authDetailsToString(event.getAuthDetails()));
  }

  public static String authDetailsToString(AuthDetails authDetails) {
    try {
      return objectMapper.writeValueAsString(authDetails);
    } catch (JsonProcessingException e) {
      log.warn("Could not auth details, use fallback method", e);
    }

    return String
        .format("AuthDetails (client: %s, ip: %s, realm: %s, user: %s)", authDetails.getClientId(),
            authDetails.getIpAddress(), authDetails.getRealmId(), authDetails.getUserId());
  }
}
