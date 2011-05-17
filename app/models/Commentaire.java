package models;

import play.data.binding.As;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import siena.Generator;
import siena.Id;
import siena.Model;
import siena.Table;

import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import java.util.Date;

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

    @ManyToOne
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
}

