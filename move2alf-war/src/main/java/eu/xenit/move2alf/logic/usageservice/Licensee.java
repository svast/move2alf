package eu.xenit.move2alf.logic.usageservice;

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
        this.companyName = nonNullString(companyName);
        this.street = nonNullString(street);
        this.city = nonNullString(city);
        this.postalCode = nonNullString(postalCode);
        this.state = nonNullString(state);
        this.country = nonNullString(country);

        this.contactPerson = nonNullString(contactPerson);
        this.email = nonNullString(email);
        this.telephone = nonNullString(telephone);
    }
    
    private String nonNullString(String in) {
    	return (in != null) ? in : "";
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

