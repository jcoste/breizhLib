package controllers;


import controllers.security.Role;
import controllers.security.Secure;
import models.Commentaire;
import models.User;
import play.data.validation.Required;
import play.libs.Crypto;
import play.mvc.Controller;
import play.mvc.With;

import java.util.List;

@With(Secure.class)
public class Users extends Controller {

    @Role("member")
    public static void infos() {
        User user = Secure.getUser();
        if (user != null) {
            render(user);
        }
        Application.index();
    }

    @Role("member")
    public static void commentaires() {
        User user = Secure.getUser();
        List<Commentaire> commentaires = user.commentaires();
        for (Commentaire commentaire : commentaires) {
            commentaire.livre.get();
        }
        render(commentaires);
    }

    @Role("member")
    public static void edit() {
        User user = Secure.getUser();
        if (user != null) {
            render(user);
        }
        infos();
    }

    @Role("member")
    public static void postEdit(@Required String nom, @Required String prenom) {
        User user = Secure.getUser();
        if (user != null) {
            user.nom = nom;
            user.prenom = prenom;
            user.update();
        }
        infos();
    }

    @Role("member")
    public static void modifPwd(@Required String oldwpd, @Required String pwd, @Required String pwdconfirm) {
        User user = Secure.getUser();
        validation.equals(user.password, Crypto.passwordHash(oldwpd)).message("<span class=\"error\">mot de passe incorrect</span>");
        validation.equals(pwd, pwdconfirm).message("<span class=\"error\">mot de passe incorrect</span>");
        if (validation.hasErrors()) {
            render("Users/infos.html");
        }
        if (user != null) {
            user.password = pwd;
            user.update();
        }
        infos();
    }

    @Role("admin")
    public static void index() {
        List<User> users = User.findAll();
        render(users);
    }

    @Role("member")
    public static void emprunts() {
        render();
    }
}
