package susankyatech.com.consultancymanagement.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import susankyatech.com.consultancymanagement.Application.App;
import susankyatech.com.consultancymanagement.Fragment.LoginFragment;
import susankyatech.com.consultancymanagement.Generic.FragmentKeys;
import susankyatech.com.consultancymanagement.Generic.Keys;
import susankyatech.com.consultancymanagement.Model.Client;
import susankyatech.com.consultancymanagement.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    private void init() {
        if (isAccountLoggedIn()){
            Client client = App.db().getObject(FragmentKeys.CLIENT, Client.class);
            if (client.detail == null){
                Intent i = new Intent(LoginActivity.this, WelcomeActivity.class);
                startActivity(i);
                finish();
            }else {
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }

        }
        getSupportFragmentManager().beginTransaction().replace(R.id.login_container, new LoginFragment()).commit();
    }

    private boolean isAccountLoggedIn() {
        return App.db().getBoolean(Keys.USER_LOGGED_IN);
    }
}
