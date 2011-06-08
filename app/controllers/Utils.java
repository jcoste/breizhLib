package controllers;


import com.google.appengine.api.datastore.Blob;
import models.Editeur;
import models.Picture;
import play.Logger;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static String createImage(byte[] bytes, String iSBN, boolean resize) throws Exception {
        Logger.info("Utils.createImage");
        if (bytes != null) {
            Picture imageFile = new Picture();
            imageFile.image = new Blob(bytes);
            imageFile.name = iSBN + ".jpg";
            imageFile.insert();
            return "/shared/" + imageFile.name;
        }
        return null;
    }

    public static List<String> initListEditeurs() {
        List<Editeur> allEditeurs = Editeur.all(Editeur.class).order("nom").fetch();
        List<String> editeurs = new ArrayList<String>();
        for (Editeur pEditeur : allEditeurs) {
            editeurs.add(pEditeur.nom);
        }
        return editeurs;
    }

}
