package controllers;

import controllers.security.Secure;
import models.socialoauth.Role;
import play.modules.router.Get;
import play.modules.router.Post;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Versions extends Controller {

    @Role("admin")
    @Get("/android/add")
    public static void add() {
        render();
    }

    @Role("admin")
    @Post("/version/save")
    public static void save() {
        add();
    }


}