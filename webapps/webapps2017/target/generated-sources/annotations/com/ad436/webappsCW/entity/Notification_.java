package com.ad436.webappsCW.entity;

import com.ad436.webappsCW.entity.SystemUser;
import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2017-05-14T15:17:11")
@StaticMetamodel(Notification.class)
public class Notification_ { 

    public static volatile SingularAttribute<Notification, Boolean> request;
    public static volatile SingularAttribute<Notification, BigDecimal> amount;
    public static volatile SingularAttribute<Notification, SystemUser> sender;
    public static volatile SingularAttribute<Notification, Boolean> rejected;
    public static volatile SingularAttribute<Notification, BigDecimal> receiverNewBalance;
    public static volatile SingularAttribute<Notification, SystemUser> recipient;
    public static volatile SingularAttribute<Notification, String> currency;
    public static volatile SingularAttribute<Notification, Long> id;
    public static volatile SingularAttribute<Notification, BigDecimal> senderNewBalance;
    public static volatile SingularAttribute<Notification, Date> transactionDate;
    public static volatile SingularAttribute<Notification, Boolean> seen;

}