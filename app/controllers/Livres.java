package controllers;


import models.Livre;
import play.Logger;
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
            debut = max - (max-dept)/dept;
            page = debut / dept;
        }
        List<Livre> livres = Livre.find("order by dateAjout desc").from(debut).fetch(dept);
        render(livres, page, dept, max);
    }

    public static void last() {
        List<Livre> livres = Livre.find("order by dateAjout desc").from(0).fetch(NB_NEWS_PAR_PAGE);
        render(livres);
    }

    public static void show(Long id) {
        if (id == null) {
            render();
        }
        Livre livre = Livre.findById(id);
        render(livre);
    }

    public static void reserver(Long id,int page,String redirect) {
        if (id == null) {
            render();
        }
        Livre livre = Livre.findById(id);
        //TODO gestion des réservations


        livre.etat = livre.etat.getNextState();
        livre.save();
        if(redirect.equals("last")){
           last();
        }else if(redirect.equals("show")){
           show(id);
        }
        index(page);
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
        Livre livre = new Livre(titre, editeur, image, description, iSBN);
        livre.create();
        show(livre.getId());
    }

    public static void postComment(Long bookId, @Required String nom, @Required String content) {
        Livre livre = Livre.findById(bookId);
        if (validation.hasErrors()) {
            render("Livres/show.html", livre);
        }
        livre.addComment(nom, content);
        show(bookId);
    }
}
