package model;

//File: TimeSlot.java
import java.time.DayOfWeek;

public class TimeSlot {
String id; DayOfWeek day; int startPeriod; int endPeriod;
public TimeSlot(String id, DayOfWeek day, int startPeriod, int endPeriod) {
   this.id = id; this.day = day; this.startPeriod = startPeriod; this.endPeriod = endPeriod;
}
public String getId() { return id; }
public DayOfWeek getDay() { return day; }
public int getStartPeriod() { return startPeriod; }
public int getEndPeriod() { return endPeriod; }
public boolean overlapsWith(TimeSlot other) {
   if (this.day != other.day) return false;
   return Math.max(this.startPeriod, other.startPeriod) <= Math.min(this.endPeriod, other.endPeriod);
}
}