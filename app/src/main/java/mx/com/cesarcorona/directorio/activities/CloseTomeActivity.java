package mx.com.cesarcorona.directorio.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.firebase.geofire.example.Example;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.Random;

import mx.com.cesarcorona.directorio.MainActivity;
import mx.com.cesarcorona.directorio.R;
import mx.com.cesarcorona.directorio.Utils.DateUtils;
import mx.com.cesarcorona.directorio.adapter.NegocioPorCategoriaAdapter;
import mx.com.cesarcorona.directorio.custom.ExpandableGridView;
import mx.com.cesarcorona.directorio.pojo.Categoria;
import mx.com.cesarcorona.directorio.pojo.Negocio;
import mx.com.cesarcorona.directorio.pojo.PremiumBanner;

import static mx.com.cesarcorona.directorio.activities.CategoriaActivity.ALL_NEGOCIO_REFERENCE;
import static mx.com.cesarcorona.directorio.activities.CategoriaActivity.PREMIUM_REFERENCE;
import static mx.com.cesarcorona.directorio.activities.NegocioPorCategoriaActivity.NEGOCIOS_REFERENCE;

public class CloseTomeActivity extends BaseAnimatedActivity implements NegocioPorCategoriaAdapter.NegocioSelectedListener {

    public static String GEO_REFERENCE = "geohash";
    public static String ALL_NEGOCIOS_RFERENCE ="allnegocios";


    public static String TAG = CloseTomeActivity.class.getSimpleName();

    public static int ELEMENT_SIZE = 120;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 100;



    private DatabaseReference mDatabase;
    private LinkedList<Negocio> allNegocios;
    private LinkedList<Negocio> openNegocios,closedNegocios;
    private ExpandableGridView openNegociosGrid,closedNegociosGrid;
    private NegocioPorCategoriaAdapter negocioPorCategoriaAdapterClosed,negocioPorCategoriaAdapterOpen;
    private ProgressDialog pDialog;
    private Categoria categoriaSelected;

    private CardView categoryPromo;
    private ImageView bannerPromo;
    private Negocio negocionOnMainBanner;
    private LinkedList<PremiumBanner> premiumNegocios;
    static Random rand = new Random();
    private GeoFire geoFire;
    private FusedLocationProviderClient mFusedLocationClient;
    private double latitud;
    private double longitud;
    private Location currentLocation;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_close_tome);
        initialize();
        showPremiunBanner();

        showpDialog();
        initLocation();


        categoryPromo = (CardView)findViewById(R.id.categoria_card_view);
        bannerPromo = (ImageView) findViewById(R.id.banner_promo);
        categoryPromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(negocionOnMainBanner != null){
                    Intent detailIntent = new Intent(CloseTomeActivity.this,NegocioDetailActivity.class);
                    Bundle extras = new Bundle();
                    extras.putSerializable(NegocioDetailActivity.KEY_NEGOCIO,negocionOnMainBanner);
                    detailIntent.putExtras(extras);
                    startActivity(detailIntent);
                }

            }
        });


        ImageView back_button= (ImageView)findViewById(R.id.back_arrow_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CloseTomeActivity.super.onBackPressed();
            }
        });

        ImageView homeButton=(ImageView)findViewById(R.id.home_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(CloseTomeActivity.this, MainActivity.class);
                startActivity(mainIntent);
            }
        });


    }

    private void initialize(){
//        mDatabase = FirebaseDatabase.getInstance().getReference( NEGOCIOS_REFERENCE +"/" +categoriaSelected.getDataBaseReference());
        openNegociosGrid = (ExpandableGridView) findViewById(R.id.opened_negocios_grid_view);
        closedNegociosGrid = (ExpandableGridView) findViewById(R.id.close_negocios_grid_view);
        allNegocios = new LinkedList<>();
        openNegocios = new LinkedList<>();
        premiumNegocios = new LinkedList<>();
        closedNegocios = new LinkedList<>();
        pDialog = new ProgressDialog(CloseTomeActivity.this);
        pDialog.setMessage("Por favor espera...");
        pDialog.setCancelable(false);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(GEO_REFERENCE);
         geoFire= new GeoFire(databaseReference);
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private void initLocation(){
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(CloseTomeActivity.this);
        centerMapOnCurrentLocation();


    }

    private void centerMapOnCurrentLocation(){
        if (ContextCompat.checkSelfPermission(CloseTomeActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(CloseTomeActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block


            } else {


                ActivityCompat.requestPermissions(CloseTomeActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

            }
        } else {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(CloseTomeActivity.this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                latitud = location.getLatitude();
                                longitud = location.getLongitude();
                                currentLocation = location;
                                recoveryNegocios();

                            }
                        }
                    });
        }
    }




    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    mFusedLocationClient.getLastLocation()
                            .addOnSuccessListener(CloseTomeActivity.this, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location != null) {
                                        latitud = location.getLatitude();
                                        longitud = location.getLongitude();
                                        currentLocation = location;
                                        recoveryNegocios();

                                    }
                                }
                            });

                } else {

                    //Negaron los permisos
                    hidepDialog();
                }
                return;
            }


        }
    }





    private void recoveryNegocios(){
        GeoQuery geoQuery;

        if(currentLocation == null ){
            Toast.makeText(CloseTomeActivity.this,"No hemos podido encontrar tu ubicación ",Toast.LENGTH_LONG).show();
        }else{
            geoQuery= geoFire.queryAtLocation(new GeoLocation(currentLocation.getLatitude(),currentLocation.getLongitude()),1.5);
            geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                @Override
                public void onKeyEntered(String key, GeoLocation location) {
                    Log.e(TAG,"Business match:" + key);

                    mDatabase = FirebaseDatabase.getInstance().getReference("Example/"+ALL_NEGOCIOS_RFERENCE +"/" +key);
                    mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                                Negocio negocio = dataSnapshot.getValue(Negocio.class);
                                negocio.setNegocioDataBaseReference(dataSnapshot.getRef().toString());
                                if(!allNegocios.contains(negocio) && negocio.getPublicado()!= null && negocio.getPublicado().equals("Si")){
                                    allNegocios.add(negocio);
                                    clasifyOpenOrclosed(negocio);

                                }



                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            hidepDialog();


                        }
                    });


                }

                @Override
                public void onKeyExited(String key) {
                    hidepDialog();


                }

                @Override
                public void onKeyMoved(String key, GeoLocation location) {
                    hidepDialog();


                }

                @Override
                public void onGeoQueryReady() {

                }

                @Override
                public void onGeoQueryError(DatabaseError error) {
                    hidepDialog();


                }
            });

        }
        hidepDialog();


    }


    @Override
    public void OnNegocioClicked(Negocio negocio) {
        Intent negocioDetailIntent = new Intent(CloseTomeActivity.this,NegocioDetailActivity.class);
        Bundle extras = new Bundle();
        extras.putSerializable(NegocioDetailActivity.KEY_NEGOCIO,negocio);
        negocioDetailIntent.putExtras(extras);
        startActivity(negocioDetailIntent);
    }

    private void clasifyOpenOrclosed(Negocio negocio){

            if(isOpenNow(negocio)){
                openNegocios.add(negocio);
            }else{
                closedNegocios.add(negocio);
            }


        negocioPorCategoriaAdapterClosed = new NegocioPorCategoriaAdapter(closedNegocios,CloseTomeActivity.this);
        negocioPorCategoriaAdapterOpen = new NegocioPorCategoriaAdapter(openNegocios,CloseTomeActivity.this);
        negocioPorCategoriaAdapterClosed.setLocation(currentLocation);
        negocioPorCategoriaAdapterOpen.setLocation(currentLocation);

        negocioPorCategoriaAdapterClosed.setNegocioSelectedListener(CloseTomeActivity.this);
        negocioPorCategoriaAdapterOpen.setNegocioSelectedListener(CloseTomeActivity.this);
        openNegociosGrid.setAdapter(negocioPorCategoriaAdapterOpen);
        openNegociosGrid.setExpanded(true);
        openNegociosGrid.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, openNegocios.size() * ELEMENT_SIZE));
        //openNegociosGrid.setMinimumHeight(allNegocios.size() * ELEMENT_SIZE);
        closedNegociosGrid.setAdapter(negocioPorCategoriaAdapterClosed);
        closedNegociosGrid.setExpanded(true);
        // closedNegociosGrid.setMinimumHeight(allNegocios.size() * ELEMENT_SIZE);
        closedNegociosGrid.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, closedNegocios.size() * ELEMENT_SIZE));




    }



    public static boolean isOpenNow(Negocio negocio){
           boolean isOpenNow = false;
        if(negocio.getAbierto_24_horas()!= null && negocio.getAbierto_24_horas().equals("Si")){
            return  true;
        }else{
            if(negocio.hoyAbre()){
                    String todayKeys[] = DateUtils.getCurrentDatKeys();
                    if(todayKeys!= null){
                        String startHour =null;
                        String endHour = null;
                        if(negocio.getDiasAbiertos() != null){
                            startHour = negocio.getDiasAbiertos().get(todayKeys[0]);
                            endHour = negocio.getDiasAbiertos().get(todayKeys[1]);
                            if(DateUtils.isNowInInterval(startHour,endHour)){
                                return  true;
                            }else{
                                return false;
                            }
                        }else{
                            return  false;
                        }


                    }


            }else{
                return  false;
            }
        }



        /*   if(negocio.hoyAbre()){
                  if(negocio.getAbierto_24_horas()!= null && negocio.getAbierto_24_horas().equals("Si")){
                      return  true;
                  }else{
                      String todayKeys[] = DateUtils.getCurrentDatKeys();
                      if(todayKeys!= null){
                          String startHour =null;
                          String endHour = null;
                          if(negocio.getDiasAbiertos() != null){
                              startHour = negocio.getDiasAbiertos().get(todayKeys[0]);
                              endHour = negocio.getDiasAbiertos().get(todayKeys[1]);
                              if(DateUtils.isNowInInterval(startHour,endHour)){
                                  return  true;
                              }else{
                                  return false;
                              }
                          }else{
                              return  false;
                          }


                      }
                  }

           }else{
               return  false;
           }*/

           return isOpenNow;

    }




    private void showPremiunBanner(){
        DatabaseReference premiumReference = FirebaseDatabase.getInstance().getReference("Example"+"/"+PREMIUM_REFERENCE +"/" +"premium1");
        premiumReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                PremiumBanner premiumBanner =dataSnapshot.getValue(PremiumBanner.class);
                premiumNegocios.add(premiumBanner);

                if(premiumNegocios.size()>0){
                    DatabaseReference choosedPremiumNegocioReference =FirebaseDatabase.getInstance().getReference("Example"+"/"+ALL_NEGOCIO_REFERENCE +"/" +
                            premiumNegocios.get(0).getId_negocio());
                    choosedPremiumNegocioReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            negocionOnMainBanner = dataSnapshot.getValue(Negocio.class);
                            if(negocionOnMainBanner.getBanner_premium() == null){

                                FirebaseCrash.log(TAG+": No hay banner en negocio premium" );
                                //show default banner
                                Picasso.with(CloseTomeActivity.this).load(R.drawable.hi_res_logo).fit().into(bannerPromo);
                                hidepDialog();

                            }else{
                                Picasso.with(CloseTomeActivity.this).load(negocionOnMainBanner.getBanner_premium()).fit().into(bannerPromo);
                                hidepDialog();

                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            //show default banner
                            Picasso.with(CloseTomeActivity.this).load(R.drawable.hi_res_logo).fit().into(bannerPromo);
                            hidepDialog();


                        }
                    });

                }else{
                    //show defalt banner
                    Picasso.with(CloseTomeActivity.this).load(R.drawable.hi_res_logo).fit().into(bannerPromo);
                    hidepDialog();


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //show default banner
                Picasso.with(CloseTomeActivity.this).load(R.drawable.hi_res_logo).fit().into(bannerPromo);
                hidepDialog();


            }
        });

    }

    private  PremiumBanner getRandomChestItem() {
        try {
            return premiumNegocios.get(rand.nextInt(premiumNegocios.size()));
        }
        catch (Throwable e){
            return null;
        }
    }
    
    
    
}
