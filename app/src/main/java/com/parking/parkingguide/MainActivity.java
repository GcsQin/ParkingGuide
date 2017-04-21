package com.parking.parkingguide;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.apache.log4j.chainsaw.Main;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    ActionBarDrawerToggle mToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        initView();
        initToolBarAnadDrawableLayout();
    }
    private void initView(){
        drawerLayout= (DrawerLayout) findViewById(R.id.dlMain);
        toolbar=(Toolbar)findViewById(R.id.toolBar);
    }
    private void initToolBarAnadDrawableLayout(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToggle=new ActionBarDrawerToggle(MainActivity.this,drawerLayout,toolbar,R.string.open_draw,R.string.close_draw){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }
        };
        drawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
    }


}
