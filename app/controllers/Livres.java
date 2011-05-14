package controllers;


import models.Livre;
import play.mvc.Controller;

import java.util.List;


public class Livres extends Controller {

     public static void index() {
        List<Livre> livres = Livre.findAll();
        render(livres);
     }

     public static void last() {
        List<Livre> livres =  Livre.find("order by dateAjout desc").from(0).fetch(3);
        render(livres);
     }

    public static void show(Long id) {
        if(id == null) {
            render();
        }
        Livre livre =  Livre.findById(id);
        render(livre);
     }

    public static void search() {
         render();
     }

    public static void add() {

         render();
     }

    public static void save(String titre,String editeur,String image,String description,String iSBN) {
        Livre livre = new Livre(titre,editeur,image,description,iSBN);
        livre.create();
        show(livre.getId());
     }
}
