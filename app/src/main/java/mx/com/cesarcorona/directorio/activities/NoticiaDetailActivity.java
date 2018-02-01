package mx.com.cesarcorona.directorio.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import mx.com.cesarcorona.directorio.MainActivity;
import mx.com.cesarcorona.directorio.R;
import mx.com.cesarcorona.directorio.pojo.Noticia;

public class NoticiaDetailActivity extends AppCompatActivity {


    public static String  TAG = NoticiaDetailActivity.class.getSimpleName();
    public static String NOTICIA_SELECTED ="noticia";

    private Noticia noticiaSelecionada;

    private TextView title;
    private TextView content;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticia_detail);
        noticiaSelecionada = (Noticia) getIntent().getSerializableExtra(NOTICIA_SELECTED);
        title = (TextView) findViewById(R.id.title_noticia);
        content=(TextView)findViewById(R.id.preview_text);
        image = (ImageView)findViewById(R.id.preview_image);


        if(noticiaSelecionada != null){
            title.setText(noticiaSelecionada.getTitulo());
            content.setText(noticiaSelecionada.getTexto_contenido());
            if(noticiaSelecionada.getFoto() != null && !noticiaSelecionada.getFoto().equals("")){
                Picasso.with(NoticiaDetailActivity.this).load(noticiaSelecionada.getFoto()).into(image);
            }

        }else{
            Toast.makeText(NoticiaDetailActivity.this,"Hubo un error al leer la noticia",Toast.LENGTH_LONG).show();
        }

        ImageView back_button= (ImageView)findViewById(R.id.back_arrow_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoticiaDetailActivity.super.onBackPressed();
            }
        });

        ImageView homeButton=(ImageView)findViewById(R.id.home_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(NoticiaDetailActivity.this, MainActivity.class);
                startActivity(mainIntent);
            }
        });


    }
}
