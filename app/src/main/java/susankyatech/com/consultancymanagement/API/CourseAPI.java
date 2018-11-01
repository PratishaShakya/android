package susankyatech.com.consultancymanagement.API;

import retrofit2.Call;
import retrofit2.http.GET;
import susankyatech.com.consultancymanagement.Model.Login;
import susankyatech.com.consultancymanagement.Model.Result;

public interface CourseAPI {
    @GET("students/get-courses")
    Call<Result> getStudentCourses();
}
