package com.concursive.connect.web.rest;

public class TestHandler {

  String name;

  public TestHandler(String theFullObject) {
    this.name = theFullObject;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
