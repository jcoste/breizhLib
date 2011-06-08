package controllers.security;


import models.User;

public class SecureAdapter implements ISecure {

    public static final SecureAdapter INSTANCE = new SecureAdapter();

    private static ISecure basicSecure = BasicSecure.INSTANCE;
    private static ISecure gaeSecure = GAESecure.INSTANCE;


    @Override
    public void login() {
        if (Secure.getImpl().equals("basic")) {
            basicSecure.login();
        } else {
            gaeSecure.login();
        }
    }

    @Override
    public void logout() {
        if (Secure.getImpl().equals("basic")) {
            basicSecure.logout();
        } else {
            gaeSecure.logout();
        }
    }

    @Override
    public boolean check(String profile) {
        if (Secure.getImpl().equals("basic")) {
            return basicSecure.check(profile);
        } else {
            return gaeSecure.check(profile);
        }
    }

    @Override
    public User getUser() {
        if (Secure.getImpl().equals("basic")) {
            return basicSecure.getUser();
        } else {
            return gaeSecure.getUser();
        }
    }
}
