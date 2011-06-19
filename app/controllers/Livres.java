package controllers;


import controllers.security.Role;
import controllers.security.Secure;
import models.Commentaire;
import models.Livre;
import models.User;
import play.data.validation.Required;
import play.mvc.Controller;
import play.mvc.With;
import siena.Query;
import utils.Paginator;

import java.util.List;

@With(Secure.class)
public class Livres extends Controller {

    private static int NB_PAR_PAGE = 12;

    private static int NB_NEWS_PAR_PAGE = 4;


    @Role("public")
    public static void index(int page) {
        Query<Livre> query = Livre.all(Livre.class).order("-dateAjout");
        Paginator<Livre> paginator = new Paginator<Livre>(NB_PAR_PAGE, page, "Livres.index", query);

        renderArgs.put("editeurs", Editeurs.initListEditeurs());
        render(paginator);
    }

    @Role("public")
    public static void all() {
        Query<Livre> query = Livre.all(Livre.class);
        List<Livre> livres = query.order("-dateAjout").fetch();
        render(livres);
    }

    @Role("public")
    public static void allXml() {
        Query<Livre> query = Livre.all(Livre.class);
        List<Livre> livres = query.order("-dateAjout").fetch();
        render(livres);
    }

    @Role("public")
    public static void allJson(){
        Query<Livre> query = Livre.all(Livre.class);
        List<Livre> livres = query.order("-dateAjout").fetch();

        render(livres);
    }


    @Role("public")
    public static void editeur(String editeur, int page) {
        Query<Livre> livres = Livre.all(Livre.class).filter("editeur", editeur).order("-dateAjout");
        Paginator<Livre> paginator = new Paginator<Livre>(NB_PAR_PAGE, page, "Livres.editeur", livres);

        renderArgs.put("editeurs", Editeurs.initListEditeurs());
        render(editeur, paginator);
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
        renderArgs.put("editeurs", Editeurs.initListEditeurs());
        render(livre);
    }

    @Role("public")
    public static void search() {
        render();
    }

    @Role("admin")
    public static void add() {
        renderArgs.put("editeurs", Editeurs.initListEditeurs());
        render();
    }

    @Role("admin")
    public static void save(@Required String titre, @Required String editeur, byte[] imageFile, String description, @Required String iSBN) throws Exception {
        if (validation.hasErrors()) {
            render("Livres/add.html");
        }

        if (Livre.all(Livre.class).filter("iSBN", iSBN).get() != null) {
            error("le livre existe déja en base");
        }

        String image = null;
        if (imageFile != null) {
            image = Pictures.createImage(imageFile, "ouvrages/", iSBN, true);
        }

        Livre livre = new Livre(titre, editeur, image, iSBN);
        livre.insert();
        show(livre.iSBN);
    }

    @Role("admin")
    public static void update(@Required String titre, @Required String editeur, byte[] imageFile, @Required String iSBN) throws Exception {
         Livre livre = Livre.all(Livre.class).filter("iSBN", iSBN).get();
        if (validation.hasErrors()) {
            renderArgs.put("livre", livre);
            renderArgs.put("editeurs", Editeurs.initListEditeurs());
            render("Livres/edit.html");
        }

        if (livre == null) {
            error("le livre n'existe pas en base");
        }

        String image = null;
        if (imageFile != null) {
            image = Pictures.createImage(imageFile, "ouvrages/",iSBN, true);
            livre.image = image;
        }
        livre.editeur = editeur;
        livre.titre = titre;

        livre.update();
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
