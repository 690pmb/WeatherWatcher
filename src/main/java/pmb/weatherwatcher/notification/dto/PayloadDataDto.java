package pmb.weatherwatcher.notification.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("onActionClick")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
public class PayloadDataDto {

  @JsonProperty("default")
  private PayloadDefault defaults;

  public PayloadDataDto(Operation operation, String url) {
    super();
    this.defaults = new PayloadDefault(operation, url);
  }

  public class PayloadDefault {

    private Operation operation;
    private String url;

    public PayloadDefault(Operation operation, String url) {
      this.operation = operation;
      this.url = url;
    }

    public Operation getOperation() {
      return operation;
    }

    public void setOperation(Operation operation) {
      this.operation = operation;
    }

    public String getUrl() {
      return url;
    }

    public void setUrl(String url) {
      this.url = url;
    }
  }
}
