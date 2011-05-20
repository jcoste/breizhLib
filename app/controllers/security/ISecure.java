package controllers.security;


import models.User;

public interface ISecure {
    void login();
    void logout();
    boolean check(String profile);
    User getUser();
}
