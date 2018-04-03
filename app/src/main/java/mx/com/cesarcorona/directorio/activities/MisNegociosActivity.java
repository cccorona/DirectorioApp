package mx.com.cesarcorona.directorio.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import mx.com.cesarcorona.directorio.MainActivity;
import mx.com.cesarcorona.directorio.R;
import mx.com.cesarcorona.directorio.adapter.DiasEspecialesAdapter;
import mx.com.cesarcorona.directorio.adapter.PromosAdapter;
import mx.com.cesarcorona.directorio.dialogs.SpecialDayDialog;
import mx.com.cesarcorona.directorio.dialogs.TimeSelectorDialog;
import mx.com.cesarcorona.directorio.pojo.Categoria;
import mx.com.cesarcorona.directorio.pojo.FechaEspecial;
import mx.com.cesarcorona.directorio.pojo.Negocio;
import mx.com.cesarcorona.directorio.pojo.Promocion;

import static mx.com.cesarcorona.directorio.Utils.DataLoader.PROMOS_PHOTOS_REFRENCE;
import static mx.com.cesarcorona.directorio.activities.NegocioPorCategoriaActivity.GEO_REFERENCE;
import static mx.com.cesarcorona.directorio.activities.NegocioPorCategoriaActivity.NEGOCIOS_REFERENCE;
import static mx.com.cesarcorona.directorio.activities.PublicarNegocioActivity.sizeOfFileSelectedIsBigger;
import static mx.com.cesarcorona.directorio.adapter.PromosAdapter.ADAPTER_TYPE_EDIT;

public class MisNegociosActivity extends BaseAnimatedActivity implements OnMapReadyCallback, TimeSelectorDialog.OnTimeSelectedInterface , SpecialDayDialog.OnUpdateESpecialDayInterfac, DiasEspecialesAdapter.OnServicioSelected  {



    private LinkedList<Negocio> misNegocios;
    private boolean firstTopic = true;
    private Negocio negocioSeleccionado;
    private Spinner negocioSpinner;




    /* Properties */



    private Uri bigImage;
    private Uri bigImageUrl;
    private Uri bannerImage;
    private Uri bannerImageUrl;
    private Uri fotouno;
    private Uri fotounoUrl;
    private Uri fotodos;
    private Uri fotodosUrl;
    private Uri fototres;
    private Uri fototresUrl;


    private static final int MAX_ZOOM = 16;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 100;




    public static final int BIG_FOTO = 0;
    public static final int BANNER = 1;
    public static final int FOTOUNO = 2;
    public static final int FOTODOS = 3;
    public static final int FOTOTRES = 4;

    private boolean piblishInCurse;





    private ImageView bigFoto, bannerFoto, fotoUno, fotoDos, fotoTres;
    private ImageView negocio_imagen, banner_image, rect_uno, rect_dos, rect_tres;
    private EditText nombre_negocio, negocio_descripcion;
    private Spinner categorySpinner;
    private ProgressDialog pDialog;
    private RadioGroup lunes,martes,miercoles,jueves,viernes,sabado,domingo;
    private RadioGroup abre23,tarjeta,docimilio;
    private TextView tLa,tLc,tMa,tMc,tMia,tMic,tJa,tJv,tVa,tVc,tSa,tSc,tDa,TDc;
    private Button publicarNegocioButton;
    private EditText faceBookValue , mailValue, whtasValue, twitterValue,telefonoValue,paginaWebValue;


    private LinkedList<Categoria> allCategorias;
    private boolean firstTopicCategoria;
    private Categoria categoriaSeleccionada;
    private SupportPlaceAutocompleteFragment autocompleteFragment;
    private Place placeSelected;
    private double latitud;
    private double longitud;
    private GoogleMap currentMap;
    private String currenPosition;
    private SupportMapFragment mapFragment;
    private FusedLocationProviderClient mFusedLocationClient;
    private HashMap<String,String> diasAbiertos;
    private HashMap<String,String> fechasEspeciales;
    private String currentIdDay;
    private int currentIdView;

    private Negocio negocioPorPublicar;
    private  boolean bigImageChange;
    private boolean promoChange;
    private boolean fotoUnoChange,fotodosChange,fototresChange;



    private RecyclerView diasList;
    private TextView plusDiaText;
    private DiasEspecialesAdapter diasEspecialesAdapter;
    private LinkedList<FechaEspecial> fechaEspeciales;
    private FloatingActionButton fab;












    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_negocios);
        negocioSpinner = (Spinner) findViewById(R.id.negocioSpinner);
        fab =(FloatingActionButton)findViewById(R.id.fab);

        pDialog = new ProgressDialog(MisNegociosActivity.this);
        pDialog.setMessage("Por favor espere");
        pDialog.setCancelable(false);


        initUI();
        initEnviroment();
        uploadFotoLisening();
        initMap();
        initLocation();
        initAddresSearch();

        fillNegocios();
       // fillCategorias();
        piblishInCurse = false;


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(negocioSeleccionado == null){
                    Toast.makeText(MisNegociosActivity.this,"Seleccione primero un negocio",Toast.LENGTH_LONG).show();
                }else{
                    if(negocioSeleccionado.isOpenNow()){
                        cerrarNegocio();
                    }else{
                        abrirNegocio();
                    }
                }
            }
        });





        ImageView back_button= (ImageView)findViewById(R.id.back_arrow_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MisNegociosActivity.super.onBackPressed();
            }
        });

        ImageView homeButton=(ImageView)findViewById(R.id.home_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(MisNegociosActivity.this, MainActivity.class);
                startActivity(mainIntent);
            }
        });




    }

    private void cerrarNegocio(){
        fab.setEnabled(false);

        FirebaseDatabase.getInstance().getReference("Example/allnegocios")
                .child(negocioSeleccionado.getNegocioDataBaseReference())
                .child("openNow").setValue(false)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                       fab.setEnabled(true);
                       if(task.isSuccessful()){
                           Toast.makeText(MisNegociosActivity.this,"Negocio Cerrado",
                                   Toast.LENGTH_LONG).show();
                           fab.setImageResource(R.drawable.ic_lock_white_24dp);
                       }else{
                           Toast.makeText(MisNegociosActivity.this,
                                   "No se pudo completar la operación",Toast.LENGTH_LONG).show();
                       }
                    }
                });


    }

    private void abrirNegocio(){

        fab.setEnabled(false);

        FirebaseDatabase.getInstance().getReference("Example/allnegocios")
                .child(negocioSeleccionado.getNegocioDataBaseReference())
                .child("openNow").setValue(true)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        fab.setEnabled(true);
                        if(task.isSuccessful()){
                            Toast.makeText(MisNegociosActivity.this,"Negocio Abierto",
                                    Toast.LENGTH_LONG).show();
                            fab.setImageResource(R.drawable.ic_lock_open_white_24dp);
                        }else{
                            Toast.makeText(MisNegociosActivity.this,
                                    "No se pudo completar la operación",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }



    private void fillNegocios() {
        showpDialog();
        misNegocios = new LinkedList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Example/allnegocios");

        Query query = databaseReference.orderByChild("userId").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());

        Negocio selectATopi = new Negocio();
        selectATopi.setNombre("Seleccione un negocio");
        misNegocios.add(selectATopi);



        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hidepDialog();
                fillCategorias();

                for (DataSnapshot negocioSnap : dataSnapshot.getChildren()) {
                    Negocio negocio = negocioSnap.getValue(Negocio.class);
                    negocio.setNegocioDataBaseReference(negocioSnap.getKey());
                    if(negocio.getPublicado()!= null && negocio.getPublicado().equals("Si")){
                        misNegocios.add(negocio);

                    }
                }

                LinkedList<String> list = new LinkedList<String>();
                for (Negocio topic : misNegocios) {
                    list.add(topic.getNombre());
                }
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(MisNegociosActivity.this,
                        R.layout.spinner_style, list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                negocioSpinner.setAdapter(dataAdapter);

                if(misNegocios.size()==1){
                    Toast.makeText(MisNegociosActivity.this,"No has publicado negocios, o se encuentran" +
                            " en espera de publicación por parte del administrador",Toast.LENGTH_LONG).show();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hidepDialog();

            }
        });

        negocioSpinner.setOnItemSelectedListener(new MisNegociosActivity.mySpinnerListener());


    }

    class mySpinnerListener implements Spinner.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView parent, View v, int position,
                                   long id) {


            if (firstTopic) {
                firstTopic = false;
                return;
            } else {
                if(!piblishInCurse){
                    if (position > 0) {

                        negocioSeleccionado = misNegocios.get(position);

                    } else {
                        negocioSeleccionado = null;


                    }
                    updateUI();

                }


            }


        }


        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private void updateUI(){
        if(negocioSeleccionado != null){
            nombre_negocio.setText(negocioSeleccionado.getNombre());
            if(negocioSeleccionado.getLogo_negocio() != null && !negocioSeleccionado.getLogo_negocio().equals("")){
                Picasso.with(MisNegociosActivity.this).load(negocioSeleccionado.getLogo_negocio()).into(negocio_imagen);
            }
            if(negocioSeleccionado.getBanner_premium() != null && !negocioSeleccionado.getBanner_premium().equals("")){
                Picasso.with(MisNegociosActivity.this).load(negocioSeleccionado.getBanner_premium()).into(banner_image);
            }
            if(negocioSeleccionado.getImagen_promo1() != null && !negocioSeleccionado.getImagen_promo1().equals("")){
              Picasso.with(MisNegociosActivity.this).load(negocioSeleccionado.getImagen_promo1()).into(rect_uno);
            }
            if(negocioSeleccionado.getImagen_promo2() != null && !negocioSeleccionado.getImagen_promo2().equals("")){
                Picasso.with(MisNegociosActivity.this).load(negocioSeleccionado.getImagen_promo2()).into(rect_dos);
            }
            if(negocioSeleccionado.getImagen_promo3() != null && !negocioSeleccionado.getImagen_promo3().equals("")){
                Picasso.with(MisNegociosActivity.this).load(negocioSeleccionado.getImagen_promo1()).into(rect_tres);
            }

            negocio_descripcion.setText(negocioSeleccionado.getDescripcion());
            String categriaid = negocioSeleccionado.getCategoria();
            int position = 0;
            for(Categoria micategoria:allCategorias){
                if(micategoria.getDataBaseReference() != null && micategoria.getDataBaseReference().equals(categriaid)){
                    break;
                }
                position++;
            }

            categorySpinner.setSelection(position);
            categoriaSeleccionada = allCategorias.get(position);
            String latlong[]=negocioSeleccionado.getUbicacion().split(",");
            if(latlong != null){
                latitud = Double.parseDouble(latlong[0]);
                longitud = Double.parseDouble(latlong[1]);
                currenPosition = negocioSeleccionado.getDireccionName();
                updateMapPosition();
            }


            telefonoValue.setText(""+negocioSeleccionado.getTelefono());
            whtasValue.setText(""+negocioSeleccionado.getWhatsapp());
            twitterValue.setText(""+negocioSeleccionado.getTwitter());
            faceBookValue.setText(""+negocioSeleccionado.getFacebook());
            mailValue.setText(""+negocioSeleccionado.getWeb());
            paginaWebValue.setText(""+negocioSeleccionado.getPagina_web());

            if(negocioSeleccionado.getAbierto_24_horas()!= null && negocioSeleccionado.getAbierto_24_horas().equals("Si")){
                abre23.check(R.id.yes24);
                diasAbiertos = new LinkedHashMap<>();

            }else{
                abre23.check(R.id.no24);
                diasAbiertos = negocioSeleccionado.getDiasAbiertos();
                if(diasAbiertos == null){
                    diasAbiertos = new LinkedHashMap<>();
                }

            }


            if(negocioSeleccionado.getAbre_lunes()!= null && negocioSeleccionado.getAbre_lunes().equals("Si")){
                lunes.check(R.id.yesL);
                if(negocioSeleccionado.getAbierto_24_horas() != null && negocioSeleccionado.getAbierto_24_horas().equals("No")){
                    currentIdView =R.id.la;
                    updateLabel(diasAbiertos.get("la"));
                    currentIdView =R.id.lc;
                    updateLabel(diasAbiertos.get("lc"));
                }


            }else{
                lunes.check(R.id.noL);
            }

            if(negocioSeleccionado.getAbre_martes()!= null && negocioSeleccionado.getAbre_martes().equals("Si")){
                martes.check(R.id.yesMa);
                if(negocioSeleccionado.getAbierto_24_horas() != null && negocioSeleccionado.getAbierto_24_horas().equals("No")){
                    currentIdView =R.id.ma;
                    updateLabel(diasAbiertos.get("ma"));
                    currentIdView =R.id.mc;
                    updateLabel(diasAbiertos.get("mc"));
                }

            }else{
                martes.check(R.id.noMa);
            }

            if(negocioSeleccionado.getAbre_miercoles()!= null && negocioSeleccionado.getAbre_miercoles().equals("Si")){
                miercoles.check(R.id.yesMi);
                if(negocioSeleccionado.getAbierto_24_horas() != null && negocioSeleccionado.getAbierto_24_horas().equals("No")){
                    currentIdView =R.id.mia;
                    updateLabel(diasAbiertos.get("mia"));
                    currentIdView =R.id.mic;
                    updateLabel(diasAbiertos.get("mic"));
                }

            }else{
                miercoles.check(R.id.noMi);
            }

            if(negocioSeleccionado.getAbre_jueves()!= null && negocioSeleccionado.getAbre_jueves().equals("Si")){
                jueves.check(R.id.yesJ);
                if(negocioSeleccionado.getAbierto_24_horas() != null && negocioSeleccionado.getAbierto_24_horas().equals("No")){
                    currentIdView =R.id.ja;
                    updateLabel(diasAbiertos.get("ja"));
                    currentIdView =R.id.jc;
                    updateLabel(diasAbiertos.get("jc"));
                }

            }else{
                jueves.check(R.id.noJ);
            }

            if(negocioSeleccionado.getAbre_viernes()!= null && negocioSeleccionado.getAbre_viernes().equals("Si")){
                viernes.check(R.id.yesV);
                if(negocioSeleccionado.getAbierto_24_horas() != null && negocioSeleccionado.getAbierto_24_horas().equals("No")){
                    currentIdView =R.id.va;
                    updateLabel(diasAbiertos.get("va"));
                    currentIdView =R.id.vc;
                    updateLabel(diasAbiertos.get("vc"));
                }

            }else{
                viernes.check(R.id.noV);
            }

            if(negocioSeleccionado.getAbre_sabado()!= null && negocioSeleccionado.getAbre_sabado().equals("Si")){
                sabado.check(R.id.yesS);
                if(negocioSeleccionado.getAbierto_24_horas() != null && negocioSeleccionado.getAbierto_24_horas().equals("No")){
                    currentIdView =R.id.sa;
                    updateLabel(diasAbiertos.get("sa"));
                    currentIdView =R.id.sc;
                    updateLabel(diasAbiertos.get("sc"));
                }

            }else{
                sabado.check(R.id.noS);
            }

            if(negocioSeleccionado.getAbre_domingo()!= null && negocioSeleccionado.getAbre_domingo().equals("Si")){
                domingo.check(R.id.yesD);
                if(negocioSeleccionado.getAbierto_24_horas() != null && negocioSeleccionado.getAbierto_24_horas().equals("No")){
                    currentIdView =R.id.la;
                    updateLabel(diasAbiertos.get("da"));
                    currentIdView =R.id.lc;
                    updateLabel(diasAbiertos.get("dc"));
                }

            }else{
                domingo.check(R.id.noD);
            }


            if(negocioSeleccionado.getEntrega_a_domicilio()!= null && negocioSeleccionado.getEntrega_a_domicilio().equals("Si")){
                docimilio.check(R.id.yesdomi);
            }else{
                docimilio.check(R.id.nodomi);
            }
            if(negocioSeleccionado.getAcepta_tarjeta()!= null && negocioSeleccionado.getAcepta_tarjeta().equals("Si")){
                tarjeta.check(R.id.yesta);
            }else{
                tarjeta.check(R.id.nota);
            }


            if(negocioSeleccionado.getLogo_negocio() != null){
                bigImage = Uri.parse(negocioSeleccionado.getLogo_negocio());

            }

            if(negocioSeleccionado.getBanner_premium() != null){
                bannerImage = Uri.parse(negocioSeleccionado.getBanner_premium());

            }


           if(negocioSeleccionado.getFechasEspeciales() != null){
               LinkedList<FechaEspecial> fechaEspecialList = new LinkedList<>();

               for(String key:negocioSeleccionado.getFechasEspeciales().keySet()){
                   fechaEspecialList.add(negocioSeleccionado.getFechasEspeciales().get(key));
                }

               diasEspecialesAdapter = new DiasEspecialesAdapter(MisNegociosActivity.this,fechaEspecialList);
               fechaEspeciales = fechaEspecialList;
               diasList.setAdapter(diasEspecialesAdapter);

           }



        }

    }



    private void  initUI(){
        bigFoto = (ImageView) findViewById(R.id.attach_big);
        bannerFoto = (ImageView) findViewById(R.id.attach_banner);
        fotoUno = (ImageView) findViewById(R.id.imagen_uno_up);
        fotoDos = (ImageView) findViewById(R.id.imagen_dos_up);
        fotoTres = (ImageView) findViewById(R.id.imagen_tres_up);
        negocio_imagen = (ImageView) findViewById(R.id.negocio_imagen);
        banner_image = (ImageView) findViewById(R.id.banner_image);
        rect_uno = (ImageView) findViewById(R.id.rect_uno);
        rect_dos = (ImageView) findViewById(R.id.rect_dos);
        rect_tres = (ImageView) findViewById(R.id.rect_tres);
        nombre_negocio = (EditText) findViewById(R.id.nombre_negocio);
        negocio_descripcion = (EditText) findViewById(R.id.negocio_descripcion);
        categorySpinner= (Spinner)findViewById(R.id.categorySpinner);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        publicarNegocioButton = (Button) findViewById(R.id.publicar_button_negocio);
        publicarNegocioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishAttempt();
            }
        });


        lunes = (RadioGroup)findViewById(R.id.myRadioGroupL);
        martes = (RadioGroup)findViewById(R.id.myRadioGroupMa);
        miercoles = (RadioGroup)findViewById(R.id.myRadioGroupMi);
        jueves = (RadioGroup)findViewById(R.id.myRadioGroupJ);
        viernes = (RadioGroup)findViewById(R.id.myRadioGroupV);
        sabado = (RadioGroup)findViewById(R.id.myRadioGroupS);
        domingo = (RadioGroup)findViewById(R.id.myRadioGroupD);
        abre23 = (RadioGroup)findViewById(R.id.myRadioGroup24);
        docimilio = (RadioGroup)findViewById(R.id.myRadioGroupdomi);
        tarjeta = (RadioGroup)findViewById(R.id.myRadioGrouptarjeta);


        plusDiaText = (TextView) findViewById(R.id.plus_dia_especial);
        fechaEspeciales = new LinkedList<>();

        diasList = (RecyclerView)findViewById(R.id.dias_especiales_list);
        diasList.setLayoutManager( new LinearLayoutManager(MisNegociosActivity.this,LinearLayoutManager.VERTICAL,false));
        diasList.setItemAnimator( new DefaultItemAnimator());
        diasEspecialesAdapter = new DiasEspecialesAdapter(MisNegociosActivity.this,fechaEspeciales);
        diasEspecialesAdapter.setOnServicioSelected(MisNegociosActivity.this);
        diasList.setAdapter(diasEspecialesAdapter);


        plusDiaText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpecialDayDialog specialDayDialog = new SpecialDayDialog();
                specialDayDialog.setOnUpdateESpecialDayInterfac(MisNegociosActivity.this);
                specialDayDialog.show(getSupportFragmentManager(),SpecialDayDialog.TAG);
            }
        });



        domingo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                updateDias(checkedId);
            }
        });

        lunes.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                updateDias(checkedId);
            }
        });
        martes.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                updateDias(checkedId);
            }
        });
        miercoles.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                updateDias(checkedId);
            }
        });
        jueves.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                updateDias(checkedId);
            }
        });
        viernes.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                updateDias(checkedId);
            }
        });
        sabado.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                updateDias(checkedId);
            }
        });



        tLa = (TextView)findViewById(R.id.la);
        tLc = (TextView)findViewById(R.id.lc);
        tMa = (TextView)findViewById(R.id.ma);
        tMc = (TextView)findViewById(R.id.mc);
        tMia = (TextView)findViewById(R.id.mia);
        tMic = (TextView)findViewById(R.id.mic);
        tJa = (TextView)findViewById(R.id.ja);
        tJv = (TextView)findViewById(R.id.jc);
        tVa = (TextView)findViewById(R.id.va);
        tVc = (TextView)findViewById(R.id.vc);
        tSa = (TextView)findViewById(R.id.sa);
        tSc = (TextView)findViewById(R.id.sc);
        tDa = (TextView)findViewById(R.id.da);
        TDc = (TextView)findViewById(R.id.dc);


        faceBookValue = (EditText) findViewById(R.id.facebook_value);
        mailValue = (EditText) findViewById(R.id.web_value);
        whtasValue = (EditText) findViewById(R.id.whats_value);
        telefonoValue = (EditText) findViewById(R.id.phone_value);
        twitterValue = (EditText) findViewById(R.id.twiter_value);
        paginaWebValue = (EditText)findViewById(R.id.pagina_value);

    }



    private void updateDias(int currentId){
        switch (currentId){

            case R.id.noD:
                tDa.setClickable(false);
                TDc.setClickable(false);
                diasAbiertos.remove("da");
                diasAbiertos.remove("dc");
                break;

            case R.id.yesD:
                tDa.setClickable(true);
                TDc.setClickable(true);
                break;

            case R.id.noL:
                tLa.setClickable(false);
                tLc.setClickable(false);
                diasAbiertos.remove("la");
                diasAbiertos.remove("lc");
                break;

            case R.id.yesL:
                tLa.setClickable(true);
                tLc.setClickable(true);
                break;

            case R.id.noMa:
                tMa.setClickable(false);
                tMc.setClickable(false);
                diasAbiertos.remove("ma");
                diasAbiertos.remove("mc");
                break;

            case R.id.yesMa:
                tMa.setClickable(true);
                tMc.setClickable(true);
                break;


            case R.id.noMi:
                tMia.setClickable(false);
                tMic.setClickable(false);
                diasAbiertos.remove("mia");
                diasAbiertos.remove("mic");
                break;

            case R.id.yesMi:
                tMia.setClickable(true);
                tMic.setClickable(true);
                break;


            case R.id.noJ:
                tJa.setClickable(false);
                tJv.setClickable(false);
                diasAbiertos.remove("ja");
                diasAbiertos.remove("jc");
                break;

            case R.id.yesJ:
                tJa.setClickable(true);
                tJv.setClickable(true);
                break;


            case R.id.noV:
                tVa.setClickable(false);
                tVc.setClickable(false);
                diasAbiertos.remove("va");
                diasAbiertos.remove("vc");
                break;

            case R.id.yesV:
                tVa.setClickable(true);
                tVc.setClickable(true);
                break;

            case R.id.noS:
                tSa.setClickable(false);
                tSc.setClickable(false);
                diasAbiertos.remove("sa");
                diasAbiertos.remove("sc");
                break;

            case R.id.yesS:
                tSa.setClickable(true);
                tSc.setClickable(true);
                break;

        }

    }


    public void showTimedialog(android.view.View v) {
        TimeSelectorDialog timeSelectorDialog = new TimeSelectorDialog();
        timeSelectorDialog.setOnTimeSelectedInterface(this);
        timeSelectorDialog.show(getSupportFragmentManager(),TimeSelectorDialog.TAG);
        currentIdView = v.getId();
        switch (v.getId()){
            case R.id.la:
                currentIdDay = "la";
                break;
            case R.id.lc:
                currentIdDay = "lc";

                break;
            case R.id.ma:
                currentIdDay = "ma";

                break;
            case R.id.mc:
                currentIdDay = "mc";

                break;
            case R.id.mia:
                currentIdDay = "mia";

                break;
            case R.id.mic:
                currentIdDay = "mic";

                break;
            case R.id.ja:
                currentIdDay = "ja";

                break;
            case R.id.jc:
                currentIdDay = "jc";

                break;
            case R.id.va:
                currentIdDay = "va";

                break;
            case R.id.vc:
                currentIdDay = "vc";

                break;
            case R.id.sa:
                currentIdDay = "sa";

                break;
            case R.id.sc:
                currentIdDay = "sc";

                break;
            case R.id.da:
                currentIdDay = "da";

                break;
            case R.id.dc:
                currentIdDay = "dc";

                break;
        }


    }

    private void uploadPhoto(int source) {
        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, source);
    }





    private void initAddresSearch(){
        autocompleteFragment = (SupportPlaceAutocompleteFragment)
                getSupportFragmentManager().
                        findFragmentById(R.id.place_autocomplete_fragment);
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(Place.TYPE_COUNTRY).setCountry("MX")
                .build();
        autocompleteFragment.setFilter(typeFilter);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                placeSelected = place;
                latitud = placeSelected.getLatLng().latitude;
                longitud = placeSelected.getLatLng().longitude;
                updateCodinates();


            }

            @Override
            public void onError(Status status) {
                Toast.makeText(MisNegociosActivity.this,status.getStatusMessage(),Toast.LENGTH_LONG).show();
            }
        });
        ((EditText)findViewById(R.id.place_autocomplete_search_input)).setTextSize(10.0f);
        ((EditText)findViewById(R.id.place_autocomplete_search_input)).setTextColor(Color.WHITE);


    }

    private void updateCodinates(){

        if(currentMap != null){
            if(placeSelected!= null){
                currenPosition = placeSelected.getName().toString();
            }else{

            }

            currentMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitud,longitud))
                    .title(currenPosition)).showInfoWindow();
            currentMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitud,longitud),MAX_ZOOM));
        }
    }


    private void updateMapPosition(){
        if(latitud != 0 && longitud != 0 && negocioSeleccionado != null && !negocioSeleccionado.getDireccionName().equals("")){
            currentMap.clear();
            currentMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitud,longitud))
                    .title(negocioSeleccionado.getDireccionName())).showInfoWindow();
            currentMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitud,longitud),MAX_ZOOM));
        }
    }


    private void uploadPhotoToSer(int source) {

      /*  StorageReference fotoref = FirebaseStorage.getInstance().getReference(PROMOS_PHOTOS_REFRENCE+"/"+selectedImage.getLastPathSegment());
        UploadTask uploadTask = fotoref.putFile(selectedImage);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                hidepDialog();
                showSnackbar(exception.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                bannerURL = taskSnapshot.getDownloadUrl();
                uploadAttemp();
            }
        });*/
    }

    @Override
    public void OnTimeChangedDialog(TimePicker view, int hourOfDay, int minute) {

        Calendar rightNow = Calendar.getInstance();
        Date now =Calendar.getInstance().getTime();
        int hourNow = rightNow.get(Calendar.HOUR_OF_DAY);


        boolean isPassedTime = false;
        if(hourNow>hourOfDay){
            isPassedTime = true;
        }
        String aMpM = "AM";
        if(hourOfDay >11)
        {
            aMpM = "PM";
        }
        //Make the 24 hour time format to 12 hour time format
        int currentHour;
        if(hourOfDay>11)
        {
            currentHour = hourOfDay - 12;
        }
        else
        {
            currentHour = hourOfDay;
        }

        if(hourOfDay == 0){
            hourOfDay = 24;
        }


        String hourSelected = String.valueOf(currentHour)
                + " : " + String.valueOf(minute) + " " + aMpM;
        diasAbiertos.put(currentIdDay,""+hourOfDay+":"+minute);
        updateLabel(hourSelected);

    }


    private void updateLabel(String label){
        switch (currentIdView){
            case R.id.la:
                tLa.setText(label);
                break;
            case R.id.lc:
                tLc.setText(label);

                break;
            case R.id.ma:
                tMa.setText(label);

                break;
            case R.id.mc:
                tMc.setText(label);

                break;
            case R.id.mia:
                tMia.setText(label);

                break;
            case R.id.mic:
                tMic.setText(label);

                break;
            case R.id.ja:
                tJa.setText(label);

                break;
            case R.id.jc:
                tJv.setText(label);

                break;
            case R.id.va:
                tVa.setText(label);

                break;
            case R.id.vc:
                tVc.setText(label);

                break;
            case R.id.sa:
                tSa.setText(label);

                break;
            case R.id.sc:
                tSc.setText(label);

                break;
            case R.id.da:
                tDa.setText(label);

                break;
            case R.id.dc:
                TDc.setText(label);

                break;
        }

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        currentMap = googleMap;

    }

    private void initLocation(){
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(MisNegociosActivity.this);
        centerMapOnCurrentLocation();



    }

    private void initMap(){
        mapFragment.getMapAsync(this);

    }


    private void centerMapOnCurrentLocation(){
        if (ContextCompat.checkSelfPermission(MisNegociosActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(MisNegociosActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block


            } else {


                ActivityCompat.requestPermissions(MisNegociosActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

            }
        } else {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(MisNegociosActivity.this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                latitud = location.getLatitude();
                                longitud = location.getLongitude();
                                updateCodinates();
                            }
                        }
                    });
        }
    }




    private boolean datesAreComplete(){

        int conunt= 0;
        if(abre23.getCheckedRadioButtonId() == R.id.yes24){
            return true;
        }else if(abre23.getCheckedRadioButtonId() == R.id.no24){
            if(lunes.getCheckedRadioButtonId() == R.id.yesL){
                conunt++;
            }else if(lunes.getCheckedRadioButtonId() == R.id.noL){

            }
            if(martes.getCheckedRadioButtonId() == R.id.yesMa){
                conunt++;
            }else if(martes.getCheckedRadioButtonId() == R.id.noMa){

            }
            if(miercoles.getCheckedRadioButtonId() == R.id.yesMi){
                conunt++;
            }else if(miercoles.getCheckedRadioButtonId() == R.id.noMi){

            }
            if(jueves.getCheckedRadioButtonId() == R.id.yesJ){
                conunt++;
            }else if(jueves.getCheckedRadioButtonId() == R.id.noJ){

            }
            if(viernes.getCheckedRadioButtonId() == R.id.yesV){
                conunt++;
            }else if(viernes.getCheckedRadioButtonId() == R.id.noV){

            }
            if(sabado.getCheckedRadioButtonId() == R.id.yesS){
                conunt++;
            }else if(sabado.getCheckedRadioButtonId() == R.id.noS){

            }
            if(domingo.getCheckedRadioButtonId() == R.id.yesD){
                conunt++;
            }else if(domingo.getCheckedRadioButtonId() == R.id.noD){

            }
        }

        if(conunt*2 == diasAbiertos.size()){
            return true;
        }else {
            return false;
        }


    }

    private void finisData(){
        negocioPorPublicar.setNombre(nombre_negocio.getText().toString());
        negocioPorPublicar.setDescripcion(negocio_descripcion.getText().toString());
        negocioPorPublicar.setCategoria(categoriaSeleccionada.getDataBaseReference());
        negocioPorPublicar.setWeb(mailValue.getText().toString());
        negocioPorPublicar.setTwitter(twitterValue.getText().toString());
        negocioPorPublicar.setTelefono(telefonoValue.getText().toString());
        negocioPorPublicar.setWhatsapp(whtasValue.getText().toString());
        negocioPorPublicar.setFacebook(faceBookValue.getText().toString());
        negocioPorPublicar.setUbicacion(""+latitud+","+longitud);
        negocioPorPublicar.setDiasAbiertos(diasAbiertos);
        negocioPorPublicar.setPagina_web(paginaWebValue.getText().toString());
        if(lunes.getCheckedRadioButtonId() == R.id.yesL){
            negocioPorPublicar.setAbre_lunes("Si");
        }else if(lunes.getCheckedRadioButtonId() == R.id.noL){
            negocioPorPublicar.setAbre_lunes("No");

        }
        if(martes.getCheckedRadioButtonId() == R.id.yesMa){
            negocioPorPublicar.setAbre_martes("Si");
        }else if(martes.getCheckedRadioButtonId() == R.id.noMa){
            negocioPorPublicar.setAbre_martes("No");

        }
        if(miercoles.getCheckedRadioButtonId() == R.id.yesMi){
            negocioPorPublicar.setAbre_miercoles("Si");
        }else if(miercoles.getCheckedRadioButtonId() == R.id.noMi){
            negocioPorPublicar.setAbre_miercoles("No");

        }
        if(jueves.getCheckedRadioButtonId() == R.id.yesJ){
            negocioPorPublicar.setAbre_jueves("Si");
        }else if(jueves.getCheckedRadioButtonId() == R.id.noJ){
            negocioPorPublicar.setAbre_jueves("No");

        }
        if(viernes.getCheckedRadioButtonId() == R.id.yesV){
            negocioPorPublicar.setAbre_viernes("Si");
        }else if(viernes.getCheckedRadioButtonId() == R.id.noV){
            negocioPorPublicar.setAbre_viernes("No");

        }
        if(sabado.getCheckedRadioButtonId() == R.id.yesS){
            negocioPorPublicar.setAbre_sabado("Si");
        }else if(sabado.getCheckedRadioButtonId() == R.id.noS){
            negocioPorPublicar.setAbre_sabado("No");

        }
        if(domingo.getCheckedRadioButtonId() == R.id.yesD){
            negocioPorPublicar.setAbre_domingo("Si");
        }else if(domingo.getCheckedRadioButtonId() == R.id.noD){
            negocioPorPublicar.setAbre_domingo("No");

        }

        if(abre23.getCheckedRadioButtonId() == R.id.yes24){
            negocioPorPublicar.setAbierto_24_horas("Si");
        }else if(abre23.getCheckedRadioButtonId() == R.id.no24){
            negocioPorPublicar.setAbierto_24_horas("No");

        }
        if(docimilio.getCheckedRadioButtonId() == R.id.yesdomi){
            negocioPorPublicar.setEntrega_a_domicilio("Si");
        }else if(docimilio.getCheckedRadioButtonId() == R.id.nodomi){
            negocioPorPublicar.setEntrega_a_domicilio("No");

        }

        if(tarjeta.getCheckedRadioButtonId() == R.id.yesta){
            negocioPorPublicar.setAcepta_tarjeta("Si");
        }else if(tarjeta.getCheckedRadioButtonId() == R.id.nota){
            negocioPorPublicar.setAcepta_tarjeta("No");

        }
        negocioPorPublicar.setPublicado("Si");
        negocioPorPublicar.setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());
        negocioPorPublicar.setDireccionName(currenPosition);

        if(placeSelected != null){
            latitud = placeSelected.getLatLng().latitude;
            longitud = placeSelected.getLatLng().longitude;
            negocioPorPublicar.setDireccionName(placeSelected.getName().toString());
        }


        if(!bigImageChange){
            negocioPorPublicar.setLogo_negocio(negocioSeleccionado.getLogo_negocio());
            negocioPorPublicar.setLogo(negocioSeleccionado.getLogo());
        }
        if(!promoChange){
            negocioPorPublicar.setBanner_premium(negocioSeleccionado.getBanner_premium());
            negocioPorPublicar.setPremiumBannerUrl(negocioSeleccionado.getPremiumBannerUrl());
        }
        if(!fotoUnoChange){
            negocioPorPublicar.setImagen_promo1(negocioSeleccionado.getImagen_promo1());
        }
        if(!fotodosChange){
            negocioPorPublicar.setImagen_promo1(negocioSeleccionado.getImagen_promo2());
        }
        if(!fototresChange){
            negocioPorPublicar.setImagen_promo1(negocioSeleccionado.getImagen_promo3());
        }

        LinkedHashMap<String,FechaEspecial> diasEspeciales = new LinkedHashMap<>();
        if(diasEspecialesAdapter != null && diasEspecialesAdapter.getItemCount() > 0){
            for(FechaEspecial diaEspecial:diasEspecialesAdapter.getDiasEspeciales()){
                diasEspeciales.put(""+diaEspecial.getFecha(),diaEspecial);
            }
            negocioPorPublicar.setFechasEspeciales(diasEspeciales);
        }





        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(NEGOCIOS_REFERENCE);
        String key = databaseReference.push().getKey();
        final String keyToPush = "Negocio"+key;
        databaseReference = FirebaseDatabase.getInstance().getReference("Example/"+NEGOCIOS_REFERENCE+"/"+negocioSeleccionado.getNegocioDataBaseReference());
        databaseReference.setValue(negocioPorPublicar).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(MisNegociosActivity.this,"Negocio actualizado",Toast.LENGTH_LONG).show();
                    DatabaseReference geoReference = FirebaseDatabase.getInstance().getReference(GEO_REFERENCE);
                    GeoFire geoFire = new GeoFire(geoReference);
                    geoFire.setLocation(negocioSeleccionado.getNegocioDataBaseReference(), new GeoLocation(latitud,longitud)
                            , new GeoFire.CompletionListener() {
                                @Override
                                public void onComplete(String key, DatabaseError error) {
                                    piblishInCurse = false;



                                }
                            });

                    Intent mainInten = new Intent(MisNegociosActivity.this,MainActivity.class);
                    startActivity(mainInten);
                    finish();

                }else{
                    Toast.makeText(MisNegociosActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();

                }
                hidepDialog();

            }
        });


    }



    private void fillCategorias() {
      //  showpDialog();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Example/categorias");
        Categoria selectATopi = new Categoria();
        selectATopi.setNombre("Seleccione una categoria");
        allCategorias.add(selectATopi);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot categoriaSnap : dataSnapshot.getChildren()) {
                    Categoria categoria = categoriaSnap.getValue(Categoria.class);
                    categoria.setDataBaseReference(categoriaSnap.getKey());
                    allCategorias.add(categoria);
                }

                LinkedList<String> list = new LinkedList<String>();
                for (Categoria topic : allCategorias) {
                    list.add(topic.getNombre());
                }
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(MisNegociosActivity.this,
                        R.layout.spinner_style, list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categorySpinner.setAdapter(dataAdapter);

           //     hidepDialog();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hidepDialog();

            }
        });

        categorySpinner.setOnItemSelectedListener(new MisNegociosActivity.mySpinnerListenerC());


    }





    private void uploadFotoLisening() {
        bigFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPhoto(BIG_FOTO);
            }
        });
        bannerFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPhoto(BANNER);
            }
        });
        fotoUno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPhoto(FOTOUNO);
            }
        });
        fotoDos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPhoto(FOTODOS);
            }
        });
        fotoTres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPhoto(FOTOTRES);
            }
        });


    }


    private void initEnviroment() {
        firstTopic = true;
        allCategorias = new LinkedList<>();

        pDialog = new ProgressDialog(MisNegociosActivity.this);
        pDialog.setMessage("Por favor espere");
        pDialog.setCancelable(false);
        diasAbiertos = new HashMap<>();


    }


    private void publishAttempt(){


        //  private Uri fotouno;
        // private Uri fotodos;
        // private Uri fototres;

        if(negocioSeleccionado == null){
            Toast.makeText(MisNegociosActivity.this,"Primero seleccione un negocio",Toast.LENGTH_LONG).show();

        }


        negocioPorPublicar = new Negocio();
        boolean datesComplete= datesAreComplete();
        if(!datesComplete){
            hidepDialog();
            Toast.makeText(MisNegociosActivity.this,"Rellene los horarios de los dias que abre",Toast.LENGTH_LONG).show();

        }
        if(bigImage!= null  && nombre_negocio.getText().length()>0 && negocio_descripcion.getText().length() >0 && telefonoValue.getText().length() > 0
                && categoriaSeleccionada != null && (placeSelected != null || latitud !=0 && longitud != 0) && datesComplete){
            showpDialog();

            piblishInCurse = true;

            StorageReference fotoref = FirebaseStorage.getInstance().getReference(PROMOS_PHOTOS_REFRENCE+"/"+bigImage.getLastPathSegment());
            UploadTask uploadTask = fotoref.putFile(bigImage);

            if(bigImageChange){
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(MisNegociosActivity.this,exception.getMessage(),Toast.LENGTH_LONG).show();
                        hidepDialog();
                        return;
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        if(bannerImage != null && promoChange){
                            StorageReference fotoref = FirebaseStorage.getInstance().getReference(PROMOS_PHOTOS_REFRENCE+"/"+bannerImage.getLastPathSegment());
                            UploadTask uploadTask = fotoref.putFile(bannerImage);
                            bannerImageUrl = taskSnapshot.getDownloadUrl();
                            negocioPorPublicar.setLogo_negocio(taskSnapshot.getDownloadUrl().toString());
                            negocioPorPublicar.setLogo(taskSnapshot.getDownloadUrl().toString());


                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Toast.makeText(MisNegociosActivity.this,exception.getMessage(),Toast.LENGTH_LONG).show();
                                    hidepDialog();
                                    return;
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                                    bannerImageUrl = taskSnapshot.getDownloadUrl();
                                    negocioPorPublicar.setBanner_premium(bannerImageUrl.toString());
                                    negocioPorPublicar.setPremiumBannerUrl(bannerImageUrl.toString());
                                    fillDataRemain();
                                }
                            });
                        }else{
                            fillDataRemain();
                        }

                    }
                });
            }else{
                if(bannerImage != null && promoChange){
                     fotoref = FirebaseStorage.getInstance().getReference(PROMOS_PHOTOS_REFRENCE+"/"+bannerImage.getLastPathSegment());
                     uploadTask = fotoref.putFile(bannerImage);

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(MisNegociosActivity.this,exception.getMessage(),Toast.LENGTH_LONG).show();
                            hidepDialog();
                            return;
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                            bannerImageUrl = taskSnapshot.getDownloadUrl();
                            negocioPorPublicar.setBanner_premium(bannerImageUrl.toString());
                            negocioPorPublicar.setPremiumBannerUrl(bannerImageUrl.toString());
                            fillDataRemain();
                        }
                    });
                }else{
                    fillDataRemain();
                }
            }







        }else{
            Toast.makeText(MisNegociosActivity.this,"Rellene todos los datos para publicar su negocio",Toast.LENGTH_LONG).show();
            return;
        }



             /*  StorageReference fotoref = FirebaseStorage.getInstance().getReference(PROMOS_PHOTOS_REFRENCE+"/"+selectedImage.getLastPathSegment());
        UploadTask uploadTask = fotoref.putFile(selectedImage);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                hidepDialog();
                showSnackbar(exception.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                bannerURL = taskSnapshot.getDownloadUrl();
                uploadAttemp();
            }
        });*/

    }


    private void fillDataRemain(){


        //optional images
        //
        if(fotouno!= null && fotoUnoChange){
            StorageReference fotoref = FirebaseStorage.getInstance().getReference(PROMOS_PHOTOS_REFRENCE+"/"+fotouno.getLastPathSegment());
            UploadTask uploadTask = fotoref.putFile(fotouno);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(MisNegociosActivity.this,exception.getMessage(),Toast.LENGTH_LONG).show();
                    hidepDialog();
                    return;
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    negocioPorPublicar.setImagen_promo1( taskSnapshot.getDownloadUrl().toString());
                    if(fotodos!= null && fotodosChange){
                        StorageReference fotoref = FirebaseStorage.getInstance().getReference(PROMOS_PHOTOS_REFRENCE+"/"+fotodos.getLastPathSegment());
                        UploadTask uploadTask = fotoref.putFile(fotodos);

                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Toast.makeText(MisNegociosActivity.this,exception.getMessage(),Toast.LENGTH_LONG).show();
                                hidepDialog();

                                return;
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                                negocioPorPublicar.setImagen_promo2( taskSnapshot.getDownloadUrl().toString());
                                if(fototres!= null){
                                    StorageReference fotoref = FirebaseStorage.getInstance().getReference(PROMOS_PHOTOS_REFRENCE+"/"+fototres.getLastPathSegment());
                                    UploadTask uploadTask = fotoref.putFile(fototres);

                                    uploadTask.addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            Toast.makeText(MisNegociosActivity.this,exception.getMessage(),Toast.LENGTH_LONG).show();
                                            hidepDialog();

                                            return;
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                                            negocioPorPublicar.setImagen_promo3( taskSnapshot.getDownloadUrl().toString());
                                            finisData();
                                        }
                                    });
                                }else{
                                    //roced wit data
                                    finisData();
                                }
                            }
                        });
                    }else{
                        //proced with data
                        finisData();
                    }

                }
            });
        }else{
            // proced wuth data
            if(fotodos!= null && fotodosChange){
                StorageReference fotoref = FirebaseStorage.getInstance().getReference(PROMOS_PHOTOS_REFRENCE+"/"+fotodos.getLastPathSegment());
                UploadTask uploadTask = fotoref.putFile(fotodos);

                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(MisNegociosActivity.this,exception.getMessage(),Toast.LENGTH_LONG).show();
                        hidepDialog();

                        return;
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        negocioPorPublicar.setImagen_promo2( taskSnapshot.getDownloadUrl().toString());
                        if(fototres!= null){
                            StorageReference fotoref = FirebaseStorage.getInstance().getReference(PROMOS_PHOTOS_REFRENCE+"/"+fototres.getLastPathSegment());
                            UploadTask uploadTask = fotoref.putFile(fototres);

                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Toast.makeText(MisNegociosActivity.this,exception.getMessage(),Toast.LENGTH_LONG).show();
                                    hidepDialog();

                                    return;
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                                    negocioPorPublicar.setImagen_promo3( taskSnapshot.getDownloadUrl().toString());
                                    finisData();
                                }
                            });
                        }else{
                            //roced wit data
                            finisData();
                        }
                    }
                });
            }else{
                //proced with data
                if(fototres!= null && fototresChange){
                    StorageReference fotoref = FirebaseStorage.getInstance().getReference(PROMOS_PHOTOS_REFRENCE+"/"+fototres.getLastPathSegment());
                    UploadTask uploadTask = fotoref.putFile(fototres);

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(MisNegociosActivity.this,exception.getMessage(),Toast.LENGTH_LONG).show();
                            hidepDialog();

                            return;
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                            negocioPorPublicar.setImagen_promo3( taskSnapshot.getDownloadUrl().toString());
                            finisData();
                        }
                    });
                }else{
                    //roced wit data
                    finisData();
                }
            }
        }

    }


    class mySpinnerListenerC implements Spinner.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView parent, View v, int position,
                                   long id) {


            if (firstTopicCategoria) {
                firstTopicCategoria = false;
                return;
            } else {
                if (position > 0) {

                    categoriaSeleccionada = allCategorias.get(position);

                } else {
                    categoriaSeleccionada = null;


                }
            }


        }


        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && null != data && requestCode != MY_PERMISSIONS_REQUEST_LOCATION) {

            String[] filePathColumn = {MediaStore.Images.Media.DATA};


            switch (requestCode) {
                case BIG_FOTO:
                    bigImage = data.getData();
                    Cursor cursor = getContentResolver().query(data.getData(),
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    if(sizeOfFileSelectedIsBigger(picturePath)){
                        bigImage = null;
                        Toast.makeText(MisNegociosActivity.this,"El tamaño maximo " +
                                "por foto es de 512kb",Toast.LENGTH_LONG).show();
                    }else{
                        negocio_imagen.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                        bigImageChange = true;
                    }



                    break;
                case BANNER:
                    bannerImage = data.getData();
                    cursor = getContentResolver().query(data.getData(),
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    if(sizeOfFileSelectedIsBigger(picturePath)){
                        bannerImage = null;
                        Toast.makeText(MisNegociosActivity.this,"El tamaño maximo " +
                                "por foto es de 512kb",Toast.LENGTH_LONG).show();
                    }else{
                        banner_image.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                        promoChange = true;
                    }


                    break;
                case FOTOUNO:
                    fotouno = data.getData();
                    cursor = getContentResolver().query(data.getData(),
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    if(sizeOfFileSelectedIsBigger(picturePath)){
                        fotouno = null;
                        Toast.makeText(MisNegociosActivity.this,"El tamaño maximo " +
                                "por foto es de 512kb",Toast.LENGTH_LONG).show();
                    }else{
                        rect_uno.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                        fotoUnoChange = true;
                    }


                    break;
                case FOTODOS:
                    fotodos = data.getData();
                    cursor = getContentResolver().query(data.getData(),
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    if(sizeOfFileSelectedIsBigger(picturePath)){
                        fotodos = null;
                        Toast.makeText(MisNegociosActivity.this,"El tamaño maximo " +
                                "por foto es de 512kb",Toast.LENGTH_LONG).show();
                    }else{
                        rect_dos.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                        fotodosChange = true;
                    }


                    break;
                case FOTOTRES:
                    fototres = data.getData();
                    cursor = getContentResolver().query(data.getData(),
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    if(sizeOfFileSelectedIsBigger(picturePath)){
                        fototres = null;
                        Toast.makeText(MisNegociosActivity.this,"El tamaño maximo " +
                                "por foto es de 512kb",Toast.LENGTH_LONG).show();
                    }else{
                        rect_tres.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                        fototresChange = true;
                    }


                    break;
            }


        }

    }


    @Override
    public void OnAddEspecialDay(FechaEspecial fechaEspecial) {

        if(diasEspecialesAdapter.containsFEcha(fechaEspecial)){
            Toast.makeText(MisNegociosActivity.this,"La fecha ya esta en los dias especiales",Toast.LENGTH_LONG).show();
        }else{
            diasEspecialesAdapter.addDiaEspecial(fechaEspecial);

        }

    }

    @Override
    public void ONDeleteServicio(FechaEspecial diaEspecial) {

    }



}
