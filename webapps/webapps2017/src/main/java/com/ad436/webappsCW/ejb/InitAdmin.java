package com.ad436.webappsCW.ejb;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package com.ad436.webappsCW.ejb;
import com.ad436.webappsCW.entity.SystemUser;
import com.ad436.webappsCW.entity.SystemUserGroup;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author alex
 */
@Startup
@Singleton
public class InitAdmin {

    @PersistenceContext(unitName = "webappsPU")
    EntityManager em;

    @EJB
    TransactionService ts;

    /**
     * Run when the application is started, if no user with the username
     * 'admin1' exists, one is created.
     */
    @PostConstruct
    public void adminInit() {
        try {
            SystemUser sys_user;
            SystemUserGroup sys_user_group;

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String passwd = "admin1";
            md.update(passwd.getBytes("UTF-8"));
            byte[] digest = md.digest();
            BigInteger bigInt = new BigInteger(1, digest);
            String paswdToStoreInDB = bigInt.toString(16);

        // apart from the default constructor which is required by JPA
            // you need to also implement a constructor that will make the following code succeed
            if (ts.checkUser("admin1")) {
                System.out.println("admin already exists");
            } else {
                sys_user = new SystemUser("admin1", paswdToStoreInDB, "Mr", "Admin", "GBP", new BigDecimal(1000));
                sys_user_group = new SystemUserGroup("admin1", "admins");
                em.persist(sys_user);
                em.persist(sys_user_group);
                em.flush();
                System.out.println("admin user created");

            }

        } catch (UnsupportedEncodingException | NoSuchAlgorithmException ex) {
            Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
