package controllers.multioauth;

import controllers.security.Secure;
import models.multioauth.ISecure;
import models.multioauth.IUser;
import play.Play;
import play.modules.fbconnect.FBConnectPlugin;
import play.mvc.Scope;
import play.mvc.results.Redirect;

public class FBSecure implements ISecure {

    public static final String ID = "fbconnect";

    UserManagement um;

    public FBSecure(UserManagement um) {
        this.um = um;
    }

    public void login() {
        throw new Redirect(Play.plugin(FBConnectPlugin.class).session().getLoginUrl());
    }

    public void logout() {
        session().put(SESSION_EMAIL_KEY, null);
        session().put(SESSION_IMPL_KEY, null);
        Secure.authentification();
    }

    public boolean check(String profile) {
        return false;
    }

    public void oauthCallback(String callback, String oauth_token, String oauth_verifier) throws Exception {
        throw new IllegalAccessException();
    }

    public IUser getUser() {
        IUser user = null;
        if (session().get(SESSION_EMAIL_KEY) != null) {
            user = um.getByEmail(session().get(SESSION_EMAIL_KEY).toLowerCase());
            if (user == null) {
                user = um.createUser(session().get(SESSION_EMAIL_KEY),null);
                user.setActif(true);
                user.save();
            }
            user.setActif(true);
            user.save();
        }
        return user;
    }

    private Scope.Session session() {
        return Scope.Session.current();
    }
}
