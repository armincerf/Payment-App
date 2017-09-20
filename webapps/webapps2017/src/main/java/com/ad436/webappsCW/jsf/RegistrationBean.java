package com.ad436.webappsCW.jsf;

import com.ad436.webappsCW.ejb.UserService;
import com.ad436.webappsCW.entity.SystemUser;
import java.util.List;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 * Registers a user or admin
 *
 * @author ad436
 */
@Named
@RequestScoped
@DeclareRoles({"Admin", "User"})
public class RegistrationBean {

    @EJB
    UserService usrSrv;

    String username;
    String userpassword;
    String name;
    String surname;
    String currency;

    public RegistrationBean() {

    }

    /**
     * If the user doesn't already exist, register them
     *
     * @return
     */
    public String register() {
        if (!usrSrv.userExist(username)) {
            usrSrv.registerUser(username, userpassword, name, surname, currency);
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("User already exists"));
            return "registration";
        }
        return "login";
    }

    /**
     * if the username doesn't already exist, register them as an admin
     *
     * @return
     */
    public String registerAdmin() {
        if (!usrSrv.userExist(username)) {
            usrSrv.registerAdminUser(username, userpassword, name, surname, currency);
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("User already exists"));
            return "users";
        }
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("User registered"));
        return "users";
    }

    @RolesAllowed("Admin")
    public List<SystemUser> getRegisteredUsers() {
        return usrSrv.getAllUsers();
    }

    public UserService getUsrSrv() {
        return usrSrv;
    }

    public void setUsrSrv(UserService usrSrv) {
        this.usrSrv = usrSrv;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserpassword() {
        return userpassword;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setUserpassword(String userpassword) {
        this.userpassword = userpassword;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

}
