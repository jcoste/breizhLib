package controllers;

import models.Commentaire;
import play.mvc.Controller;

import java.util.List;


public class Commentaires extends Controller {

    private static int NB_NEWS_PAR_PAGE = 4;

     public static void last() {
        List<Commentaire> commentaires = Commentaire.all(Commentaire.class).order("dateAjout").fetch(NB_NEWS_PAR_PAGE);
        render(commentaires);
    }
}
