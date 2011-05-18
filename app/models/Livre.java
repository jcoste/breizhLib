package models;


import play.data.binding.As;
import play.data.validation.Required;
import siena.*;

import javax.persistence.Lob;
import java.util.Date;
import java.util.List;

@siena.Table("Livre")
public class Livre extends Model {

    @Id(Generator.AUTO_INCREMENT)
    public Long id;

    @Required
    @Column("titre")
    public String titre;
    @Required
    @Column("editeur")
    public String editeur;
    @Lob
    public String description;
    @Required
    @Column("image")
    public String image;
    @Required
    @Column("iSBN")
    @Index("iSBN_index")
    public String iSBN;

    @As("yyyy-MM-dd")
    public Date dateAjout;

    public String etat;

    @Column("reservationEncours")
    public Reservation reservationEncours;


    public Livre() {
        super();
        setEtat(EtatLivre.DISP0NIBLE);
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

    /**
     * retourne 'true' si l'utilisateur à emprunté ce livre
     *
     * @return
     */
    public boolean hasRead() {
        // TODO Long userId
        return true;
    }

    public String toString() {
        return titre;
    }

    public Long getId() {
        return id;
    }

    public static Livre findById(Long bookId) {
        return initData(Livre.all(Livre.class).filter("id", bookId).get());
    }

    public static Livre initData(Livre livre) {
        if (livre != null) {
            if (livre.reservationEncours != null) {
                livre.reservationEncours.get();
            }
        }
        return livre;
    }

    public static List<Livre> findAll() {
        return Livre.all(Livre.class).fetch();
    }

    public void setEtat(EtatLivre etat) {
        this.etat = etat.getClasseCss();
    }

    public EtatLivre getEtat() {
        return EtatLivre.fromCss(this.etat);
    }

    public List<Reservation> getHistoriqueReservation() {
        return Reservation.all(Reservation.class).filter("emprunt", this).fetch();
    }

    public List<Commentaire> getCommentaires() {
        return Commentaire.all(Commentaire.class).filter("livre",this).fetch();
    }
}


