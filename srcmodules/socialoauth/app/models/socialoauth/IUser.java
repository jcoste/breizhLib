package models.socialoauth;


import java.util.Date;

public interface IUser {

    boolean isActif();

    void setActif(boolean actif);

    boolean isAdmin();

    void setAdmin(boolean admin);

    Date getDateConnexion();

    void setDateConnexion(Date dateConnexion);

    String getEmail();

    void save();
}
