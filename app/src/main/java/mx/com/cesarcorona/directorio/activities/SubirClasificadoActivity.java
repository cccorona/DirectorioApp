package mx.com.cesarcorona.directorio.activities;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import mx.com.cesarcorona.directorio.R;
import mx.com.cesarcorona.directorio.pojo.CategoriaClasificado;
import mx.com.cesarcorona.directorio.pojo.Clasificado;

public class SubirClasificadoActivity extends BaseAnimatedActivity {



    public static String TAG = SubirClasificadoActivity.class.getSimpleName();

    public static String CATEGORIA_CLAS_REFERENCE ="categoriasClasificados";
    public static String CLASIFICADOS_REFERENCE ="clasificados";


    private ProgressDialog pDialog;
    private EditText titulo , contenido , telefono;
    private Spinner categoriasSpinner;
    private Button publicarButton;
    private boolean firstTopic;
    private LinkedList<CategoriaClasificado> categoriaClasificados;
    private CategoriaClasificado categoriaSeleccionada;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subir_clasificado);
        firstTopic = true;
        categoriaClasificados = new LinkedList<>();
        titulo = (EditText)findViewById(R.id.title_clasificado);
        contenido = (EditText)findViewById(R.id.descripcion_clasificado);
        telefono = (EditText)findViewById(R.id.telefono_clasificado);
        publicarButton = (Button) findViewById(R.id.publicar_button);
        categoriasSpinner = (Spinner)findViewById(R.id.categoria_spinner);
        pDialog = new ProgressDialog(SubirClasificadoActivity.this);
        pDialog.setMessage("Por favor espere...");
        pDialog.setCancelable(false);
        fillCategorias();
        publicarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadAttempt();
            }
        });


    }


    private void uploadAttempt(){
        if(telefono.getText().length() >0 && titulo.getText().length() >0&&
                contenido.getText().length()  >0 && categoriaSeleccionada != null){
            if(titulo.getText().length() < 35){
                if(contenido.getText().length() < 700){
                    uploadClasificado();
                }else{
                    Toast.makeText(SubirClasificadoActivity.this,"El contenido debe ser menor a 700 caracteres",Toast.LENGTH_LONG).show();

                }

            }else{
                Toast.makeText(SubirClasificadoActivity.this,"El titulo debe ser menor a 35 caracteres",Toast.LENGTH_LONG).show();

            }

        }else {
            Toast.makeText(SubirClasificadoActivity.this,"Rellene todos los campos",Toast.LENGTH_LONG).show();
        }

    }

    private void uploadClasificado(){
        showpDialog();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(CLASIFICADOS_REFERENCE);
        Clasificado clasificado = new Clasificado();
        clasificado.setContenido(contenido.getText().toString());
        clasificado.setTitulo(titulo.getText().toString());
        clasificado.setCategoriaReference(categoriaSeleccionada.getDataBaseReference());

        databaseReference.push().setValue(clasificado, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError == null){
                    Toast.makeText(SubirClasificadoActivity.this,"Clasificado publicado correctamente",Toast.LENGTH_LONG).show();
                    hidepDialog();

                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            Thread.currentThread()
                                    .setName(this.getClass().getSimpleName() + ": " + Thread.currentThread().getName());


                           // SubirClasificadoActivity.super.onBackPressed();

                        }
                    };

                    Timer timer = new Timer();
                    timer.schedule(task, 1000);

                }else{
                    Toast.makeText(SubirClasificadoActivity.this,"Hubo un error al subir el clasificado, intente despues",Toast.LENGTH_LONG).show();
                }
                hidepDialog();
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

                    categoriaSeleccionada = categoriaClasificados.get(position);

                }else{
                    categoriaSeleccionada = null;



                }
            }


        }

        @Override
        public void onNothingSelected(AdapterView parent) {
            // TODO Auto-generated method stub
            // Do nothing.
        }

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
        CategoriaClasificado selectATopi = new CategoriaClasificado();
        selectATopi.setDisplay_title("Seleccione una categoria");
        selectATopi.setType("Seleccione una categoria");
        categoriaClasificados.add(selectATopi);

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
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(SubirClasificadoActivity.this,
                        R.layout.spinner_style, list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categoriasSpinner.setAdapter(dataAdapter);

                hidepDialog();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hidepDialog();

            }
        });

        categoriasSpinner.setOnItemSelectedListener(new SubirClasificadoActivity.mySpinnerListener());



    }


}
