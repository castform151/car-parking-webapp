package com.group.CarParking.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.cloud.firestore.DocumentReference;

public class Worker {
  public Worker() {
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public int getCost() {
    return cost;
  }

  public void setCost(int cost) {
    this.cost = cost;
  }

  public double getWorkedHours() {
    return workedHours;
  }

  public void setWorkedHours(double workedHours) {
    this.workedHours = workedHours;
  }

  private boolean available;
  private String id;

  public Worker(String name, List<String> services, int cost) {
    this.name = name;
    this.services = services;
    this.cost = cost;
  }

  private String name;
  private List<DocumentReference> locations;
  private double numberOfRatings;
  private double rating, workedHours;
  private List<String> services;
  private int cost;

  public boolean isAvailable() {
    return available;
  }

  public void setAvailable(boolean available) {
    this.available = available;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<DocumentReference> getLocations() {
    return locations;
  }

  public void setLocations(List<DocumentReference> locations) {
    this.locations = locations;
  }

  public double getNumberOfRatings() {
    return numberOfRatings;
  }

  public void setNumberOfRatings(double numberOfRatings) {
    this.numberOfRatings = numberOfRatings;
  }

  public double getRating() {
    return rating;
  }

  public void setRating(double rating) {
    this.rating = rating;
  }

  public List<String> getServices() {
    return services;
  }

  public void setServices(List<String> services) {
    this.services = services;
  }

  public Map<String, Object> toJSON() {
    var map = new HashMap<String, Object>();
    map.put("available", this.available);
    map.put("numberOfRatings", this.numberOfRatings);
    map.put("name", this.name);
    map.put("services", this.services);
    map.put("rating", this.rating);
    map.put("locations", this.locations);
    map.put("workedHours", this.workedHours);
    map.put("cost", this.cost);
    return map;
  }

  public Map<String, Object> toMap() {
    return this.toJSON();
  }
}
