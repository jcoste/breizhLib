package models;

import play.data.binding.As;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
public class Commentaire extends Model {

    @Required
    public String nom;
    @Lob
    @Required
    @MaxSize(10000)
    public String commentaire;

    @ManyToOne
    public Livre livre;

    @As("yyyy-MM-dd")
    public Date dateAjout;

    public Commentaire(Livre livre,String nom, String content) {
        this.nom = nom;
        this.livre = livre;
        this.commentaire = content;
        this.dateAjout = new Date();
    }

    @Override
    public String toString() {
        return nom + " : " + commentaire;
    }
}

