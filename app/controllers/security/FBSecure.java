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
        return false;
    }

    @Override
    public User getUser() {
        User user = null;
        if (session.get("userEmail") != null) {
            user = User.find(session.get("userEmail"));
            if (user == null) {
                user = new User(session.get("userEmail"));
                user.actif = true;
                user.insert();
            }
            user.actif = true;
            user.update();
        }
        return user;
    }
}
