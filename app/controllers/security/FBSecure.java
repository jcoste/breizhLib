package controllers.security;

import models.User;
import play.Play;
import play.modules.fbconnect.FBConnectPlugin;
import play.mvc.Scope;
import play.mvc.results.Redirect;

public class FBSecure implements ISecure {

    public static final FBSecure INSTANCE = new FBSecure();
    public static final String ID = "fbconnect";

    private FBSecure() {
    }

    @Override
    public void login() {
        throw new Redirect(Play.plugin(FBConnectPlugin.class).session().getLoginUrl());
    }

    @Override
    public void logout() {
        session().put(SESSION_EMAIL_KEY, null);
        session().put(SESSION_IMPL_KEY, null);
        Secure.authetification();
    }

    @Override
    public boolean check(String profile) {
        return false;
    }

    @Override
    public void oauthCallback(String callback, String oauth_token, String oauth_verifier) throws Exception {
        throw new IllegalAccessException();
    }

    @Override
    public User getUser() {
        User user = null;
        if (session().get(SESSION_EMAIL_KEY) != null) {
            user = User.find(session().get(SESSION_EMAIL_KEY));
            if (user == null) {
                user = new User(session().get(SESSION_EMAIL_KEY));
                user.actif = true;
                user.insert();
            }
            user.actif = true;
            user.update();
        }
        return user;
    }

    private Scope.Session session() {
        return Scope.Session.current();
    }
}
