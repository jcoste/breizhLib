package controllers;

import controllers.security.Secure;
import models.Livre;
import models.User;
import play.modules.gae.GAE;
import play.mvc.Controller;
import play.mvc.With;

import java.util.List;

@With(Secure.class)
public class Application extends Controller {

    public static void index() {
        if (GAE.isLoggedIn()) {
            Livres.index(0);
        }
        List<Livre> livres = Livre.all(Livre.class).fetch();
        render(livres);
    }

     public static void infos() {
        User user = Secure.getUser();
        if (user != null) {
            render(user);
        }
        index();
    }

}