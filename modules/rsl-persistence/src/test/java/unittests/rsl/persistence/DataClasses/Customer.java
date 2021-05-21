package unittests.rsl.persistence.DataClasses;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Customer {

    @Id
    @GeneratedValue
    long id;

    // Persistent Fields:
    private String firstname;
    private String lastname;

    // Constructor:
    public Customer(String firstname, String lastname) {
        this.firstname = firstname;
        this.lastname = lastname;
    }

    // Accessor Methods:
    public String getFirstname() { return this.firstname; }
    public String getLastname() { return this.lastname; }

    // String Representation:
    @Override
    public String toString() {
        return String.format("%s %s)", this.firstname, this.lastname);
    }

}
