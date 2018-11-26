package susankyatech.com.consultancymanagement.Model;

public class Appointment {
    public String appointmentTime;
    public String appointmentDate;
    public String consultancyName;
    public String counselorName;

    public Appointment(String appointmentTime, String appointmentDate, String consultancyName, String counselorName) {
        this.appointmentTime = appointmentTime;
        this.appointmentDate = appointmentDate;
        this.consultancyName = consultancyName;
        this.counselorName = counselorName;
    }
}
