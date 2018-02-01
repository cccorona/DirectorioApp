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
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.TimerTask;

import mx.com.cesarcorona.directorio.MainActivity;
import mx.com.cesarcorona.directorio.R;
import mx.com.cesarcorona.directorio.dialogs.TimeSelectorDialog;
import mx.com.cesarcorona.directorio.pojo.Categoria;
import mx.com.cesarcorona.directorio.pojo.Negocio;

import static mx.com.cesarcorona.directorio.Utils.DataLoader.PROMOS_PHOTOS_REFRENCE;
import static mx.com.cesarcorona.directorio.activities.NegocioPorCategoriaActivity.GEO_REFERENCE;
import static mx.com.cesarcorona.directorio.activities.NegocioPorCategoriaActivity.NEGOCIOS_REFERENCE;

public class PublicarNegocioActivity extends BaseAnimatedActivity implements OnMapReadyCallback, TimeSelectorDialog.OnTimeSelectedInterface {


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




    private ImageView bigFoto, bannerFoto, fotoUno, fotoDos, fotoTres;
    private ImageView negocio_imagen, banner_image, rect_uno, rect_dos, rect_tres;
    private EditText nombre_negocio, negocio_descripcion;
    private Spinner categorySpinner;
    private ProgressDialog pDialog;
    private RadioGroup lunes,martes,miercoles,jueves,viernes,sabado,domingo;
    private RadioGroup abre23,tarjeta,docimilio;
    private TextView tLa,tLc,tMa,tMc,tMia,tMic,tJa,tJv,tVa,tVc,tSa,tSc,tDa,TDc;
    private Button publicarNegocioButton;
    private EditText faceBookValue , mailValue, whtasValue, twitterValue,telefonoValue;


    private LinkedList<Categoria> allCategorias;
    private boolean firstTopic;
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







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicar_negocio);
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




        initEnviroment();
        uploadFotoLisening();
        fillCategorias();
        initMap();
        initLocation();
        initAddresSearch();

        ImageView back_button= (ImageView)findViewById(R.id.back_arrow_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PublicarNegocioActivity.super.onBackPressed();
            }
        });

        ImageView homeButton=(ImageView)findViewById(R.id.home_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(PublicarNegocioActivity.this, MainActivity.class);
                startActivity(mainIntent);
            }
        });




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

        pDialog = new ProgressDialog(PublicarNegocioActivity.this);
        pDialog.setMessage("Por favor espere");
        pDialog.setCancelable(false);
        diasAbiertos = new HashMap<>();


    }


    private void publishAttempt(){


      //  private Uri fotouno;
       // private Uri fotodos;
       // private Uri fototres;

        negocioPorPublicar = new Negocio();

        if(bigImage!= null &&  bannerImage != null && nombre_negocio.getText().length()>0 && negocio_descripcion.getText().length() >0
                && categoriaSeleccionada != null && placeSelected != null && latitud !=0 && longitud != 0 && datesAreComplete()){
            showpDialog();

            StorageReference fotoref = FirebaseStorage.getInstance().getReference(PROMOS_PHOTOS_REFRENCE+"/"+bigImage.getLastPathSegment());
        UploadTask uploadTask = fotoref.putFile(bigImage);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(PublicarNegocioActivity.this,exception.getMessage(),Toast.LENGTH_LONG).show();
                hidepDialog();
                return;
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                bigImageUrl = taskSnapshot.getDownloadUrl();
                negocioPorPublicar.setLogo(bigImageUrl.toString());
                negocioPorPublicar.setLogo_negocio(bigImageUrl.toString());
                negocioPorPublicar.setUrl_logo(bigImageUrl.toString());
                negocioPorPublicar.setFull_url_logo(bigImageUrl.toString());
                StorageReference fotoref = FirebaseStorage.getInstance().getReference(PROMOS_PHOTOS_REFRENCE+"/"+bannerImage.getLastPathSegment());
                UploadTask uploadTask = fotoref.putFile(bannerImage);

                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(PublicarNegocioActivity.this,exception.getMessage(),Toast.LENGTH_LONG).show();
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

            }
           });





        }else{
            Toast.makeText(PublicarNegocioActivity.this,"Rellene todos los datos para publicar su negocio",Toast.LENGTH_LONG).show();
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
        if(fotouno!= null){
           StorageReference fotoref = FirebaseStorage.getInstance().getReference(PROMOS_PHOTOS_REFRENCE+"/"+fotouno.getLastPathSegment());
           UploadTask uploadTask = fotoref.putFile(fotouno);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(PublicarNegocioActivity.this,exception.getMessage(),Toast.LENGTH_LONG).show();
                    hidepDialog();
                    return;
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    negocioPorPublicar.setImagen_promo1( taskSnapshot.getDownloadUrl().toString());
                    if(fotodos!= null){
                        StorageReference fotoref = FirebaseStorage.getInstance().getReference(PROMOS_PHOTOS_REFRENCE+"/"+fotodos.getLastPathSegment());
                        UploadTask uploadTask = fotoref.putFile(fotodos);

                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Toast.makeText(PublicarNegocioActivity.this,exception.getMessage(),Toast.LENGTH_LONG).show();
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
                                            Toast.makeText(PublicarNegocioActivity.this,exception.getMessage(),Toast.LENGTH_LONG).show();
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
            finisData();
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

        if(conunt == diasAbiertos.size()){
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
        negocioPorPublicar.setUbicacion(""+placeSelected.getLatLng().latitude+","+placeSelected.getLatLng().longitude);
        negocioPorPublicar.setDiasAbiertos(diasAbiertos);
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
        negocioPorPublicar.setPublicado("No");
        negocioPorPublicar.setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(NEGOCIOS_REFERENCE);
        String key = databaseReference.push().getKey();
        final String keyToPush = "Negocio"+key;
        databaseReference = FirebaseDatabase.getInstance().getReference("Example/"+NEGOCIOS_REFERENCE+"/"+keyToPush);
        databaseReference.setValue(negocioPorPublicar).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(PublicarNegocioActivity.this,"Negocio esperando publicacion de la administracion",Toast.LENGTH_LONG).show();
                    DatabaseReference geoReference = FirebaseDatabase.getInstance().getReference(GEO_REFERENCE);
                    GeoFire geoFire = new GeoFire(geoReference);
                    geoFire.setLocation(keyToPush, new GeoLocation(placeSelected.getLatLng().latitude, placeSelected.getLatLng().longitude)
                            , new GeoFire.CompletionListener() {
                                @Override
                                public void onComplete(String key, DatabaseError error) {
                                    if(error == null){
                                        Intent mainInent = new Intent(PublicarNegocioActivity.this, MainActivity.class);
                                        startActivity(mainInent);
                                    }else{

                                    }
                                }
                            });
                }else{
                    Toast.makeText(PublicarNegocioActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();

                }
                hidepDialog();

            }
        });


    }



    private void fillCategorias() {
        showpDialog();
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
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(PublicarNegocioActivity.this,
                        R.layout.spinner_style, list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categorySpinner.setAdapter(dataAdapter);

                hidepDialog();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hidepDialog();

            }
        });

        categorySpinner.setOnItemSelectedListener(new PublicarNegocioActivity.mySpinnerListener());


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        currentMap = googleMap;

    }

    private void initLocation(){
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(PublicarNegocioActivity.this);
        centerMapOnCurrentLocation();



    }

    private void initMap(){
        mapFragment.getMapAsync(this);

    }


    private void centerMapOnCurrentLocation(){
        if (ContextCompat.checkSelfPermission(PublicarNegocioActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(PublicarNegocioActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block


            } else {


                ActivityCompat.requestPermissions(PublicarNegocioActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

            }
        } else {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(PublicarNegocioActivity.this, new OnSuccessListener<Location>() {
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
                    negocio_imagen.setImageBitmap(BitmapFactory.decodeFile(picturePath));


                    break;
                case BANNER:
                    bannerImage = data.getData();
                    cursor = getContentResolver().query(data.getData(),
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                     columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                     picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    banner_image.setImageBitmap(BitmapFactory.decodeFile(picturePath));

                    break;
                case FOTOUNO:
                    fotouno = data.getData();
                    cursor = getContentResolver().query(data.getData(),
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    picturePath = cursor.getString(columnIndex);
                    rect_uno.setImageBitmap(BitmapFactory.decodeFile(picturePath));

                    break;
                case FOTODOS:
                    fotodos = data.getData();
                    cursor = getContentResolver().query(data.getData(),
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    picturePath = cursor.getString(columnIndex);
                    rect_dos.setImageBitmap(BitmapFactory.decodeFile(picturePath));

                    break;
                case FOTOTRES:
                    fototres = data.getData();
                    cursor = getContentResolver().query(data.getData(),
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    picturePath = cursor.getString(columnIndex);
                    rect_tres.setImageBitmap(BitmapFactory.decodeFile(picturePath));

                    break;
            }


        }

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
                tSa.setClickable(false);
                diasAbiertos.remove("sa");
                diasAbiertos.remove("sc");
                break;

            case R.id.yesS:
                tSa.setClickable(true);
                tSa.setClickable(true);
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

    private void showpDialog() {
        if (!pDialog.isShowing()){
            pDialog.show();
        }
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
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
                Toast.makeText(PublicarNegocioActivity.this,status.getStatusMessage(),Toast.LENGTH_LONG).show();
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


        String hourSelected = String.valueOf(currentHour)
                + " : " + String.valueOf(minute) + " " + aMpM;
        diasAbiertos.put(currentIdDay,hourSelected);
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


    class mySpinnerListener implements Spinner.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView parent, View v, int position,
                                   long id) {


            if (firstTopic) {
                firstTopic = false;
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

}
