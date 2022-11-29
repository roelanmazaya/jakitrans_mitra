package com.jakitrans.mc.activity.payment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jakitrans.mc.R;
import com.jakitrans.mc.constants.BaseApp;
import com.jakitrans.mc.constants.Constants;
import com.jakitrans.mc.json.payment.RequestPaymentJson;
import com.jakitrans.mc.json.payment.VaResponseJson;
import com.jakitrans.mc.models.User;
import com.jakitrans.mc.models.payment.PaymentMethod;
import com.jakitrans.mc.models.payment.VirtualAccount;
import com.jakitrans.mc.utils.Log;
import com.jakitrans.mc.utils.PicassoTrustAll;
import com.jakitrans.mc.utils.Utility;
import com.jakitrans.mc.utils.api.ServiceGenerator;
import com.jakitrans.mc.utils.api.service.PaymentService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KonfirmasiActivity extends AppCompatActivity {
    private Context context;
    private User user;
    private ImageView ivMethod, backButton;
    private TextView tvMethod, tvAmount, tvTax, tvTotal;
    private ProgressBar progressBar;
    private Button button;
    String amount, fee;
    int total =0;
    private PaymentMethod paymentMethod;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konfirmasi);
        context = this;
        user = BaseApp.getInstance(context).getLoginUser();
        ivMethod = findViewById(R.id.imageViewMethod);
        tvMethod = findViewById(R.id.textViewMethod);
        tvAmount = findViewById(R.id.textViewAmount);
        tvTax = findViewById(R.id.textViewTax);
        tvTotal = findViewById(R.id.textViewTotal);
        progressBar = findViewById(R.id.progressBar);
        button = findViewById(R.id.buttonBayar);
        backButton = findViewById(R.id.back_btn);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String intentdata = getIntent().getStringExtra(Constants.INTENT_METHOD);
        amount = getIntent().getStringExtra(Constants.INTENT_AMOUNT);

        paymentMethod = new Gson().fromJson(intentdata, PaymentMethod.class);
        total = Integer.parseInt(amount) + Integer.parseInt(paymentMethod.getBiaya());
        PicassoTrustAll.getInstance(context)
                .load(paymentMethod.getImage())
                .error(R.drawable.logo)
                .into(ivMethod);
        tvMethod.setText(paymentMethod.getNama());
        tvAmount.setText(Utility.toformatRupiah(amount));
        tvTax.setText(Utility.toformatRupiah(paymentMethod.getBiaya()));
        tvTotal.setText(Utility.toformatRupiah(String.valueOf(total)));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postVa();
            }
        });
    }
    private void postVa(){
        progressBar.setVisibility(View.VISIBLE);
        RequestPaymentJson requestPaymentJson = new RequestPaymentJson();
        requestPaymentJson.setId(String.valueOf(paymentMethod.getId()));
        requestPaymentJson.setIdUser(user.getId());
        requestPaymentJson.setNamaDepan(user.getNamamitra());
        requestPaymentJson.setNamaBelakang(user.getNamamerchant());
        requestPaymentJson.setAmount(amount);
        PaymentService service = ServiceGenerator.createService(PaymentService.class, user.getId(), user.getPhone());
        service.va(requestPaymentJson).enqueue(new Callback<VaResponseJson>() {
            @Override
            public void onResponse(Call<VaResponseJson> call, Response<VaResponseJson> response) {
                progressBar.setVisibility(View.GONE);
                if(response.isSuccessful()){
                    if(response.body().getCode().equalsIgnoreCase("200")){
                        VirtualAccount virtualAccount = response.body().getVaModel();
                        Log.e("NVOICE", virtualAccount.getExternalId());
                        Intent intent = new Intent(context, PaymentFixedActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra(Constants.INTENT_ID, virtualAccount.getExternalId());
                        startActivity(intent);
                        finish();
                    }else {
                        Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<VaResponseJson> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                t.printStackTrace();
            }
        });
    }


}