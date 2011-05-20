package models;

import play.data.binding.As;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import siena.*;

import javax.persistence.Lob;
import java.util.Date;
import java.util.List;

@Table("Commentaire")
public class Commentaire extends Model {

    @Id(Generator.AUTO_INCREMENT)
    public Long id;

    @Required
    public String nom;
    @Lob
    @Required
    @MaxSize(10000)
    public String commentaire;

    @Column("livre")
    public Livre livre;

    @As("yyyy-MM-dd")
    public Date dateAjout;

    public Commentaire(Livre livre, String nom, String content) {
        this.nom = nom;
        this.livre = livre;
        this.commentaire = content;
        this.dateAjout = new Date();
    }

    @Override
    public String toString() {
        return nom + " : " + commentaire;
    }

    public static List<Commentaire> findAll() {
        return Commentaire.all(Commentaire.class).fetch();
    }


}

