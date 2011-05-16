package models;


import play.data.binding.As;
import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Livre extends Model {

    @Required
    public String titre;
    @Required
    public String editeur;
    @Lob
    public String description;
    @Required
    public String image;
    @Required
    public String iSBN;

    @As("yyyy-MM-dd")
    public Date dateAjout;

    @OneToMany(mappedBy = "livre",cascade = CascadeType.ALL)
    public List<Commentaire> commentaires;

    public Livre() {
        this.commentaires = new ArrayList<Commentaire>();
    }

    public Livre(String titre, String editeur, String image, String description, String iSBN) {
        this();
        this.description = description;
        this.titre = titre;
        this.editeur = editeur;
        this.image = image;
        this.iSBN = iSBN;
        this.dateAjout = new Date();
    }

    public void addComment(String nom, String content) {
        Commentaire commentaire = new Commentaire(this,nom, content);
        commentaire.create();
        this.commentaires.add(commentaire);
    }

    /**
     * retourne 'true' si l'utilisateur à emprunté ce livre
     * @return
     */
    public boolean hasRead(){
        // TODO Long userId
        return true;
    }

    public String toString() {
        return titre;
    }

    public Date getDateAjout() {
        return dateAjout;
    }

    public void setDateAjout(Date dateAjout) {
        this.dateAjout = dateAjout;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getEditeur() {
        return editeur;
    }

    public void setEditeur(String editeur) {
        this.editeur = editeur;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getiSBN() {
        return iSBN;
    }

    public void setiSBN(String iSBN) {
        this.iSBN = iSBN;
    }
}


