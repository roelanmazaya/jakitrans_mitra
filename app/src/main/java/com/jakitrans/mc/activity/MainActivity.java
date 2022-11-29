package com.jakitrans.mc.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

import com.jakitrans.mc.R;
import com.jakitrans.mc.constants.BaseApp;
import com.jakitrans.mc.constants.Constants;
import com.jakitrans.mc.constants.VersionChecker;
import com.jakitrans.mc.fragment.HistoryFragment;
import com.jakitrans.mc.fragment.HomeFragment;
import com.jakitrans.mc.fragment.MenuFragment;
import com.jakitrans.mc.fragment.MessageFragment;
import com.jakitrans.mc.fragment.SettingsFragment;
import com.jakitrans.mc.json.MapKeyResponse;
import com.jakitrans.mc.models.MapKeyModel;
import com.jakitrans.mc.models.User;
import com.jakitrans.mc.utils.api.ServiceGenerator;
import com.jakitrans.mc.utils.api.service.MerchantService;


import java.util.List;
import java.util.Objects;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {
    long mBackPressed;
    public static String apikey;
    public static String fcmkey;
    LinearLayout mAdViewLayout;

    @SuppressLint("StaticFieldLeak")
    public static MainActivity mainActivity;
    private FragmentManager fragmentManager;
    BottomNavigationView navigation;
    private TextView Tokoku;
    int previousSelect = 0;
    private FirebaseAuth mAuth;
    private BroadcastReceiver broadcastReceiver;
    public static MainActivity getInstance() {
        return mainActivity;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Menu menu = navigation.getMenu();
            menu.findItem(R.id.home).setIcon(R.drawable.ic_store_s);
            menu.findItem(R.id.history).setIcon(R.drawable.ic_history_s);
            menu.findItem(R.id.chat).setIcon(R.drawable.ic_pesan);
            menu.findItem(R.id.menu).setIcon(R.drawable.ic_menu_s);
            menu.findItem(R.id.settings).setIcon(R.drawable.ic_settings_s);
            switch (item.getItemId()) {
                case R.id.home:
                    HomeFragment homeFragment = new HomeFragment();
                    navigationItemSelected(0);
                    item.setIcon(R.drawable.ic_store);
                    loadFrag(homeFragment, getString(R.string.menu_store), fragmentManager);
                    return true;

                case R.id.history:
                    HistoryFragment historyFragment = new HistoryFragment();
                    navigationItemSelected(1);
                    item.setIcon(R.drawable.ic_history);
                    loadFrag(historyFragment, getString(R.string.menu_history), fragmentManager);
                    return true;

                case R.id.chat:
                    MessageFragment messageFragment = new MessageFragment();
                    navigationItemSelected(2);
                    item.setIcon(R.drawable.ic_pesan_s);
                    loadFrag(messageFragment, getString(R.string.menu_chat), fragmentManager);
                    return true;

                case R.id.menu:
                    MenuFragment menuFragment = new MenuFragment();
                    navigationItemSelected(3);
                    item.setIcon(R.drawable.ic_menu);
                    loadFrag(menuFragment, getString(R.string.menu_menu), fragmentManager);
                    return true;

                case R.id.settings:
                    SettingsFragment settingsFragment = new SettingsFragment();
                    navigationItemSelected(4);
                    item.setIcon(R.drawable.ic_settings);
                    loadFrag(settingsFragment, getString(R.string.menu_settings), fragmentManager);
                    return true;

            }
            return false;
        }
    };
    //get geo api key
    List<MapKeyModel> mapkeylist;
    private void getmapkey() {
        MapKeyModel mwmodel = new MapKeyModel();
        mwmodel.setId(1);
        MerchantService service = ServiceGenerator.createService(MerchantService.class, "admin", "12345");
        service.mwapikey().enqueue(new Callback<MapKeyResponse>() {
            @Override
            public void onResponse(@NonNull Call<MapKeyResponse> call, @NonNull Response<MapKeyResponse> response) {

                if (response.isSuccessful()) {
                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("found")) {
                        mapkeylist = response.body().getData();
                        String getkey = mapkeylist.get(0).getMapkey();
                        apikey = getkey;
                        //apikey = getString(R.string.google_maps_key);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<MapKeyResponse> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mAdViewLayout = findViewById(R.id.adView);
        fragmentManager = getSupportFragmentManager();
        Tokoku = findViewById(R.id.tokoku);
        navigation = findViewById(R.id.navigation);
        navigation.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setItemIconTintList(null);
        Menu menu = navigation.getMenu();
        menu.findItem(R.id.home).setIcon(R.drawable.ic_store);
        HomeFragment homeFragment = new HomeFragment();
        loadFrag(homeFragment, getString(R.string.menu_store), fragmentManager);

        User loginUser = BaseApp.getInstance(this).getLoginUser();
        if(loginUser != null){
            Constants.USERID = loginUser.getId();
            Tokoku.setText(loginUser.getNamamerchant());
        }
        //apikey = getString(R.string.google_maps_key);
        getmapkey();
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Constants.versionname = Objects.requireNonNull(packageInfo).versionName;
        //broadcast
        FirebaseMessaging.getInstance().subscribeToTopic("mitra").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_LONG).show();
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Check_version();
    }

    public void Check_version(){
        VersionChecker versionChecker = new VersionChecker(this);
        versionChecker.execute();
    }

    @Override
    public void onBackPressed() {
        int count = this.getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            if (mBackPressed + 2000 > System.currentTimeMillis()) {
                super.onBackPressed();
            } else {
                clickDone();

            }
        } else {
            super.onBackPressed();
        }
    }

    public void clickDone() {
        new AlertDialog.Builder(this, R.style.DialogStyle)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(getString(R.string.app_name))
                .setMessage(getString(R.string.exit))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }


    public void loadFrag(Fragment f1, String name, FragmentManager fm) {
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.Container, f1, name);
        ft.commit();
    }

    public void navigationItemSelected(int position) {
        previousSelect = position;
    }




}
