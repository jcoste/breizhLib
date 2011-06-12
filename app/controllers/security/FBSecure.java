package controllers.security;

import com.google.gson.JsonObject;
import models.User;
import play.Play;
import play.mvc.Controller;
import play.mvc.Scope;
import play.mvc.results.Redirect;
import play.modules.fbconnect.FBConnectPlugin;

public class FBSecure extends Controller implements ISecure {

    public static final FBSecure INSTANCE = new FBSecure();

    private FBSecure() {
    }

    @Override
    public void login() {
        throw new Redirect(Play.plugin(FBConnectPlugin.class).session().getLoginUrl());
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
                User user = getUser();
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
            if (user == null) {
                user = new User(session.get("userEmail"));
                user.insert();
            }
        }
        return user;
    }
}
