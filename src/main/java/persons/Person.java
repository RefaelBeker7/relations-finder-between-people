package persons;

public class Person {
    private Name fullName;
    private Address address;

    public Person(Name fullName, Address address) {
        this.fullName = fullName;
        this.address = address;
    }

    public Name getFullName() {
        return fullName;
    }

    public void setFullName(Name fullName) {
        this.fullName = fullName;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Person {" + fullName + ", " + address + "}";
    }

}
