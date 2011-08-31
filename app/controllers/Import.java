package controllers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import controllers.security.Secure;
import models.Livre;
import models.socialoauth.Role;
import play.modules.router.Get;
import play.mvc.Controller;
import play.mvc.With;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.Collection;
import java.util.List;

@With(Secure.class)
public class Import extends Controller {

    //TODO a ajouter dans les propriétés
    public static final String SERVER_URL = "http://breizh-lib.appspot.com/";

    @Role("admin")
    @Get(value = "/import/books.json", format = "json")
    public static void books() {
        try {
            URL url = new URL(SERVER_URL + "/export/books.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer sb = new StringBuffer();
            String line = reader.readLine();
            sb.append(line);
            while (line != null) {
              line = reader.readLine();
                sb.append(line);
            }


            Gson gson = new Gson();
            Type collectionType = new TypeToken<Collection<Livre>>(){}.getType();
            Collection<Livre> livres = (List<Livre>) gson.fromJson(sb.toString(),collectionType);
            renderText(livres.size());
        } catch (Exception ex) {

        }
        renderText("");
    }

    @Role("admin")
    @Get(value = "/import/commentaires.json", format = "json")
    public static void all() {
        renderText("");
    }

    @Role("admin")
    @Get("/import/users.json")
    public static void users() {
        renderText("");
    }


    @Role("admin")
    @Get(value = "/import/reservations.json", format = "json")
    public static void allJson() {
        renderText("");
    }
}
