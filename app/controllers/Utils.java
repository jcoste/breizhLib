package controllers;


import play.Play;
import play.libs.Images;

import java.io.File;

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

    public static String createImage(File imageFile,String iSBN){
      if(imageFile != null){
            File newFile= Play.getFile("/public/shared/" + iSBN + "." + imageFile.getName().substring(imageFile.getName().lastIndexOf('.') + 1));
            Images.resize(imageFile, newFile, 100, 133);
            return "/public/shared/"+newFile.getName();
        }
        return null;
    }
}
