package susankyatech.com.consultancymanagement.Activity;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import susankyatech.com.consultancymanagement.Application.App;
import susankyatech.com.consultancymanagement.Fragment.AddGalleryFragment;
import susankyatech.com.consultancymanagement.Fragment.GalleryFragment;
import susankyatech.com.consultancymanagement.Fragment.LoginFragment;
import susankyatech.com.consultancymanagement.Fragment.ProfileFragment;
import susankyatech.com.consultancymanagement.Fragment.SearchFragment;
import susankyatech.com.consultancymanagement.R;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.navigation_view)
    NavigationView navigationView;
    @BindView(R.id.drawable_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.main_app_bar)
    Toolbar mToolbar;

    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        init();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new SearchFragment()).commit();

    }

    private void init() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Home");
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        View navView = navigationView.inflateHeaderView(R.layout.nav_header_layout);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                userMenuSelector(item);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void userMenuSelector(MenuItem item) {
        switch (item.getItemId()){
            case R.id.search:
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new SearchFragment()).commit();

                break;
            case R.id.profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new ProfileFragment()).commit();

                break;
            case R.id.add_gallery:
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new AddGalleryFragment()).commit();

                break;
            case R.id.list_gallery:
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new GalleryFragment()).commit();

                break;
            case R.id.logout:
                App.logOut(this);
                break;
        }
        drawerLayout.closeDrawers();
    }
}
