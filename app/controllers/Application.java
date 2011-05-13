package controllers;

import models.Livre;
import play.mvc.Controller;

import java.util.List;


public class Application extends Controller {

    public static void index() {
        List<Livre> livres = Livre.findAll();
        render(livres);
    }

}