package controllers;

import com.google.appengine.api.datastore.Blob;
import models.Picture;
import play.Logger;
import play.mvc.Controller;

import java.io.ByteArrayInputStream;


public class Pictures extends Controller {


    public static void getPicture(String file) {
        Picture picture = Picture.findByNname(file);
        //response.setContentTypeIfNotSet(picture.image.type());
        renderBinary(new ByteArrayInputStream(picture.image.getBytes()));
    }

    public static String createImage(byte[] bytes, String iSBN, boolean resize) throws Exception {
        if (bytes != null) {
            Picture imageFile = new Picture();
            imageFile.image = new Blob(bytes);
            imageFile.name = iSBN + ".jpg";
            imageFile.insert();
            return "/shared/" + imageFile.name;
        }
        return null;
    }
}
