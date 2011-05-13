import models.Livre;
import play.Logger;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;
 
@OnApplicationStart
public class Bootstrap extends Job {
 
    public void doJob() {
        Logger.info("bootStrap load data");
        // Check if the database is empty
        if(Livre.count() == 0) {
            Fixtures.load("initial-data.yml");
        }
    }
 
}