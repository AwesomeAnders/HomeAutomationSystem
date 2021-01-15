public class User {
    private String userName;
    private int userID;
    private String pwd;
    private Roles role;

    enum Roles{undefined, admin, user}

    public User(String userName, String pwd) {
        this.userName = userName;
        if(userName.equals("admin")) {
            this.userID = 1;
            this.role = Roles.admin;
            this.pwd = pwd;
        } else {
        this.userID = 0;
        this.pwd = pwd;
        this.role = Roles.undefined;
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

    public Roles getRole() {
        return role;
    }

    public void setRole(Roles role) {
        this.role = role;
    }
}
