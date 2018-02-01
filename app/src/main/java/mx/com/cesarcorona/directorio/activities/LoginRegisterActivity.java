package mx.com.cesarcorona.directorio.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mx.com.cesarcorona.directorio.MainActivity;
import mx.com.cesarcorona.directorio.R;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginRegisterActivity extends BaseAnimatedActivity implements FirebaseAuth.AuthStateListener {

    public static String TAG = LoginRegisterActivity.class.getSimpleName();
    public static final String USER_REFERENCE = "usuarios";



    public static int RC_SIGN_IN = 100;
    public static int REGISTRAR = 200;
    public static int LOGIN = 300;
    public static int RECOVERY = 900;





    private Button registrarseButton , ingresarButton;
    private EditText correoText , passText;
    private LinearLayout rootView;
    private int loginType;
    private TextView olvideLink;
    private ProgressDialog pDialog;
    private FirebaseAuth mAuth;











    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login_register);
        rootView = (LinearLayout) findViewById(R.id.root_view);
        registrarseButton = (Button)findViewById(R.id.registrarse_button);
        ingresarButton = (Button)findViewById(R.id.ingresar_button);
        correoText = (EditText)findViewById(R.id.correo_field);
        passText = (EditText)findViewById(R.id.pass_field);
        olvideLink = (TextView)findViewById(R.id.olvide_link);

        pDialog = new ProgressDialog(LoginRegisterActivity.this);
        pDialog.setMessage("Por favor espere");
        pDialog.setCancelable(false);
        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(this);


        olvideLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent recoveryIntent = new Intent(LoginRegisterActivity.this, SendPassActivity.class);
                startActivityForResult(recoveryIntent,RECOVERY);
            }
        });


        initProviders();


        ImageView back_button= (ImageView)findViewById(R.id.back_arrow_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginRegisterActivity.super.onBackPressed();
            }
        });

        ImageView homeButton=(ImageView)findViewById(R.id.home_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(LoginRegisterActivity.this, MainActivity.class);
                startActivity(mainIntent);
            }
        });






    }






    @Override
    public void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(LoginRegisterActivity.this);
    }




    private void initProviders(){
        registrarseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerAttempt();
            }
        });
        ingresarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginAttempt();
            }
        });

    }









    private void registerAttempt(){
        if(correoText.getText().length() > 1 &&  passText.getText().length() > 1 ){
            if(isValidEmail(correoText.getText().toString())){
                registerUser();
            }else{
                showSnackbar(R.string.correo_valido_error);
            }
        }else{
            showSnackbar(R.string.credinciales_faltantes);

        }
    }

    private void loginAttempt(){
        if(correoText.getText().length() > 1 &&  passText.getText().length() > 1 ){
            if(isValidEmail(correoText.getText().toString())){
                loginUser();
            }else{
                showSnackbar(R.string.correo_valido_error);
            }
        }else{
            showSnackbar(R.string.credinciales_faltantes);

        }
    }

    private void loginUser(){
        loginType = LOGIN;
        showpDialog();
        mAuth.signInWithEmailAndPassword(correoText.getText().toString(), passText.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginRegisterActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                        }
                        hidepDialog();

                    }
                });
    }



    private void registerUser(){
        showpDialog();
        loginType = REGISTRAR;
        mAuth.createUserWithEmailAndPassword(correoText.getText().toString(), passText.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginRegisterActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                        }
                        hidepDialog();


                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);




    }






    private void showSnackbar( int errorMessageRes) {
        Snackbar.make(rootView, errorMessageRes, Snackbar.LENGTH_LONG).show();
    }

    private void showSnackbar( String errorMessageRes) {
        Snackbar.make(rootView, errorMessageRes, Snackbar.LENGTH_LONG).show();
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }


    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        hidepDialog();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            if(loginType == REGISTRAR){
                gotoActivity(PublicarNegocioActivity.class);
            }else if(loginType == LOGIN){
                gotoActivity(PublicarNegocioActivity.class);
            }
        } else {
            // User is signed out
        }
    }


    private void gotoActivity(Class clase){
        startActivity(new Intent(this,clase));
        finish();
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

