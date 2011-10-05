package controllers;

import controllers.security.Secure;
import models.User;
import play.Logger;
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

    @Get("/")
    public static void index() {
        Logger.info(params.get("action"));
        Livres.last();
    }

    @Get("/contact")
    public static void contact() {
        index();
    }

    @Get("/init")
    public static void initDev() {
        if (Play.mode.equals(Play.Mode.DEV)) {
            LoadDevData.doJob();
        }
        index();
    }

    public static void empty() {
        render();
    }

    public static void informations() {
        if (((User) Secure.getUser()).email == null) {
            Users.edit();
        } else {
            index();
        }
    }

}