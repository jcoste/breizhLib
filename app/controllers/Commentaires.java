package controllers;

import models.Commentaire;
import play.mvc.Controller;

import java.util.List;


public class Commentaires extends Controller {

     public static void last() {
        List<Commentaire> commentaires = Commentaire.find("order by dateAjout desc").from(0).fetch(3);
        render(commentaires);
    }
}
