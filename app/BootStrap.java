import models.User;
import play.Logger;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

@OnApplicationStart
public class BootStrap extends Job {

    public void doJob() {
        // Check if the database is empty
        if (User.all(User.class).count() == 0) {
            Logger.info("[DEV mode] load data");
            User admin = new User("admin");
            admin.email = "admin";
            admin.nom = "Admin";
            admin.insert();
        }
    }

}