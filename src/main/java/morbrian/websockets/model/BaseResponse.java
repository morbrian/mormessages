package morbrian.websockets.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.Map;

@XmlRootElement(name = "response") public class BaseResponse {

  @XmlElement(name = "status") private Status status;

  @XmlElement(name = "data") private Map<String, Object> data;

  public BaseResponse() {
  }

  public BaseResponse(Status status) {
    this.status = status;
  }

  public void addData(String name, Object value) {
    if (data == null) {
      data = new HashMap<>();
    }
    data.put(name, value);
  }

  public Status getStatus() {
    return status;
  }

  public Map<String, Object> getData() {
    return data;
  }

  public void setData(Map<String, Object> data) {
    this.data = data;
  }

  @Override public boolean equals(Object other) {
    if (other == null || this.getClass() != other.getClass()) {
      return false;
    }

    BaseResponse otherBase = (BaseResponse) other;
    return (status != null && status.equals(otherBase.status) || status == otherBase.status) && (
        data != null && data.equals(otherBase.data) || data == otherBase.data);
  }
}
