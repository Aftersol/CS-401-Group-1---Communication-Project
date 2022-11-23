import java.io.Serializable;

public class Message implements Serializable {
	protected int ID;
	protected int userID;
    protected String type;
    protected String status;
    protected String text;
    public boolean typelock = false;

    public Message(){
        this.type = "Undefined";
        this.status = "Undefined";
        this.text = "Undefined";
    }

    public Message(String type, String status, String text, int UID){
        setType(type);
        setStatus(status);
        setText(text);
        setUserID(UID);
    }

    public Message(String type, String status, String text){
        setType(type);
        setStatus(status);
        setText(text);
    }

    private void setType(String type){
    	type = type.toLowerCase();
    	if (typelock == true) {
    		return;
    	}
    	else if (type == "login" || type == "text" || type == "logout" || type == "signup") {
    			this.type = type;
    			typelock = true;
    	}
    }

    public void setStatus(String status){ 
    	this.status = status;
    }

    public void setText(String text){
    	this.text = text;
    }

    public String getType(){  
    	return type;
    }

    public String getStatus(){
    	return status;
    }

    public String getText(){
    	return text;
    }
    
    public String toString() {
    	return "Message type: " + type + " " + status + "\nContent: " + text;
    }
    
    int getID() { 
    	return this.ID;
    }

    int getUserID() { 
    	return this.userID; 
    }

    private void setID(int id) {
        this.ID = id;
    }

    private void setUserID(int userID) {
        this.userID = userID;
    }

}