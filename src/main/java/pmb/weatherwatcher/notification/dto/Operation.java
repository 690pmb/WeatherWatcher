package pmb.weatherwatcher.notification.dto;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Operation {
  OPEN_WINDOW("openWindow"),
  FOCUS_LAST_FOCUSED_OR_OPEN("focusLastFocusedOrOpen"),
  NAVIGATE_LAST_FOCUSED_OR_OPEN("navigateLastFocusedOrOpen"),
  SEND_REQUEST("sendRequest");

  private final String code;

  Operation(String code) {
    this.code = code;
  }

  @JsonValue
  public String getCode() {
    return code;
  }
}
