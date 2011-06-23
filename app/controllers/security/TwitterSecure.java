package controllers.security;


import models.User;
import models.oauthclient.Credentials;
import play.Logger;
import play.Play;
import play.exceptions.UnexpectedException;
import play.modules.oauthclient.OAuthClient;
import play.mvc.Controller;
import play.mvc.Router;

import java.util.HashMap;
import java.util.Map;

public class TwitterSecure extends Controller implements ISecure {

     public static final TwitterSecure INSTANCE = new TwitterSecure();

    private String consumerKey;
	private String consumerSecret;
    private String callback;

    private OAuthClient connector = null;
	private static OAuthClient getConnector() {
		if (INSTANCE.connector == null) {
			INSTANCE.connector = new OAuthClient(
					"http://twitter.com/oauth/request_token",
					"http://twitter.com/oauth/access_token",
					"http://twitter.com/oauth/authorize",
					INSTANCE.consumerKey,
					INSTANCE.consumerSecret);
		}
		return INSTANCE.connector;
	}

     private TwitterSecure(){
           init();
     }

     public void init(){
        if(!Play.configuration.containsKey("twitter.consumerKey")) {
            throw new UnexpectedException("OAuth Twitter requires that you specify twitter.consumerKey in your application.conf");
        }
        if(!Play.configuration.containsKey("twitter.consumerSecret")){
            throw new UnexpectedException("OAuth Twitter requires that you specify twitter.consumerSecret in your application.conf");
        }
        if(!Play.configuration.containsKey("twitter.callback")){
            throw new UnexpectedException("OAuth Twitter requires that you specify twitter.callback in your application.conf");
        }
        consumerKey = Play.configuration.getProperty("twitter.consumerKey");
        consumerSecret = Play.configuration.getProperty("twitter.consumerSecret");
        callback = Router.getFullUrl(Play.configuration.getProperty("twitter.callback"));

    }

    @Override
    public void login() {
        try {
            authenticate(callback);
        } catch (Exception e) {
            Logger.error(e.getMessage());
        }

    }

    public static void authenticate(String callback) throws Exception {
		// 1: get the request token
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("callback", callback);
		String callbackURL = Router.getFullUrl(request.controller + ".oauthCallback", args);
		getConnector().authenticate(getCredentials(), callbackURL);
	}

    public static void oauthCallback(String callback, String oauth_token, String oauth_verifier) throws Exception {
		// 2: get the access token
        Logger.info("token :" + oauth_token);
		getConnector().retrieveAccessToken(getCredentials(), oauth_verifier);
        session.put("userEmail", getConnector().getProvider().getResponseParameters().get("screen_name"));
		redirect(callback);
	}

    @Override
    public void logout() {
        session.put("userEmail", null);
        session.put("secureimpl", null);
        Secure.authetification();
    }

    @Override
    public boolean check(String profile) {
        return false;
    }

    private static ThreadLocal<Credentials> _session = new ThreadLocal<Credentials>();

    public static Credentials getCredentials() {
        if(_session.get() == null){
            _session.set(new Credentials());
        }
        return _session.get();
    }
    @Override
    public User getUser() {
        User user = null;
        if (session.get("userEmail") != null) {
            user = User.findByUsername(session.get("userEmail"));
            if (user == null) {
                user = new User(null);
                user.username = session.get("userEmail");
                user.actif = true;
                user.insert();
            }
             user.actif = true;
            user.update();
        }
        return user;
    }

}
