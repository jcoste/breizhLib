package controllers.security;


import controllers.socialoauth.UserManagement;
import models.User;
import models.socialoauth.ISecure;
import models.socialoauth.IUser;

import java.util.HashMap;
import java.util.Map;

public class SecureAdapter implements ISecure, UserManagement {

    private Map<String, ISecure> secureMap = new HashMap<String, ISecure>();

    private static String DEFAULT_IMPL;

    public SecureAdapter(String defaultId) {
        DEFAULT_IMPL = defaultId;
    }

    public void registerSecure(String id, ISecure secure) {
        secureMap.put(id, secure);
    }

    public void login() {
        getSecure().login();
    }

    public void logout() {
        getSecure().logout();
        Secure.authentification();
    }

    public ISecure getSecure() {
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
        return false;
    }

    public IUser getUser() {
        return getSecure().getUser();
    }

    public IUser getByUsername(String username) {
        User user = User.findByUsername(username);
        initUser(user);
        return user;
    }

    public IUser getByEmail(String email) {
        User user = User.find(email);
        initUser(user);
        return user;
    }

    public IUser createUser(String email, String username) {
        return new User(email, username);
    }

    private void initUser(User user) {
        if(user != null){
            if (user.isPublic == null) {
                user.isPublic = Boolean.FALSE;
            }

            if (user.publicUsername == null) {
                user.publicUsername = Boolean.FALSE;
            }
            user.update();
        }
    }


}
