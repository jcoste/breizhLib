package controllers;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.Transform;
import models.multioauth.Role;
import models.Picture;
import play.data.validation.Required;
import play.modules.router.Get;
import play.modules.router.Post;
import play.mvc.Controller;

import java.io.ByteArrayInputStream;
import java.util.List;


public class Pictures extends Controller {

    @Get("/shared/{file}")
    public static void getPicture(String file) {
        Picture picture = Picture.findByNname(file);
        //response.setContentTypeIfNotSet(picture.image.type());
        renderBinary(new ByteArrayInputStream(picture.image.getBytes()));
    }

    public static Picture createImage(byte[] bytes, String path, String iSBN, boolean resize) throws Exception {
        if (bytes != null) {
            Picture imageFile = Picture.findByNname(iSBN + ".jpg");
            if (imageFile == null) {
                imageFile = new Picture();
                imageFile.image = new Blob(bytes);
                imageFile.name = iSBN + ".jpg";
                imageFile.path = path;
                imageFile.insert();
            } else {
                //TODO �craser l'image
            }
            return imageFile;
        }
        return null;
    }

    public static void resize() {
        List<Picture> pictures = Picture.all(Picture.class).filter("path", "ouvrages/").fetch();

        for (Picture picture : pictures) {
            resizeImage(picture, 100, 133);
        }
        explore();
    }

    public static void resizeImage(Picture imageFile, int width, int heigth) {
        ImagesService imagesService = ImagesServiceFactory.getImagesService();

        Image oldImage = ImagesServiceFactory.makeImage(imageFile.image.getBytes());
        Transform resize = ImagesServiceFactory.makeResize(width, heigth);

        Image newImage = imagesService.applyTransform(resize, oldImage);

        byte[] newImageData = newImage.getImageData();

        imageFile.image = new Blob(newImageData);
        imageFile.update();
    }

    @Get("/explorer")
    public static void explore() {
        List<Picture> pictures = Picture.all(Picture.class).order("path").fetch();
        render(pictures);
    }


    @Role("admin")
    @Post("/picture/save")
    public static void save(@Required String name, @Required String path, byte[] imageFile) throws Exception {
        if (validation.hasErrors()) {
            render("Pictures/explore.html");
        }
        String image = null;
        if (imageFile != null) {
            Picture picture = Pictures.createImage(imageFile, path, name, true);
            image = picture.getUrl();
        }
        explore();
    }
}
