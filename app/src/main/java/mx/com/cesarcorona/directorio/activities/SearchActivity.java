package mx.com.cesarcorona.directorio.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.Normalizer;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import mx.com.cesarcorona.directorio.MainActivity;
import mx.com.cesarcorona.directorio.R;
import mx.com.cesarcorona.directorio.Utils.DateUtils;
import mx.com.cesarcorona.directorio.adapter.NegocioPorCategoriaAdapter;
import mx.com.cesarcorona.directorio.custom.ExpandableGridView;
import mx.com.cesarcorona.directorio.pojo.Negocio;

import static mx.com.cesarcorona.directorio.activities.CloseTomeActivity.ALL_NEGOCIOS_RFERENCE;
import static mx.com.cesarcorona.directorio.activities.CloseTomeActivity.ELEMENT_SIZE;
import static mx.com.cesarcorona.directorio.activities.CloseTomeActivity.GEO_REFERENCE;
import static mx.com.cesarcorona.directorio.activities.CloseTomeActivity.isOpenNow;
import static mx.com.cesarcorona.directorio.activities.NegocioPorCategoriaActivity.NEGOCIOS_REFERENCE;

/**
 * Created by ccabrera on 15/08/17.
 */

public class SearchActivity extends BaseAnimatedActivity  implements NegocioPorCategoriaAdapter.NegocioSelectedListener {


    public static final String TAG = SearchActivity.class.getSimpleName();
    public static String CATEGORY_REFERENCE = "categorias";
    public static String ALL_NEGOCIOS_REFERENCE = "Example/allnegocios";
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 100;


    private EditText textSearch;
    private Button buttonSearch;
    private CheckBox abierto, alimentos, tipo, aDomicilio, masCercano, always, creditCard, promocion;

    private DatabaseReference mDatabase;
    private LinkedList<Negocio> allNegocios;
    private LinkedList<Negocio> filteredNegocios;
    private ProgressDialog pDialog;
    private GridView resultGrid;
    private boolean needFromWebSeach;

    private GeoFire geoFire;
    private FusedLocationProviderClient mFusedLocationClient;
    private double latitud;
    private double longitud;
    private Location currentLocation;

    private LinkedList<Negocio> abiertosNegocios;
    private LinkedList<Negocio> alimentosNegocios;
    private LinkedList<Negocio> servicioDomicilio;
    private LinkedList<Negocio> masCercanoNegocios;
    private LinkedList<Negocio> abiertos24;
    private LinkedList<Negocio> aceptanTerjeta;
    private LinkedList<Negocio> conPromocion;
    private LinkedList<Negocio> textNegocios;
    private LinkedList<Negocio> todosLosFiltros;
    private LinkedList<String> negocioCercanoREference;


    private LinkedList<Negocio> openNegocios,closedNegocios;
    private ExpandableGridView openNegociosGrid,closedNegociosGrid;
    private NegocioPorCategoriaAdapter negocioPorCategoriaAdapterClosed,negocioPorCategoriaAdapterOpen;








    public static final int ABIERTO_STEP = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.search_dialog_layout);
        textSearch = (EditText) findViewById(R.id.search_edit_text);
        buttonSearch = (Button) findViewById(R.id.search_button_filter);
        abierto = (CheckBox) findViewById(R.id.open_filter);
        alimentos = (CheckBox) findViewById(R.id.food_filter);
        tipo = (CheckBox) findViewById(R.id.food_type_filter);
        aDomicilio = (CheckBox) findViewById(R.id.delivery_filter);
        masCercano = (CheckBox) findViewById(R.id.closest_filter);
        always = (CheckBox) findViewById(R.id.always_open_filter);
        creditCard = (CheckBox) findViewById(R.id.credit_filter);
        promocion = (CheckBox) findViewById(R.id.prom_filter);
        resultGrid = (GridView) findViewById(R.id.result_grid);
        mDatabase = FirebaseDatabase.getInstance().getReference(ALL_NEGOCIOS_REFERENCE);
        allNegocios = new LinkedList<>();
        filteredNegocios = new LinkedList<>();
        negocioCercanoREference = new LinkedList<>();
        needFromWebSeach = false;
        filteredNegocios = new LinkedList<>();
        pDialog = new ProgressDialog(SearchActivity.this);
        pDialog.setMessage("Por favor espera...");
        pDialog.setCancelable(false);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(GEO_REFERENCE);
        geoFire = new GeoFire(databaseReference);
        //  recoveryNegocios();
        initLocation();
        //recoveryNegocios();


        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filteredNegocios = new LinkedList<>();
                startSearch();
            }
        });


        ImageView back_button = (ImageView) findViewById(R.id.back_arrow_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchActivity.super.onBackPressed();
            }
        });

        ImageView homeButton = (ImageView) findViewById(R.id.home_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(SearchActivity.this, MainActivity.class);
                startActivity(mainIntent);
            }
        });


        openNegociosGrid = (ExpandableGridView) findViewById(R.id.opened_negocios_grid_view);
        closedNegociosGrid = (ExpandableGridView) findViewById(R.id.close_negocios_grid_view);
        allNegocios = new LinkedList<>();
        openNegocios = new LinkedList<>();
        closedNegocios = new LinkedList<>();


    } // Fin onCreate()


    private void searchBYAbierto() {
        if (abierto.isChecked()) {
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_WEEK);
            String paramDay = "";
            switch (day) {
                case Calendar.SUNDAY:
                    // Current day is Sunday
                    paramDay = "abre_domingo";
                    break;

                case Calendar.MONDAY:
                    // Current day is Monday
                    paramDay = "abre_lunes";


                    break;

                case Calendar.TUESDAY:
                    paramDay = "abre_martes";


                    break;
                case Calendar.THURSDAY:
                    paramDay = "abre_miercoles";


                    break;
                case Calendar.WEDNESDAY:
                    paramDay = "abre_jueves";


                    break;
                case Calendar.FRIDAY:
                    paramDay = "abre_viernes";


                    break;
                case Calendar.SATURDAY:
                    paramDay = "abre_sabado";

                    break;
                // etc.
            }
            String currentKeys[] = DateUtils.getCurrentDatKeys();

            abiertosNegocios = new LinkedList<>();
            final DatabaseReference allnegocios = FirebaseDatabase.getInstance().getReference(ALL_NEGOCIOS_REFERENCE);
            Query query = allnegocios.orderByChild(paramDay).equalTo("Si");

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot negocioSnap : dataSnapshot.getChildren()) {
                        Negocio negocio = negocioSnap.getValue(Negocio.class);
                        negocio.setNegocioDataBaseReference(negocioSnap.getKey());
                        if (isOpenNow(negocio)) {
                            abiertosNegocios.add(negocio);
                        }
                    }
                    //Continue
                    for (Negocio negocioAbiero : abiertosNegocios) {
                        if (!listaContieneNegocio(negocioAbiero)) {
                            filteredNegocios.add(negocioAbiero);
                        }
                    }
                    searchByAlimentos();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            searchByAlimentos();
        }


    }

    private boolean listaContieneNegocio(Negocio negocio) {
        boolean loContiene = false;
        for (Negocio negocioENLista : filteredNegocios) {
            if (negocioENLista.getNegocioDataBaseReference().equals(negocio.getNegocioDataBaseReference())) {
                loContiene = true;
                break;
            }
        }
        return loContiene;
    }


    private void startSearch() {
        showpDialog();
        searchBYAbierto();
    }


    private void searchByAlimentos() {

        if (alimentos.isChecked()) {
            alimentosNegocios = new LinkedList<>();
            final DatabaseReference allnegocios = FirebaseDatabase.getInstance().getReference(ALL_NEGOCIOS_REFERENCE);
            Query query = allnegocios.orderByChild("categoria").equalTo("categoria1");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot negocioSnap : dataSnapshot.getChildren()) {
                        Negocio negocio = negocioSnap.getValue(Negocio.class);
                        negocio.setNegocioDataBaseReference(negocioSnap.getKey());
                        alimentosNegocios.add(negocio);
                    }

                    //Continue
                    for (Negocio negocioAbiero : alimentosNegocios) {
                        if (!listaContieneNegocio(negocioAbiero)) {
                            filteredNegocios.add(negocioAbiero);
                        }
                    }
                    searbyEtnregaAdomicilio();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            searbyEtnregaAdomicilio();
        }


    }

    public void searbyEtnregaAdomicilio() {
        servicioDomicilio = new LinkedList<>();

        if (aDomicilio.isChecked()) {
            final DatabaseReference allnegocios = FirebaseDatabase.getInstance().getReference(ALL_NEGOCIOS_REFERENCE);
            Query query = allnegocios.orderByChild("entrega_a_domicilio").equalTo("Si");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot negocioSnap : dataSnapshot.getChildren()) {
                        Negocio negocio = negocioSnap.getValue(Negocio.class);
                        negocio.setNegocioDataBaseReference(negocioSnap.getKey());
                        servicioDomicilio.add(negocio);
                    }

                    //Continue
                    for (Negocio negocioAbiero : servicioDomicilio) {
                        if (!listaContieneNegocio(negocioAbiero)) {
                            filteredNegocios.add(negocioAbiero);
                        }
                    }
                    searcBy24Horas();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            searcBy24Horas();
        }
    }


    private void searcBy24Horas() {
        abiertos24 = new LinkedList<>();

        if (always.isChecked()) {
            final DatabaseReference allnegocios = FirebaseDatabase.getInstance().getReference(ALL_NEGOCIOS_REFERENCE);
            Query query = allnegocios.orderByChild("abierto_24_horas").equalTo("Si");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot negocioSnap : dataSnapshot.getChildren()) {
                        Negocio negocio = negocioSnap.getValue(Negocio.class);
                        negocio.setNegocioDataBaseReference(negocioSnap.getKey());
                        abiertos24.add(negocio);
                    }

                    //Continue
                    for (Negocio negocioAbiero : abiertos24) {
                        if (!listaContieneNegocio(negocioAbiero)) {
                            filteredNegocios.add(negocioAbiero);
                        }
                    }
                    searchByTarjeta();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            searchByTarjeta();
        }
    }

    private void searchByTarjeta() {
        aceptanTerjeta = new LinkedList<>();

        if (creditCard.isChecked()) {
            final DatabaseReference allnegocios = FirebaseDatabase.getInstance().getReference(ALL_NEGOCIOS_REFERENCE);
            Query query = allnegocios.orderByChild("acepta_tarjeta").equalTo("Si");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot negocioSnap : dataSnapshot.getChildren()) {
                        Negocio negocio = negocioSnap.getValue(Negocio.class);
                        negocio.setNegocioDataBaseReference(negocioSnap.getKey());
                        aceptanTerjeta.add(negocio);
                    }

                    //Continue
                    for (Negocio negocioAbiero : aceptanTerjeta) {
                        if (!listaContieneNegocio(negocioAbiero)) {
                            filteredNegocios.add(negocioAbiero);
                        }
                    }
                    serachByEntrytext();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            serachByEntrytext();
        }
    }


    private void serachByEntrytext() {
        if (textSearch.getText().toString().length() > 0) {
            textNegocios = new LinkedList<>();
            final DatabaseReference allnegociosReference = FirebaseDatabase.getInstance().getReference(ALL_NEGOCIOS_REFERENCE);
            allnegociosReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot negocioSnap : dataSnapshot.getChildren()) {
                        Negocio negocio = negocioSnap.getValue(Negocio.class);
                        if(negocio!= null){
                            negocio.setNegocioDataBaseReference(negocioSnap.getKey());

                            try {
                                String nombreNormalizado= Normalizer.normalize(negocio.getNombre(), Normalizer.Form.NFD);
                                String contenidoNormalizado= Normalizer.normalize(negocio.getDescripcion(), Normalizer.Form.NFD);
                                contenidoNormalizado = contenidoNormalizado.replaceAll("[^\\p{ASCII}]", "");
                                nombreNormalizado = nombreNormalizado.replaceAll("[^\\p{ASCII}]", "");
                                if(nombreNormalizado.toLowerCase().contains(textSearch.getText().toString()) ||
                                        contenidoNormalizado.toLowerCase().contains(textSearch.getText().toString())){
                                    textNegocios.add(negocio);
                                }

                            }catch (Exception e){

                            }


                        }

                    }

                    //Continue
                    for (Negocio negocioAbiero : textNegocios) {
                        if (!listaContieneNegocio(negocioAbiero)) {
                            filteredNegocios.add(negocioAbiero);
                        }
                    }
                    mergeClosest();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            mergeClosest();
        }

}



private void mergeClosest(){
        if(masCercano.isChecked()){
            LinkedList<Negocio> negociosCercanosFinal = new LinkedList<>();
            for(String negocioCercano:negocioCercanoREference){
                for(Negocio filtros:filteredNegocios){
                    if(filtros.getNegocioDataBaseReference().equals(negocioCercano)){
                        negociosCercanosFinal.add(filtros);
                    }
                }
            }
            filteredNegocios = negociosCercanosFinal;
            searchFunciones();
        }else {
            searchFunciones();
        }

}



    private void searchFunciones(){
     /*   filteredNegocios = new LinkedList<>(allNegocios);
        showpDialog();
        if(alimentos.isChecked()){
            //
        }else{
            if(abierto.isChecked()){
                removeClosedNegocios();
            }else if(aDomicilio.isChecked()){
                removeNoToDomicilio();
            }else if(always.isChecked()){
                removeNotOpened24();
            }else if(creditCard.isChecked()){
                removeNotCreditCard();
            }else if(promocion.isChecked()){
                removeNotWithPromotion();
            }
        }*/


        LinkedList<Negocio> publicados = new LinkedList<>();
        for(Negocio negocioPublicado:filteredNegocios){
            if(negocioPublicado.getPublicado() != null && negocioPublicado.getPublicado().equals("Si")){
                publicados.add(negocioPublicado);
            }
        }


        filteredNegocios = publicados;

        //NegocioPorCategoriaAdapter negocioPorCategoriaAdapter = new NegocioPorCategoriaAdapter(filteredNegocios,SearchActivity.this);
        //negocioPorCategoriaAdapter.setNegocioSelectedListener(SearchActivity.this);
        //resultGrid.setAdapter(negocioPorCategoriaAdapter);
        clasifyOpenOrclosed();
        hidepDialog();


    }

    private void removeClosedNegocios(){
        LinkedList<Negocio> negociosToRemove = new LinkedList<>();
        for(Negocio negocio:filteredNegocios){
            String startHour = negocio.getOpen_time();
            String endHour = negocio.getClose_time();
            if(DateUtils.isNowInInterval(startHour,endHour)){

            }else{
                negociosToRemove.add(negocio);
            }
        }

        for(Negocio negocioToRemove:negociosToRemove){
            filteredNegocios.remove(negocioToRemove);
        }
    }


    private void removeNoToDomicilio(){
        LinkedList<Negocio> negociosToRemove = new LinkedList<>();
        for(Negocio negocio:filteredNegocios){
            if(!negocio.isEntregaADomicilio()){
                negociosToRemove.add(negocio);
            }
        }

        for(Negocio negocioToRemove:negociosToRemove){
            filteredNegocios.remove(negocioToRemove);
        }
    }

    private void removeNotOpened24(){
        LinkedList<Negocio> negociosToRemove = new LinkedList<>();
        for(Negocio negocio:filteredNegocios){
            if(!negocio.isOpen24Hours()){
                negociosToRemove.add(negocio);
            }
        }

        for(Negocio negocioToRemove:negociosToRemove){
            filteredNegocios.remove(negocioToRemove);
        }
    }

    private void removeNotCreditCard(){
        LinkedList<Negocio> negociosToRemove = new LinkedList<>();
        for(Negocio negocio:filteredNegocios){
            if(!negocio.isAceptaTArjeta()){
                negociosToRemove.add(negocio);
            }
        }

        for(Negocio negocioToRemove:negociosToRemove){
            filteredNegocios.remove(negocioToRemove);
        }
    }

    private void removeNotWithPromotion(){
        LinkedList<Negocio> negociosToRemove = new LinkedList<>();
        for(Negocio negocio:filteredNegocios){
            if(!negocio.isPremiumStatus()){
                negociosToRemove.add(negocio);
            }
        }

        for(Negocio negocioToRemove:negociosToRemove){
            filteredNegocios.remove(negocioToRemove);
        }
    }


    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private void recoveryNegocios(){
        GeoQuery geoQuery;

        if(currentLocation == null ){
            Toast.makeText(SearchActivity.this,"No hemos podido encontrar tu ubicación ",Toast.LENGTH_LONG).show();
        }else{
            geoQuery= geoFire.queryAtLocation(new GeoLocation(currentLocation.getLatitude(),currentLocation.getLongitude()),1.5);
            geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                @Override
                public void onKeyEntered(String key, GeoLocation location) {
                    Log.e(TAG,"Business match:" + key);
                    negocioCercanoREference.add(key);

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
        Intent negocioDetailIntent = new Intent(SearchActivity.this,NegocioDetailActivity.class);
        Bundle extras = new Bundle();
        extras.putSerializable(NegocioDetailActivity.KEY_NEGOCIO,negocio);
        negocioDetailIntent.putExtras(extras);
        startActivity(negocioDetailIntent);
    }

    private void initLocation(){
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(SearchActivity.this);
        centerMapOnCurrentLocation();


    }

    private void centerMapOnCurrentLocation(){
        if (ContextCompat.checkSelfPermission(SearchActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(SearchActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block


            } else {


                ActivityCompat.requestPermissions(SearchActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

            }
        } else {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(SearchActivity.this, new OnSuccessListener<Location>() {
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
                            .addOnSuccessListener(SearchActivity.this, new OnSuccessListener<Location>() {
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

    private void clasifyOpenOrclosed(){
        openNegocios = new LinkedList<>();
        closedNegocios = new LinkedList<>();
        for(Negocio negocio:filteredNegocios){
            if(isOpenNow(negocio)){
                openNegocios.add(negocio);
            }else{
                closedNegocios.add(negocio);
            }
        }

        negocioPorCategoriaAdapterClosed = new NegocioPorCategoriaAdapter(closedNegocios,SearchActivity.this);
        negocioPorCategoriaAdapterOpen = new NegocioPorCategoriaAdapter(openNegocios,SearchActivity.this);
        if(currentLocation != null){
            negocioPorCategoriaAdapterClosed.setLocation(currentLocation);
            negocioPorCategoriaAdapterOpen.setLocation(currentLocation);
        }

        negocioPorCategoriaAdapterClosed.setNegocioSelectedListener(SearchActivity.this);
        negocioPorCategoriaAdapterOpen.setNegocioSelectedListener(SearchActivity.this);
        openNegociosGrid.setAdapter(negocioPorCategoriaAdapterOpen);
        openNegociosGrid.setExpanded(true);
        openNegociosGrid.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, openNegocios.size() * ELEMENT_SIZE));
        //openNegociosGrid.setMinimumHeight(allNegocios.size() * ELEMENT_SIZE);
        closedNegociosGrid.setAdapter(negocioPorCategoriaAdapterClosed);
        closedNegociosGrid.setExpanded(true);
        // closedNegociosGrid.setMinimumHeight(allNegocios.size() * ELEMENT_SIZE);
        closedNegociosGrid.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, closedNegocios.size() * ELEMENT_SIZE));



        //  showPremiunBanner();


    }



}
