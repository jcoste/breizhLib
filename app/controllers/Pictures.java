package controllers;

import com.google.appengine.api.datastore.Blob;
import models.Picture;
import models.socialoauth.Role;
import play.data.validation.Required;
import play.libs.Images;
import play.modules.router.Get;
import play.modules.router.Post;
import play.mvc.Controller;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;


public class Pictures extends Controller {

    @Get("/shared/{file}")
    public static void getPicture(String file) {
        Picture picture = Picture.findByNname(file);
        //response.setContentTypeIfNotSet(picture.image.type());
        if(picture != null && picture.image != null) {
            renderBinary(new ByteArrayInputStream(picture.image.getBytes()));
        }
        else
        {
            // TODO not found image
        }
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
                imageFile.image = new Blob(bytes);
                imageFile.update();
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
        File old = null;
        File newImage = new File("tmp.jpg");
        try {
            old = new File(new URI(imageFile.getUrl()));
        } catch (URISyntaxException e) {
            error("erreur lors de la création de l'image");
        }

        Images.resize(old, newImage, width, heigth);
        byte[] bytes = new byte[(int) newImage.length()];

        InputStream is = null;
        try {
            is = new FileInputStream(newImage);
            int offset = 0;
            int numRead = 0;
            while (offset < bytes.length
                    && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }
            if (offset < bytes.length) {
                throw new IOException("Could not completely read file " + newImage.getName());
            }
             is.close();
        } catch (FileNotFoundException e) {
            error("erreur lors de la création de l'image");
        } catch (IOException e) {
            error("erreur lors de la création de l'image");
        }

        imageFile.image = new Blob(bytes);
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
