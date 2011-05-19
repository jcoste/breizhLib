package controllers;


import models.Commentaire;
import models.Livre;
import play.data.validation.Required;
import play.mvc.Controller;
import siena.Query;

import java.util.ArrayList;
import java.util.List;


public class Livres extends Controller {

    private static int NB_PAR_PAGE = 2;

    private static int NB_NEWS_PAR_PAGE = 4;

    public static List<String> editeurs = new ArrayList<String>();

    static{
        editeurs.add("Pearson");
        editeurs.add("Eyrolles");
        editeurs.add("O'Reilly");
        editeurs.add("APress");
        editeurs.add("Packt");
    }

    public static void index(int page) {
        Query<Livre> query = Livre.all(Livre.class);
        int  max = Livre.findAll().size();
        int debut = pagination(page,max,NB_PAR_PAGE);
        List<Livre> livres = query.order("dateAjout").fetch(NB_PAR_PAGE, debut);

        renderArgs.put("editeurs",editeurs);
        renderArgs.put("dept",NB_PAR_PAGE);
        render(livres, page, max);
    }

    private static int pagination(int page, int max,int nbParPage){
       if (page < 0) {
            page = 0;
        }

        int dept = NB_PAR_PAGE;
        int debut = (page * dept);
        if (debut >= max) {
            debut = max - (max - dept) / dept;
            page = debut / dept;
        }
        return debut;
    }

    public static void editeur(String editeur,int page) {
        int max =  Livre.all(Livre.class).filter("editeur",editeur).count();
        int dept = NB_PAR_PAGE;
        int debut = pagination(page,max,NB_PAR_PAGE);

        List<Livre> livres = Livre.all(Livre.class).filter("editeur",editeur).order("dateAjout").fetch(dept, debut);

        renderArgs.put("editeurs",editeurs);
        render(livres, page, dept, max,editeur);
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
        renderArgs.put("editeurs",editeurs);
        render();
    }

    public static void save(@Required String titre, @Required String editeur, String image, String description, @Required String iSBN) {
        if (validation.hasErrors()) {
            render("Livres/add.html");
        }

        if (Livre.all(Livre.class).filter("iSBN", iSBN).get() != null) {
            error("le livre existe déja en base");
        }

        Livre livre = new Livre(titre, editeur, image, iSBN);
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
