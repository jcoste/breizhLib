package controllers;


import com.google.appengine.api.datastore.Blob;
import models.Editeur;
import models.Picture;
import play.Logger;
import play.Play;
import play.libs.Images;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static int pagination(int page, int max, int nbParPage) {
        if (page < 0) {
            page = 0;
        }

        int dept = nbParPage;
        int debut = (page * dept);
        if (debut >= max) {
            debut = max - (max - dept) / dept;
            page = debut / dept;
        }
        return debut;
    }

    public static String createImage(byte[] bytes, String iSBN, boolean resize) throws Exception {
        Logger.info("Utils.createImage");
        if (bytes != null) {
                Logger.info("PROD create image /shared/"+bytes);
                Picture imageFile = new Picture();
                imageFile.image = new Blob(bytes);
                imageFile.name = iSBN + ".jpg" ;
                imageFile.insert();
                Logger.info("/shared/" + imageFile.name);
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
