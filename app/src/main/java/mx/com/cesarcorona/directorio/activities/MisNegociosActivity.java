package mx.com.cesarcorona.directorio.activities;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;

import mx.com.cesarcorona.directorio.R;
import mx.com.cesarcorona.directorio.adapter.PromosAdapter;
import mx.com.cesarcorona.directorio.pojo.Negocio;
import mx.com.cesarcorona.directorio.pojo.Promocion;

import static mx.com.cesarcorona.directorio.adapter.PromosAdapter.ADAPTER_TYPE_EDIT;

public class MisNegociosActivity extends BaseAnimatedActivity {



    private LinkedList<Negocio> misNegocios;
    private boolean firstTopic = true;
    private Negocio negocioSeleccionado;
    private Spinner negocioSpinner;
    private ProgressDialog pDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_negocios);
        negocioSpinner = (Spinner) findViewById(R.id.negocioSpinner);

        pDialog = new ProgressDialog(MisNegociosActivity.this);
        pDialog.setMessage("Por favor espere");
        pDialog.setCancelable(false);

        fillNegocios();


    }


    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }



    private void fillNegocios() {
        showpDialog();
        misNegocios = new LinkedList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Example/allnegocios");

        Query query = databaseReference.orderByChild("userId").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());

        Negocio selectATopi = new Negocio();
        selectATopi.setNombre("Seleccione un negocio");
        misNegocios.add(selectATopi);



        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot negocioSnap : dataSnapshot.getChildren()) {
                    Negocio negocio = negocioSnap.getValue(Negocio.class);
                    negocio.setNegocioDataBaseReference(negocioSnap.getKey());
                    if(negocio.getPublicado()!= null && negocio.getPublicado().equals("Si")){
                        misNegocios.add(negocio);

                    }
                }

                LinkedList<String> list = new LinkedList<String>();
                for (Negocio topic : misNegocios) {
                    list.add(topic.getNombre());
                }
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(MisNegociosActivity.this,
                        R.layout.spinner_style, list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                negocioSpinner.setAdapter(dataAdapter);

                hidepDialog();
                if(misNegocios.size()==1){
                    Toast.makeText(MisNegociosActivity.this,"No has publicado negocios, o se encuentran" +
                            " en espera de publicación por parte del administrador",Toast.LENGTH_LONG).show();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hidepDialog();

            }
        });

        negocioSpinner.setOnItemSelectedListener(new MisNegociosActivity.mySpinnerListener());


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

                    negocioSeleccionado = misNegocios.get(position);

                } else {
                    negocioSeleccionado = null;



                }

                updateUI();
            }


        }


        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private void updateUI(){

    }

}
