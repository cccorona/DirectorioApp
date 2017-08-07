package mx.com.cesarcorona.directorio.activities;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;

import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;

import mx.com.cesarcorona.directorio.R;
import mx.com.cesarcorona.directorio.adapter.CategoryAdapter;
import mx.com.cesarcorona.directorio.pojo.Categoria;

public class CategoriaActivity extends AppCompatActivity {

    public static String CATEGORY_REFERENCE ="categorias";
    public static String TAG = CategoriaActivity.class.getSimpleName();

    private DatabaseReference mDatabase;
    private LinkedList<Categoria> allCategories;
    private GridView categoriesGrid;
    private CategoryAdapter categoryAdapter;
    private ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria);
        initialize();
        showpDialog();
        recoverCategorys();
        FirebaseCrash.log(TAG + "Activity created");

    }

    private void recoverCategorys(){
        ValueEventListener categoryListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot categoriaSnap : dataSnapshot.getChildren()) {
                    Categoria categoria = categoriaSnap.getValue(Categoria.class);
                    allCategories.add(categoria);
                }

                categoryAdapter = new CategoryAdapter(allCategories,CategoriaActivity.this);
                categoriesGrid.setAdapter(categoryAdapter);
                hidepDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                FirebaseCrash.log(TAG + "loadPost:onCancelled" + databaseError.toException());
            }
        };
        mDatabase.addListenerForSingleValueEvent(categoryListener);
    }

    private void initialize(){
        mDatabase = FirebaseDatabase.getInstance().getReference(CATEGORY_REFERENCE);
        categoriesGrid = (GridView) findViewById(R.id.categorias_grind_view);
        allCategories = new LinkedList<>();
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
}
