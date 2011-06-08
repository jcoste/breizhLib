package controllers;

import controllers.security.Role;
import controllers.security.Secure;
import models.Editeur;
import play.data.validation.Required;
import play.mvc.Controller;
import play.mvc.With;

import java.util.List;


@With(Secure.class)
public class Editeurs extends Controller {

    @Role("admin")
    public static void add() {
        render();
    }

    @Role("admin")
    public static void postEditeur(@Required String nom, String site, byte[] imageFile, String image) {
        if (validation.hasErrors()) {
            render("Editeurs/add.html");
        }
        Editeur editeur = Editeur.findByNom(nom);
        if (editeur != null) {
            error("l'éditeur existe déja en base");
        }

        if (imageFile != null) {
            try {
                image = Utils.createImage(imageFile, nom, false);
            } catch (Exception e) {
                error(e.getLocalizedMessage());
            }
        }


        editeur = new Editeur(nom, site);
        editeur.image = image;
        editeur.insert();
        Livres.add();
    }

    @Role("public")
    public static void index() {
        List<Editeur> editeurs = Editeur.findAll();
        render(editeurs);
    }
}
