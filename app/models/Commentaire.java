package models;

import play.data.binding.As;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import siena.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Table("Commentaire")
public class Commentaire extends Model {

    @Id(Generator.AUTO_INCREMENT)
    public Long id;

    public String uid;

    @Required
    public String nom;
    @Required
    @MaxSize(10000)
    public String commentaire;

    @Max(value = 5)
    public int note;

    @Column("livre")
    public Livre livre;

    @Column("user")
    public User user;

    @As("yyyy-MM-dd")
    public Date dateAjout;

    public Commentaire(Livre livre, User user, String nom, String content, int note) {
        this.nom = nom;
        this.livre = livre;
        this.commentaire = content;
        this.dateAjout = new Date();
        this.user = user;
        this.note = note;
    }

    @Override
    public String toString() {
        return nom + " : " + commentaire;
    }

    public String getUid() {
        if (uid == null) {
            uid = "C" + id;
        }
        return uid;
    }

    public static List<Commentaire> findAll() {
        return Commentaire.all(Commentaire.class).fetch();
    }

    public static Commentaire findByUID(String uid) {
        return Commentaire.all(Commentaire.class).filter("uid", uid).get();
    }


    public static List<Commentaire> findLike(String recherche) {
        List<Commentaire> allCommetairess = findAll();
        List<Commentaire> commentaires = new ArrayList<Commentaire>();
        for (Commentaire commentaire : allCommetairess) {
            if (commentaire.commentaire.toLowerCase().contains(recherche.toLowerCase()) ||
                    commentaire.nom.toLowerCase().contains(recherche.toLowerCase())) {
                commentaires.add(commentaire);
            }
        }
        return commentaires;
    }
}

