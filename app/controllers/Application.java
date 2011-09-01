package controllers;

import controllers.security.Secure;
import models.Livre;
import models.User;
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

    public static final String API_CODE = "1234";

    @Get("/")
    public static void index() {
        if (Secure.getUser() != null) {
            Livres.index(0,"date");
        }
        List<Livre> livres = Livre.all(Livre.class).fetch();
        render(livres);
    }

    @Get("/contact")
    public static void contact(){
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
        if (((User)Secure.getUser()).email == null) {
            Users.edit();
        } else {
            Application.index();
        }
    }

}