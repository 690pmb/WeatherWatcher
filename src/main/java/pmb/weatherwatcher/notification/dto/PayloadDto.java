package pmb.weatherwatcher.notification.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("notification")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
public class PayloadDto {
  private String title;
  private String body;
  private PayloadDataDto data;
  private Boolean requireInteraction = true;

  public PayloadDto(String title, String body, PayloadDataDto data) {
    this.title = title;
    this.body = body;
    this.data = data;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public PayloadDataDto getData() {
    return data;
  }

  public void setData(PayloadDataDto data) {
    this.data = data;
  }

  public Boolean getRequireInteraction() {
    return requireInteraction;
  }

  public void setRequireInteraction(Boolean requireInteraction) {
    this.requireInteraction = requireInteraction;
  }
}
