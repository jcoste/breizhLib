package models;


import models.tag.LivreTag;
import models.tag.Tag;
import play.data.binding.As;
import play.data.validation.Required;
import play.modules.gae.GAE;
import siena.*;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

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

    public Livre(String titre, String editeur, String image, String iSBN) {
        this();
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
        return GAE.isLoggedIn();
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

    public static Livre findByISBN(String iSBN) {
        return initData(Livre.all(Livre.class).filter("iSBN", iSBN).get());
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
        return Commentaire.all(Commentaire.class).filter("livre", this).fetch();
    }

    public List<Tag> getTags() {
        List<Tag> tags = LivreTag.findByLivre(this);
        return tags;
    }

    public int getNote() {
        int nb = getCommentaires().size();


        if (nb > 0) {
            int val = 0;

            for (Commentaire comment : getCommentaires()) {
                comment.get();
                val += comment.note;
            }
            return val / nb;
        } else {
            return 0;
        }
    }
}


