package models;


import play.data.validation.Required;
import siena.Column;
import siena.Generator;
import siena.Id;
import siena.Model;

@siena.Table("Version")
public class Version extends Model {

    @Id(Generator.AUTO_INCREMENT)
    public Long id;

    @Required
    public String version;

    @Required
    public int versionCode;

    @Column(value = "backlog")
    public String backlog;

    public boolean last;

    public String serverUrl;


    public Version(String version, int code) {
        this.version = version;
        this.versionCode = code;
    }

    public static Version find() {
        Version version = Version.all(Version.class).filter("last", true).get();
        return version;
    }

    @Override
    public String toString() {
        return version;
    }
}
