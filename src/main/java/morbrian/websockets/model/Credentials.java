package morbrian.websockets.model;

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

  @Override public boolean equals(Object other) {
    if (other == null || this.getClass() != other.getClass()) {
      return false;
    }

    Credentials otherBase = (Credentials) other;
    return (username == null ? otherBase.username == null : username.equals(otherBase.username))
        && (password == null ? otherBase.password == null : password.equals(otherBase.password));
  }
}
