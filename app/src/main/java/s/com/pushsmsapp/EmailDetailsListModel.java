package s.com.pushsmsapp;

public class EmailDetailsListModel {

    int _eid;
    String emailid;

    public int get_eid() {
        return _eid;
    }

    public void set_eid(int _eid) {
        this._eid = _eid;
    }

    public String getEmailid() {
        return emailid;
    }

    public void setEmailid(String emailid) {
        this.emailid = emailid;
    }

    public String getEmailsubject() {
        return emailsubject;
    }

    public void setEmailsubject(String emailsubject) {
        this.emailsubject = emailsubject;
    }

    public String getEmailbody() {
        return emailbody;
    }

    public void setEmailbody(String emailbody) {
        this.emailbody = emailbody;
    }

    String emailsubject;
    String emailbody;

    public EmailDetailsListModel() {
    }

    public EmailDetailsListModel(String emailid, String emailsubject, String emailbody) {
        this.emailid = emailid;
        this.emailsubject = emailsubject;
        this.emailbody = emailbody;
    }

    public EmailDetailsListModel(int _eid, String emailid, String emailsubject, String emailbody) {
        this._eid = _eid;
        this.emailid = emailid;
        this.emailsubject = emailsubject;
        this.emailbody = emailbody;
    }


}
