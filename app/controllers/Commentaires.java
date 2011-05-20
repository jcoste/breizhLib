package controllers;

import controllers.security.Secure;
import models.Commentaire;
import play.mvc.Controller;
import play.mvc.With;

import java.util.List;


@With(Secure.class)
public class Commentaires extends Controller {

    private static int NB_PAR_PAGE = 6;

    private static int NB_NEWS_PAR_PAGE = 4;


    public static void last() {
        List<Commentaire> commentaires = Commentaire.all(Commentaire.class).order("-dateAjout").fetch(NB_NEWS_PAR_PAGE);
        for (Commentaire commentaire : commentaires) {
            commentaire.livre.get();
        }

        render(commentaires);
    }

    public static void index(int page) {

        if (page < 0) {
            page = 0;
        }
        int max = Commentaire.findAll().size();
        int dept = NB_PAR_PAGE;
        int debut = (page * dept);
        if (debut >= max) {
            debut = max - (max - dept) / dept;
            page = debut / dept;
        }
        List<Commentaire> commentaires = Commentaire.all(Commentaire.class).order("-dateAjout").fetch(dept, debut);

        for (Commentaire commentaire : commentaires) {
            commentaire.livre.get();
        }
        render(commentaires, page, dept, max);
    }
}
