package com.smagar.sisimangi;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.smagar.sisimangi.api.ApiService;
import com.smagar.sisimangi.api.Server;
import com.smagar.sisimangi.model.PrefManager;
import com.smagar.sisimangi.model.ResponseData;
import com.smagar.sisimangi.model.SesionManager;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.smagar.sisimangi.model.SesionManager.KEY_NIP;
import static com.smagar.sisimangi.model.SesionManager.KEY_STATUS;
import static com.smagar.sisimangi.model.SesionManager.KEY_USERNAME;

public class MenuActivity extends AppCompatActivity {

    @BindView(R.id.btnAbsensi)
    Button btnScan;
    @BindView(R.id.btnTentang)
    Button btnTentang;
    @BindView(R.id.btnLogout)
    Button btnLogout;
    @BindView(R.id.txtUsername)
    TextView txtUsername;
    @BindView(R.id.txtStatus)
    TextView txtStatus;
    @BindView(R.id.txtNip)
    TextView txtNip;
    @BindView(R.id.btnPetunjuk)
    Button btnPetunjuk;

    ProgressDialog pDialog;
    IntentIntegrator intentIntegrator;

    ApiService API;


    // String username, status, nip, chiper;

    SesionManager sesion;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        ButterKnife.bind(this);

        API = Server.getAPIService();

        sesion = new SesionManager(this);
        HashMap user= sesion.getUserDetails();

//nyoba SESSION
        txtUsername.setText((CharSequence) user.get(KEY_USERNAME));
        txtStatus.setText((CharSequence) user.get(KEY_STATUS));
        txtNip.setText((CharSequence) user.get(KEY_NIP));

        btnTentang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, TentangActivity.class);
                startActivity(intent);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sesion.logoutUser();
                Intent i = new Intent(MenuActivity.this, MainActivity.class);
                startActivity(i);

            }
        });

        btnPetunjuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrefManager prefManager = new PrefManager(getApplicationContext());
                prefManager.setFirstTimeLaunch(true);
                startActivity(new Intent(MenuActivity.this, PetunjukActivity.class));
                finish();
            }
        });

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentIntegrator = new IntentIntegrator(MenuActivity.this);
                intentIntegrator.initiateScan();
            }
        });


//REMINDER TIAP PAGI DAN PETANG

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null){

            if (result.getContents() == null){

               // Toast.makeText(this, "TIDAK ADA DATA", Toast.LENGTH_SHORT).show();

            }else{

                // jika qrcode berisi data
                try{
                    JSONObject object = new JSONObject(result.getContents());
                    String chiper = object.getString("id"); //ambil nilai QRCODE
                    String absent = "";
//                    Toast.makeText(this, "qrcode : " +chiper, Toast.LENGTH_LONG).show();

                    //WAKTU ABSEN SORE
                    int from= 100;
                    int to = 1200;

                    int dari = 1201;
                    int ke = 2400;


                    Date date = new Date();
                    Calendar c = Calendar.getInstance();
                    c.setTime(date);


                    String nip = txtNip.getText().toString();
                    String status = txtStatus.getText().toString();


                 //   Toast.makeText(this, "waktu sekarang"+date,Toast.LENGTH_LONG).show();
                    int t = c.get(Calendar.HOUR_OF_DAY) * 100 + c.get(Calendar.MINUTE);

                    if (t >= from && t <= to){ //Rentang waktu absen datang
                        absent = "Datang";
                        Absen(absent, chiper, nip, status);

                    }else if (t >= dari && t <= ke){ //Waktu absen pulang
                        absent = "Pulang";
                        Absen(absent, chiper, nip, status);

                    }else {
                        //btnScan.setEnabled(false);
                        Toast.makeText(this, "TELAT BANGSAT", Toast.LENGTH_LONG).show();
                    }

                }catch (Exception e){

                    e.printStackTrace();
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_SHORT).show();

                }
            }

        }else{

            super.onActivityResult(requestCode, resultCode, data);

        }
    }

    private void Absen(final String absent, final String chiper, final String nip, final String status) {
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(true);
        pDialog.setMessage("Tunggu yak...");
        pDialog.show();

        API.absen(absent, chiper, nip,  status).enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                try{
                    if (response.isSuccessful()){
                        ResponseData responseData = response.body();

                        if (responseData.getSuccess().equals("1")){
                            pDialog.cancel();
                            Toast.makeText(MenuActivity.this, "SUKSES 0 "+ responseData.getMessage(), Toast.LENGTH_LONG).show();
                        }else {

                            pDialog.cancel();
                            Toast.makeText(MenuActivity.this, "SUKSES 0 "+responseData.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {

                pDialog.cancel();
                Toast.makeText(MenuActivity.this, "FAILURE"+t.getMessage(), Toast.LENGTH_LONG).show();

            }
        });

    }

}
