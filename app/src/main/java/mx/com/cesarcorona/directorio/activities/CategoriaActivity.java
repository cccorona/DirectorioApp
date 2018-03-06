package mx.com.cesarcorona.directorio.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import mx.com.cesarcorona.directorio.MainActivity;
import mx.com.cesarcorona.directorio.R;
import mx.com.cesarcorona.directorio.adapter.CategoryAdapter;
import mx.com.cesarcorona.directorio.pojo.Categoria;
import mx.com.cesarcorona.directorio.pojo.Negocio;
import mx.com.cesarcorona.directorio.pojo.PremiumBanner;

public class CategoriaActivity extends BaseAnimatedActivity implements CategoryAdapter.CategorySelectedListener{

    public static String CATEGORY_REFERENCE ="/categorias";
    public static String ALL_NEGOCIO_REFERENCE ="allnegocios";

    public static String PREMIUM_REFERENCE = "premium";
    public static String TAG = CategoriaActivity.class.getSimpleName();
    public static String ITEM_SELECTED ="categoria";


    private DatabaseReference mDatabase;
    private LinkedList<Categoria> allCategories;
    private GridView categoriesGrid;
    private CategoryAdapter categoryAdapter;
    private ProgressDialog pDialog;
    private CardView categoryPromo;
    private ImageView bannerPromo;
    private Negocio negocionOnMainBanner;
    private LinkedList<PremiumBanner> premiumNegocios;
    static Random rand = new Random();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria);
        categoryPromo = (CardView)findViewById(R.id.categoria_card_view);
        bannerPromo = (ImageView) findViewById(R.id.banner_promo);
        categoryPromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(negocionOnMainBanner != null){
                    Intent detailIntent = new Intent(CategoriaActivity.this,NegocioDetailActivity.class);
                    Bundle extras = new Bundle();
                    extras.putSerializable(NegocioDetailActivity.KEY_NEGOCIO,negocionOnMainBanner);
                    detailIntent.putExtras(extras);
                    startActivity(detailIntent);
                }else{
                    Toast.makeText(CategoriaActivity.this,"PH MOVIL, anunciate con nosotros",Toast.LENGTH_LONG).show();
                }

            }
        });



        initialize();
        showpDialog();
        recoverCategorys();
        showPremiunBanner();
        FirebaseCrash.log(TAG + "Activity created");

        ImageView back_button= (ImageView)findViewById(R.id.back_arrow_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategoriaActivity.super.onBackPressed();
            }
        });

        ImageView homeButton=(ImageView)findViewById(R.id.home_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(CategoriaActivity.this, MainActivity.class);
                startActivity(mainIntent);
            }
        });

    }

    private void recoverCategorys(){
        ValueEventListener categoryListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot categoriaSnap : dataSnapshot.getChildren()) {
                    Categoria categoria = categoriaSnap.getValue(Categoria.class);
                    categoria.setDataBaseReference(categoriaSnap.getKey());
                    if(categoria!= null && !categoria.getNombre().equals("Agregar titulo")){
                        allCategories.add(categoria);

                    }
                }

                categoryAdapter = new CategoryAdapter(allCategories,CategoriaActivity.this);
                categoryAdapter.setCategorySelectedListener(CategoriaActivity.this);
                categoriesGrid.setAdapter(categoryAdapter);
                hidepDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                FirebaseCrash.log(TAG + "loadPost:onCancelled" + databaseError.toException());
                hidepDialog();
            }
        };
        mDatabase.addListenerForSingleValueEvent(categoryListener);
    }

    private void initialize(){
        mDatabase = FirebaseDatabase.getInstance().getReference("Example"+"/"+CATEGORY_REFERENCE);
        categoriesGrid = (GridView) findViewById(R.id.categorias_grind_view);
        allCategories = new LinkedList<>();
        premiumNegocios = new LinkedList<>();
        pDialog = new ProgressDialog(CategoriaActivity.this);
        pDialog.setMessage("Por favor espera...");
        pDialog.setCancelable(false);
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void OnCategoryClicked(Categoria categoria) {
        Bundle extras = new Bundle();
        extras.putSerializable(ITEM_SELECTED,categoria);
        Intent negociosPorCategoriaIntent = new Intent(CategoriaActivity.this,NegocioPorCategoriaActivity.class);
        negociosPorCategoriaIntent.putExtras(extras);
        startActivity(negociosPorCategoriaIntent);
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
                             if(negocionOnMainBanner != null && negocionOnMainBanner.getBanner_premium() != null){

                                 Picasso.with(CategoriaActivity.this).load(negocionOnMainBanner.getBanner_premium()).fit().into(bannerPromo);
                                 hidepDialog();

                             }else{
                                 FirebaseCrash.log(TAG+": No hay banner en negocio premium" );
                                 //show default banner
                                 Picasso.with(CategoriaActivity.this).load(R.drawable.hi_res_logo).fit().into(bannerPromo);
                                 hidepDialog();

                             }

                         }

                         @Override
                         public void onCancelled(DatabaseError databaseError) {
                                //show default banner
                             Picasso.with(CategoriaActivity.this).load(R.drawable.hi_res_logo).fit().into(bannerPromo);
                             hidepDialog();


                         }
                     });

                 }else{
                     //show defalt banner
                     Picasso.with(CategoriaActivity.this).load(R.drawable.hi_res_logo).fit().into(bannerPromo);
                     hidepDialog();


                 }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                   //show default banner
                Picasso.with(CategoriaActivity.this).load(R.drawable.hi_res_logo).fit().into(bannerPromo);
                hidepDialog();


            }
        });

    }

    private  void getRandomChestItem() {

    }


}
