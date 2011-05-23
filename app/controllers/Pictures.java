package controllers;

import models.Picture;
import play.mvc.Controller;

import java.io.ByteArrayInputStream;


public class Pictures extends Controller {


    public static void getPicture(String file) {
        Picture picture = Picture.findByNname(file);
        //response.setContentTypeIfNotSet(picture.image.type());
        renderBinary( new ByteArrayInputStream(picture.image.getBytes()));
    }
}
