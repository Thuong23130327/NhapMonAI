package model;

//File: Lecturer.java
import java.util.ArrayList;
import java.util.List;
public class Lecturer {
String id;
String name;
List<String> qualifiedCourseIds = new ArrayList<>();
List<String> preferredTimeSlotIds = new ArrayList<>();
List<String> undesiredTimeSlotIds = new ArrayList<>();

public Lecturer(String id, String name, List<String> qualifiedCourseIds) {
   this.id = id; this.name = name; this.qualifiedCourseIds = qualifiedCourseIds;
}
public String getId() { return id; }
public String getName() { return name; }
public List<String> getQualifiedCourseIds() { return qualifiedCourseIds; }
public List<String> getPreferredTimeSlotIds() { return preferredTimeSlotIds; }
public List<String> getUndesiredTimeSlotIds() { return undesiredTimeSlotIds; }
public void setPreferredTimeSlotIds(List<String> ids) { this.preferredTimeSlotIds = ids; }
public void setUndesiredTimeSlotIds(List<String> ids) { this.undesiredTimeSlotIds = ids; }
}