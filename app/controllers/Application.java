package controllers;

import controllers.security.Secure;
import models.Livre;
import play.Play;
import play.mvc.Controller;
import play.mvc.With;
import utils.LoadDevData;

import java.util.List;

@With(Secure.class)
public class Application extends Controller {

    public static void index() {
        if (Secure.getUser() != null) {
            Livres.index(0);
        }
        List<Livre> livres = Livre.all(Livre.class).fetch();
        render(livres);
    }

    public static void initDev() {
        if (Play.mode.equals(Play.Mode.DEV)) {
            LoadDevData.doJob();
        }
        index();
    }

     public static void empty() {
        render();
    }

}