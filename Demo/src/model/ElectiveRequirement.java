package model;

//File: ElectiveRequirement.java
import java.util.List;
public class ElectiveRequirement {
String studentGroupId; List<String> electiveCourseIds; int creditsRequired;
public ElectiveRequirement(String studentGroupId, List<String> electiveCourseIds, int creditsRequired) {
   this.studentGroupId = studentGroupId; this.electiveCourseIds = electiveCourseIds; this.creditsRequired = creditsRequired;
}
public String getStudentGroupId() { return studentGroupId; }
public List<String> getElectiveCourseIds() { return electiveCourseIds; }
public int getCreditsRequired() { return creditsRequired; }
}
