package controllers;


import models.Livre;
import play.data.validation.Required;
import play.mvc.Controller;

import java.util.List;


public class Livres extends Controller {

    public static void index(int page) {
        if(page < 0){
            page = 0;
        }
        int max = Livre.findAll().size();
        int dept = 2;
        int debut = (page * dept);
        int fin = (page * dept) + dept;

        if(max < fin){
            fin =  max;
            debut =  fin-dept;
            page =  debut/dept;
        }

        List<Livre> livres = Livre.find("order by dateAjout desc").from(debut).fetch(fin);
        render(livres, page, dept, max);
    }

    public static void last() {
        List<Livre> livres = Livre.find("order by dateAjout desc").from(0).fetch(3);
        render(livres);
    }

    public static void show(Long id) {
        if (id == null) {
            render();
        }
        Livre livre = Livre.findById(id);
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
