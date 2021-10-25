package s.com.pushsmsapp;

public class MesaageHistory {

    int _id;
    String sender;
    String message;
    String status;
    String date;

    public MesaageHistory() {
    }

    public MesaageHistory(String sender, String message, String status, String date) {
        this.sender = sender;
        this.message = message;
        this.status = status;
        this.date = date;
    }

    public MesaageHistory(int _id, String sender, String message, String status, String date) {
        this._id = _id;
        this.sender = sender;
        this.message = message;
        this.status = status;
        this.date = date;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
