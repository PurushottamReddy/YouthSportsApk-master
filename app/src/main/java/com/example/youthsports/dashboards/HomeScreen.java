package com.example.youthsports.dashboards;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;


import com.example.youthsports.R;
import com.example.youthsports.authentication.ProfileFragment;
import com.example.youthsports.authentication.signin;
import com.example.youthsports.network.JwtTokenService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeScreen extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    private DashboardFragment dashboardFragment=new DashboardFragment();

    private GroupFragment groupFragment = new GroupFragment();
    private ProfileFragment profileFragment=new ProfileFragment();

    private EventsInCalendarViewFragment eventsInCalendarViewFragment =new EventsInCalendarViewFragment();

    private AchievementsOfUserFragment achievementsOfUserFragment =new AchievementsOfUserFragment();

    private JwtTokenService jwtTokenService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home_screen);
        jwtTokenService = new JwtTokenService(getApplicationContext());
        checkIfAlreadyLoggedIn();
        findByIds();
        populateDataInBottomNavigationView();
        setDefaultOptionBottomNavigation();
        setOnClickListeners();
    }

    private void checkIfAlreadyLoggedIn(){

        if(!jwtTokenService.hasValidToken()){
            Intent intent = new Intent(this, signin.class);
            startActivity(intent);
            finish();
        }

    }

    private void setDefaultOptionBottomNavigation() {

        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        switchFragment(dashboardFragment);
    }

    private void setOnClickListeners() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getTitle().toString()) {
                    case "Dashboard":
                        switchFragment(dashboardFragment);
                        return true;
                    case "Chat":
                        switchFragment(groupFragment);
                        return true;
                    case "Calendar":
                        switchFragment(eventsInCalendarViewFragment);
                        return true;
                    case "Achievements":
                        switchFragment(achievementsOfUserFragment);
                        return true;
                    case "Profile":
                        switchFragment(profileFragment);
                        return true;
                }
                return false;
            }
        });
    }

    private void populateDataInBottomNavigationView() {
        bottomNavigationView.inflateMenu(R.menu.bottom_navigation_menu);
    }



    public void switchFragment(Fragment fragment)
    {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
    }
    private void findByIds(){
        bottomNavigationView=findViewById(R.id.bottomnavigation);
    }
}
