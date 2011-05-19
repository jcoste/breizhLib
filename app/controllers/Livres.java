package controllers;


import models.Commentaire;
import models.Livre;
import play.data.validation.Required;
import play.mvc.Controller;

import java.util.List;


public class Livres extends Controller {

    private static int NB_PAR_PAGE = 2;

    private static int NB_NEWS_PAR_PAGE = 4;

    public static void index(int page) {
        if (page < 0) {
            page = 0;
        }
        int max = Livre.findAll().size();
        int dept = NB_PAR_PAGE;
        int debut = (page * dept);
        if (debut >= max) {
            debut = max - (max - dept) / dept;
            page = debut / dept;
        }
        List<Livre> livres = Livre.all(Livre.class).order("dateAjout").fetch(dept, debut);
        render(livres, page, dept, max);
    }

    public static void last() {
        List<Livre> livres = Livre.all(Livre.class).order("dateAjout").fetch(NB_NEWS_PAR_PAGE);
        render(livres);
    }

    public static void show(String id) {
        if (id == null) {
            render();
        }
        Livre livre = Livre.findByISBN(id);
        render(livre);
    }

    public static void search() {
        render();
    }

    public static void add() {
        render();
    }

    public static void save(@Required String titre, @Required String editeur, String image, String description, @Required String iSBN) {
        if (validation.hasErrors()) {
            render("Livres/add.html");
        }

        if (Livre.all(Livre.class).filter("iSBN", iSBN).get() != null) {
            error("le livre existe déja en base");
        }

        Livre livre = new Livre(titre, editeur, image, description, iSBN);
        livre.insert();
        show(livre.iSBN);
    }

    public static void postComment(String bookId, @Required String nom, @Required String content) {
        Livre livre = Livre.findByISBN(bookId);
        if (validation.hasErrors()) {
            render("Livres/show.html", livre);
        }
        Commentaire commentaire = new Commentaire(livre, nom, content);
        commentaire.insert();
        show(bookId);
    }
}
