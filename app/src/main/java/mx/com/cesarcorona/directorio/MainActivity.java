package mx.com.cesarcorona.directorio;

import android.*;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.Date;
import java.util.List;

import mx.com.cesarcorona.directorio.activities.BaseAnimatedActivity;
import mx.com.cesarcorona.directorio.activities.CategoriaActivity;
import mx.com.cesarcorona.directorio.activities.ClasificadosActivity;
import mx.com.cesarcorona.directorio.activities.ClasificadosCategoriasActivity;
import mx.com.cesarcorona.directorio.activities.CloseTomeActivity;
import mx.com.cesarcorona.directorio.activities.LoginRegisterActivity;
import mx.com.cesarcorona.directorio.activities.MisNegociosActivity;
import mx.com.cesarcorona.directorio.activities.NoticiasActivity;
import mx.com.cesarcorona.directorio.activities.PromocionesActivity;
import mx.com.cesarcorona.directorio.activities.PublicarNegocioActivity;
import mx.com.cesarcorona.directorio.activities.PublicarPromocionActivity;
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
    private ShareActionProvider mShareActionProvider;

    private boolean showLogout;
    private int toucheNumber;

    private NavigationView navigationView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        showLogout = false;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                String shareBody = "Estoy usando Ph Movil desde mi Android,http://www.phmovil.com";
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Compartir via"));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

         navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ImageView logoPHMOVIL = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.imageView);
        hideItem();

        logoPHMOVIL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(toucheNumber < 7){
                    toucheNumber++;
                }else{
                    Toast.makeText(MainActivity.this,"Salir habilitado",Toast.LENGTH_LONG).show();
                    showItem();
                }
            }
        });


        initViewsAndStartLisening();

        checkPermissions();




    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

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


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_publicar) {
            if(FirebaseAuth.getInstance().getCurrentUser()== null){
                Intent publicarNegocioInent = new Intent(MainActivity.this, LoginRegisterActivity.class);
                startActivity(publicarNegocioInent);
            }else{
                Intent publicarNegocioInent = new Intent(MainActivity.this, PublicarNegocioActivity.class);
                startActivity(publicarNegocioInent);
            }


        } else if (id == R.id.pub_clas) {
            Intent clasificadoIntent = new Intent(MainActivity.this, SubirClasificadoActivity.class);
            startActivity(clasificadoIntent);
        } else if(id ==R.id.nav_about_us){
            try {
                Intent intent= new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.phmovil.com/acercade"));
                startActivity(intent);
            }catch (Exception e){
                Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
            }


        }  else if(id == R.id.nav_mis_megocios){
            if(FirebaseAuth.getInstance().getCurrentUser()== null){
                Intent publicarNegocioInent = new Intent(MainActivity.this, LoginRegisterActivity.class);
                startActivity(publicarNegocioInent);
            }else{
                Intent publicarNegocioInent = new Intent(MainActivity.this, MisNegociosActivity.class);
                startActivity(publicarNegocioInent);
            }

        }else if(id ==R.id.nav_promociones){
            if(FirebaseAuth.getInstance().getCurrentUser()== null){
                Intent publicarNegocioInent = new Intent(MainActivity.this, LoginRegisterActivity.class);
                startActivity(publicarNegocioInent);
            }else{
                Intent publicarNegocioInent = new Intent(MainActivity.this, PublicarPromocionActivity.class);
                startActivity(publicarNegocioInent);
            }

        }else if(id ==R.id.nav_terminos){
            try {
                Intent intent= new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.phmovil.com/terminosycondiciones"));
                startActivity(intent);
            }catch (Exception e){

            }



            }else if(id ==R.id.nav_salir){
            if(FirebaseAuth.getInstance().getCurrentUser()== null){

            }else{
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(MainActivity.this,"Usuario deslogeado",Toast.LENGTH_LONG).show();
            }

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
              /*  Intent noticiasIntent = new Intent(MainActivity.this, NoticiasActivity.class);
                startActivity(noticiasIntent);*/

                new SingleDateAndTimePickerDialog.Builder(MainActivity.this)
                        .mustBeOnFuture()
                        //.bottomSheet()
                        //.curved()
                        //.minutesStep(15)

                        //.displayHours(false)
                        //.displayMinutes(false)

                        //.todayText("aujourd'hui")

                        .displayListener(new SingleDateAndTimePickerDialog.DisplayListener() {
                            @Override
                            public void onDisplayed(SingleDateAndTimePicker picker) {
                                //retrieve the SingleDateAndTimePicker
                            }
                        })

                        .title("Seleccione dia y hora")
                        .listener(new SingleDateAndTimePickerDialog.Listener() {
                            @Override
                            public void onDateSelected(Date date) {
                                Toast.makeText(MainActivity.this,date.toString(),Toast.LENGTH_LONG).show();
                            }
                        }).display();

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
                    Toast.makeText(MainActivity.this,"Es necesario aceptar los permisos para localizar los negocios",Toast.LENGTH_LONG).show();
                }


            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

            }
        }).check();
    }


    private void hideItem()
    {
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_salir).setVisible(false);
    }

    private void showItem(){
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_salir).setVisible(true);
    }
}
