package controllers.multioauth;

import models.multioauth.IUser;


public interface UserManagement {

    IUser getByUsername(String username);

    IUser getByEmail(String email);

    IUser createUser(String email,String username);
}
