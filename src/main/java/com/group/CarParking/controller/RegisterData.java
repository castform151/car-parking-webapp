package com.group.CarParking.controller;

public class RegisterData {
  private String name, email, password, carnum, mnumber, address;

  public String getAddress() {
    return this.address;
  }

  public void setAddress(String a) {
    this.address = a;
  }

  public String getName() {
    return name;
  }

  public String getMnumber() {
    return mnumber;
  }

  public void setMnumber(String mnumber) {
    this.mnumber = mnumber;
  }

  public String getCarnum() {
    return carnum;
  }

  public void setCarnum(String carnum) {
    this.carnum = carnum;
  }

  public String getEmail() {
    return email;
  }

  public String getPassword() {
    return password;
  }

  @Override
  public String toString() {
    return name + email + password;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}