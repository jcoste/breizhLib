package models;


import com.google.appengine.api.datastore.Blob;
import siena.Generator;
import siena.Id;
import siena.Model;
import siena.Table;

@Table("image")
public class Picture extends Model {

    @Id(Generator.AUTO_INCREMENT)
    public Long id;

    public Blob image;
    public String name;

    public static Picture findByNname(String file) {
        return Picture.all(Picture.class).filter("name", file).get();
    }
}
