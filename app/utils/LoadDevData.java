package utils;

import models.Commentaire;
import models.Editeur;
import models.Livre;
import models.User;
import play.Logger;
import play.libs.Crypto;

import java.util.Date;


public class LoadDevData {

    public static void doJob() {
        if (Livre.findAll().size() == 0) {
            Logger.info("load data DEV");
            Editeur editeur = new Editeur("PEARSON", "http://www.pearson.fr/");
            editeur.image = "http://www.breizhjug.org/_/rsrc/1260802482922/bibliotheque/Logo%20pearson%202008.png?height=108&width=320";
            editeur.insert();

            Livre livre = new Livre("Apache Maven", "PEARSON", "http://www.pearson.fr/Resources/titles/27440100730370/Images/27440100730370L.gif", "978-2-7440-2337-8");
            livre.insert();
            livre.addTag("Maven");

            livre = new Livre("Coder Proprement", "PEARSON", "http://www.pearson.fr/Resources/titles/27440100643800/Images/27440100643800L.gif", "978-2-7440-2327-9");
            livre.insert();

            livre = new Livre("Programmation concurrente en Java", "PEARSON", "http://www.pearson.fr/Resources/titles/27440100754850/Images/27440100754850L.gif", "978-2-7440-2333-0");
            livre.insert();
            livre.addTag("Java");

            livre = new Livre("Développement d'applications professionnelles avec Android 2", "PEARSON", "http://www.pearson.fr/Resources/titles/27440100948550/Images/27440100948550L.gif", "978-2-7440-2452-8");
            livre.insert();
            livre.addTag("Java");
            livre.addTag("Android");

            User devAdminUser = new User("admin@breizhlib.org", null);
            devAdminUser.isAdmin = true;
            devAdminUser.isPublic = true;
            devAdminUser.dateConnexion = new Date();
            devAdminUser.dateCreation = new Date();
            devAdminUser.password = Crypto.passwordHash("1234");
            devAdminUser.nom = "Guernion";
            devAdminUser.prenom = "Sylvain";
            devAdminUser.save();


            Commentaire commentaire = new Commentaire(livre, devAdminUser, "Guernion Sylvain", "test commentaire", 5);
            commentaire.insert();
            livre.popularite = livre.getCommentaires().size();
            livre.update();
        }
    }
}
