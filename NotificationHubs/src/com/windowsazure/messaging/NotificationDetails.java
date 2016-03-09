package com.windowsazure.messaging;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.digester3.Digester;
import org.xml.sax.SAXException;

/**
 * "Get Notification Message Telemetry" result object as defined in https://msdn.microsoft.com/en-us/library/mt608135.aspx
 *
 * TODO add ApnsOutcomeCounts, MpnsOutcomeCounts, WnsOutcomeCounts, GcmOutcomeCounts, AdmOutcomeCounts
 */
public class NotificationDetails implements Serializable {

  private static final long serialVersionUID = -5334715903250590288L;
  private static final ThreadLocal<Digester> xmlParser;
  private String notificationId;
  private String location;
  private String state;
  private Date enqueueTime;
  private Date startTime;
  private Date endTime;
  private String notificationBody;
  private final List<String> tags = new ArrayList<>();
  private final List<String> targetPlatforms = new ArrayList<>();

  static {
    xmlParser = new ThreadLocal<Digester>() {
      @Override
      protected Digester initialValue() {
        Digester digester = new Digester();
        digester.addObjectCreate("NotificationDetails", NotificationDetails.class);
        for (String property : Arrays.asList("NotificationId", "Location", "State",
          "EnqueueTime", "StartTime", "EndTime", "NotificationBody", "Tags", "TargetPlatforms")) {
          digester.addCallMethod("*/" + property, "set" + property, 1);
          digester.addCallParam("*/" + property, 0);
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
    this.enqueueTime = enqueueTime != null ? DatatypeConverter.parseDateTime(enqueueTime).getTime() : null;
  }

  public Date getStartTime() {
    return startTime;
  }

  public void setStartTime(String startTime) {
    this.startTime = startTime != null ? DatatypeConverter.parseDateTime(startTime).getTime() : null;
  }

  public Date getEndTime() {
    return endTime;
  }

  public void setEndTime(String endTime) {
    this.endTime = endTime != null ? DatatypeConverter.parseDateTime(endTime).getTime() : null;
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
}
