package mx.com.cesarcorona.directorio.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.Location;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.Exclude;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import mx.com.cesarcorona.directorio.MainActivity;
import mx.com.cesarcorona.directorio.R;
import mx.com.cesarcorona.directorio.Utils.DateUtils;
import mx.com.cesarcorona.directorio.dialogs.FullScreenDialog;
import mx.com.cesarcorona.directorio.pojo.Negocio;


import static mx.com.cesarcorona.directorio.R.id.map;
import static mx.com.cesarcorona.directorio.R.id.transition_current_scene;

public class NegocioDetailActivity extends BaseAnimatedActivity  implements OnMapReadyCallback,BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener{



    public static String TAG = NegocioDetailActivity.class.getSimpleName();
    public static String KEY_NEGOCIO = "negocio";
    public static String KEY_NEGOCIO_ID="negocioid";
    public static int GALLERY_PROMO_DURATION = 4000;
    private static final int MAX_ZOOM = 16;
    private FusedLocationProviderClient mFusedLocationClient;
    private boolean firstTopic;





    private GoogleMap mMap;
    private ImageView negocioImagen,phoneAction,whatsAction,twitterAction,emailAction,navegarButton,faceAction;
    private TextView nombreNegocio, horarioNegocio,descripcionNegocio,openStatus,aDocmicilio,phoneValue,whatsValue,twitterValue,emailValue,faceValue,paginaWebValue;
    private SliderLayout promoGallery;
    private Negocio negocioSeleccionado;
    private PagerIndicator pagerIndicator;
    private Location lastLocation;
    private TextView direccionName,nombreGrandeNegocio;
    private Spinner semanaSpinner;
    private LinkedList<String> diasSemama;
    private String diaSeleccionado;
    private ImageView zoomIcon;
    private TextView dia_text;
    private View bigPhoneAction, bigWahtsAction,bigTwitterAction,bigEmailAction,bigFaceAction,bigWebPageAction;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_negocio_detail);
        negocioSeleccionado = (Negocio) getIntent().getExtras().getSerializable(KEY_NEGOCIO);
        firstTopic = true;

        negocioImagen = (ImageView) findViewById(R.id.negocio_imagen);
        phoneAction = (ImageView) findViewById(R.id.phone_action);
        whatsAction = (ImageView) findViewById(R.id.whats_action);
        twitterAction = (ImageView) findViewById(R.id.twitter_action);
        paginaWebValue = (TextView) findViewById(R.id.pagina_value);
        semanaSpinner = (Spinner) findViewById(R.id.semana_spinner);
        zoomIcon = (ImageView)findViewById(R.id.zoom_icon);
        dia_text = (TextView)findViewById(R.id.dia_text);
        diasSemama = new LinkedList<>();

        emailAction = (ImageView) findViewById(R.id.web_action);
        navegarButton = (ImageView) findViewById(R.id.button_navegar);
        faceAction = (ImageView) findViewById(R.id.facebook_action);
        nombreNegocio = (TextView) findViewById(R.id.nombre_grande_negocio);
        descripcionNegocio = (TextView)findViewById(R.id.negocio_descripcion);
        horarioNegocio = (TextView) findViewById(R.id.horario_negocio);
        openStatus = (TextView) findViewById(R.id.open_status_negocio);
        aDocmicilio = (TextView) findViewById(R.id.domicilio_status_negocio);
        phoneValue = (TextView) findViewById(R.id.phone_value);
        whatsValue = (TextView) findViewById(R.id.whats_value);
        twitterValue = (TextView) findViewById(R.id.twiter_value);
        faceValue = (TextView) findViewById(R.id.facebook_value) ;
        emailValue = (TextView) findViewById(R.id.web_value);
        promoGallery= (SliderLayout) findViewById(R.id.slider);
        pagerIndicator = (PagerIndicator)findViewById(R.id.custom_indicator);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        direccionName = (TextView)findViewById(R.id.direccopn_name);

        bigPhoneAction =findViewById(R.id.big_click_phone);
        bigWahtsAction = findViewById(R.id.big_click_whats);
        bigTwitterAction = findViewById(R.id.big_click_twitter);
        bigEmailAction = findViewById(R.id.big_click_correo);
        bigFaceAction =  findViewById(R.id.big_click_facebook);
        bigWebPageAction = findViewById(R.id.big_click_web_page);



        bigPhoneAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(negocioSeleccionado.getTelefono() == null || negocioSeleccionado.getTelefono().equals("")){
                    Toast.makeText(NegocioDetailActivity.this,"El negocio no cuenta con ningun teléfono",Toast.LENGTH_LONG).show();
                }else{
                    try{
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        if(negocioSeleccionado.getTelefono()!=null){
                            String stringPhone = negocioSeleccionado.getTelefono().replace("-","");
                            intent.setData(Uri.parse("tel:" + stringPhone));
                        }
                        startActivity(intent);
                    }catch (Exception e){
                        Toast.makeText(NegocioDetailActivity.this,"Su teléfono no cuenta con niguna aplicación para realizar esta acción",Toast.LENGTH_LONG).show();
                    }
                }




            }
        });


        bigWahtsAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickWhatsApp();

            }
        });

        bigTwitterAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTwitterClick();
            }
        });

        bigEmailAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(negocioSeleccionado.getWeb()== null || negocioSeleccionado.getWeb().equals("")){
                    Toast.makeText(NegocioDetailActivity.this,"El negocio no cuenta con un correo para contacto",Toast.LENGTH_LONG).show();

                }else{
                    try{
                        String addresses[] ={negocioSeleccionado.getWeb()};
                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Contacto");
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                        }
                    }catch (Exception e){
                        Toast.makeText(NegocioDetailActivity.this,"Tu teléfono no cuenta con ninguna aplicacón para mandara correos",Toast.LENGTH_LONG).show();

                    }
                }


            }
        });



        bigFaceAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFacebookClicked();
            }
        });


        bigWebPageAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paginaWebActio();

            }
        });





        zoomIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FullScreenDialog fullScreenDialog  = new FullScreenDialog();
                Bundle extras = new Bundle();
                extras.putString(FullScreenDialog.IMAGE_PATH,negocioSeleccionado.getLogo_negocio());
                fullScreenDialog.setArguments(extras);
                fullScreenDialog.show(getSupportFragmentManager(),FullScreenDialog.TAG);
            }
        });


        loadMapa();
        setValues();
        loadPromos();
        startListening();
        getLocation();
        setWeek();

        ImageView back_button= (ImageView)findViewById(R.id.back_arrow_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NegocioDetailActivity.super.onBackPressed();
            }
        });

        ImageView homeButton=(ImageView)findViewById(R.id.home_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(NegocioDetailActivity.this, MainActivity.class);
                startActivity(mainIntent);
            }
        });





    }


    private void setWeek(){
        diasSemama.add("Ver dia");
        diasSemama.add("Domingo");
        diasSemama.add("Lunes");
        diasSemama.add("Martes");
        diasSemama.add("Miercoles");
        diasSemama.add("Jueves");
        diasSemama.add("Viernes");
        diasSemama.add("Sabado");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(NegocioDetailActivity.this,
                R.layout.spinner_style, diasSemama);
        dataAdapter.setDropDownViewResource(R.layout.spinner_center_item);
        semanaSpinner.setAdapter(dataAdapter);
        semanaSpinner.setOnItemSelectedListener(new NegocioDetailActivity.mySpinnerListener());
        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        semanaSpinner.setSelection(day);
        if(negocioSeleccionado.getAbierto_24_horas().equals("Si")){
            semanaSpinner.setEnabled(false);
            semanaSpinner.setClickable(false);
        }
        updateUI(day);




    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng negocioLatlog = null;
        String latLon[] = negocioSeleccionado.getUbicacion().split(",");
        if(latLon!= null &&latLon.length>=2){
             negocioLatlog = new LatLng(Double.parseDouble(latLon[0]),Double.parseDouble(latLon[1]));
            mMap.addMarker(new MarkerOptions().position(negocioLatlog).title(negocioSeleccionado.getNombre()));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(negocioLatlog,MAX_ZOOM));

        }
       // LatLng negocioLatLog = new LatLng(Double.parseDouble(negocioSeleccionado.getUbicacion().get("lat")),Double.parseDouble(negocioSeleccionado.getUbicacion().get("lon")));
       // mMap.addMarker(new MarkerOptions().position(negocioLatLog).title(negocioSeleccionado.getDisplay_title()));
       // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(negocioLatLog,MAX_ZOOM));


    }

    private void loadMapa(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);
    }

    private void setValues(){
        Picasso.with(NegocioDetailActivity.this).load(negocioSeleccionado.getLogo_negocio()).resize(300,180).into(negocioImagen);
        nombreNegocio.setText(negocioSeleccionado.getNombre());
        direccionName.setText(negocioSeleccionado.getDireccionName());

        //StringBuilder stringBuilder = new StringBuilder(negocioSeleccionado.);
        //stringBuilder.append("\n").append(negocioSeleccionado.getOpen_time()).append("-").append(negocioSeleccionado.getClose_time());
        if(negocioSeleccionado.getAbierto_24_horas()!= null && negocioSeleccionado.getAbierto_24_horas().equals("Si")){
            horarioNegocio.setText("Abierto");

        }else{
            if(CloseTomeActivity.isOpenNow(negocioSeleccionado)){
                openStatus.setBackgroundColor(getResources().getColor(R.color.colorAbierto));
                openStatus.setText("Abierto");
                if(negocioSeleccionado.getAbierto_24_horas().equals("Si")){
                    horarioNegocio.setText("Abierto 24 horas");

                }else{
                    String dateKeys[] = DateUtils.getCurrentDatKeys();
                    if(dateKeys!= null && dateKeys.length >0){
                        horarioNegocio.setText(DateUtils.formatDate(negocioSeleccionado.getDiasAbiertos().get(dateKeys[0]),
                                negocioSeleccionado.getDiasAbiertos().get(dateKeys[1])));

                    }
                }


            }else{
                horarioNegocio.setText("Cerrado");
            }

        }


        descripcionNegocio.setText(negocioSeleccionado.getDescripcion());
        phoneValue.setText(negocioSeleccionado.getTelefono());
        whatsValue.setText(negocioSeleccionado.getWhatsapp());
        emailValue.setText(negocioSeleccionado.getWeb());
        twitterValue.setText(negocioSeleccionado.getTwitter());
        paginaWebValue.setText(negocioSeleccionado.getPagina_web());
        faceValue.setText(negocioSeleccionado.getFacebook());

        if(negocioSeleccionado.getEntrega_a_domicilio().equals("Si")){
            aDocmicilio.setVisibility(View.VISIBLE);
        }


        if(CloseTomeActivity.isOpenNow(negocioSeleccionado)){
            openStatus.setBackgroundColor(getResources().getColor(R.color.colorAbierto));
            openStatus.setText("Abierto");
        }

    }

    private void loadPromos(){

        LinkedList<String> urlsPromos = new LinkedList<>();
        urlsPromos.add(negocioSeleccionado.getImagen_promo1());
        urlsPromos.add(negocioSeleccionado.getImagen_promo2());
        urlsPromos.add(negocioSeleccionado.getImagen_promo3());
        int promosAdde = 0;

        if(urlsPromos!= null && urlsPromos.size() >0){
            for(String name : urlsPromos){
                if(name!= null && !name.equals("")){
                    promosAdde++;
                    TextSliderView textSliderView = new TextSliderView(this);
                    textSliderView
                            .description("")
                            .image(name)
                            .setScaleType(BaseSliderView.ScaleType.Fit)
                            .setOnSliderClickListener(this);

                    //add your extra information
                    textSliderView.bundle(new Bundle());
                    textSliderView.getBundle()
                            .putString("extra",name);

                    promoGallery.addSlider(textSliderView);
                }

            }
            if(promosAdde>0){
                promoGallery.setVisibility(View.VISIBLE);
                promoGallery.setDuration(GALLERY_PROMO_DURATION);
                promoGallery.addOnPageChangeListener(this);
                promoGallery.setCustomIndicator(pagerIndicator);
                promoGallery.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Visible);
            }

        }


    }


    private void startListening(){

        phoneAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


              //  startActivity(intent);//check permissions
            }
        });

        whatsAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        twitterAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        faceAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        emailAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        paginaWebValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });



        navegarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String latlong[] = negocioSeleccionado.getUbicacion().split(",");
                String lat ="";
                String lon="";

                if(latlong != null && latlong.length>1){
                    lat = latlong[0];
                    lon = latlong[1];

                }
                Uri gmmIntentUri = Uri.parse("google.navigation:q="+lat+","+lon);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });

    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
       String bigPicture =  slider.getBundle().getString("extra");
        FullScreenDialog fullScreenDialog  = new FullScreenDialog();
        Bundle extras = new Bundle();
        extras.putString(FullScreenDialog.IMAGE_PATH,bigPicture);
        fullScreenDialog.setArguments(extras);
        fullScreenDialog.show(getSupportFragmentManager(),FullScreenDialog.TAG);


    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    public void onClickWhatsApp() {

        if(negocioSeleccionado.getWhatsapp() == null || negocioSeleccionado.getWhatsapp().equals("")){
            Toast.makeText(this, "El negocio no proporciono este metodo de contacto", Toast.LENGTH_SHORT).show();
            return;
        }else{
            PackageManager pm=getPackageManager();
            try {

                Intent waIntent = new Intent(Intent.ACTION_SEND);
                waIntent.setType("text/plain");
                String text = "YOUR TEXT HERE";

                PackageInfo info=pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
                waIntent.setPackage("com.whatsapp");

                waIntent.putExtra(Intent.EXTRA_TEXT, text);
                startActivity(Intent.createChooser(waIntent, "Enviar mensaje a"));

            } catch (PackageManager.NameNotFoundException e) {
                Toast.makeText(this, "WhatsApp no se encuentra instalado en tu telefono", Toast.LENGTH_SHORT).show();
            }
        }



    }

    public void onTwitterClick(){

        if(negocioSeleccionado.getTwitter() == null || negocioSeleccionado.getTwitter().equals("")){
            Toast.makeText(this, "El negocio no proporciono este metodo de contacto", Toast.LENGTH_SHORT).show();
            return;
        }else{
            try{
                Intent tweetIntent = new Intent(Intent.ACTION_SEND);
                tweetIntent.putExtra(Intent.EXTRA_TEXT, "This is a Test.");
                tweetIntent.setType("text/plain");

                PackageManager packManager = getPackageManager();
                List<ResolveInfo> resolvedInfoList = packManager.queryIntentActivities(tweetIntent,  PackageManager.MATCH_DEFAULT_ONLY);

                boolean resolved = false;
                for(ResolveInfo resolveInfo: resolvedInfoList){
                    if(resolveInfo.activityInfo.packageName.startsWith("com.twitter.android")){
                        tweetIntent.setClassName(
                                resolveInfo.activityInfo.packageName,
                                resolveInfo.activityInfo.name );
                        resolved = true;
                        break;
                    }
                }
                if(resolved){
                    startActivity(tweetIntent);
                }else{
                    Toast.makeText(this, "Twitter no se encontro en tu dispositivo", Toast.LENGTH_LONG).show();
                }

            }catch (Exception e){
                Toast.makeText(this, "Twitter no se encontro en tu dispositivo", Toast.LENGTH_LONG).show();

            }


        }


    }


    public void onFacebookClicked() {

          if(negocioSeleccionado.getFacebook() == null || negocioSeleccionado.getFacebook().equals("")){
              Toast.makeText(this, "El negocio no proporciono se Facebook", Toast.LENGTH_LONG).show();
               return;
          }else{
              PackageManager pm = getPackageManager();
              Uri uri = Uri.parse(negocioSeleccionado.getFacebook());

              try {
                  ApplicationInfo applicationInfo = pm.getApplicationInfo("com.facebook.katana", 0);
                  if (applicationInfo.enabled) {
                      uri = Uri.parse("fb://facewebmodal/f?href=" + negocioSeleccionado.getFacebook());
                  }
                  startActivity(new Intent(Intent.ACTION_VIEW, uri));

              }

              catch (Exception e) {
                  Toast.makeText(this, "No nay aplicaciones  en tu dispositivo para continuar con la acción", Toast.LENGTH_LONG).show();

              }

          }


        }




        public void paginaWebActio(){

        if(negocioSeleccionado.getPagina_web() == null || negocioSeleccionado.getPagina_web().equals("")){
            Toast.makeText(this, "El negocio no proporciono una pagina web", Toast.LENGTH_LONG).show();
            return;
        }else{
            try{
                String url = negocioSeleccionado.getPagina_web();
                if(url!=null){
                    if (!url.startsWith("http://") && !url.startsWith("https://"))
                        url = "http://" + url;
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            }catch (Exception e){
                Toast.makeText(NegocioDetailActivity.this,"No existen aplicaciones  en tu teléfono para manejar esta acción ",Toast.LENGTH_LONG).show();
            }
        }




        }


      @SuppressLint("MissingPermission")
      public void getLocation(){
          mFusedLocationClient.getLastLocation()
                  .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                      @Override
                      public void onSuccess(Location location) {
                          // Got last known location. In some rare situations this can be null.
                          if (location != null) {
                              lastLocation = location;
                          }
                      }
                  });
      }


    class mySpinnerListener implements Spinner.OnItemSelectedListener
    {
        @Override
        public void onItemSelected(AdapterView parent, View v, int position,
                                   long id) {


            if(firstTopic){
                firstTopic = false;

                return;
            }else{
                if(position >0){

                    diaSeleccionado = diasSemama.get(position);
                    updateUI(position);


                }else{
                    diaSeleccionado = null;

                }

            }


        }

        @Override
        public void onNothingSelected(AdapterView parent) {
            // TODO Auto-generated method stub
            // Do nothing.
        }

    }


    private void  updateUI(int dia){


          if(negocioSeleccionado.getAbierto_24_horas().equals("Si")){
              return;
          }

        switch (dia) {
            case Calendar.SUNDAY:
                // Current day is Sunday
                dia_text.setText("Domingo");
                break;

            case Calendar.MONDAY:
                dia_text.setText("Lunes");


                break;

            case Calendar.TUESDAY:
                dia_text.setText("Martes");


                break;
            case Calendar.THURSDAY:
                dia_text.setText("Miercoles");

                break;
            case Calendar.WEDNESDAY:
                dia_text.setText("Jueves");


                break;
            case Calendar.FRIDAY:
                dia_text.setText("Viernes");


                break;
            case Calendar.SATURDAY:
                dia_text.setText("Sabado");

                break;
            // etc.
        }



        String dateKeys[] = DateUtils.getKeyPerDay(dia);
        if(dateKeys!= null && dateKeys.length >0){
            String startHour = negocioSeleccionado.getDiasAbiertos().get(dateKeys[0]);
            String endHour = negocioSeleccionado.getDiasAbiertos().get(dateKeys[1]);

            if(startHour == null || endHour == null){
                horarioNegocio.setText("Cerrado");

            }else{
                horarioNegocio.setText(DateUtils.formatDate(negocioSeleccionado.getDiasAbiertos().get(dateKeys[0]),
                        negocioSeleccionado.getDiasAbiertos().get(dateKeys[1])));
            }

        }else{
            horarioNegocio.setText("Cerrado");
        }
    }




    }



