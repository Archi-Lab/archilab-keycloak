package de.innovationhub.prox.proxkeycloakspi.threads;

import de.innovationhub.prox.proxkeycloakspi.prox.service.ProfessorService;
import de.innovationhub.prox.proxkeycloakspi.prox.service.UnexpectedResponseException;
import de.innovationhub.prox.proxkeycloakspi.utils.KeycloakUtils;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.jboss.logging.Logger;

public class CreateProfessorProfileThread extends Thread {

  private static final BlockingQueue<CreateProfessorMessage> usersToCreateProfessorProfiles = new LinkedBlockingQueue<>();
  private final Logger log = Logger.getLogger(CreateProfessorProfileThread.class);
  private final ProfessorService professorService;
  private final KeycloakUtils keycloakUtils;

  public CreateProfessorProfileThread() {
    this.professorService = new ProfessorService();
    this.keycloakUtils = new KeycloakUtils();
  }

  @Override
  public void run() {
    while (true) {
      try {
        var message = usersToCreateProfessorProfiles.take();
        var userModel = message.getUserModel();
        var keycloakSession = message.getKeycloakSession();
        var professor = professorService.buildProfessorFromUserModel(userModel);
        var accessToken = this.keycloakUtils.getAccessToken(userModel, keycloakSession, 60L);

        try {
          //If no profile exists
          if (!professorService.profileExists(professor, accessToken)) {
            //Create one
            var result = professorService.createProfessor(professor, accessToken);
            if (result) {
              log.info(
                  "Successfully created a professor profile for user with id '" + professor.getId()
                      + "'");

              //TODO: Send E-Mail to user
            } else {
              log.error("Unexpected error while creating professor profile");
              usersToCreateProfessorProfiles.put(message);
            }
          } else {
            log.debug("User with id '" + professor.getId() + "' already has a professor profile");
          }
        } catch (UnexpectedResponseException | IOException e) {
          log.error("Could not create profile", e);
          usersToCreateProfessorProfiles.put(message);
        }

        //Wait 30s to prevent "DDoS" TODO
        Thread.sleep(30000);
      } catch (InterruptedException e) {
        log.error("Error occured during creation of profile", e);
        Thread.currentThread().interrupt();
      } catch (IllegalArgumentException e) {
        log.error("Error building professor model", e);
      }
    }
  }

  public static void putUser(CreateProfessorMessage message) throws InterruptedException {
    usersToCreateProfessorProfiles
        .removeIf(e -> e.getUserModel().getId().equals(message.getUserModel().getId()));
    usersToCreateProfessorProfiles.put(message);
  }
}
