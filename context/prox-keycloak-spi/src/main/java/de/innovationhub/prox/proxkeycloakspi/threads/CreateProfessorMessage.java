package de.innovationhub.prox.proxkeycloakspi.threads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;

@Getter
@AllArgsConstructor
public class CreateProfessorMessage {
  private final UserModel userModel;
  private final KeycloakSession keycloakSession;
}
