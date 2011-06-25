package controllers.security;


import models.User;

import java.util.HashMap;
import java.util.Map;

public class SecureAdapter implements ISecure {

    public static final SecureAdapter INSTANCE = new SecureAdapter();

    private Map<String,ISecure> secureMap = new HashMap<String, ISecure>();

    private SecureAdapter(){
        secureMap.put("basic",BasicSecure.INSTANCE);
        secureMap.put("gae",GAESecure.INSTANCE);
        secureMap.put("fbconnect",FBSecure.INSTANCE);
        secureMap.put("twitter",TwitterSecure.INSTANCE);
        secureMap.put("yahoo",YahooSecure.INSTANCE);
    }


    @Override
    public void login() {
       secureMap.get(Secure.getImpl()).login();
    }

    @Override
    public void logout() {
       secureMap.get(Secure.getImpl()).logout();
    }

    @Override
    public void oauthCallback(String callback, String oauth_token, String oauth_verifier) throws Exception {
         secureMap.get(Secure.getImpl()).oauthCallback(callback,oauth_token,oauth_verifier);
    }

    @Override
    public boolean check(String profile) {
        ISecure secure = secureMap.get(Secure.getImpl());
        if ("public".equals(profile)) {
            return true;
        }
        if ("admin".equals(profile)) {
            return secure.getUser() == null ? false: secure.getUser().isAdmin;
        } else if ("member".equals(profile)) {
            return secure.getUser() != null;
        }
        return false;
    }

    @Override
    public User getUser() {
        return  secureMap.get(Secure.getImpl()).getUser();
    }
}
