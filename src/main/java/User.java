public class User {
    private String userName;
    private int userID;
    private String pwd;
    private Role role;

    enum Role {undefined, admin, user}

    public User(String userName, String pwd) {
        this.userName = userName;
        if(userName.equals("admin")) {
            this.userID = 1;
            this.role = Role.admin;
            this.pwd = pwd;
        } else {
        this.userID = 0;
        this.pwd = pwd;
        this.role = Role.undefined;
        }
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
