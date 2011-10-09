package controllers.security;


import controllers.Application;
import controllers.socialoauth.*;
import models.ServerType;
import models.Serveur;
import models.socialoauth.ISecure;
import models.socialoauth.IUser;
import models.socialoauth.Role;
import play.cache.Cache;
import play.modules.router.Get;
import play.mvc.Before;
import play.mvc.Controller;

import java.util.Date;

public class Secure extends Controller {

    private static SecureAdapter secure = new SecureAdapter(TwitterSecure.ID);

    static {
        secure.registerSecure(BasicSecure.ID, BasicSecure.INSTANCE);
        secure.registerSecure(FBSecure.ID, new FBSecure(secure));
        secure.registerSecure(TwitterSecure.ID, new TwitterSecure(secure));
        secure.registerSecure(YahooSecure.ID, new YahooSecure(secure));
        secure.registerSecure(LinkedInSecure.ID, new LinkedInSecure(secure));
        secure.registerSecure(GoogleSecure.ID, new GoogleSecure(secure));
    }

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

    public static void authentification() {
        IUser user = secure.getUser();
        if (user != null) {
            session.put(ISecure.SESSION_EMAIL_KEY, user.getEmail());
            session.put("userIsAdmin", user.isAdmin());
            user.setDateConnexion(new Date());
            user.save();
            Application.index();
        } else {
            session.put(ISecure.SESSION_EMAIL_KEY, null);
            session.put("userIsAdmin", null);
            Application.index();
        }
    }

    @Get("/login-{impl}")
    public static void login(String impl) {
        if (impl == null || impl.equals("all")) {
            Integer authFailcount = (Integer) Cache.get(session.get("authfail"));
            render(authFailcount);
        } else {
            session.put(ISecure.SESSION_IMPL_KEY, impl);
            secure.login();
        }
    }

    @Get("/logout")
    public static void logout() {
        secure.logout();
    }

    @Get("/oauthCallback")
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
        ISecure isecure = secure.getSecure();

        if ("public".equals(profile)) {
            return true;
        }
        if ("admin".equals(profile)) {
            return isecure.getUser() == null ? false : isecure.getUser().isAdmin();
        } else if ("member".equals(profile)) {
            return isecure.getUser() != null;
        } else if ("api".equals(profile)) {
            Serveur serveur = Serveur.findByType(ServerType.EXPORT);
            return serveur.code.equals(params.get("apicode"));
        }
        return false;
    }

    private static void onCheckFailed(String profile) {
        forbidden();
    }

    public static IUser getUser() {
        return secure.getUser();
    }

}
