package net.cookiemunch.model;

/** One system a request must be carried out in. {@code operation} is locate, export, erase or optOut. */
public record FulfillmentSystem(String system, String operation) {}
