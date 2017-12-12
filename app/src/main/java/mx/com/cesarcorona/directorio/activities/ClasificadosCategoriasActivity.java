package mx.com.cesarcorona.directorio.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;

import mx.com.cesarcorona.directorio.R;
import mx.com.cesarcorona.directorio.adapter.ListAdapter;
import mx.com.cesarcorona.directorio.pojo.Categoria;
import mx.com.cesarcorona.directorio.pojo.CategoriaClasificado;

import static mx.com.cesarcorona.directorio.activities.ClasificadosActivity.CATEGORIA_SELECCIONADA;

public class ClasificadosCategoriasActivity extends AppCompatActivity {


    public static String TAG = ClasificadosCategoriasActivity.class.getSimpleName();

    public static String CATEGORIA_CLAS_REFERENCE ="categoriasClasificados";
    public static String CLASIFICADOS_REFERENCE ="clasificados";


    private ProgressDialog pDialog;
    private EditText titulo , contenido , telefono;
    private Spinner categoriasSpinner;
    private Button publicarButton;
    private boolean firstTopic;
    private LinkedList<CategoriaClasificado> categoriaClasificados;
    private ListView categoriaClasificadosList;
    private CategoriaClasificado categoriaSeleccionada;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clasificados_categorias);
        categoriaClasificados = new LinkedList<>();
        pDialog = new ProgressDialog(ClasificadosCategoriasActivity.this);
        pDialog.setMessage("Por favor espere...");
        pDialog.setCancelable(false);
        categoriaClasificadosList  = (ListView) findViewById(R.id.lista_categorias);
        fillCategorias();



    }


    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


    private void fillCategorias(){
        showpDialog();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(CATEGORIA_CLAS_REFERENCE);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot categoriaSnap : dataSnapshot.getChildren()) {
                    CategoriaClasificado categoria = categoriaSnap.getValue(CategoriaClasificado.class);
                    categoria.setDataBaseReference(categoriaSnap.getKey());
                    categoriaClasificados.add(categoria);
                }

                LinkedList<String> list = new LinkedList<String>();
                for(CategoriaClasificado topic:categoriaClasificados){
                    list.add(topic.getDisplay_title());
                }
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ClasificadosCategoriasActivity.this,
                        R.layout.spinner_style, list);
                ListAdapter myListAdapter = new ListAdapter(list,ClasificadosCategoriasActivity.this);
                //dataAdapter.setDropDownViewResource(R.layout.big_layout_row);
                categoriaClasificadosList.setAdapter(myListAdapter);
                categoriaClasificadosList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        CategoriaClasificado categoriaSeleccionada = categoriaClasificados.get(position);
                        Intent clasificadosIntent = new Intent(ClasificadosCategoriasActivity.this,ClasificadosActivity.class);
                        clasificadosIntent.putExtra(CATEGORIA_SELECCIONADA,categoriaSeleccionada);
                        startActivity(clasificadosIntent);

                    }
                });

                hidepDialog();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hidepDialog();

            }
        });


    }


}
