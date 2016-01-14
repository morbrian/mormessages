package morbrian.websockets.model;

public class Status {

  private int code = -1;
  private String type;
  private String details;

  public Status() {
  }

  public Status(Type statusType) {
    this.code = statusType.ordinal();
    this.type = statusType.name();
  }

  public Status(Type statusType, String details) {
    this(statusType);
    this.details = details;
  }

  public int getCode() {
    return code;
  }

  public String getType() {
    return type;
  }

  public String getDetails() {
    return details;
  }

  @Override public boolean equals(Object other) {
    if (other == null || this.getClass() != other.getClass()) {
      return false;
    }

    Status otherStatus = (Status) other;
    return code == otherStatus.code && (type != null && type.equals(otherStatus.type)
        || type == otherStatus.type) && (details != null && details.equals(otherStatus.details)
        || details == otherStatus.details);
  }

  public enum Type {
    SUCCESS("success"), UNSPECIFIED("unspecified"), ERROR("error"), UNAUTHORIZED("unauthorized");

    private String value;

    Type(String value) {
      this.value = value;
    }
  }

}
