package models;


import play.data.validation.Required;
import siena.*;

import java.util.List;
import java.util.UUID;

@Table("Livre")
public class Serveur extends Model {

    @Id(Generator.AUTO_INCREMENT)
    public Long id;

    @Required
    @Column("name")
    public String name;
    @Required
    @Column("url")
    public String url;
    @Required
    @Column("code")
    public String code;
    @Required
    @Column("type")
    public ServerType type;

    public boolean defaut;


    public Serveur() {
        super();
    }

    public Serveur(String name, String url, String code, ServerType type) {
        this();
        this.name = name;
        this.url = url;
        if (type.equals(ServerType.EXPORT)) {
            this.code = generateCode();
        }
        this.type = type;

    }

    public String generateCode() {
        return UUID.randomUUID().toString();
    }


    public Long getId() {
        return id;
    }

    public static List<Serveur> findAll() {
        return Serveur.all(Serveur.class).fetch();
    }

    public static Serveur findByType(ServerType type) {
        return Serveur.all(Serveur.class).filter("type", type).get();
    }

    public static Serveur findDefaut() {
        return Serveur.all(Serveur.class).filter("type", ServerType.IMPORT).filter("defaut", true).get();
    }
}


