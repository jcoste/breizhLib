package models;


import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "LIVRE")
public class Livre extends Model {

    public String titre;
    public String editeur;
    public String description;
    public String image;
    public String iSBN;

    public Livre(String titre,String editeur,String description,String image,String iSBN) {
        this.description = description;
        this.editeur = editeur;
        this.image = image;
        this.iSBN = iSBN;
    }
}
