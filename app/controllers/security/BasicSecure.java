package controllers.security;


import controllers.Application;
import models.User;
import models.socialoauth.ISecure;
import models.socialoauth.IUser;
import net.tanesha.recaptcha.ReCaptchaFactory;
import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;
import notifiers.Mails;
import org.apache.commons.mail.EmailException;
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

import java.io.UnsupportedEncodingException;

public class BasicSecure extends Controller implements ISecure {

    public static final BasicSecure INSTANCE = new BasicSecure();
    public static final String ID = "basic";


    public void login() {
        basiclogin();
    }

    public void logout() {
        session.put(SESSION_EMAIL_KEY, null);
        session.put(SESSION_IMPL_KEY, null);
    }

    public void oauthCallback(String callback, String oauth_token, String oauth_verifier) throws Exception {
        throw new IllegalAccessException();
    }

    public boolean check(String profile) {
        return false;
    }

    public static void basiclogin() {
        render();
    }

    @Get("/user/new")
    public static void newuser(String email, String nom, String prenom) {
        String captcha = ReCaptchaFactory.newReCaptcha("6LcyNccSAAAAAFgrBNO5Nsu1yF3ykiY5uj0v11F6", "6LcyNccSAAAAALycebaVRr6Hi5ZiELwW9OEskbkB", false).createRecaptchaHtml(null, null);
        render(email, nom, prenom, captcha);
    }

    @Post("/user/new")
    public static void postNewuser(@Required String email, String nom, String prenom, @Required @Equals("passwordconfirm") String password,
                                   @Required String passwordconfirm) throws Throwable {
        User user = User.find(email);
        validation.isTrue(user == null).message(Messages.get("error", Messages.get("email_already_register")));

        ReCaptchaImpl reCaptcha = new ReCaptchaImpl();
        reCaptcha.setPrivateKey("6LcyNccSAAAAALycebaVRr6Hi5ZiELwW9OEskbkB");
        String challenge = params.get("recaptcha_challenge_field");
        String uresponse = params.get("recaptcha_response_field");
        ReCaptchaResponse reCaptchaResponse =
                reCaptcha.checkAnswer("127.0.0.1", challenge, uresponse);

        validation.isTrue(reCaptchaResponse.isValid()).message(Messages.get("error", Messages.get("invalid_captcha")));

        if (validation.hasErrors()) {
            String captcha = ReCaptchaFactory.newReCaptcha("6LcyNccSAAAAAFgrBNO5Nsu1yF3ykiY5uj0v11F6", "6LcyNccSAAAAALycebaVRr6Hi5ZiELwW9OEskbkB", false).createRecaptchaHtml(null, null);

            render("security/BasicSecure/newuser.html", email, nom, prenom, captcha);
        }
        user = new User(email, null);
        user.nom = nom;
        user.prenom = prenom;
        user.password = Crypto.passwordHash(password);


        boolean validationInscription = Boolean.parseBoolean(Play.configuration.getProperty("authbasic.inscription.validation"));
        if (validationInscription) {
            String activationID = Codec.UUID();
            Cache.set(activationID, email, "10mn");
            Mails.validationInscription(user, activationID);
            user.actif = false;
        } else {
            user.actif = true;
        }
        user.insert();
        authenticate(email, password, false);
    }

    @Get("/user/activate")
    public static void activerCompte(@Required String id) {
        IUser user = User.find((String) Cache.get(id));
        user.setActif(true);
        user.save();
        Application.index();
    }

    @Post("/user/auth")
    public static void authenticate(@Required String username, @Required String password, boolean remember) throws Throwable {
        User user = User.find(username);
        validation.isTrue(user != null).message(Messages.get("error", Messages.get("unknown_user")));
        if (user != null) {
            validation.isTrue(user.password != null).message(Messages.get("error", Messages.get("type_compte_invalide")));
            validation.equals(user.password, Crypto.passwordHash(password)).message(Messages.get("error", Messages.get("error_password")));
        }
        if (validation.hasErrors()) {
            if (session.get("authfail") == null) {
                String randomID = Codec.UUID();
                session.put("authfail", randomID);
            }

            Integer authFailCount = (Integer) Cache.get(session.get("authfail"));
            if (authFailCount == null) {
                authFailCount = Integer.valueOf(0);
            }
            Cache.set(session.get("authfail"), Integer.valueOf(authFailCount) + 1, "20mn");
            Secure.login(null);
        }

        session.remove("authfail");
        session.put(SESSION_EMAIL_KEY, username.toLowerCase());
        session.put(SESSION_IMPL_KEY, "basic");
        Secure.authentification();
    }


    public IUser getUser() {
        IUser user = null;
        if (session.get(SESSION_EMAIL_KEY) != null) {
            user = User.find(session.get(SESSION_EMAIL_KEY));
        }
        return user;
    }

    public static void resetPassword() {
        render();
    }

    @Post("/user/resetPassword/{email}")
    public static void sendResetPassword(@Required String email) throws EmailException, UnsupportedEncodingException {
        User user = User.find(email);
        validation.isTrue(user != null).message(Messages.get("error", Messages.get("unknown_email")));
        validation.isTrue(user != null && user.password != null).message(Messages.get("error", Messages.get("type_compte_invalide_chg_pwd")));
        if (validation.hasErrors()) {
            render("security/BasicSecure/resetPassword.html");
        }
        String randomID = Codec.UUID();
        Cache.set(randomID, email, "10mn");

        Mails.lostPassword(user, randomID);

        Secure.login(null);
    }

    @Get("/user/resetPassword/{id}")
    public static void changePassword(@Required String id) {
        session.put("userResetPwd", Cache.get(id));
        render();
    }

    @Post("/user/changePassword")
    public static void postChangePassword(@Required @Equals("newPwd2") String newPwd, @Required String newPwd2) {
        validation.isTrue(session.contains("userResetPwd")).message(Messages.get("error", "id inconnu"));
        if (validation.hasErrors()) {
            render("security/BasicSecure/changePassword.html");
        }
        User user = User.find(session.get("userResetPwd"));
        user.password = Crypto.passwordHash(newPwd);
        user.update();
        session.remove("userResetPwd");
        Secure.login(null);
    }

}
