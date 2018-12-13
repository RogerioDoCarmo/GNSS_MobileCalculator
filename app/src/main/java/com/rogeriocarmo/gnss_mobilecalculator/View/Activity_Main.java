package com.rogeriocarmo.gnss_mobilecalculator.View;

import android.os.Environment;
import android.support.annotation.NonNull;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.rogeriocarmo.gnss_mobilecalculator.R;

import java.io.File;
import java.util.ArrayList;

import com.rogeriocarmo.gnss_mobilecalculator.Controller.SingletronController;
import com.rogeriocarmo.gnss_mobilecalculator.Model.CoordenadaGeodesica;

public class Activity_Main extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
            Fragment_Main.OnFragmentInteractionListener,
            Fragment_Import.OnFragmentInteractionListener,
            Fragment_SaveTXT.OnFragmentInteractionListener,
            Fragment_SaveRINEX.OnFragmentInteractionListener,
            Fragment_GoogleMaps.OnFragmentInteractionListener,
            Fragment_About.OnFragmentInteractionListener,
            Fragment_RecyclerView_Epchs.OnListFragmentInteractionListener
        {

    SingletronController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.side_bar_activity);
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

        /*
         *Epocas Boas
         * ID = 313
         * ID = 298 ==> A MELHOR!!!!
         * ID = 212
         * ID = 227
         * */
        controller = SingletronController.getInstance();
        controller.processamento_completo(getApplicationContext());
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

//        if (getFragmentManager().getBackStackEntryCount() == 0) {
//            this.finish();
//        } else {
//            getFragmentManager().popBackStack();
//        }

//        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
//            getSupportFragmentManager().popBackStack();
//        } else {
//            finish();
//        }
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

    private void abrir_arquivo(){
//        File mPath = new File(Environment.getExternalStorageDirectory() + "//DIR//");
//        FileDialog fileDialog = new FileDialog(this, mPath, ".txt");
//        fileDialog.addFileListener(new FileDialog.FileSelectedListener() {
//            public void fileSelected(File file) {
//                Log.d(getClass().getName(), "selected file " + file.toString());
//            }
//        });
//        //fileDialog.addDirectoryListener(new FileDialog.DirectorySelectedListener() {
//        //  public void directorySelected(File directory) {
//        //      Log.d(getClass().getName(), "selected dir " + directory.toString());
//        //  }
//        //});
//        //fileDialog.setSelectDirectoryOption(false);
//        fileDialog.showDialog();

        /////////////////////////////////////////////////////////////////////////////////////////////////
        //Create FileOpenDialog and register a callback
        /////////////////////////////////////////////////////////////////////////////////////////////////
        final String copy;

        SimpleFileDialog FileOpenDialog =  new SimpleFileDialog(Activity_Main.this, "FileOpen",
                new SimpleFileDialog.SimpleFileDialogListener()
                {
                    @Override
                    public void onChosenDir(String chosenDir)
                    {
                        // The code in this function will be executed when the dialog OK button is pushed
                        String m_chosen = chosenDir;
//                        copy = m_chosen;
                        File newFile = new File(m_chosen);
//                        fileCopy = newFile;
                        Toast.makeText(getApplicationContext(), "Chosen FileOpenDialog File: " +
                                m_chosen, Toast.LENGTH_LONG).show();
                    }
                });

        //You can change the default filename using the public variable "Default_File_Name"
        FileOpenDialog.Default_File_Name = "";
        FileOpenDialog.chooseFile_or_Dir();

        /////////////////////////////////////////////////////////////////////////////////////////////////

        File selected = FileOpenDialog.getFileSelected();


    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Fragment fragment = null;
        Class fragmentClass = null;
        Bundle bundle = new Bundle();

        int id = item.getItemId();

        switch (id) {
            case R.id.import_files:
//                fragmentClass = Fragment_Import.class;
                abrir_arquivo();
                break;
            case R.id.save_txt:
                fragmentClass = Fragment_SaveTXT.class;
                break;
            case R.id.list_epchs:
                fragmentClass = Fragment_RecyclerView_Epchs.class;
                break;
            case R.id.show_maps:
                fragmentClass = Fragment_GoogleMaps.class;
                ArrayList<CoordenadaGeodesica> valores = controller.getResultadosGeodeticos();
                bundle.putParcelableArrayList("Coord", valores);
                break;
            case R.id.save_rinex:
                fragmentClass = Fragment_SaveRINEX.class;
                break;
            case R.id.show_about:
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

            // public void onListFragmentInteraction(DummyContent.DummyItem item)
            @Override
            public void onListFragmentInteraction() {

            }
        }
