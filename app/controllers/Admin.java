package controllers;

import controllers.security.Secure;
import models.socialoauth.Role;
import play.modules.router.Get;
import play.mvc.Controller;
import play.mvc.With;

@Role("admin")
@With(Secure.class)
public class Admin extends Controller {


    @Get("/admin")
    public static void index() {
        render();
    }

}