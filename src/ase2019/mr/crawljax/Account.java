package ase2019.mr.crawljax;

public class Account implements Cloneable {
	private String username;		//the username value
	private String password;		//the password value
	private String usernameParam;	//the name of the username parameter in a request
	private String passwordParam;	//the name of the password parameter in a request
	
	public Account(String username, String password) {
		this.username = username;
		this.password = password;
		this.usernameParam = "";
		this.passwordParam = "";
	}
	
	public Account() {
		this.username = "";
		this.password = "";
		this.usernameParam = "";
		this.passwordParam = "";
	}
	
	public Account(String username, String password, String usernameParam, String passwordParam) {
		this.username = username;
		this.password = password;
		this.usernameParam = usernameParam;
		this.passwordParam = passwordParam;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsernameParam() {
		return usernameParam;
	}

	public void setUsernameParam(String usernameParam) {
		this.usernameParam = usernameParam;
	}

	public String getPasswordParam() {
		return passwordParam;
	}

	public void setPasswordParam(String passwordParam) {
		this.passwordParam = passwordParam;
	}
	
	public boolean isEmpty(){
		if ( username == null || password == null ){
			return true;
		}
		
		if(username.isEmpty() && usernameParam.isEmpty() 
				&& password.isEmpty() && passwordParam.isEmpty()){
			return true;
		}
		return false;
	}
	
	public Object clone() throws CloneNotSupportedException{
			return super.clone();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj==null || !(obj instanceof Account)){
			return false;
		}
		
		Account that = (Account)obj;
		
		if(this.username==null && that.username==null &&
				this.usernameParam==null && that.usernameParam==null &&
				this.password==null && that.password==null &&
				this.passwordParam==null && that.passwordParam==null){
			return true;
		}
		
		if((this.username!=null && that.username==null) ||
				(this.username==null && that.username!=null)){
			return false;
		}
		
		if((this.usernameParam!=null && that.usernameParam==null) ||
				(this.usernameParam==null && that.usernameParam!=null)){
			return false;
		}
		
		if((this.password!=null && that.password==null) ||
				(this.password==null && that.password!=null)){
			return false;
		}
		
		if((this.passwordParam!=null && that.passwordParam==null) ||
				(this.passwordParam==null && that.passwordParam!=null)){
			return false;
		}
		
		
		return (((this.username==null && that.username==null) || this.username.equals(that.username)) &&
				((this.usernameParam==null && that.usernameParam==null) || this.usernameParam.equals(that.usernameParam)) &&
				((this.password==null && that.password==null) || this.password.equals(that.password)) &&
				((this.passwordParam==null && that.passwordParam==null) || this.passwordParam.equals(that.passwordParam)));
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "[Account: username="+username+" pwd="+password+"]";
	}

	public boolean isAnonymous(){
		return username == null || password == null || (username.isEmpty() && password.isEmpty());
	}

	
}