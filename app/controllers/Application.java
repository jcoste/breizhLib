package controllers;

import controllers.security.Secure;
import models.Livre;
import play.Play;
import play.modules.router.Get;
import play.modules.router.ServeStatic;
import play.modules.router.StaticRoutes;
import play.mvc.Controller;
import play.mvc.With;
import utils.LoadDevData;

import java.util.List;

@With(Secure.class)
@StaticRoutes({
        @ServeStatic(value = "/public/", directory = "public"),
        @ServeStatic(value = "/images/", directory = "images")
})
public class Application extends Controller {

    @Get("/")
    public static void index() {
        if (Secure.getUser() != null) {
            Livres.index(0);
        }
        List<Livre> livres = Livre.all(Livre.class).fetch();
        render(livres);
    }

    @Get("/init")
    public static void initDev() {
        if (Play.mode.equals(Play.Mode.DEV)) {
            LoadDevData.doJob();
        }
        index();
    }

}