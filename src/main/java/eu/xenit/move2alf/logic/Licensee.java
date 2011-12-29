package eu.xenit.move2alf.logic;

public class Licensee {
    private String companyName;
    private String street;
    private String city;
    private String postalCode;
    private String state;
    private String country;

    private String contactPerson;
    private String email;
    private String telephone;

    public Licensee(String companyName, String street, String city, String postalCode, String state, 
            String country, String contactPerson, String email, String telephone) {
        this.companyName = companyName;
        this.street = street;
        this.city = city;
        this.postalCode = postalCode;
        this.state = state;
        this.country = country;

        this.contactPerson = contactPerson;
        this.email = email;
        this.telephone = telephone;
    }

    public String getCompanyName() { return this.companyName; }
    public String getStreet() { return this.street; }
    public String getCity() { return this.city; }
    public String getPostalCode() { return this.postalCode; }
    public String getState() { return this.state; }
    public String getCountry() { return this.country; }

    public String getContactPerson() { return this.contactPerson; }
    public String getEmail() { return this.email; }
    public String getTelephone() { return this.telephone; }
}

