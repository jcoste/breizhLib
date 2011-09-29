package controllers;

import controllers.security.Secure;
import models.Version;
import models.socialoauth.Role;
import play.data.validation.Required;
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
    public static void save(@Required String version,@Required int versionCode, String backlog) {

        Version versiondb = Version.all(Version.class).filter("version", version).get();

        if (validation.hasErrors()) {
            renderArgs.put("params.version", version);
            renderArgs.put("params.versionCode", versionCode);
            renderArgs.put("params.backlog", backlog);
            render("Versions/add.html");
        }

        if (versiondb == null) {
            Version last = Version.all(Version.class).filter("last",true).get();
            last.last = false;
            last.save();

            versiondb = new Version(version, versionCode);
            versiondb.backlog = backlog;
            versiondb.last = true;
            versiondb.save();
        }
        AndroidAPI.android();

    }


}