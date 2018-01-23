package mx.com.cesarcorona.directorio.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import mx.com.cesarcorona.directorio.R;

public class SendPassActivity extends BaseAnimatedActivity {


    public static String TAG = SendPassActivity.class.getSimpleName();
    public static int RECOVERY_SUCCESS = 9090;


    private Button enviarContrasena;
    private EditText emailField;
    private LinearLayout rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_pass);


        enviarContrasena = (Button) findViewById(R.id.enviar_button);
        emailField = (EditText) findViewById(R.id.correo_field);
        rootView = (LinearLayout) findViewById(R.id.root_view);


        enviarContrasena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recoveryPassAttempt();
            }
        });

    }


    private void recoveryPassAttempt() {

        if (emailField.getText().length() > 0) {
            if (LoginRegisterActivity.isValidEmail(emailField.getText().toString())) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(emailField.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SendPassActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                } else {
                                    setResult(RECOVERY_SUCCESS);
                                    finish();
                                }
                            }
                        });
            } else {
                showSnackbar(R.string.correo_valido_error);

            }
        }


    }

    private void showSnackbar(int errorMessageRes) {
        Snackbar.make(rootView, errorMessageRes, Snackbar.LENGTH_LONG).show();
    }

}
