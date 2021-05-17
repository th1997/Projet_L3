package fr.projetl3.deltapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projetl3.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

public class RegisterApp extends AppCompatActivity {

    private ImageButton retour;
    private EditText email, nom, prenom, pwd1, pwd2;
    private Button bRegister;
    private TextView userLoginPage;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_register_app);
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(getResources().getColor(android.R.color.background_dark));
        setupUIViews();
        retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainActivity);
                finish();
            }
        });
        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        userLoginPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginApp.class));
            }
        });

    }

    private void setupUIViews(){
        retour = (ImageButton) findViewById(R.id.btn_go_to_main);
        email = (EditText)findViewById(R.id.emailRegister);
        nom = (EditText)findViewById(R.id.nomRegister);
        prenom = (EditText)findViewById(R.id.prenomRegister);
        pwd1 = (EditText)findViewById(R.id.pwd1Register);
        pwd2 = (EditText)findViewById(R.id.pwd2Register);
        bRegister = (Button)findViewById(R.id.bRegister);
        userLoginPage = (TextView)findViewById(R.id.tvLogin);
        progressBar = (ProgressBar)findViewById(R.id.progressBarRegister);
    }

    private void register() {
        String emailText = email.getText().toString();
        String nomText = nom.getText().toString();
        String prenomText = prenom.getText().toString();
        String pwd1Text = pwd1.getText().toString();
        String pwd2Text = pwd2.getText().toString();

        if(emailText.isEmpty()){
            email.setError("Veuillez remplir ce champ");
            email.requestFocus();
        } else if(!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()){
            email.setError("Email invalide");
            email.requestFocus();
            progressBar.setVisibility(View.INVISIBLE);
        } else if(nomText.isEmpty()){
            nom.setError("Veuillez remplir ce champ");
            nom.requestFocus();
        } else if(prenomText.isEmpty()){
            prenom.setError("Veuillez remplir ce champ");
            prenom.requestFocus();
        } else if(pwd1Text.isEmpty()){
            pwd1.setError("Veuillez remplir ce champ");
            pwd1.requestFocus();
        } else if(pwd1Text.length() < 8){
            pwd1.setError("Le mot de passe doit contenir au moins 8 caractères");
        } else if(pwd2Text.isEmpty()){
            pwd2.setError("Veuillez remplir ce champ");
            pwd2.requestFocus();
        } else if(!pwd1Text.equalsIgnoreCase(pwd2Text)){
            pwd2.setError("Les mot de passe doivent correspondre");
            pwd2.requestFocus();
        }  else {
            progressBar = (ProgressBar) findViewById(R.id.progressBarRegister);
            progressBar.setVisibility(View.VISIBLE);
            try {
                Toast.makeText(RegisterApp.this, "Visibility = " + progressBar.getVisibility(), Toast.LENGTH_LONG).show();
            } catch (Exception e){
                Toast.makeText(RegisterApp.this, "User data creation failed! " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

            try {
                mAuth.createUserWithEmailAndPassword(emailText, pwd1Text).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            User user = new User(emailText, nomText, prenomText);
                            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                            DatabaseReference usersRef = rootRef.child("users");
                            usersRef.child(mAuth.getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegisterApp.this, "L'utilisateur à bien été enregistré !", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(RegisterApp.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                        } else {
                            Toast.makeText(RegisterApp.this, "Erreur lors de l'inscription, veuillez réessayer!", Toast.LENGTH_LONG).show();
                            //progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
                progressBar.setVisibility(View.INVISIBLE);
            } catch (Exception e){
                Toast.makeText(RegisterApp.this, "User data creation failed! " + e.getMessage(), Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
            progressBar.setVisibility(View.INVISIBLE);

        }
    }
}