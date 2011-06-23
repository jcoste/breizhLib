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

public class YahooSecure extends Controller implements ISecure {

     public static final YahooSecure INSTANCE = new YahooSecure();

    private String consumerKey;
	private String consumerSecret;
    private String callback;

    private OAuthClient connector = null;
	private static OAuthClient getConnector() {
		if (INSTANCE.connector == null) {
			INSTANCE.connector = new OAuthClient(
					"https://api.login.yahoo.com/oauth/v2/get_request_token",
					"https://api.login.yahoo.com/oauth/v2/request_auth",
					"https://api.login.yahoo.com/oauth/v2/get_token",
					INSTANCE.consumerKey,
					INSTANCE.consumerSecret);
		}
		return INSTANCE.connector;
	}

     private YahooSecure(){
           init();
     }

     public void init(){
        if(!Play.configuration.containsKey("yahoo.consumerKey")) {
            throw new UnexpectedException("OAuth yahoo requires that you specify yahoo.consumerKey in your application.conf");
        }
        if(!Play.configuration.containsKey("yahoo.consumerSecret")){
            throw new UnexpectedException("OAuth yahoo requires that you specify yahoo.consumerSecret in your application.conf");
        }
        if(!Play.configuration.containsKey("yahoo.callback")){
            throw new UnexpectedException("OAuth yahoo requires that you specify yahoo.callback in your application.conf");
        }
        consumerKey = Play.configuration.getProperty("yahoo.consumerKey");
        consumerSecret = Play.configuration.getProperty("yahoo.consumerSecret");
        callback = Router.getFullUrl(Play.configuration.getProperty("yahoo.callback"));

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
        }
        return user;
    }

}
