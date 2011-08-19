package controllers.socialoauth;

import models.socialoauth.Credentials;
import models.socialoauth.ISecure;
import models.socialoauth.IUser;
import play.Logger;
import play.Play;
import play.exceptions.UnexpectedException;
import play.mvc.Router;
import play.mvc.Scope;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * TODO  en cours
 */
public class LinkedInSecure  extends OAuthSecure implements ISecure {

    public static final String ID = "linkedin";

    UserManagement um;

    public LinkedInSecure(UserManagement um) {
        super(" https://api.linkedin.com/uas/oauth/requestToken",
                "https://www.linkedin.com/uas/oauth/authenticate",
                "https://api.linkedin.com/uas/oauth/accessToken");
        this.um = um;
        init();
    }

    public void init() {
        if (!Play.configuration.containsKey("linkedin.consumerKey")) {
            throw new UnexpectedException("OAuth linkedin requires that you specify linkedin.consumerKey in your application.conf");
        }
        if (!Play.configuration.containsKey("google.consumerSecret")) {
            throw new UnexpectedException("OAuth linkedin requires that you specify linkedin.consumerSecret in your application.conf");
        }
        if (!Play.configuration.containsKey("google.callback")) {
            throw new UnexpectedException("OAuth linkedin requires that you specify linkedin.callback in your application.conf");
        }
        consumerKey = Play.configuration.getProperty("linkedin.consumerKey");
        consumerSecret = Play.configuration.getProperty("linkedin.consumerSecret");
        callback = Router.getFullUrl(Play.configuration.getProperty("linkedin.callback"));

    }

    public void authenticate(String callback) throws Exception {
        // 1: get the request token
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("callback", callback);
        String callbackURL = Router.getFullUrl(request.controller + ".oauthCallback", args);
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
        IUser user = um.getByEmail(email);
        if (user != null) {
            user.setDateConnexion(new Date());
            user.save();
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
