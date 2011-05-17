import models.Livre;
import play.Logger;
import play.Play;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;
 
@OnApplicationStart
public class Bootstrap extends Job {
 
    public void doJob() {
        if(Livre.count() == 0 &&  Play.mode.name() == "DEV") {
            Logger.info("bootStrap load data (DEV)");
            Fixtures.load("initial-data.yml");
        }
    }
 
}