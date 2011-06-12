package controllers.security;


import models.User;

public class SecureAdapter implements ISecure {

    public static final SecureAdapter INSTANCE = new SecureAdapter();

    private static ISecure basicSecure = BasicSecure.INSTANCE;
    private static ISecure gaeSecure = GAESecure.INSTANCE;
    private static ISecure fbsecure = FBSecure.INSTANCE;


    @Override
    public void login() {
        if (Secure.getImpl().equals("basic")) {
            basicSecure.login();
        } else if(Secure.getImpl().equals("gae")) {
            gaeSecure.login();
        } else {
           fbsecure.login();
        }
    }

    @Override
    public void logout() {
        if (Secure.getImpl().equals("basic")) {
            basicSecure.logout();
        } else if(Secure.getImpl().equals("gae")){
            gaeSecure.logout();
        } else {
           fbsecure.logout();
        }
    }

    @Override
    public boolean check(String profile) {
        if (Secure.getImpl().equals("basic")) {
            return basicSecure.check(profile);
        } else if(Secure.getImpl().equals("gae")) {
            return gaeSecure.check(profile);
        } else {
           return fbsecure.check(profile);
        }
    }

    @Override
    public User getUser() {
        if (Secure.getImpl().equals("basic")) {
            return basicSecure.getUser();
        } else if(Secure.getImpl().equals("gae")) {
            return gaeSecure.getUser();
        } else {
           return fbsecure.getUser();
        }
    }
}
