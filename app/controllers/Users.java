package controllers;


import controllers.security.Role;
import controllers.security.Secure;
import models.Commentaire;
import models.User;
import notifiers.Mails;
import play.cache.Cache;
import play.data.validation.Required;
import play.i18n.Messages;
import play.libs.Codec;
import play.libs.Crypto;
import play.mvc.Controller;
import play.mvc.With;

import java.io.UnsupportedEncodingException;
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
    public static void postEdit(@Required String nom, @Required String prenom,String email) throws UnsupportedEncodingException {
        User user = Secure.getUser();
        if (user != null) {
            user.nom = nom;
            user.prenom = prenom;
            user.update();
            if(email != null && user.email == null){
                User anotherUser = User.find(email);
                if(anotherUser != null){
                     error("email déjà utilisé par un autre compte");
                    //TODO fusion de comptes
                }
                String validationID = Codec.UUID();
                Cache.set(validationID, email, "10mn");
                Mails.validationEmail(user, validationID);
                edit();
            }
        }
        infos();
    }

    @Role("member")
    public static void modifPwd(@Required String oldwpd, @Required String pwd, @Required String pwdconfirm) {
        User user = Secure.getUser();
        validation.equals(user.password, Crypto.passwordHash(oldwpd)).message(Messages.get("error", "mot de passe incorrect"));
        validation.equals(pwd, pwdconfirm).message(Messages.get("error","mot de passe incorrect"));
        if (validation.hasErrors()) {
            render("Users/infos.html");
        }
        if (user != null) {
            user.password = Crypto.passwordHash(pwd);
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


     public static void validateEmail(@Required String id){
        User user = Secure.getUser();
        if(user != null){
           String email = (String) Cache.get(id);
           if(email != null){
               user.email = email;
               user.update();
           }else{
               error("le lien a expiré");
           }
        }


    }
}
