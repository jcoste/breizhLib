package models;

import play.data.binding.As;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import siena.*;

import java.util.Date;
import java.util.List;

@Table("Commentaire")
public class Commentaire extends Model {

    @Id(Generator.AUTO_INCREMENT)
    public Long id;

    @Required
    public String nom;
    @Required
    @MaxSize(10000)
    public String commentaire;

    @Max(value = 5)
    public int note;

    @Column("livre")
    public Livre livre;

    public User user;

    @As("yyyy-MM-dd")
    public Date dateAjout;

    public Commentaire(Livre livre,User user, String nom, String content,int note) {
        this.nom = nom;
        this.livre = livre;
        this.commentaire = content;
        this.dateAjout = new Date();
        this.user =user;
        this.note= note;
    }

    @Override
    public String toString() {
        return nom + " : " + commentaire;
    }

    public static List<Commentaire> findAll() {
        return Commentaire.all(Commentaire.class).fetch();
    }


}

