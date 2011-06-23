package notifiers;


import models.User;
import play.mvc.Mailer;

import java.io.UnsupportedEncodingException;

public class Mails extends Mailer {


    public static void validationInscription(User user) throws UnsupportedEncodingException {

      setFrom("team@breizhjug.org");
      setSubject("Validation de votre inscription au breizhlIB");
      addRecipient(user.email);
      send(user);
   }


   public static void lostPassword(User user,String randomID) throws UnsupportedEncodingException {

      setFrom("team@breizhjug.org");
      setSubject("Créez un nouveau mot de passe");
      addRecipient(user.email);
      send(user, randomID);
   }



}
