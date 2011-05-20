package controllers;

import controllers.security.Role;
import controllers.security.Secure;
import play.Logger;
import play.mvc.Controller;
import play.mvc.With;


@With(Secure.class)
public class Admin extends Controller {

    @Role("admin")
    public static void index() {
       Logger.info("admin page");
       Application.index();
    }
}