package controllers;

import com.google.appengine.api.datastore.Blob;
import models.Picture;
import play.mvc.Controller;

import java.io.ByteArrayInputStream;
import java.util.List;


public class Pictures extends Controller {


    public static void getPicture(String file) {
        Picture picture = Picture.findByNname(file);
        //response.setContentTypeIfNotSet(picture.image.type());
        renderBinary(new ByteArrayInputStream(picture.image.getBytes()));
    }

    public static String createImage(byte[] bytes,String path, String iSBN, boolean resize) throws Exception {
        if (bytes != null) {
            Picture imageFile = new Picture();
            imageFile.image = new Blob(bytes);
            imageFile.name = iSBN + ".jpg";
            imageFile.path = path;
            imageFile.insert();
            return "/shared/"+imageFile.path + imageFile.name;
        }
        return null;
    }


     public static void explore() {
        List<Picture> pictures = Picture.all(Picture.class).order("path").fetch();
        render(pictures);
    }
}
