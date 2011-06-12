package controllers.security;

import com.google.gson.JsonObject;
import models.User;
import play.Play;
import play.exceptions.UnexpectedException;
import play.libs.OAuth2;
import play.libs.WS;
import play.libs.ws.WSUrlFetch;
import play.mvc.Controller;
import play.mvc.Router;
import play.mvc.results.Redirect;


public class FBSecureBase extends Controller implements ISecure {

    public static final FBSecureBase INSTANCE = new FBSecureBase();

    private static final String authorizationURL = "https://graph.facebook.com/oauth/authorize";
    private static final String accessTokenURL = "https://graph.facebook.com/oauth/access_token";
    private static String clientid = "178515308872430";
    private static String secret = "64ebb3897900bd6d6dbfd813e3996a1f";
    private static String apiKey = "a064d2b63fc015434cd8a45b4e21dd61";

    private OAuth2 oauth;


    private FBSecureBase() {
        init();
        oauth = new OAuth2(authorizationURL, accessTokenURL, clientid, secret);


    }

    public static String getAuthURL(String authCode) {
        String redirect_uri = Router.getFullUrl("security.secure.authetification");
        return accessTokenURL + "?client_id=" +
                clientid + "&redirect_uri=" +
                redirect_uri + "&client_secret=" + secret + "&code=" + authCode;
    }

    @Override
    public void login() {
       oauth.requestAccessToken();
    }

    public static void callback() {

       String code = params.get("code");

       if(code != null){
            String authUrl = getAuthURL(code);
            WSUrlFetch ws = new WSUrlFetch();
            String response = ws.newRequest(authUrl).get().getString();
            String accessToken = null;
            Integer expires = null;
            String[] pairs = response.split("&");
            for (String pair : pairs) {
                String[] kv = pair.split("=");
                if (kv.length != 2) {
                    throw new UnexpectedException("fbconnect got an unexpected auth response from facebook");
                } else {
                    if (kv[0].equals("access_token")) {
                        accessToken = kv[1];
                    }
                    if (kv[0].equals("expires")) {
                        expires = Integer.valueOf(kv[1]);
                    }
                }
            }
            if (accessToken != null && expires != null) {
                String uri = "https://graph.facebook.com/me?access_token="+ WS.encode(accessToken);
                JsonObject jsonData = ws.newRequest(uri).get().getJson().getAsJsonObject();
                session.put("userEmail", jsonData.get("email"));
            } else {
                throw new UnexpectedException("fbconnect could not find access token and expires in facebook callback");
            }
        }


       String redirect_uri = Router.getFullUrl("security.secure.authetification");
       throw new Redirect(redirect_uri);
    }

    @Override
    public void logout() {
        session.put("userEmail", null);
        session.put("secureimpl", null);
        Secure.authetification();
    }

    @Override
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

    @Override
    public User getUser() {
        User user = null;
        if (session.get("userEmail") != null) {
            user = User.find(session.get("userEmail"));
        }
        return user;
    }


     public void init(){
        if(!Play.configuration.containsKey("fbconnect.id")) {
            throw new UnexpectedException("fbconnect requires that you specify fbconnect.id in your application.conf");
        }
        if(!Play.configuration.containsKey("fbconnect.apiKey")){
            throw new UnexpectedException("fbconnect requires that you specify fbconnect.apiKey in your application.conf");
        }
        if(!Play.configuration.containsKey("fbconnect.secret")){
            throw new UnexpectedException("fbconnect requires that you specify fbconnect.secret in your application.conf");
        }
        clientid = Play.configuration.getProperty("fbconnect.id");
        apiKey = Play.configuration.getProperty("fbconnect.apiKey");
        secret = Play.configuration.getProperty("fbconnect.secret");
    }
}
