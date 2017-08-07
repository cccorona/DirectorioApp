package mx.com.cesarcorona.directorio.activities;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;

import mx.com.cesarcorona.directorio.R;
import mx.com.cesarcorona.directorio.adapter.CategoryAdapter;
import mx.com.cesarcorona.directorio.adapter.NegocioPorCategoriaAdapter;
import mx.com.cesarcorona.directorio.custom.ExpandableGridView;
import mx.com.cesarcorona.directorio.pojo.Categoria;
import mx.com.cesarcorona.directorio.pojo.Negocio;

public class NegocioPorCategoriaActivity extends AppCompatActivity implements NegocioPorCategoriaAdapter.NegocioSelectedListener {


    public static String CATEGORY_REFERENCE ="negocios";
    public static String TAG = NegocioPorCategoriaActivity.class.getSimpleName();
    public static String ITEM_SELECTED ="categoria";
    public static int ELEMENT_SIZE = 120;


    private DatabaseReference mDatabase;
    private LinkedList<Negocio> allNegocios;
    private ExpandableGridView openNegociosGrid,closedNegociosGrid;
    private NegocioPorCategoriaAdapter negocioPorCategoriaAdapterClosed,negocioPorCategoriaAdapterOpen;
    private ProgressDialog pDialog;
    private Categoria categoriaSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_negocio_por_categoria);
        initialize();
        showpDialog();
        recoveryNegocios();

    }

    private void initialize(){
        categoriaSelected = (Categoria) getIntent().getExtras().getSerializable(ITEM_SELECTED);
        mDatabase = FirebaseDatabase.getInstance().getReference(CategoriaActivity.CATEGORY_REFERENCE +"/" +categoriaSelected.getDataBaseReference()+"/"+CATEGORY_REFERENCE);
        openNegociosGrid = (ExpandableGridView) findViewById(R.id.opened_negocios_grid_view);
        closedNegociosGrid = (ExpandableGridView) findViewById(R.id.close_negocios_grid_view);
        allNegocios = new LinkedList<>();
        pDialog = new ProgressDialog(NegocioPorCategoriaActivity.this);
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

    private void recoveryNegocios(){
        ValueEventListener categoryListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot negocioSnap : dataSnapshot.getChildren()) {
                    Negocio negocio = negocioSnap.getValue(Negocio.class);
                    negocio.setNegocioDataBaseReference(negocioSnap.getRef().toString());
                    allNegocios.add(negocio);
                }

                negocioPorCategoriaAdapterClosed = new NegocioPorCategoriaAdapter(allNegocios,NegocioPorCategoriaActivity.this);
                negocioPorCategoriaAdapterOpen = new NegocioPorCategoriaAdapter(allNegocios,NegocioPorCategoriaActivity.this);
                negocioPorCategoriaAdapterClosed.setNegocioSelectedListener(NegocioPorCategoriaActivity.this);
                negocioPorCategoriaAdapterOpen.setNegocioSelectedListener(NegocioPorCategoriaActivity.this);
                openNegociosGrid.setAdapter(negocioPorCategoriaAdapterOpen);
                openNegociosGrid.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, allNegocios.size() * ELEMENT_SIZE));
                //openNegociosGrid.setMinimumHeight(allNegocios.size() * ELEMENT_SIZE);
                closedNegociosGrid.setAdapter(negocioPorCategoriaAdapterClosed);
               // closedNegociosGrid.setMinimumHeight(allNegocios.size() * ELEMENT_SIZE);
                closedNegociosGrid.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, allNegocios.size() * ELEMENT_SIZE));



                hidepDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                FirebaseCrash.log(TAG + "loadPost:onCancelled" + databaseError.toException());
            }
        };
        mDatabase.addListenerForSingleValueEvent(categoryListener);
    }


    @Override
    public void OnNegocioClicked(Negocio negocio) {

    }
}
