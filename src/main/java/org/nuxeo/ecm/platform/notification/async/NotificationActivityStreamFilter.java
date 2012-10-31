package org.nuxeo.ecm.platform.notification.async;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.nuxeo.ecm.activity.ActivitiesList;
import org.nuxeo.ecm.activity.ActivitiesListImpl;
import org.nuxeo.ecm.activity.Activity;
import org.nuxeo.ecm.activity.ActivityHelper;
import org.nuxeo.ecm.activity.ActivityReply;
import org.nuxeo.ecm.activity.ActivityStream;
import org.nuxeo.ecm.activity.ActivityStreamFilter;
import org.nuxeo.ecm.activity.ActivityStreamService;
import org.nuxeo.ecm.activity.ActivityStreamServiceImpl;
import org.nuxeo.runtime.api.Framework;

public class NotificationActivityStreamFilter implements ActivityStreamFilter {

    public static final String ID = "NotificationActivityStreamFilter";

    public enum QueryType {
        ACTIVITY_STREAM_FOR_ACTOR, ACTIVITY_STREAM_FROM_ACTOR
    }

    public static final String OBJECT_PARAMETER = "object";

    public static final String USER_ACTIVITY_STREAM_NAME = "userActivityStream";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public boolean isInterestedIn(Activity activity) {
        return false;
    }

    @Override
    public void handleNewActivity(ActivityStreamService activityStreamService,
            Activity activity) {
        // nothing for now
    }

    @Override
    public void handleRemovedActivities(
            ActivityStreamService activityStreamService,
            Collection<Serializable> activityIds) {
        // nothing for now
    }

    @SuppressWarnings("unchecked")
    @Override
    public ActivitiesList query(ActivityStreamService activityStreamService,
            Map<String, Serializable> parameters, long offset, long limit) {

        String object = (String) parameters.get(OBJECT_PARAMETER);
        if (object == null) {
            throw new IllegalArgumentException(OBJECT_PARAMETER
                    + " is required");
        }

        EntityManager em = ((ActivityStreamServiceImpl) activityStreamService).getEntityManager();
        Query query;

        query = em.createQuery("select activity from Activity activity where activity.object = :object order by activity.publishedDate desc");
        query.setParameter("object", object);

        if (limit > 0) {
            query.setMaxResults((int) limit);
            if (offset > 0) {
                query.setFirstResult((int) offset);
            }
        }
        return new ActivitiesListImpl(query.getResultList());
    }

    @Override
    public void handleRemovedActivities(ActivityStreamService arg0,
            ActivitiesList arg1) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void handleRemovedActivityReply(ActivityStreamService arg0,
            Activity arg1, ActivityReply arg2) {
        // TODO Auto-generated method stub
        
    }

}
