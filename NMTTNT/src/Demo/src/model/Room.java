package model;

//File: Room.java
public class Room {
String id; int capacity; String type; String locationCluster;
public Room(String id, int capacity, String type, String locationCluster) {
   this.id = id; this.capacity = capacity; this.type = type; this.locationCluster = locationCluster;
}
public String getId() { return id; }
public int getCapacity() { return capacity; }
public String getType() { return type; }
public String getLocationCluster() { return locationCluster; }
}