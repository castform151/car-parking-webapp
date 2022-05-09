package com.group.CarParking.model;

public class CarParkService {
  private int cost;
  private String service;

  @Override
  public String toString() {
    return "cost=" + cost + " service=" + service;
  }

  public int getCost() {
    return cost;
  }

  public void setCost(int cost) {
    this.cost = cost;
  }

  public String getService() {
    return service;
  }

  public void setService(String service) {
    this.service = service;
  }
}
