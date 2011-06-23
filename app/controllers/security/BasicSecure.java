package controllers.security;


import models.User;
import notifiers.Mails;
import org.apache.commons.mail.EmailException;
import play.Logger;
import play.cache.Cache;
import play.data.validation.Required;
import play.libs.Codec;
import play.libs.Crypto;
import play.libs.Images;
import play.mvc.Controller;

import java.io.UnsupportedEncodingException;

public class BasicSecure extends Controller implements ISecure {

    public static final BasicSecure INSTANCE = new BasicSecure();


    public void login() {
        basiclogin();
    }

    public void logout() {
        session.put("userEmail", null);
        session.put("secureimpl", null);
        Secure.authetification();
    }

    public boolean check(String profile) {
        if ("public".equals(profile)) {
            return true;
        }
        if ("admin".equals(profile)) {
            if (session.get("userEmail") != null) {
                User user = User.find(session.get("userEmail"));
                return user.isAdmin;
            }
            return false;
        } else if ("member".equals(profile)) {
            return session.get("userEmail") != null;
        }

        return false;
    }

    public static void basiclogin() {
        render();
    }

    public static void newuser(String email, String nom, String prenom) {
        String randomID = Codec.UUID();
        render(email, nom, prenom, randomID);
    }

    public static void postNewuser(@Required String email, String nom, String prenom, @Required String password,
                                   @Required String passwordconfirm, @Required String captcha, String randomID) throws Throwable {
        User user = User.find(email);
        validation.isTrue(user == null).message("<span class=\"error\">email déjà enregistré</span>");
        validation.equals(captcha, Cache.get(randomID)).message("<span class=\"error\">Invalid captcha. Please type it again</span>");
        validation.equals(password, passwordconfirm).message("<span class=\"error\">mot de passe incorrect</span>");
        if (validation.hasErrors()) {
            Logger.info(validation.errorsMap().toString());
            render("security/BasicSecure/newuser.html", email, nom, prenom, randomID);
        }
        user = new User(email);
        user.nom = nom;
        user.prenom = prenom;
        user.password = Crypto.passwordHash(password);
        user.insert();

        authenticate(email, password, false);
    }


    public static void authenticate(@Required String username, String password, boolean remember) throws Throwable {
        User user = User.find(username);
        validation.isTrue(user != null).message("<span class=\"error\">Utilisateur inconu</span>");
        validation.equals(user.password, Crypto.passwordHash(password)).message("<span class=\"error\">mot de passe incorrect</span>");
        if (validation.hasErrors()) {
            if (session.get("authfail") == null) {
                String randomID = Codec.UUID();
                session.put("authfail",randomID);
            }

            Integer authFailCount = (Integer) Cache.get(session.get("authfail"));
            if(authFailCount == null){
                authFailCount = Integer.valueOf(0);
            }
            Cache.set(session.get("authfail"),Integer.valueOf(authFailCount) + 1, "20mn");
            Secure.login();
        }

        session.remove("authfail");
        session.put("userEmail", username);
        session.put("secureimpl", "basic");
        Secure.authetification();
    }

    public static void captcha(String id) {
        Images.Captcha captcha = Images.captcha();
        String code = captcha.getText("#0000FD");
        Cache.set(id, code, "10mn");
        renderBinary(captcha);
    }


    public User getUser() {
        User user = null;
        if (session.get("userEmail") != null) {
            user = User.find(session.get("userEmail"));
        }
        return user;
    }

    public static void resetPassword() {
        render();
    }

    public static void sendResetPassword(@Required String email) throws EmailException, UnsupportedEncodingException {
        User user = User.find(email);
        validation.isTrue(user != null).message("<span class=\"error\">email inconnu</span>");
        validation.isTrue(user != null && user.password != null).message("<span class=\"error\">Compte non valide pour un changement de mot de passe</span>");
        if (validation.hasErrors()) {
            render("security/BasicSecure/resetPassword.html");
        }
        String randomID = Codec.UUID();
        Cache.set(randomID,email, "10mn");

        Mails.lostPassword(user,randomID);

        Secure.login();
    }

    public static void changePassword(@Required String id){
        session.put("userResetPwd", Cache.get(id));
        render();
    }

    public static void postChangePassword(@Required String newPwd,@Required String newPwd2){
        validation.isTrue(session.contains("userResetPwd")).message("<span class=\"error\">id inconnu</span>");
        validation.equals(newPwd, newPwd2).message("<span class=\"error\">mot de passe incorrect</span>");
        if (validation.hasErrors()) {
            render("security/BasicSecure/changePassword.html");
        }

         Logger.info("email : "+session.get("userResetPwd"));
        User user = User.find(session.get("userResetPwd"));
        user.password = Crypto.passwordHash(newPwd);
        user.update();
        Logger.info("change pwd ok");
        session.remove("userResetPwd");
        Secure.login();
    }

}
