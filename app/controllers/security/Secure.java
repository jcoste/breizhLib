package controllers.security;


import controllers.Application;
import models.User;
import play.mvc.Before;
import play.mvc.Controller;

import java.util.Date;


public class Secure extends Controller {

    private static ISecure secure = SecureAdapter.INSTANCE;

    @Before(unless = {"login", "logout"})
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
        if (session.get("secureimpl") != null) {
            return session.get("secureimpl");
        } else {
            return "gae";
        }
    }

    public static void authetification() {
        User user = secure.getUser();
        if (user != null) {
            session.put("userEmail", user.email);
            session.put("userIsAdmin", user.isAdmin);
            user.dateConnexion = new Date();
            user.update();
            Application.index();
        } else {
            session.put("userEmail", null);
            session.put("userIsAdmin", null);
            Application.index();
        }
    }

    public static void login() {
        render();
    }

    public static void logout() {
        secure.logout();
    }

    public static void glogin() {
        session.put("secureimpl","gae");
        secure.login();
    }

    public static void fblogin() {
        session.put("secureimpl","fbconnect");
        secure.login();
    }

    public static void tlogin() {
        session.put("secureimpl","twiter");
        secure.login();
    }

    public static void ylogin() {
        session.put("secureimpl","yahoo");
        secure.login();
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
