package controllers.security;

import models.User;
import play.libs.OAuth2;

/**
 * Created by IntelliJ IDEA.
 * User: sylvain
 * Date: 09/06/11
 * Time: 19:13
 * To change this template use File | Settings | File Templates.
 */
public class FBSecure implements ISecure {

    public static final FBSecure INSTANCE = new FBSecure();

    private static final String authorizationURL  ="";
    private static final String accessTokenURL ="";
    private static final String clientid = "";
    private static final String secret ="";

    private OAuth2 oauth;


    private FBSecure(){
         oauth = new OAuth2(authorizationURL,accessTokenURL,clientid,secret);
    }

    @Override
    public void login() {
        //To change body of implemented methods use File | Settings | File Templates.
        oauth.
    }

    @Override
    public void logout() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean check(String profile) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public User getUser() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
