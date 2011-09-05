package models;


import controllers.security.Secure;
import models.tag.LivreTag;
import models.tag.Tag;
import models.tag.Taggable;
import play.data.binding.As;
import play.data.validation.Required;
import siena.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@siena.Table("Livre")
public class Livre extends Model implements Taggable {

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

    public Integer popularite = 0;

    public Boolean preview;

    @Column("reservationEncours")
    public Reservation reservationEncours;

    public transient boolean isNotPresent;


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
     * TODO retourne 'true' si l'utilisateur à emprunté ce livre
     *
     * @return
     */
    public boolean hasRead() {
        return Secure.getUser() != null;
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
        return Reservation.all(Reservation.class).filter("emprunt", this).filter("isAnnuler", false).fetch();
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

    public void addTag(String tag) {
        Tag newTag = Tag.findOrCreateByName(tag);
        LivreTag livreTag = LivreTag.findByTagAndLivre(newTag, this);
        if (livreTag == null) {
            livreTag = new LivreTag(this, newTag);
            livreTag.insert();
        }
    }

    public static List<Livre> findLikeTitre(String text) {
        List<Livre> allLivres = findAll();
        List<Livre> livres = new ArrayList<Livre>();
        for (Livre livre : allLivres) {
            if (livre.titre.toLowerCase().contains(text.toLowerCase())) {
                livres.add(livre);
            }
        }
        return livres;
    }
}


