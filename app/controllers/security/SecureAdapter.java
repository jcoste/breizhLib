package controllers.security;


import models.User;

import java.util.HashMap;
import java.util.Map;

public class SecureAdapter implements ISecure {

    public static final SecureAdapter INSTANCE = new SecureAdapter();

    private Map<String, ISecure> secureMap = new HashMap<String, ISecure>();

    public static String DEFAULT_IMPL = TwitterSecure.ID;

    private SecureAdapter() {
        secureMap.put(BasicSecure.ID, BasicSecure.INSTANCE);
        secureMap.put(FBSecure.ID, FBSecure.INSTANCE);
        secureMap.put(TwitterSecure.ID, TwitterSecure.INSTANCE);
        secureMap.put(YahooSecure.ID, YahooSecure.INSTANCE);
    }


    @Override
    public void login() {
        if (secureMap.containsKey(Secure.getImpl())) {
            secureMap.get(Secure.getImpl()).login();
        } else {
            secureMap.get(DEFAULT_IMPL).login();
        }
    }

    @Override
    public void logout() {
        if (secureMap.containsKey(Secure.getImpl())) {
            secureMap.get(Secure.getImpl()).logout();
        } else {
            secureMap.get(DEFAULT_IMPL).logout();
        }
    }

    @Override
    public void oauthCallback(String callback, String oauth_token, String oauth_verifier) throws Exception {
        secureMap.get(Secure.getImpl()).oauthCallback(callback, oauth_token, oauth_verifier);
    }

    @Override
    public boolean check(String profile) {
        ISecure secure = null;
        if (secureMap.containsKey(Secure.getImpl())) {
            secure = secureMap.get(Secure.getImpl());
        } else {
            secure = secureMap.get(DEFAULT_IMPL);
        }

        if ("public".equals(profile)) {
            return true;
        }
        if ("admin".equals(profile)) {
            return secure.getUser() == null ? false : secure.getUser().isAdmin;
        } else if ("member".equals(profile)) {
            return secure.getUser() != null;
        }
        return false;
    }

    @Override
    public User getUser() {
        if (secureMap.containsKey(Secure.getImpl())) {
            return secureMap.get(Secure.getImpl()).getUser();
        } else {
            return secureMap.get(DEFAULT_IMPL).getUser();
        }

    }
}
