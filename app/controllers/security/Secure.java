package controllers.security;


import controllers.Application;
import models.User;
import play.cache.Cache;
import play.mvc.Before;
import play.mvc.Controller;

import java.util.Date;


public class Secure extends Controller {

    private static ISecure secure = SecureAdapter.INSTANCE;

    @Before(unless = {"login", "logout", "oauthCallback"})
    public static void checkAccess() throws Throwable {
        // Checks
        Role role = getActionAnnotation(Role.class);
        if (role != null) {
            checkRole(role);
        }
        role = getControllerInheritedAnnotation(Role.class);
        if (role != null) {
            checkRole(role);
        }
    }

    public static String getImpl() {
        if (session != null && session.get(ISecure.SESSION_IMPL_KEY) != null) {
            return session.get(ISecure.SESSION_IMPL_KEY);
        } else {
            return TwitterSecure.ID;
        }
    }

    public static void authetification() {
        User user = secure.getUser();
        if (user != null) {
            session.put(ISecure.SESSION_EMAIL_KEY, user.email);
            session.put("userIsAdmin", user.isAdmin);
            user.dateConnexion = new Date();
            user.update();
            Application.index();
        } else {
            session.put(ISecure.SESSION_EMAIL_KEY, null);
            session.put("userIsAdmin", null);
            Application.index();
        }
    }

    public static void login(String impl) {
        if (impl == null) {
            Integer authFailcount = (Integer) Cache.get(session.get("authfail"));
            render(authFailcount);
        } else {
            session.put(ISecure.SESSION_IMPL_KEY, impl);
            secure.login();
        }
    }

    public static void logout() {
        secure.logout();
    }

    public static void oauthCallback(String callback, String oauth_token, String oauth_verifier) throws Exception {
        secure.oauthCallback(callback, oauth_token, oauth_verifier);
    }

    private static void checkRole(Role role) {
        for (String profile : role.value()) {
            boolean hasProfile = check(profile);
            if (!hasProfile) {
                onCheckFailed(profile);
            }
        }
    }

    public static boolean check(String profile) {
        return secure.check(profile);
    }

    private static void onCheckFailed(String profile) {
        forbidden();
    }

    public static User getUser() {
        return secure.getUser();
    }

}
