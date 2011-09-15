package models;


import play.data.validation.Required;
import siena.Generator;
import siena.Id;
import siena.Model;

@siena.Table("Faq")
public class Faq extends Model {

    @Id(Generator.AUTO_INCREMENT)
    public Long id;

    @Required
    public String question;

    public String reponse;


    public Faq(String question, String reponse) {
        this.question = question;
        this.reponse = reponse;
    }
}
