package mx.com.cesarcorona.directorio.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import mx.com.cesarcorona.directorio.R;

public class NoticiaDetailActivity extends AppCompatActivity {


    public static String  TAG = NoticiaDetailActivity.class.getSimpleName();
    public static String NOTICIA_SELECTED ="noticia";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticia_detail);
    }
}
