package org.nuxeo.ecm.platform.notification.async;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.nuxeo.ecm.platform.ec.placeful.Annotation;

@Entity
public class PeriodicUserSubscription extends Annotation {

    private static final long serialVersionUID = -4511099450448368569L;

    private int usId;

    private String notification;

    private String userId;

    private String docId;

    public PeriodicUserSubscription() {
        this(null, null, null);
    }

    public PeriodicUserSubscription(String notification, String user, String docId) {
        this.notification = notification;
        userId = user;
        this.docId = docId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public int getId() {
        return usId;
    }

    // Not used => remove, except is this is needed for the Entity mechanism (?)
    public void setId(int id) {
        usId = id;
    }

    public String getNotification() {
        return notification;
    }

    // Not used => remove, except is this is needed for the Entity mechanism (?)
    public void setNotification(String notif) {
        notification = notif;
    }

    public String getDocId() {
        return docId;
    }

    // Not used => remove, except is this is needed for the Entity mechanism (?)
    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getUserId() {
        return userId;
    }

    // Not used => remove, except is this is needed for the Entity mechanism (?)
    public void setUserId(String userId) {
        this.userId = userId;
    }

}
