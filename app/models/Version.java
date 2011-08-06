package models;


import play.data.validation.Required;
import siena.Generator;
import siena.Id;
import siena.Model;

@siena.Table("Version")
public class Version extends Model {

    @Id(Generator.AUTO_INCREMENT)
    public Long id;

    @Required
    public String version;



    public Version(String version) {
           this.version = version;
    }

    public static Version find() {
        Version version = Version.all(Version.class).get();
        return version;
    }

    @Override
    public String toString() {
        return version;
    }
}
