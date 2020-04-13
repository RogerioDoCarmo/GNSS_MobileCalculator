package com.rogeriocarmo.gnss_mobilecalculator.NewApp;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.rogeriocarmo.gnss_mobilecalculator.Controller.SingletronController;
import com.rogeriocarmo.gnss_mobilecalculator.Model.CoordenadaGeodesica;
import com.rogeriocarmo.gnss_mobilecalculator.R;
import com.rogeriocarmo.gnss_mobilecalculator.View.Fragment_About;
import com.rogeriocarmo.gnss_mobilecalculator.View.Fragment_Analysis_Epch;
import com.rogeriocarmo.gnss_mobilecalculator.View.Fragment_GoogleMaps;
import com.rogeriocarmo.gnss_mobilecalculator.View.Fragment_Import;
import com.rogeriocarmo.gnss_mobilecalculator.View.Fragment_Main;
import com.rogeriocarmo.gnss_mobilecalculator.View.Fragment_RecyclerView_Epchs;
import com.rogeriocarmo.gnss_mobilecalculator.View.Fragment_ShowRINEX;
import com.rogeriocarmo.gnss_mobilecalculator.View.Fragment_ShowResults;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        Fragment_Main.OnFragmentInteractionListener,
        Fragment_Import.OnFragmentInteractionListener,
        Fragment_ShowResults.OnFragmentInteractionListener,
        Fragment_ShowRINEX.OnFragmentInteractionListener,
        Fragment_GoogleMaps.OnFragmentInteractionListener,
        Fragment_About.OnFragmentInteractionListener,
        Fragment_RecyclerView_Epchs.OnListFragmentInteractionListener,
        Fragment_Analysis_Epch.OnFragmentInteractionListener
{

    SingletronController controller;
    static NavigationView navigationView;
    static DrawerLayout drawerCopy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.side_bar_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawerCopy = drawer;
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        definir_fragment_inicial();

        controller = SingletronController.getInstance();
        definir_sidebar_inativa();
    }

    private void definir_sidebar_inativa(){
        Menu menuNav = navigationView.getMenu();

        MenuItem navItem1 = menuNav.findItem(R.id.menu_import_files);
        navItem1.setEnabled(false);

        MenuItem navItem2 = menuNav.findItem(R.id.menu_show_results);
        navItem2.setEnabled(true);

        MenuItem navItem3 = menuNav.findItem(R.id.menu_list_epchs);
        navItem3.setEnabled(false);

        MenuItem navItem4 = menuNav.findItem(R.id.menu_analise_epch);
        navItem4.setEnabled(false);

        MenuItem navItem5 = menuNav.findItem(R.id.menu_show_maps);
        navItem5.setEnabled(true);

        MenuItem navItem6 = menuNav.findItem(R.id.menu_save_rinex);
        navItem6.setEnabled(true);
    }

    public static void definir_sidebar_ativa(){
        Menu menuNav = navigationView.getMenu();

        MenuItem navItem2 = menuNav.findItem(R.id.menu_show_results);
        navItem2.setEnabled(true);

        MenuItem navItem3 = menuNav.findItem(R.id.menu_list_epchs);
        navItem3.setEnabled(true);

        MenuItem navItem4 = menuNav.findItem(R.id.menu_show_maps);
        navItem4.setEnabled(true);

        MenuItem navItem5 = menuNav.findItem(R.id.menu_save_rinex);
        navItem5.setEnabled(true);

        MenuItem navItem6 = menuNav.findItem(R.id.menu_analise_epch);
        navItem6.setEnabled(true);

//        drawerCopy.openDrawer(GravityCompat.START); FIXME
//        Toast.makeText(mContext, "Processamento concluído!!!", Toast.LENGTH_SHORT).show();
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
//        fragmentTransaction.addToBackStack(null); TODO
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() { // TODO REVISAR PARA FUNCIONAR CORRETAMETE
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Fragment fragment = null;
        Class fragmentClass = null;
        Bundle bundle = new Bundle();

        int id = item.getItemId();

        switch (id) {
            case R.id.menu_import_files:
                fragmentClass = Fragment_Import.class;
                break;
            case R.id.menu_show_results:
                fragmentClass = Fragment_ShowResults.class;
                break;
            case R.id.menu_list_epchs:
                fragmentClass = Fragment_RecyclerView_Epchs.class;
                break;
            case R.id.menu_analise_epch:
                fragmentClass = Fragment_Analysis_Epch.class;
                break;
            case R.id.menu_show_maps:
                fragmentClass = Fragment_GoogleMaps.class;
                ArrayList<CoordenadaGeodesica> valores = controller.getResultadosGeodeticos();
                bundle.putParcelableArrayList("Coord", valores); //TODO Obter direto no Fragment do Maps
                break;
            case R.id.menu_save_rinex:
                fragmentClass = Fragment_ShowRINEX.class;
                break;
            case R.id.menu_show_about:
                fragmentClass = Fragment_About.class;
                break;
            default:
                definir_fragment_inicial();
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

    @Override
    public void onListFragmentInteraction() {

    }
}
