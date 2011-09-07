package models;

import play.data.binding.As;
import siena.Model;

import java.util.Date;


public abstract class UpdatableModel extends Model implements  Updatable{

    @As("yyyy-MM-dd")
    public Date lastmaj;

    @Override
    public void insert() {
        lastmaj = new Date();
        super.save();
    }

    @Override
    public void update() {
        lastmaj = new Date();
        super.save();
    }

    @Override
    public void save() {
        lastmaj = new Date();
        super.save();
    }


    /**
     * lors de l'import,on ne doit pas modifier la date de modification de l'objet importé
     */
    public void saveImport() {
        if(lastmaj == null){
          lastmaj = new Date();
        }
        super.save();

    }

    public Date getLastMaj() {
        return lastmaj;
    }
}
