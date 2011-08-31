package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import controllers.security.Secure;
import models.Livre;
import models.socialoauth.Role;
import play.modules.router.Get;
import play.mvc.Controller;
import play.mvc.With;
import serializers.LivreSerializer;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@With(Secure.class)
public class Import extends Controller {

    //TODO a ajouter dans les propriétés
    public static final String SERVER_URL = "http://breizh-lib.appspot.com/";

    @Role("admin")
    @Get(value = "/import/books.json", format = "json")
    public static void books() {
        try {
            Type type = new TypeToken<Livre>(){}.getType();
            Gson gson = new GsonBuilder().registerTypeAdapter(type,new LivreSerializer()).create();
            URL url = new URL(SERVER_URL + "export/books.json");
            JsonReader reader = new JsonReader(new InputStreamReader(url.openStream()));
            reader.beginArray();
            List<Livre> livres = new ArrayList<Livre>();
              while (reader.hasNext()) {
                livres.add(gson.<Livre>fromJson(reader,type));
              }
              reader.endArray();
            renderText(livres.size());
        } catch (Exception ex) {
            renderText("KO"+ex);
        }
        renderText("OK");
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
