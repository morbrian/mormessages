package morbrian.mormessages.model;

import java.util.Objects;

public class Credentials {

  private String username;
  private String password;

  public Credentials() {
  }

  public Credentials(String username, String password) {
    this.username = username;
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public String userInfo() {
    return username + ":" + password;
  }

  @Override public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Credentials that = (Credentials) o;
    return Objects.equals(getUsername(), that.getUsername()) && Objects
        .equals(getPassword(), that.getPassword());
  }

  @Override public int hashCode() {
    return Objects.hash(getUsername(), getPassword());
  }
}
