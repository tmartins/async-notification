package org.nuxeo.ecm.platform.notification.async;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.mail.MessagingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.common.utils.i18n.I18NUtils;
import org.nuxeo.ecm.activity.Activity;
import org.nuxeo.ecm.activity.ActivityStreamService;
import org.nuxeo.ecm.automation.features.PlatformFunctions;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventBundle;
import org.nuxeo.ecm.core.event.PostCommitFilteringEventListener;
import org.nuxeo.ecm.platform.ec.notification.email.EmailHelper;
import org.nuxeo.ecm.platform.ec.notification.service.NotificationServiceHelper;
import org.nuxeo.runtime.api.Framework;

public class PostmanEventListener implements PostCommitFilteringEventListener {

    private static final Log log = LogFactory.getLog(PostmanEventListener.class);

    private ActivityStreamService activityStreamService = getActivityStreamService();

    private EmailHelper emailHelper = new EmailHelper();

    private PlatformFunctions pf = new PlatformFunctions();

    @Override
    public void handleEvent(EventBundle events) throws ClientException {
        for (Event event : events) {
            String frequency = event.getName().replace("Subscription", "");
            log.info("Processing " + frequency + " notifications");

            // Get activities
            Map<String, Serializable> parameters = new HashMap<String, Serializable>();
            parameters.put(NotificationActivityStreamFilter.OBJECT_PARAMETER,
                    frequency);
            List<Activity> activities = activityStreamService.query(
                    NotificationActivityStreamFilter.ID, parameters);

            Map<String, List<Activity>> userActivities = new HashMap<String, List<Activity>>();
            for (Activity activity : activities) {
                String targetUser = activity.getTarget();
                if (!userActivities.containsKey(targetUser)) {
                    userActivities.put(targetUser, new ArrayList<Activity>());
                }
                userActivities.get(targetUser).add(activity);

            }

            for (String username : userActivities.keySet()) {
                processUserActivities(username, userActivities.get(username));
            }
        }
    }

    @Override
    public boolean acceptEvent(Event event) {
        if (activityStreamService == null) {
            log.error("Could not get activityStreamService");
            return false;
        }
        return true;
    }

    protected void processUserActivities(String username,
            List<Activity> activities) {

        Map<String, Object> mail = new HashMap<String, Object>();

        try {
            mail.put("mail.to", pf.getEmail(username));
        } catch (Exception e1) {
            log.error(e1);
            return;
        }

        String subject = "Notification summary";
        subject = NotificationServiceHelper.getNotificationService().getEMailSubjectPrefix()
                + subject;
        mail.put("subject", subject);
        mail.put("template", "notificationSummary");

        mail.put("frequency", ((Activity) activities.get(0)).getObject());

        List<Map<String, String>> notifs = new ArrayList<Map<String, String>>();
        for (Activity activity : activities) {
            Map<String, String> activityInfo = new HashMap<String, String>();
            activityInfo.put("actor", activity.getDisplayActor());
            activityInfo.put("documentTitle", activity.getDisplayObject());
            activityInfo.put("date", activity.getPublishedDate().toGMTString());
            activityInfo.put("action", I18NUtils.getMessageString("messages",
                    activity.getVerb(), null, Locale.ENGLISH));
            notifs.add(activityInfo);
        }

        mail.put("notifications", notifs);

        try {
            emailHelper.sendmail(mail);
        } catch (MessagingException e) {
            log.warn("Failed to send notification email to: "
                    + e.getClass().getName() + ": " + e.getMessage());
        } catch (Exception e) {
            log.error("Failed to send notification email ", e);
        }
    }

    protected static ActivityStreamService getActivityStreamService() {
        return Framework.getLocalService(ActivityStreamService.class);
    }

}