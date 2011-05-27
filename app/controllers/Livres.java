package controllers;


import controllers.security.Role;
import controllers.security.Secure;
import models.Commentaire;
import models.Livre;
import models.Picture;
import models.User;
import play.data.validation.Required;
import play.mvc.Controller;
import play.mvc.With;
import siena.Query;

import java.io.File;
import java.util.List;

@With(Secure.class)
public class Livres extends Controller {

    private static int NB_PAR_PAGE = 6;

    private static int NB_NEWS_PAR_PAGE = 4;


    @Role("public")
    public static void index(int page) {
        Query<Livre> query = Livre.all(Livre.class);
        int max = Livre.findAll().size();
        int debut = Utils.pagination(page, max, NB_PAR_PAGE);
        List<Livre> livres = query.order("-dateAjout").fetch(NB_PAR_PAGE, debut);

        renderArgs.put("editeurs", Utils.initListEditeurs());
        renderArgs.put("dept", NB_PAR_PAGE);
        render(livres, page, max);
    }

    @Role("public")
    public static void all() {
        Query<Livre> query = Livre.all(Livre.class);
        List<Livre> livres = query.order("-dateAjout").fetch();
        render(livres);
    }


    @Role("public")
    public static void editeur(String editeur, int page) {
        int max = Livre.all(Livre.class).filter("editeur", editeur).count();
        int dept = NB_PAR_PAGE;
        int debut = Utils.pagination(page, max, NB_PAR_PAGE);

        List<Livre> livres = Livre.all(Livre.class).filter("editeur", editeur).order("-dateAjout").fetch(dept, debut);

        renderArgs.put("editeurs", Utils.initListEditeurs());
        render(livres, page, dept, max, editeur);
    }


    @Role("public")
    public static void last() {
        List<Livre> livres = Livre.all(Livre.class).order("-dateAjout").fetch(NB_NEWS_PAR_PAGE);
        render(livres);
    }

    @Role("public")
    public static void show(String id) {
        if (id == null) {
            render();
        }
        Livre livre = Livre.findByISBN(id);
        User user = Secure.getUser();
        render(livre, user);
    }


    @Role("admin")
    public static void edit(Long id) {
        if (id == null) {
            render();
        }
        Livre livre = Livre.findById(id);
        render(livre);
    }

    @Role("public")
    public static void search() {
        render();
    }

    @Role("admin")
    public static void add() {
        renderArgs.put("editeurs", Utils.initListEditeurs());
        render();
    }

    @Role("admin")
    public static void save(@Required String titre, @Required String editeur, byte[] imageFile, String image, String description, @Required String iSBN) throws Exception {
        if (validation.hasErrors()) {
            render("Livres/add.html");
        }

        if (Livre.all(Livre.class).filter("iSBN", iSBN).get() != null) {
            error("le livre existe déja en base");
        }

        if (imageFile != null) {
            image = Utils.createImage(imageFile, iSBN, true);
        }

        Livre livre = new Livre(titre, editeur, image, iSBN);
        livre.insert();
        show(livre.iSBN);
    }


    @Role("member")
    public static void postComment(String bookId, @Required String nom, @Required String content, @Required int note) {
        Livre livre = Livre.findByISBN(bookId);
        if (validation.hasErrors()) {
            render("Livres/show.html", livre);
        }


        User user = Secure.getUser();
        Commentaire commentaire = new Commentaire(livre, user, nom, content, note);
        commentaire.insert();
        show(bookId);
    }
}
