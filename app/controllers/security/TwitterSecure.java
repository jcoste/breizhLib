package controllers.security;


import models.User;
import play.modules.oauthclient.ICredentials;
import play.modules.oauthclient.OAuthClient;
import play.mvc.Controller;
import play.mvc.Router;

import java.util.HashMap;
import java.util.Map;

public class TwitterSecure extends Controller implements ISecure {

     public static final TwitterSecure INSTANCE = new TwitterSecure();

    private static OAuthClient connector = null;
	private static OAuthClient getConnector() {
		if (connector == null) {
			connector = new OAuthClient(
					"http://twitter.com/oauth/request_token",
					"http://twitter.com/oauth/access_token",
                    "http://twitter.com/oauth/authorize",
					"1SZiYOUuvJ6r98xboU0NQ",
					"yajGF4U43Oe12wIuyZEL6sGNbH7GvXiPGEGvet9C8");
		}
		return connector;
	}

    @Override
    public void login() {


    }

    public static void authenticate(String callback) throws Exception {
		// 1: get the request token
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("callback", callback);
		String callbackURL = Router.getFullUrl(request.controller + ".oauthCallback", args);
		getConnector().authenticate(getTwiterUser(), callbackURL);
	}

    public static void oauthCallback(String callback, String oauth_token, String oauth_verifier) throws Exception {
		// 2: get the access token
		getConnector().retrieveAccessToken(getTwiterUser(), oauth_verifier);
		redirect(callback);
	}

    @Override
    public void logout() {
        session.put("userEmail", null);
        session.put("secureimpl", null);
        _session.set(null);
        Secure.authetification();
    }

    @Override
    public boolean check(String profile) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private static ThreadLocal<TwitterUser> _session = new ThreadLocal<TwitterUser>();

    public static TwitterUser getTwiterUser() {
        if(_session.get() == null){
            _session.set(new TwitterUser());
        }
        return _session.get();
    }

    @Override
    public User getUser() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public static class TwitterUser implements ICredentials{

       private String token;

        private String secret;

        public void setToken(String token) {
            this.token = token;
        }

        public String getToken() {
            return token;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public String getSecret() {
            return secret;
        }
    }

}
