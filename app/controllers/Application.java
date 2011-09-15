package controllers;

import controllers.security.Secure;
import models.User;
import play.Play;
import play.modules.router.Get;
import play.modules.router.ServeStatic;
import play.modules.router.StaticRoutes;
import play.mvc.Controller;
import play.mvc.With;
import utils.LoadDevData;

@With(Secure.class)
@StaticRoutes({
        @ServeStatic(value = "/public/", directory = "public"),
        @ServeStatic(value = "/images/", directory = "images")
})
public class Application extends Controller {


    @Get("/contact")
    public static void contact() {
        Livres.last();
    }

    @Get("/init")
    public static void initDev() {
        if (Play.mode.equals(Play.Mode.DEV)) {
            LoadDevData.doJob();
        }
        Livres.last();
    }

    public static void empty() {
        render();
    }

    public static void informations() {
        if (((User) Secure.getUser()).email == null) {
            Users.edit();
        } else {
            Livres.last();
        }
    }

}