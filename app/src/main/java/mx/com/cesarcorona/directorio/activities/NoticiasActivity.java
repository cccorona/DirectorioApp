package mx.com.cesarcorona.directorio.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;

import mx.com.cesarcorona.directorio.R;
import mx.com.cesarcorona.directorio.adapter.ClasificadosAdapter;
import mx.com.cesarcorona.directorio.adapter.NoticiasAdapter;
import mx.com.cesarcorona.directorio.pojo.Clasificado;
import mx.com.cesarcorona.directorio.pojo.Noticia;

import static mx.com.cesarcorona.directorio.activities.SubirClasificadoActivity.CLASIFICADOS_REFERENCE;

public class NoticiasActivity extends BaseAnimatedActivity implements NoticiasAdapter.OnNewsInterface {


    public static String TAG = NoticiasActivity.class.getSimpleName();
    public static String NOTICIAS_REFERENCE ="noticias";


    private ProgressDialog pDialog;
    private ListView noticiasListView;
    private LinkedList<Noticia> noticias;
    private NoticiasAdapter noticiasAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticias);
        pDialog = new ProgressDialog(NoticiasActivity.this);
        pDialog.setMessage("Por favor espere...");
        pDialog.setCancelable(false);
        noticias = new LinkedList<>();
        noticiasListView = (ListView) findViewById(R.id.lista_noticias);

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
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(NOTICIAS_REFERENCE);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    Noticia noticia = dataSnapshot1.getValue(Noticia.class);
                    noticias.add(noticia);
                }
                noticiasAdapter = new NoticiasAdapter(noticias,NoticiasActivity.this);
                noticiasListView.setAdapter(noticiasAdapter);
                noticiasAdapter.setOnNewsInterface(NoticiasActivity.this);
                hidepDialog();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hidepDialog();
                Toast.makeText(NoticiasActivity.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public void OnNewClicked(Noticia noticia) {
        Intent detailIntent = new Intent(NoticiasActivity.this,NoticiaDetailActivity.class);
        detailIntent.putExtra(NoticiaDetailActivity.NOTICIA_SELECTED,noticia);
        startActivity(detailIntent);
    }
}
