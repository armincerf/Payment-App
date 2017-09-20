package com.ad436.webappsCW.jsf;

import java.io.Serializable;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 * Logs the user in or out
 *
 * @author alex
 */
@Named
@RequestScoped
public class LoginBean implements Serializable {

    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * If the given username and password are accepted, redirect to the
     * /users/index page else redirect to the error page
     *
     * @return
     */
    public String login() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
        try {
            //this method will actually check in the realm you configured in the web.xml file for the provided credentials
            request.login(this.username, this.password);
            //for testing
            //request.login("a", "a");
        } catch (ServletException e) {
            context.addMessage(null, new FacesMessage("Login failed:" + e));
            return "error";
        }
        System.out.println(request.getRequestURI());
        return "/faces/users/index.xhtml";
    }

    /**
     * logs out the user and redirects to the login page
     *
     * @return
     */
    public String logout() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        try {
            //this method will disassociate the principal from the session (effectively logging him/her out)
            request.logout();
            return "login";
        } catch (ServletException e) {
            context.addMessage(null, new FacesMessage("Logout failed."));
            return "login";
        }
    }
}
