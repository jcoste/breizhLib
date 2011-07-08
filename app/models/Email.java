package models;


import play.data.validation.Required;
import siena.Generator;
import siena.Id;
import siena.Model;

import java.util.List;

@siena.Table("Email")
public class Email  extends Model {

    @Id(Generator.AUTO_INCREMENT)
    public Long id;

    @Required
    public String email;

    public Boolean valid;

    public User user;


    public Email(String email) {
           this.email = email;
    }

    public static Email find(String nom) {
        Email email = Email.all(Email.class).filter("email", nom).get();
        return email;
    }

    public static List<Email>  findByUser(User user) {
        List<Email> email = Email.all(Email.class).filter("user", user).fetch();
        return email;
    }

}
