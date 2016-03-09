package com.windowsazure.messaging;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.digester3.Digester;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

/**
 * "Get Notification Message Telemetry" result object as defined in https://msdn.microsoft.com/en-us/library/mt608135.aspx
 */
public class NotificationDetails implements Serializable {

  private static final long serialVersionUID = 5020317174414641455L;
  private static final ThreadLocal<Digester> xmlParser;
  private static final Log logger = LogFactory.getLog(NotificationDetails.class);
  private String notificationId;
  private String location;
  /** 
   * Known values so far: 
   *  - Abandoned
   *  - Completed
   *  - Enqueued
   *  - NoTargetFound
   *  - Processing
   *  - Unknown
   */
  private String state;
  private Date enqueueTime;
  private Date startTime;
  private Date endTime;
  private String notificationBody;
  private final List<String> tags = new ArrayList<>();
  private final List<String> targetPlatforms = new ArrayList<>();
  private Map<String, Integer> admOutcomeCounts;
  private Map<String, Integer> apnsOutcomeCounts;
  private Map<String, Integer> gcmOutcomeCounts;
  private Map<String, Integer> mpnsOutcomeCounts;
  private Map<String, Integer> wnsOutcomeCounts;

  static {
    xmlParser = new ThreadLocal<Digester>() {
      @Override
      protected Digester initialValue() {
        Digester digester = new Digester();
        digester.addObjectCreate("NotificationDetails", NotificationDetails.class);
        // for each known simple property, call the corresponding setter:
        for (String property : Arrays.asList("NotificationId", "Location", "State",
          "EnqueueTime", "StartTime", "EndTime", "NotificationBody", "Tags", "TargetPlatforms")) {
          digester.addCallMethod("NotificationDetails/" + property, "set" + property, 1);
          digester.addCallParam("NotificationDetails/" + property, 0);
        }
        // for each service provider, call the corresponding outcome-count method:
        for (String notificationType : Arrays.asList("Adm", "Apns", "Gcm", "Mpns", "Wns")) {
          digester.addCallMethod("NotificationDetails/" + notificationType + "OutcomeCounts/Outcome",
            "add" + notificationType + "Outcome", 2);
          digester.addCallParam("NotificationDetails/" + notificationType + "OutcomeCounts/Outcome/Name", 0);
          digester.addCallParam("NotificationDetails/" + notificationType + "OutcomeCounts/Outcome/Count", 1);
        }
        return digester;
      }
    };
  }

  public String getNotificationId() {
    return notificationId;
  }

  public void setNotificationId(String notificationId) {
    this.notificationId = notificationId;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public Date getEnqueueTime() {
    return enqueueTime;
  }

  public void setEnqueueTime(String enqueueTime) {
    this.enqueueTime = parseDate(enqueueTime);
  }

  public Date getStartTime() {
    return startTime;
  }

  public void setStartTime(String startTime) {
    this.startTime = parseDate(startTime);
  }

  public Date getEndTime() {
    return endTime;
  }

  public void setEndTime(String endTime) {
    this.endTime = parseDate(endTime);
  }

  public String getNotificationBody() {
    return notificationBody;
  }

  public void setNotificationBody(String notificationBody) {
    this.notificationBody = notificationBody;
  }

  public List<String> getTags() {
    return tags;
  }

  public void setTags(String tags) {
    this.tags.clear();
    Collections.addAll(this.tags, tags.split(","));
  }

  public List<String> getTargetPlatforms() {
    return targetPlatforms;
  }

  public void setTargetPlatforms(String targetPlatforms) {
    this.targetPlatforms.clear();
    Collections.addAll(this.targetPlatforms, targetPlatforms.split(","));
  }

  public Map<String, Integer> getAdmOutcomeCounts() {
    return admOutcomeCounts;
  }

  public void addAdmOutcome(String name, String count) {
    if (admOutcomeCounts == null) {
      admOutcomeCounts = new TreeMap<>();
    }
    admOutcomeCounts.put(name, parseInt(count));
  }

  public Map<String, Integer> getApnsOutcomeCounts() {
    return apnsOutcomeCounts;
  }

  public void addApnsOutcome(String name, String count) {
    if (apnsOutcomeCounts == null) {
      apnsOutcomeCounts = new TreeMap<>();
    }
    apnsOutcomeCounts.put(name, parseInt(count));
  }

  public Map<String, Integer> getGcmOutcomeCounts() {
    return gcmOutcomeCounts;
  }

  public void addGcmOutcome(String name, String count) {
    if (gcmOutcomeCounts == null) {
      gcmOutcomeCounts = new TreeMap<>();
    }
    gcmOutcomeCounts.put(name, parseInt(count));
  }

  public Map<String, Integer> getMpnsOutcomeCounts() {
    return mpnsOutcomeCounts;
  }

  public void addMpnsOutcome(String name, String count) {
    if (mpnsOutcomeCounts == null) {
      mpnsOutcomeCounts = new TreeMap<>();
    }
    mpnsOutcomeCounts.put(name, parseInt(count));
  }

  public Map<String, Integer> getWnsOutcomeCounts() {
    return wnsOutcomeCounts;
  }

  public void addWnsOutcome(String name, String count) {
    if (wnsOutcomeCounts == null) {
      wnsOutcomeCounts = new TreeMap<>();
    }
    wnsOutcomeCounts.put(name, parseInt(count));
  }

  public static NotificationDetails parse(InputStream content) throws IOException, SAXException {
    return xmlParser.get().parse(content);
  }

  @Override
  public String toString() {
    return "NotificationDetails{" +
      "notificationId='" + notificationId + '\'' +
      ", location='" + location + '\'' +
      ", state='" + state + '\'' +
      ", enqueueTime=" + enqueueTime +
      ", startTime=" + startTime +
      ", endTime=" + endTime +
      ", tags=" + tags +
      ", targetPlatforms=" + targetPlatforms +
      ", notificationBody='" + notificationBody + '\'' +
      '}';
  }

  private Date parseDate(String value) {
    if (value != null) {
      try {
        return DatatypeConverter.parseDateTime(value).getTime();
      } catch (RuntimeException e) {
        logger.warn("Cannot parse date: '" + value + "'");
      }
    }
    return null;
  }

  private Integer parseInt(String value) {
    if (value != null) {
      try {
        return Integer.valueOf(value);
      } catch (RuntimeException e) {
        logger.warn("Cannot parse integer: '" + value + "'");
      }
    }
    return null;
  }
}
