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

    public static String createImage(File imageFile, String iSBN, boolean resize) throws Exception {
        Logger.info("Utils.createImage");
        if (imageFile != null) {
            if (Play.mode.isDev()) {
                if (Play.getFile("/public/shared/").exists()) {
                    Play.getFile("/public/shared/").mkdir();
                }
                File newFile = Play.getFile("/public/shared/" + iSBN + "." + imageFile.getName().substring(imageFile.getName().lastIndexOf('.') + 1));
                if (resize) {
                    Images.resize(imageFile, newFile, 100, 133);
                } else {
                    imageFile.renameTo(newFile);
                }
                return "/public/shared/" + newFile.getName();
            } else {
                Logger.info("PROD create image /shared/");
                FileInputStream is = new FileInputStream(imageFile);

                byte[] data = getBytes(is);
                Blob blobData = null;
                if (data.length > 0) {
                    blobData = new Blob(data);
                }
                Picture image = new Picture();
                image.image = blobData;
                image.name = iSBN + "." + imageFile.getName().substring(imageFile.getName().lastIndexOf('.') + 1);
                image.insert();
                Logger.info("/shared/" + image.name);
                return "/shared/" + image.name;
            }
        }
        return null;
    }

    public static byte[] getBytes(InputStream is) throws IOException {

        int len;
        int size = 1024;
        byte[] buf;

        if (is instanceof ByteArrayInputStream) {
            size = is.available();
            buf = new byte[size];
            len = is.read(buf, 0, size);
        } else {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            buf = new byte[size];
            while ((len = is.read(buf, 0, size)) != -1)
                bos.write(buf, 0, len);
            buf = bos.toByteArray();
        }
        return buf;
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
