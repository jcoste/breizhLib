package controllers.security;


import models.User;

public class SecureAdapter implements ISecure {

    public static final SecureAdapter INSTANCE = new SecureAdapter();

    private static ISecure basicSecure = BasicSecure.INSTANCE;
    private static ISecure gaeSecure = GAESecure.INSTANCE;
    private static ISecure fbsecure = FBSecure.INSTANCE;
    private static ISecure tsecure = TwitterSecure.INSTANCE;


    @Override
    public void login() {
        if (Secure.getImpl().equals("basic")) {
            basicSecure.login();
        } else if(Secure.getImpl().equals("gae")) {
            gaeSecure.login();
        } else if(Secure.getImpl().equals("fbconnect")){
           fbsecure.login();
        } else{
            tsecure.login();
        }
    }

    @Override
    public void logout() {
        if (Secure.getImpl().equals("basic")) {
            basicSecure.logout();
        } else if(Secure.getImpl().equals("gae")){
            gaeSecure.logout();
        } else if(Secure.getImpl().equals("fbconnect")){
           fbsecure.logout();
        }  else{
            tsecure.logout();
        }
    }

    @Override
    public boolean check(String profile) {
        if (Secure.getImpl().equals("basic")) {
            return basicSecure.check(profile);
        } else if(Secure.getImpl().equals("gae")) {
            return gaeSecure.check(profile);
        } else if(Secure.getImpl().equals("fbconnect")){
           return fbsecure.check(profile);
        } else {
           return tsecure.check(profile);
        }
    }

    @Override
    public User getUser() {
        if (Secure.getImpl().equals("basic")) {
            return basicSecure.getUser();
        } else if(Secure.getImpl().equals("gae")) {
            return gaeSecure.getUser();
        } else if(Secure.getImpl().equals("fbconnect")){
           return fbsecure.getUser();
        } else {
           return tsecure.getUser();
        }
    }
}
