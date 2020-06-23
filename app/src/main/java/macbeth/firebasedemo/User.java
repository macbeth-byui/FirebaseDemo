package macbeth.firebasedemo;

public class User {

    // Firebase requires either the fields to be public or public getters must exist
    public String firstName;
    public String lastName;
    public String major;

    // Default Constructor is Required for Firebase
    public User() {
    }

    // Non Default Constructor for this App to create objects easier
    public User(String firstName, String lastName, String major) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.major = major;
    }

    // Used to display in the ListView
    @Override
    public String toString() {
        return firstName + " " + lastName + " (" + major + ")";
    }
}
