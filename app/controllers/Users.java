package controllers;


import controllers.security.Role;
import controllers.security.Secure;
import models.Commentaire;
import models.Email;
import models.User;
import notifiers.Mails;
import play.Logger;
import play.Play;
import play.cache.Cache;
import play.data.validation.Equals;
import play.data.validation.Required;
import play.i18n.Messages;
import play.libs.Codec;
import play.libs.Crypto;
import play.modules.router.Get;
import play.modules.router.Post;
import play.mvc.Controller;
import play.mvc.With;

import java.io.UnsupportedEncodingException;
import java.util.List;

@With(Secure.class)
public class Users extends Controller {

    @Role("member")
    @Get("/user/infos")
    public static void infos() {
        User user = (User) Secure.getUser();
        if (user != null) {
            render(user);
        }
        Application.index();
    }

    @Role("member")
    @Get("/user/commentaires")
    public static void commentaires() {
        User user = (User) Secure.getUser();
        List<Commentaire> commentaires = user.commentaires();
        for (Commentaire commentaire : commentaires) {
            commentaire.livre.get();
        }
        render(commentaires);
    }

    @Role("member")
    @Get("/user/edit")
    public static void edit() {
        User user = (User) Secure.getUser();
        if (user != null) {
            List<Email> emails = Email.findByUser(user);
            render(user, emails);
        }
        infos();
    }

    @Role("member")
    @Post("/user/edit")
    public static void postEdit(@Required String nom, @Required String prenom, String email) throws UnsupportedEncodingException {
        User user = (User) Secure.getUser();
        if (user != null) {
            user.nom = nom;
            user.prenom = prenom;
            user.update();

            boolean hasnext = true;
            int i = 1;
            Logger.info("email+ do");
            do {
                String valeurEmail = request.params.get("email" + i);
                Logger.info("email + " + i + " = " + valeurEmail);
                if (valeurEmail == null) {
                    hasnext = false;
                } else if (!valeurEmail.equals(email)) {
                    Email userEmail = Email.find(valeurEmail);
                    if (userEmail == null) {
                        userEmail = new Email(valeurEmail);
                        userEmail.user = user;
                        userEmail.insert();
                        validationEmail(userEmail, user);
                    } else {
                        error("email déjà utilisé");
                    }
                }
                i++;
            } while (hasnext);


            if (email != null && user.email == null) {
                User anotherUser = User.find(email);
                if (anotherUser != null) {
                    error("email déjà utilisé par un autre compte");
                    //TODO fusion de comptes
                }
                boolean validationEmail = Boolean.parseBoolean(Play.configuration.getProperty("authbasic.email.validation"));
                if (validationEmail) {
                    String validationID = Codec.UUID();
                    Cache.set(validationID, email, "10mn");
                    Mails.validationEmail(user, email, validationID);
                } else {
                    user.email = email;
                    user.update();
                }
                edit();
            }
        }
        infos();
    }

    private static void validationEmail(Email email, User user) throws UnsupportedEncodingException {
        boolean validationEmail = Boolean.parseBoolean(Play.configuration.getProperty("email.validation"));
        if (validationEmail) {
            String validationID = Codec.UUID();
            Cache.set(validationID, email.email, "10mn");
            Mails.validationAddEmail(user, email.email, validationID);
        } else {
            email.valid = true;
            email.update();
        }
    }

    @Role("member")
    public static void modifPwd(@Required String oldwpd, @Required @Equals("pwdconfirm") String pwd, @Required String pwdconfirm) {
        User user = (User) Secure.getUser();
        validation.equals(user.password, Crypto.passwordHash(oldwpd)).message(Messages.get("error", "mot de passe incorrect"));
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
    @Get("/users")
    public static void index() {
        List<User> users = User.findAll();
        render(users);
    }

    @Role("member")
    @Get("/user/emprunts")
    public static void emprunts() {
        render();
    }


    @Get("/user/validate-{id}")
    public static void validateEmail(@Required String id) {
        User user = (User) Secure.getUser();
        if (user != null) {
            String email = (String) Cache.get(id);
            if (email != null) {
                user.email = email;
                user.update();
            } else {
                error("le lien a expiré");
            }
            edit();
        }
    }

    @Get("/user/validateEmail-{id}")
    public static void validateAddEmail(@Required String id) {
        User user = (User) Secure.getUser();
        if (user != null) {
            String email = (String) Cache.get(id);
            if (email != null) {
                Email userEmail = Email.find(email);
                if (userEmail != null) {
                    userEmail.valid = true;
                    userEmail.update();
                }

            } else {
                error("le lien a expiré");
            }
            edit();
        }
    }
}
