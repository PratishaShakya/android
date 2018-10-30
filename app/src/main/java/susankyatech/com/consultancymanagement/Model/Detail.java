package susankyatech.com.consultancymanagement.Model;

import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.util.List;

public class Detail {
    public int client_id;
    public File coverPhoto;
    public String cover_photo;
    public String location;
    public String phone;
    public List<String> courses;
    public List<String> countries;
    public String description;
    public String established;
    public String achievements;
    @SerializedName("id")
    public int detail_id;
}
