package mx.com.cesarcorona.directorio;

import android.*;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import mx.com.cesarcorona.directorio.activities.BaseAnimatedActivity;
import mx.com.cesarcorona.directorio.activities.CategoriaActivity;
import mx.com.cesarcorona.directorio.activities.ClasificadosActivity;
import mx.com.cesarcorona.directorio.activities.ClasificadosCategoriasActivity;
import mx.com.cesarcorona.directorio.activities.CloseTomeActivity;
import mx.com.cesarcorona.directorio.activities.NoticiasActivity;
import mx.com.cesarcorona.directorio.activities.PromocionesActivity;
import mx.com.cesarcorona.directorio.activities.SearchActivity;
import mx.com.cesarcorona.directorio.activities.SubirClasificadoActivity;

public class MainActivity extends BaseAnimatedActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private CardView categoriaCard;
    private CardView seachCard;
    private CardView promosCard;
    private CardView clasificadosCard;
    private CardView closeToMeCard;
    private CardView noticiasCard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initViewsAndStartLisening();

        checkPermissions();

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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if(id == R.id.pub_clas){
            Intent clasificadoIntent = new Intent(MainActivity.this, SubirClasificadoActivity.class);
            startActivity(clasificadoIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initViewsAndStartLisening(){
        categoriaCard = (CardView) findViewById(R.id.categoria_card_view);
        categoriaCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent categorysActivity = new Intent(MainActivity.this, CategoriaActivity.class);
                startActivity(categorysActivity);
            }
        });

        seachCard = (CardView) findViewById(R.id.search_card_view);
        seachCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent categorysActivity = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(categorysActivity);
            }
        });

        promosCard = (CardView)findViewById(R.id.promo_card_view);
        promosCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent categorysActivity = new Intent(MainActivity.this, PromocionesActivity.class);
                startActivity(categorysActivity);
            }
        });

        clasificadosCard = (CardView)findViewById(R.id.clasificados_card);
        clasificadosCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent clasificadosIntent = new Intent(MainActivity.this, ClasificadosCategoriasActivity.class);
                startActivity(clasificadosIntent);
            }
        });

        closeToMeCard = (CardView)findViewById(R.id.close_to_me);
        closeToMeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent clasificadosIntent = new Intent(MainActivity.this, CloseTomeActivity.class);
                startActivity(clasificadosIntent);
            }
        });

        noticiasCard = (CardView)findViewById(R.id.noticiasCard);
        noticiasCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent noticiasIntent = new Intent(MainActivity.this, NoticiasActivity.class);
                startActivity(noticiasIntent);
            }
        });

    }


    private void checkPermissions(){
        Dexter.withActivity(this)
                .withPermissions(
                        android.Manifest.permission.ACCESS_NETWORK_STATE,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if(!report.areAllPermissionsGranted()){
                    Toast.makeText(MainActivity.this,"",Toast.LENGTH_LONG).show();
                }


            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

            }
        }).check();
    }
}
