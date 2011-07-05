package controllers.security;


import controllers.Application;
import controllers.Users;
import models.User;
import models.oauthclient.Credentials;
import play.Play;
import play.exceptions.UnexpectedException;
import play.mvc.Router;
import play.mvc.Scope;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TwitterSecure extends OAuthSecure implements ISecure {

    public static final TwitterSecure INSTANCE = new TwitterSecure();
    public static final String ID = "twitter";

    private TwitterSecure() {
        super("http://twitter.com/oauth/request_token",
                "http://twitter.com/oauth/access_token",
                "http://twitter.com/oauth/authorize");
        init();
    }

    public void init() {
        if (!Play.configuration.containsKey("twitter.consumerKey")) {
            throw new UnexpectedException("OAuth Twitter requires that you specify twitter.consumerKey in your application.conf");
        }
        if (!Play.configuration.containsKey("twitter.consumerSecret")) {
            throw new UnexpectedException("OAuth Twitter requires that you specify twitter.consumerSecret in your application.conf");
        }
        if (!Play.configuration.containsKey("twitter.callback")) {
            throw new UnexpectedException("OAuth Twitter requires that you specify twitter.callback in your application.conf");
        }
        consumerKey = Play.configuration.getProperty("twitter.consumerKey");
        consumerSecret = Play.configuration.getProperty("twitter.consumerSecret");
        callback = Router.getFullUrl(Play.configuration.getProperty("twitter.callback"));

    }


    public void authenticate(String callback) throws Exception {
        // 1: get the request token
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("callback", callback);
        String callbackURL = Router.getFullUrl(request.controller + ".oauthCallback", args);
        INSTANCE.getConnector().authenticate(getCredentials(), callbackURL);
    }

    public void oauthCallback(String callback, String oauth_token, String oauth_verifier) throws Exception {
        // 2: get the access token
        INSTANCE.getConnector().retrieveAccessToken(getCredentials(), oauth_verifier);
		String username = getConnector().getProvider().getResponseParameters().get("screen_name").toLowerCase();
		User user = User.findByUsername(username);
        if (user != null) {
			user.dateConnexion = new Date();
            user.update();
		}
        session().put(SESSION_EMAIL_KEY, username);
        redirect(callback);
    }

    private static ThreadLocal<Credentials> _session = new ThreadLocal<Credentials>();

    public static Credentials getCredentials() {
        if (_session.get() == null) {
            _session.set(new Credentials());
        }
        return _session.get();
    }

    @Override
    public User getUser() {
        User user = null;
        if (session().get(SESSION_EMAIL_KEY) != null) {
            user = User.findByUsername(session().get(SESSION_EMAIL_KEY));
            if (user == null) {
                user = new User(null);
                user.username = session().get(SESSION_EMAIL_KEY);
                user.actif = true;
                user.insert();
            }
            user.actif = true;
            user.update();
        }
        return user;
    }

    protected Scope.Session session() {
        return Scope.Session.current();
    }

    public static void informations() {
        if (Secure.getUser().email == null) {
            Users.edit();
        } else {
            Application.index();
        }
    }

}
