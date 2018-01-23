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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import mx.com.cesarcorona.directorio.R;
import mx.com.cesarcorona.directorio.dialogs.TimeSelectorDialog;
import mx.com.cesarcorona.directorio.pojo.Categoria;

public class PublicarNegocioActivity extends BaseAnimatedActivity implements OnMapReadyCallback, TimeSelectorDialog.OnTimeSelectedInterface {


    private Uri bigImage;
    private Uri bannerImage;
    private Uri fotouno;
    private Uri fotodos;
    private Uri fototres;

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
        publicarNegocioButton = (Button) findViewById(R.id.publicar_button);
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
        domingo = (RadioGroup)findViewById(R.id.myRadioGroupdomi);
        docimilio = (RadioGroup)findViewById(R.id.myRadioGrouptarjeta);


        tLa = (TextView)findViewById(R.id.la);
        tLc = (TextView)findViewById(R.id.lc);
        tMa = (TextView)findViewById(R.id.ma);
        tMc = (TextView)findViewById(R.id.mc);
        tMia = (TextView)findViewById(R.id.mia);
        tMic = (TextView)findViewById(R.id.mc);
        tJa = (TextView)findViewById(R.id.ja);
        tJv = (TextView)findViewById(R.id.jc);
        tVa = (TextView)findViewById(R.id.va);
        tVc = (TextView)findViewById(R.id.vc);
        tSa = (TextView)findViewById(R.id.sa);
        tSc = (TextView)findViewById(R.id.sc);
        tDa = (TextView)findViewById(R.id.da);
        TDc = (TextView)findViewById(R.id.dc);



        initEnviroment();
        uploadFotoLisening();
        fillCategorias();
        initMap();
        initLocation();
        initAddresSearch();


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
        if (!pDialog.isShowing())
            pDialog.show();
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
