package s.com.pushsmsapp;

public class AuthorizeSenderClass {

    int _authid;
    String authsender;
    String authmessage;
    String authmsgnocontain;

    public AuthorizeSenderClass() {
    }

    public AuthorizeSenderClass(String authsender, String authmessage, String authmsgnocontain) {
        this.authsender = authsender;
        this.authmessage = authmessage;
        this.authmsgnocontain = authmsgnocontain;
    }

    public AuthorizeSenderClass(int _authid, String authsender, String authmessage, String authmsgnocontain) {
        this._authid = _authid;
        this.authsender = authsender;
        this.authmessage = authmessage;
        this.authmsgnocontain = authmsgnocontain;
    }

    public int get_authid() {
        return _authid;
    }

    public void set_authid(int _authid) {
        this._authid = _authid;
    }

    public String getAuthsender() {
        return authsender;
    }

    public void setAuthsender(String authsender) {
        this.authsender = authsender;
    }

    public String getAuthmessage() {
        return authmessage;
    }

    public void setAuthmessage(String authmessage) {
        this.authmessage = authmessage;
    }

    public String getAuthmsgnocontain() {
        return authmsgnocontain;
    }

    public void setAuthmsgnocontain(String authmsgnocontain) {
        this.authmsgnocontain = authmsgnocontain;
    }
}
