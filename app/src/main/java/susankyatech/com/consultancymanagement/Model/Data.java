package susankyatech.com.consultancymanagement.Model;

import com.google.gson.annotations.SerializedName;

import java.security.PublicKey;
import java.util.List;

public class Data {
    public User user;
    public Client client;
    public int id;
    public List<String> courses;
    public List<String> countries;
    public List<Client> clients;
    public String address;
    public String phone;
    public String description;
    public String established;
    public String achievements;
    public String email;
    public String name;
    public String dob;
    public boolean is_student;
    public String jwt_token;
    @SerializedName("detail")
    public EnquiryDetails enquiry_details;
    public String ad_image;
    public List<Banner> banners;

}
