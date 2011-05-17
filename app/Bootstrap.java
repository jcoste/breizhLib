import play.Logger;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;

@OnApplicationStart
public class Bootstrap extends Job {

    public void doJob() {
        //if(  Play.mode.name() == "DEV") {
        Logger.info("bootStrap load data (DEV)");
        Fixtures.load("initial-data.yml");
        // }
    }

}