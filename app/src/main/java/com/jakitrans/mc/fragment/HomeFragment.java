package com.jakitrans.mc.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.jakitrans.mc.R;
import com.jakitrans.mc.activity.IntroActivity;
import com.jakitrans.mc.activity.WalletActivity;
import com.jakitrans.mc.activity.WithdrawActivity;
import com.jakitrans.mc.activity.payment.TopupSaldoActivity;
import com.jakitrans.mc.activity.transfer.PilihTujuanActivity;
import com.jakitrans.mc.constants.BaseApp;
import com.jakitrans.mc.item.OrderItem;
import com.jakitrans.mc.json.GetOnRequestJson;
import com.jakitrans.mc.json.HomeRequestJson;
import com.jakitrans.mc.json.HomeResponseJson;
import com.jakitrans.mc.json.ResponseJson;
import com.jakitrans.mc.models.TransaksiMerchantModel;
import com.jakitrans.mc.models.User;
import com.jakitrans.mc.models.fcm.DriverResponse;
import com.jakitrans.mc.utils.SettingPreference;
import com.jakitrans.mc.utils.Utility;
import com.jakitrans.mc.utils.api.ServiceGenerator;
import com.jakitrans.mc.utils.api.service.MerchantService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Objects;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private Context context;
    private RecyclerView orderanmasuk;
    private OrderItem orderitem;
    private TextView saldo;
    private ShimmerFrameLayout homeshimmer;
    private RelativeLayout rlnodata;
    private List<TransaksiMerchantModel> order;
    private SettingPreference sp;
    private Button onoff;
    private String status;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View getView = inflater.inflate(R.layout.fragment_home, container, false);
        context = getContext();
        orderanmasuk = getView.findViewById(R.id.orderanmasuk);
        RelativeLayout topup = getView.findViewById(R.id.topup);
        RelativeLayout withdraw = getView.findViewById(R.id.withdraw);
        RelativeLayout detail = getView.findViewById(R.id.detail);
        RelativeLayout tranfer = getView.findViewById(R.id.transfer);
        saldo = getView.findViewById(R.id.saldo);
        homeshimmer = getView.findViewById(R.id.shimmerhomme);
        rlnodata = getView.findViewById(R.id.rlnodata);
        sp = new SettingPreference(context);
        onoff = getView.findViewById(R.id.onoff);

        topup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, TopupSaldoActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);

            }
        });



        withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, WithdrawActivity.class);
                i.putExtra("type","withdraw");
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);

            }
        });

        detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, WalletActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);

            }
        });

        tranfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, PilihTujuanActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        orderanmasuk.setHasFixedSize(true);
        orderanmasuk.setNestedScrollingEnabled(false);
        orderanmasuk.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        getdata();
        return getView;
    }

    private void shimmershow() {
        orderanmasuk.setVisibility(View.GONE);
        homeshimmer.startShimmerAnimation();
        homeshimmer.setVisibility(View.VISIBLE);

    }

    private void shimmertutup() {
        homeshimmer.stopShimmerAnimation();
        homeshimmer.setVisibility(View.GONE);
        orderanmasuk.setVisibility(View.VISIBLE);
    }

    @SuppressLint("SetTextI18n")
    private void getdata() {
        shimmershow();
        if (order !=null) {
            order.clear();
        }
        onoff.setSelected(false);
        onoff.setText("wait...");
        onoff.setEnabled(false);
        User loginUser = BaseApp.getInstance(context).getLoginUser();
        if(loginUser != null){
            MerchantService merchantService = ServiceGenerator.createService(MerchantService.class, loginUser.getNoTelepon(), loginUser.getPassword());
            HomeRequestJson param = new HomeRequestJson();
            param.setNotelepon(loginUser.getNoTelepon());
            param.setIdmerchant(loginUser.getId_merchant());
            param.setIdmitra(loginUser.getId());
            merchantService.home(param).enqueue(new Callback<HomeResponseJson>() {
                @Override
                public void onResponse(Call<HomeResponseJson> call, Response<HomeResponseJson> response) {
                    if (response.isSuccessful()) {
                        if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("success")) {
                            sp.updateCurrency(response.body().getCurrency());
                            sp.updateabout(response.body().getAboutus());
                            sp.updateemail(response.body().getEmail());
                            sp.updatephone(response.body().getPhone());
                            sp.updateweb(response.body().getWebsite());
                            sp.updatecurrencytext(response.body().getCurrencytext());
                            order = response.body().getData();
                            shimmertutup();
                            Utility.currencyTXT(saldo,response.body().getSaldo(),context);
                            orderitem = new OrderItem(context, order, R.layout.item_order);
                            orderanmasuk.setAdapter(orderitem);
                            if (response.body().getData().isEmpty()) {
                                orderanmasuk.setVisibility(View.GONE);
                                rlnodata.setVisibility(View.VISIBLE);
                            } else {
                                orderanmasuk.setVisibility(View.VISIBLE);
                                rlnodata.setVisibility(View.GONE);
                            }
                            User user = response.body().getUser().get(0);
                            onoff.setEnabled(true);
                            if (user.getStatus_merchant().equals("1")) {
                                onoff.setSelected(true);
                                onoff.setText("On");
                            } else {
                                onoff.setSelected(false);
                                onoff.setText("Off");
                            }

                            status = user.getStatus_merchant();
                            onoff.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    getturnon(status);
                                }
                            });
                            saveUser(user);
                            if (HomeFragment.this.getActivity() != null) {
                                Realm realm = BaseApp.getInstance(HomeFragment.this.getActivity()).getRealmInstance();
                                User loginUser = BaseApp.getInstance(HomeFragment.this.getActivity()).getLoginUser();
                                realm.beginTransaction();
                                loginUser.setWalletSaldo(Long.parseLong(response.body().getSaldo()));
                                realm.commitTransaction();
                            }
                        } else {
                            Realm realm = BaseApp.getInstance(context).getRealmInstance();
                            realm.beginTransaction();
                            realm.delete(User.class);
                            realm.commitTransaction();
                            BaseApp.getInstance(context).setLoginUser(null);
                            startActivity(new Intent(context, IntroActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            requireActivity().finish();
                        }
                    }
                }

                @Override
                public void onFailure(Call<HomeResponseJson> call, Throwable t) {

                }
            });
        }
    }

    @SuppressLint("SetTextI18n")
    private void getturnon(String statuson) {
        onoff.setSelected(false);
        onoff.setText("Wait...");
        onoff.setEnabled(false);
        User loginUser = BaseApp.getInstance(context).getLoginUser();
        MerchantService userService = ServiceGenerator.createService(
                MerchantService.class, loginUser.getNoTelepon(), loginUser.getPassword());
        GetOnRequestJson param = new GetOnRequestJson();
        param.setId(loginUser.getId_merchant());
        param.setToken(loginUser.getToken_merchant());
        if (statuson.equals("1")) {
            param.setTurn("2");
        } else {
            param.setTurn("1");
        }

        userService.turnon(param).enqueue(new Callback<ResponseJson>() {
            @Override
            public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                if (response.isSuccessful()) {
                    if (Objects.requireNonNull(response.body()).getMessage().equals("success")) {
                        onoff.setEnabled(true);
                        status = response.body().getData();
                        if (response.body().getData().equals("1")) {
                            onoff.setSelected(true);
                            onoff.setText("On");
                        } else if (response.body().getData().equals("2")) {
                            onoff.setSelected(false);
                            onoff.setText("Off");
                        }
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseJson> call, Throwable t) {

            }
        });
    }

    private void saveUser(User user) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(User.class);
        realm.copyToRealm(user);
        realm.commitTransaction();
        BaseApp.getInstance(context).setLoginUser(user);
    }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @SuppressWarnings("unused")
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageEvent(final DriverResponse response) {
        Log.e("IN PROGRESS", response.getResponse());
        if (response.getResponse().equals("2") || response.getResponse().equals("3") || response.getResponse().equals("4") || response.getResponse().equals("5")) {
            getdata();
        }

    }


}
