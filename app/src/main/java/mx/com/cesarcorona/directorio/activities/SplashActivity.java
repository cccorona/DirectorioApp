package mx.com.cesarcorona.directorio.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import java.util.Timer;
import java.util.TimerTask;

import mx.com.cesarcorona.directorio.MainActivity;
import mx.com.cesarcorona.directorio.R;

/**
 * Created by ccabrera on 04/08/17.
 */

public class SplashActivity extends AppCompatActivity{



        public static final String TAG = SplashActivity.class.getSimpleName();
        private static final long SPLASH_SCREEN_DELAY = 3000;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.splash_activity_layout);


            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    Thread.currentThread()
                            .setName(this.getClass().getSimpleName() + ": " + Thread.currentThread().getName());


                    goToMainActivity();

                }
            };

            Timer timer = new Timer();
            timer.schedule(task, SPLASH_SCREEN_DELAY);



        } // Fin onCreate()


        private void goToMainActivity() {
            Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(mainIntent);
            finish();
        }

} // Fin SplashScreen.java