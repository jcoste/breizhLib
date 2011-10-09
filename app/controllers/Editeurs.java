package controllers;

import controllers.security.Secure;
import models.Editeur;
import models.Picture;
import models.socialoauth.Role;
import play.data.validation.Required;
import play.i18n.Messages;
import play.modules.router.Get;
import play.modules.router.Post;
import play.mvc.Controller;
import play.mvc.With;

import java.util.ArrayList;
import java.util.List;


@With({Secure.class,Widgets.class})
public class Editeurs extends Controller {

    @Role("admin")
    @Get("/editeur/add")
    public static void add() {
        render();
    }

    public static List<String> initListEditeurs() {
        List<Editeur> allEditeurs = Editeur.all(Editeur.class).order("nom").fetch();
        List<String> editeurs = new ArrayList<String>();
        for (Editeur pEditeur : allEditeurs) {
            editeurs.add(pEditeur.nom);
        }
        return editeurs;
    }

    @Role("admin")
    @Post("/editeur/add")
    public static void postEditeur(@Required String nom, String site, byte[] imageFile) {
        if (validation.hasErrors()) {
            render("Editeurs/add.html");
        }
        Editeur editeur = Editeur.findByNom(nom);
        if (editeur != null) {
            error(Messages.get("editeur_already_exist"));
        }

        String image = null;
        if (imageFile != null) {
            try {
                Picture picture = Pictures.createImage(imageFile, "editeurs/", nom, false);
                image = picture.getUrl();
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
    @Get("/editeurs")
    public static void index() {
        List<Editeur> editeurs = Editeur.findAll();
        render(editeurs);
    }

    @Role("public")
    @Get(value = "/editeurs.json", format = "json")
    public static void allJson() {
        List<Editeur> editeurs = Editeur.findAll();
        renderJSON(editeurs);
    }
}
