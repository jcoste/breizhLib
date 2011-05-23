package models;

import play.data.validation.Required;
import siena.Generator;
import siena.Id;
import siena.Model;

import java.util.List;

@siena.Table("Editeur")
public class Editeur extends Model {


    @Id(Generator.AUTO_INCREMENT)
    public Long id;

    @Required
    public String nom;

    public String site;

    public String image;

    public Editeur(String nom, String site) {
        super();
        this.nom = nom;
        this.site = site;
    }


    public static Editeur findByNom(String nom) {
        return Editeur.all(Editeur.class).filter("nom", nom).get();
    }

    public static List<Editeur> findAll() {
        return Editeur.all(Editeur.class).order("nom").fetch();
    }
}
