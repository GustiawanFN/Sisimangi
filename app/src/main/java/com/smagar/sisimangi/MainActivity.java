package com.smagar.sisimangi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.smagar.sisimangi.api.ApiService;
import com.smagar.sisimangi.api.Server;
import com.smagar.sisimangi.model.ResponseData;
import com.smagar.sisimangi.model.SesionManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {

    @BindView(R.id.edtUser)
    EditText edtusername;
    @BindView(R.id.edtPass)
    EditText password;
    @BindView(R.id.btnLogin)
    Button btnLogin;

    ApiService API;
    SesionManager session;
    String nip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(MainActivity.this);

        API = Server.getAPIService();

        session = new SesionManager(getApplicationContext());

//        session.checkLogin();
        if (session.isLoggedIn()) {

            Intent i = new Intent(MainActivity.this, MenuActivity.class);

            startActivity(i);
            finish();
        }


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String user = edtusername.getText().toString();
                String pass = password.getText().toString();


                if (validateLogin(user, pass)){ //validasi
                    //login
                    doLogin(user, pass); //login

                }
            }
        });
    }


    //validasi login
    private boolean validateLogin(String username, String password){
        if(username == null || username.trim().length() == 0){
            Toast.makeText(this, "Masukkan Username", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password == null || password.trim().length() == 0){
            Toast.makeText(this, "Masukkan Password", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }



    private void doLogin(final String username, final String password) {

        API.login(username, password).enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {

                if (response.isSuccessful()){
                    ResponseData responseData = response.body();

                    if(responseData.getSuccess().equals("1")){

                        String username = response.body().getUsername();
                        String status = response.body().getStatus();//guru atau staff?

                                if (status.equals("Guru")){
                                    nip = response.body().getNip();

                                }else {
                                    nip = response.body().getNik();
                                }


                        session.createLoginSession(username,  nip, status);
//cek sesion
                        if (session.isLoggedIn()){
                            Intent intent = new Intent(MainActivity.this, MenuActivity.class);

                           // intent.putExtra()

                            startActivity(intent);
                        }
                    }else {
                        Toast.makeText(MainActivity.this, "Username atau pass salah", Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(MainActivity.this, "Cek koneksi internet anda", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
