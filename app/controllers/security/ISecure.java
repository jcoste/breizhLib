package controllers.security;


import models.User;

public interface ISecure {

    String SESSION_IMPL_KEY = "secureimpl";

    String SESSION_EMAIL_KEY = "userEmail";

    void login();

    void logout();

    void oauthCallback(String callback, String oauth_token, String oauth_verifier) throws Exception;

    boolean check(String profile);

    User getUser();
}
