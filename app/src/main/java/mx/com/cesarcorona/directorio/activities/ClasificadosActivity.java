package mx.com.cesarcorona.directorio.activities;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;

import mx.com.cesarcorona.directorio.R;
import mx.com.cesarcorona.directorio.adapter.ClasificadosAdapter;
import mx.com.cesarcorona.directorio.pojo.CategoriaClasificado;
import mx.com.cesarcorona.directorio.pojo.Clasificado;

import static mx.com.cesarcorona.directorio.activities.SubirClasificadoActivity.CLASIFICADOS_REFERENCE;

public class ClasificadosActivity extends BaseAnimatedActivity {



    public static String TAG = ClasificadosActivity.class.getSimpleName();
    public static String CATEGORIA_SELECCIONADA = "categoriaSeleccionada";

    private ListView listaClasificados;
    private ClasificadosAdapter clasificadosAdapter;
    private LinkedList<Clasificado> clasificados;
    private ProgressDialog pDialog;
    private CategoriaClasificado categoriaClasificado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clasificados);
        pDialog = new ProgressDialog(ClasificadosActivity.this);
        pDialog.setMessage("Por favor espere...");
        pDialog.setCancelable(false);
        clasificados = new LinkedList<>();
        listaClasificados = (ListView) findViewById(R.id.lista_clasificados);
        categoriaClasificado = (CategoriaClasificado) getIntent().getSerializableExtra(CATEGORIA_SELECCIONADA);

        fillList();
    }


    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private void fillList(){
        showpDialog();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Example/");
        Query clasficiadosPorCategoria = databaseReference.child(CLASIFICADOS_REFERENCE)
                .orderByChild("referencia_categoria").equalTo(categoriaClasificado.getNombre());
        clasficiadosPorCategoria.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    Clasificado clasificado = dataSnapshot1.getValue(Clasificado.class);
                    clasificado.setReference(dataSnapshot1.getKey());
                    if(clasificado != null && clasificado.getPublicado().equalsIgnoreCase("si")){
                        clasificados.add(clasificado);

                    }
                }

                clasificadosAdapter = new ClasificadosAdapter(ClasificadosActivity.this,clasificados);
                listaClasificados.setAdapter(clasificadosAdapter);
                hidepDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hidepDialog();
                Toast.makeText(ClasificadosActivity.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

      /*  databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    Clasificado clasificado = dataSnapshot1.getValue(Clasificado.class);
                    clasificado.setReference(dataSnapshot1.getKey());
                    if(categoriaClasificado.equals(clasificado.getCategoriaReference())){
                        clasificados.add(clasificado);

                    }
                }

                clasificadosAdapter = new ClasificadosAdapter(ClasificadosActivity.this,clasificados);
                listaClasificados.setAdapter(clasificadosAdapter);
                hidepDialog();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
               hidepDialog();
                Toast.makeText(ClasificadosActivity.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });*/
    }

}
