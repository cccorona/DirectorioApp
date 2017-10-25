package mx.com.cesarcorona.directorio.activities;

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
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;

import mx.com.cesarcorona.directorio.R;
import mx.com.cesarcorona.directorio.Utils.DateUtils;
import mx.com.cesarcorona.directorio.pojo.Negocio;

import static mx.com.cesarcorona.directorio.R.id.map;
import static mx.com.cesarcorona.directorio.R.id.web_action;

public class NegocioDetailActivity extends BaseAnimatedActivity  implements OnMapReadyCallback,BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener{



    public static String TAG = NegocioDetailActivity.class.getSimpleName();
    public static String KEY_NEGOCIO = "negocio";
    public static int GALLERY_PROMO_DURATION = 4000;
    private static final int MAX_ZOOM = 16;
    private FusedLocationProviderClient mFusedLocationClient;





    private GoogleMap mMap;
    private ImageView negocioImagen,phoneAction,whatsAction,twitterAction,emailAction,navegarButton,faceAction;
    private TextView nombreNegocio, horarioNegocio,descripcionNegocio,openStatus,aDocmicilio,phoneValue,whatsValue,twitterValue,emailValue,faceValue;
    private SliderLayout promoGallery;
    private Negocio negocioSeleccionado;
    private PagerIndicator pagerIndicator;
    private Location lastLocation;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_negocio_detail);
        negocioSeleccionado = (Negocio) getIntent().getExtras().getSerializable(KEY_NEGOCIO);

        negocioImagen = (ImageView) findViewById(R.id.negocio_imagen);
        phoneAction = (ImageView) findViewById(R.id.phone_action);
        whatsAction = (ImageView) findViewById(R.id.whats_action);
        twitterAction = (ImageView) findViewById(R.id.twitter_action);
        emailAction = (ImageView) findViewById(R.id.web_action);
        navegarButton = (ImageView) findViewById(R.id.button_navegar);
        faceAction = (ImageView) findViewById(R.id.facebook_action);
        nombreNegocio = (TextView) findViewById(R.id.nombre_negocio);
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


        loadMapa();
        setValues();
        loadPromos();
        startListening();
        getLocation();



    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng negocioLatLog = new LatLng(Double.parseDouble(negocioSeleccionado.getUbicacion().get("lat")),Double.parseDouble(negocioSeleccionado.getUbicacion().get("lon")));
        mMap.addMarker(new MarkerOptions().position(negocioLatLog).title(negocioSeleccionado.getDisplay_title()));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(negocioLatLog,MAX_ZOOM));


    }

    private void loadMapa(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);
    }

    private void setValues(){
        Picasso.with(NegocioDetailActivity.this).load(negocioSeleccionado.getUrl_logo()).resize(300,180).into(negocioImagen);
        nombreNegocio.setText(negocioSeleccionado.getDisplay_title());

        StringBuilder stringBuilder = new StringBuilder(negocioSeleccionado.getOpen_days_aleter());
        stringBuilder.append("\n").append(negocioSeleccionado.getOpen_time()).append("-").append(negocioSeleccionado.getClose_time());
        horarioNegocio.setText(stringBuilder.toString());
        descripcionNegocio.setText(negocioSeleccionado.getDescripcion());
        phoneValue.setText(negocioSeleccionado.getPhone());
        whatsValue.setText(negocioSeleccionado.getWhatsapp());
        emailValue.setText(negocioSeleccionado.getWeb());
        twitterValue.setText(negocioSeleccionado.getTwitter());
        if(negocioSeleccionado.isEntregaADomicilio()){
            aDocmicilio.setVisibility(View.VISIBLE);
        }


        String startHour = negocioSeleccionado.getOpen_time();
        String endHour = negocioSeleccionado.getClose_time();
        if(DateUtils.isNowInInterval(startHour,endHour)){
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_WEEK);
//                if(negocioSeleccionado.getOpen_days().get(String.valueOf(day)).equalsIgnoreCase("Y")){
                    openStatus.setBackgroundColor(getResources().getColor(R.color.colorAbierto));
            openStatus.setText("Abierto");

  //              }
        }
    }

    private void loadPromos(){

        if(negocioSeleccionado.getUrl_promos()!= null && negocioSeleccionado.getUrl_promos().size() >0){
            promoGallery.setVisibility(View.VISIBLE);
            for(String name : negocioSeleccionado.getUrl_promos().keySet()){
                TextSliderView textSliderView = new TextSliderView(this);
                textSliderView
                        .description(name)
                        .image(negocioSeleccionado.getUrl_promos().get(name))
                        .setScaleType(BaseSliderView.ScaleType.Fit)
                        .setOnSliderClickListener(this);

                //add your extra information
                textSliderView.bundle(new Bundle());
                textSliderView.getBundle()
                        .putString("extra",name);

                promoGallery.addSlider(textSliderView);
            }
            promoGallery.setDuration(GALLERY_PROMO_DURATION);
            promoGallery.addOnPageChangeListener(this);
            promoGallery.setCustomIndicator(pagerIndicator);
            promoGallery.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Visible);
        }




    }


    private void startListening(){

        phoneAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                String stringPhone = negocioSeleccionado.getPhone().replace("-","");
                intent.setData(Uri.parse("tel:" + stringPhone));
                startActivity(intent);//check permissions
            }
        });

        whatsAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickWhatsApp();
            }
        });

        twitterAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              onTwitterClick();
            }
        });

        faceAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFacebookClicked();
            }
        });

        emailAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webAction();

            }
        });

    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

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
            Toast.makeText(this, "WhatsApp no se encuentra instalado en tu telefono", Toast.LENGTH_SHORT)
                    .show();
        }

    }

    public void onTwitterClick(){
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
    }


    public void onFacebookClicked() {

            PackageManager pm = getPackageManager();
            Uri uri = Uri.parse(negocioSeleccionado.getFacebook());

            try {
                ApplicationInfo applicationInfo = pm.getApplicationInfo("com.facebook.katana", 0);
                if (applicationInfo.enabled) {
                    uri = Uri.parse("fb://facewebmodal/f?href=" + negocioSeleccionado.getFacebook());
                }
            }

            catch (PackageManager.NameNotFoundException ignored) {
            }

            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        }

        public void webAction(){
            String url = negocioSeleccionado.getWeb();
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }


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


    }



