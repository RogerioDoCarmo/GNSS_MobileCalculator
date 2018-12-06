package View;

import android.support.v4.app.FragmentManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.rogeriocarmo.gnss_mobilecalculator.R;

import java.util.ArrayList;

import Controller.SingletronController;
import Model.CoordenadaGeodesica;

public class SideBar extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
            Fragment_Main.OnFragmentInteractionListener,
            Fragment_Import.OnFragmentInteractionListener,
            Fragment_SaveTXT.OnFragmentInteractionListener,
            Fragment_SaveRINEX.OnFragmentInteractionListener,
            Fragment_GoogleMaps.OnFragmentInteractionListener,
            Fragment_About.OnFragmentInteractionListener{

    SingletronController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_side_bar);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        definir_fragment_inicial();

        controller =  SingletronController.getInstance();
    }

    private void definir_fragment_inicial() {
        Fragment fragment = null;
        Class fragmentClass = Fragment_Main.class;

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flContent, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.side_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_exit) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        Fragment fragment = null;
        Class fragmentClass = null;
        Bundle bundle = new Bundle();

        int id = item.getItemId();

        if (id == R.id.nav_camera) { // TODO: USAR UM SWITCH
            fragmentClass = Fragment_Import.class;
        } else if (id == R.id.save_txt) {
            fragmentClass = Fragment_SaveTXT.class;
        } else if (id == R.id.show_maps) {
            fragmentClass = Fragment_GoogleMaps.class;

            ArrayList<CoordenadaGeodesica> valores =  controller.getResultadosGeodeticos();

            bundle.putParcelableArrayList("Coord", valores);

        } else if (id == R.id.save_rinex) {
            fragmentClass = Fragment_SaveRINEX.class;
        } else if (id == R.id.show_about) {
            fragmentClass = Fragment_About.class;
        }

        if (fragmentClass != null) {
            try {
                fragment = (Fragment) fragmentClass.newInstance();

                if (!bundle.isEmpty()){
                    fragment.setArguments(bundle);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.flContent, fragment);
            fragmentTransaction.commit();
        }

        setTitle(item.getTitle());

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }
}
