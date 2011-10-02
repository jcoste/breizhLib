package controllers.socialoauth;


import models.User;
import models.socialoauth.Credentials;
import models.socialoauth.ISecure;
import models.socialoauth.IUser;
import play.Logger;
import play.Play;
import play.exceptions.UnexpectedException;
import play.mvc.Router;
import play.mvc.Scope;

import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO  en cours
 */
public class GoogleSecure extends OAuthSecure implements ISecure {

    public static final String ID = "google";

    UserManagement um;

    public GoogleSecure(UserManagement um) {
        super(" https://www.google.com/accounts/OAuthGetRequestToken",
                "https://www.google.com/accounts/OAuthAuthorizeToken",
                "https://www.google.com/accounts/OAuthGetAccessToken");
        this.um = um;
        init();
    }

    public void init() {
        if (!Play.configuration.containsKey("google.consumerKey")) {
            throw new UnexpectedException("OAuth google requires that you specify google.consumerKey in your application.conf");
        }
        if (!Play.configuration.containsKey("google.consumerSecret")) {
            throw new UnexpectedException("OAuth google requires that you specify google.consumerSecret in your application.conf");
        }
        if (!Play.configuration.containsKey("google.callback")) {
            throw new UnexpectedException("OAuth google requires that you specify google.callback in your application.conf");
        }
        consumerKey = Play.configuration.getProperty("google.consumerKey");
        consumerSecret = Play.configuration.getProperty("google.consumerSecret");
        callback = Router.getFullUrl(Play.configuration.getProperty("google.callback"));

    }

    public void authenticate(String callback) throws Exception {
         // 1: get the request token
         Logger.info("---------------OK ------------------------");
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("oauth_consumer_key",consumerKey);
        args.put("oauth_signature_method","HMAC-SHA1");
        args.put("oauth_timestamp", System.currentTimeMillis()/1000 + "");
        //TODO args.put("oauth_nonce", "0c7236394b4d39d4e07f2b4c37d33651");
        //TODO args.put("oauth_signature", "19NEfAVNf%2B1fajiwqIOZeAWNKws%3D");
        args.put("callback", callback);
        args.put("scope", "https://www.google.com/books/feeds/");


        //oauth_signature&oauth_nonce
        String callbackURL = Router.getFullUrl(request.controller + ".oauthCallback", args);
        Logger.info("url--->"+callbackURL);
        getConnector().authenticate(getCredentials(), callbackURL);
    }

    public Scope.Session session() {
        return Scope.Session.current();
    }

    public void oauthCallback(String callback, String oauth_token, String oauth_verifier) throws Exception {
        // 2: get the access token
        Logger.info("token :" + oauth_token);
        getConnector().retrieveAccessToken(getCredentials(), oauth_verifier);
        String email = getConnector().getProvider().getResponseParameters().get("email").toLowerCase();
        User user = User.find(email);
        if (user != null) {
            user.dateConnexion = new Date();
            user.update();
        }
        session().put(SESSION_EMAIL_KEY, email);
        redirect(callback);
    }

    @Override
    public boolean check(String profile) {
        return false;
    }

    private static ThreadLocal<Credentials> _session = new ThreadLocal<Credentials>();

    public static Credentials getCredentials() {
        if (_session.get() == null) {
            _session.set(new Credentials());
        }
        return _session.get();
    }

    @Override
    public IUser getUser() {
        IUser user = null;
        if (session().get(SESSION_EMAIL_KEY) != null) {
            user = um.getByUsername(session().get(SESSION_EMAIL_KEY));
            if (user == null) {
                user = um.createUser(session().get(SESSION_EMAIL_KEY),null);
                user.setActif(true);
                user.save();
            }
        }
        return user;
    }

}
