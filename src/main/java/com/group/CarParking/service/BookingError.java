package com.group.CarParking.service;

public class BookingError extends Exception {
  public BookingError(String s) {
    super(s);
  }

  public BookingError() {
    super();
  }
}
