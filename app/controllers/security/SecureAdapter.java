package controllers.security;


import controllers.multioauth.FBSecure;
import controllers.multioauth.TwitterSecure;
import controllers.multioauth.UserManagement;
import models.User;
import models.multioauth.ISecure;
import models.multioauth.IUser;

import java.util.HashMap;
import java.util.Map;

public class SecureAdapter implements ISecure,UserManagement {

    public static final SecureAdapter INSTANCE = new SecureAdapter();

    private Map<String, ISecure> secureMap = new HashMap<String, ISecure>();

    private static String DEFAULT_IMPL = GAESecure.ID;

    private SecureAdapter() {
        secureMap.put(BasicSecure.ID, BasicSecure.INSTANCE);
        secureMap.put(GAESecure.ID, new GAESecure(this));
        secureMap.put(FBSecure.ID, new FBSecure(this));
        secureMap.put(TwitterSecure.ID, new TwitterSecure(this));
        secureMap.put(YahooSecure.ID, new YahooSecure(this));
    }


    public void login() {
        getSecure().login();
    }

    public void logout() {
        getSecure().logout();
    }

    private ISecure getSecure(){
       if (secureMap.containsKey(Secure.getImpl())) {
           return secureMap.get(Secure.getImpl());
        } else {
            return secureMap.get(DEFAULT_IMPL);
        }
    }

    public void oauthCallback(String callback, String oauth_token, String oauth_verifier) throws Exception {
        getSecure().oauthCallback(callback, oauth_token, oauth_verifier);
    }

    public boolean check(String profile) {
        ISecure secure = getSecure();

        if ("public".equals(profile)) {
            return true;
        }
        if ("admin".equals(profile)) {
            return secure.getUser() == null ? false : secure.getUser().isAdmin();
        } else if ("member".equals(profile)) {
            return secure.getUser() != null;
        }
        return false;
    }

    public IUser getUser() {
        return getSecure().getUser();
    }

    public IUser getByUsername(String username) {
        return User.findByUsername(username);
    }

    public IUser getByEmail(String email) {
       return User.find(email);
    }

    public IUser createUser(String email, String username) {
        return new User(email,username);
    }
}
