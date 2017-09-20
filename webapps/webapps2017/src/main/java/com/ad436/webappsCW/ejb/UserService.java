package com.ad436.webappsCW.ejb;

import com.ad436.webappsCW.entity.SystemUser;
import com.ad436.webappsCW.entity.SystemUserGroup;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 * Retrieves and stores data in the users database
 *
 * @author ad436
 */
@Stateless
public class UserService {

    @PersistenceContext
    EntityManager em;
    @EJB
    TransactionService ts;

    public UserService() {
    }

    /**
     * Attempts to access the given username, if nothing returned, returns false
     *
     * @param username
     * @return true if the user exists in the database, otherwise false
     */
    public boolean userExist(String username) {
        try {
            em.createNamedQuery("findAllUsers")
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (NoResultException e) {
            return false;
        }
        return true;

    }

    /**
     * lists all registered users
     *
     * @return a list of all SystemUser rows
     */
    public List<SystemUser> getAllUsers() {
        try {
            List<SystemUser> temp = em.createNamedQuery("getAllUsers")
                    .getResultList();
            return temp;
        } catch (NoResultException e) {
            return null;
        }

    }

    /**
     * Registers a new user in the database with balance set to £1000
     *
     * @param username
     * @param userpassword
     * @param name
     * @param surname
     * @param currency Accepts either "GBP", "USD" or "EUR"
     */
    public void registerUser(String username, String userpassword, String name, String surname, String currency) {
        try {
            SystemUser sys_user;
            SystemUserGroup sys_user_group;

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String passwd = userpassword;
            md.update(passwd.getBytes("UTF-8"));
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < digest.length; i++) {
                sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
            }
            String paswdToStoreInDB = sb.toString();
            BigDecimal amount = new BigDecimal(ts.getConversion("GBP", currency, new BigDecimal("1000")).substring(3));

            sys_user = new SystemUser(username, paswdToStoreInDB, name, surname, currency, amount); //Store £1000 worth of the users currency
            sys_user_group = new SystemUserGroup(username, "users");

            em.persist(sys_user);
            em.persist(sys_user_group);

        } catch (UnsupportedEncodingException | NoSuchAlgorithmException ex) {
            Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Registers a new admin user in the database with balance set to £1000
     *
     * @param username
     * @param userpassword
     * @param name
     * @param surname
     * @param currency Accepts either "GBP", "USD" or "EUR"
     */
    public void registerAdminUser(String username, String userpassword, String name, String surname, String currency) {
        try {
            SystemUser sys_user;
            SystemUserGroup sys_user_group;

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String passwd = userpassword;
            md.update(passwd.getBytes("UTF-8"));
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < digest.length; i++) {
                sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
            }
            String paswdToStoreInDB = sb.toString();
            BigDecimal amount = new BigDecimal(ts.getConversion("GBP", currency, new BigDecimal(1000)).substring(3));

            sys_user = new SystemUser(username, paswdToStoreInDB, name, surname, currency, amount);
            sys_user_group = new SystemUserGroup(username, "admins");

            em.persist(sys_user);
            em.persist(sys_user_group);

        } catch (UnsupportedEncodingException | NoSuchAlgorithmException ex) {
            Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
