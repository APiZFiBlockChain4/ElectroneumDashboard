package com.example.joes.electroneumwallet;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static TextView TextView_Update_Time;
    private static TextView TextView_Unpaid_Balance_ETN;
    private static TextView TextView_Unpaid_Balance_USD;
    private static TextView TextView_Unpaid_Balance_BTC;
    private static TextView TextView_Hash_Speed;
    private static TextView TextView_Price_USD;
    private static TextView TextView_Price_BTC;
    private static TextView TextView_Percentage_Changes;

    public static LinearLayout LinearLayout_Working_Top;
    public static LinearLayout LinearLayout_Working_Mid;
    public static LinearLayout LinearLayout_Working_Bot;

    public static LinearLayout LinearLayout_Invalid_Wallet;
    public static LinearLayout LinearLayout_Pending_Request;
    public static LinearLayout LinearLayout_No_Internet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupSharedPreferences();
        AssignVariable();

        if (!isDataConnectionAvailable(this)) {
            ShowNoInternet();
        } else {
            try {
                ShowPendingRequest();
                NetworkUtils.GetNetworkData();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    private void AssignVariable() {
        TextView_Update_Time = findViewById(R.id.tv_working_child_balance_update_time);

        TextView_Unpaid_Balance_ETN = findViewById(R.id.tv_working_child_unpaid_balance_etn);
        TextView_Unpaid_Balance_USD = findViewById(R.id.tv_working_child_unpaid_balance_usd);
        TextView_Unpaid_Balance_BTC = findViewById(R.id.tv_working_child_unpaid_balance_btc);
        TextView_Hash_Speed = findViewById(R.id.tv_working_child_hash_speed);
        TextView_Price_BTC = findViewById(R.id.tv_working_child_price_in_btc);
        TextView_Price_USD = findViewById(R.id.tv_working_child_price_in_usd);
        TextView_Percentage_Changes = findViewById(R.id.tv_working_child_percentage_changes);

        LinearLayout_Working_Top = findViewById(R.id.ll_working_parent_top);
        LinearLayout_Working_Mid = findViewById(R.id.ll_working_parent_mid);
        LinearLayout_Working_Bot = findViewById(R.id.ll_working_parent_bot);

        LinearLayout_Invalid_Wallet = findViewById(R.id.ll_Invalid_Wallet_parent);
        LinearLayout_Pending_Request = findViewById(R.id.ll_Pending_Request_parent);
        LinearLayout_No_Internet = findViewById(R.id.ll_No_Internet_parent);


    }

    /*
    Method Name: setupSharedPreferences
    Function: When onCreate able to GET Address, and able to auto update without close app with registers
    */
    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        NetworkUtils.URLCreator(sharedPreferences.getString(getString(R.string.pref_wallet_address_key), getString(R.string.pref_wallet_address_default)));
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    public static void ShowResult() {
        LinearLayout_Invalid_Wallet.setVisibility(View.GONE);
        LinearLayout_No_Internet.setVisibility(View.GONE);
        LinearLayout_Pending_Request.setVisibility(View.GONE);
        LinearLayout_Working_Top.setVisibility(View.VISIBLE);
        LinearLayout_Working_Mid.setVisibility(View.VISIBLE);
        LinearLayout_Working_Bot.setVisibility(View.VISIBLE);

    }

    public static void ShowNoInternet() {
        LinearLayout_Working_Top.setVisibility(View.GONE);
        LinearLayout_Working_Mid.setVisibility(View.GONE);
        LinearLayout_Working_Bot.setVisibility(View.GONE);
        LinearLayout_Invalid_Wallet.setVisibility(View.GONE);
        LinearLayout_No_Internet.setVisibility(View.VISIBLE);
        LinearLayout_Pending_Request.setVisibility(View.GONE);
    }

    public static void ShowInvalidWallet() {
        LinearLayout_Working_Top.setVisibility(View.GONE);
        LinearLayout_Working_Mid.setVisibility(View.GONE);
        LinearLayout_Working_Bot.setVisibility(View.GONE);
        LinearLayout_Invalid_Wallet.setVisibility(View.VISIBLE);
        LinearLayout_No_Internet.setVisibility(View.GONE);
        LinearLayout_Pending_Request.setVisibility(View.GONE);
    }

    public static void ShowPendingRequest() {
        LinearLayout_Working_Top.setVisibility(View.GONE);
        LinearLayout_Working_Mid.setVisibility(View.GONE);
        LinearLayout_Working_Bot.setVisibility(View.GONE);
        LinearLayout_Invalid_Wallet.setVisibility(View.GONE);
        LinearLayout_No_Internet.setVisibility(View.GONE);
        LinearLayout_Pending_Request.setVisibility(View.VISIBLE);
    }


    public static void UpdateUIWithData() {
        Double Mine_Balance_Float = Double.parseDouble(JSONUtil.Mine_Balance.substring(0, JSONUtil.Mine_Balance.length() - 4));
        Double ETN_to_USD_Float = Double.parseDouble(JSONUtil.ETN_to_USD) * Mine_Balance_Float;
        Double ETN_to_BTC_Float = Double.parseDouble(JSONUtil.ETN_to_BTC) * Mine_Balance_Float;
        DecimalFormat df = new DecimalFormat("#");
        df.setMaximumFractionDigits(8);

        TextView_Price_USD.setText(JSONUtil.ETN_to_USD);
        TextView_Price_BTC.setText(JSONUtil.ETN_to_BTC);
        TextView_Percentage_Changes.setText(JSONUtil.ETN_Price_Change);
        TextView_Hash_Speed.setText(JSONUtil.Mine_HashRate);

        TextView_Update_Time.setText(JSONUtil.Mine_LastUpdate);
        TextView_Unpaid_Balance_ETN.setText(JSONUtil.Mine_Balance);
        TextView_Unpaid_Balance_USD.setText(String.valueOf(ETN_to_USD_Float) + " USD");
        TextView_Unpaid_Balance_BTC.setText("0" + String.valueOf(df.format(ETN_to_BTC_Float)) + " BTC");

    }

    /*
    Method Name: onCreateOptionsMenu
    Function: Enable Menu Features
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int IDGathered = item.getItemId();
        if (IDGathered == R.id.action_refresh) {
            if (!isDataConnectionAvailable(this)) {
                ShowNoInternet();
            } else {
                try {
                    ShowPendingRequest();
                    setupSharedPreferences();
                    NetworkUtils.GetNetworkData();
                    return true;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        } else if (IDGathered == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister VisualizerActivity as an OnPreferenceChangedListener to avoid any memory leaks.
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(getString(R.string.pref_wallet_address_key))) {
            NetworkUtils.URLCreator(sharedPreferences.getString(getString(R.string.pref_wallet_address_key), getString(R.string.pref_wallet_address_default)));
            try {
                NetworkUtils.GetNetworkData();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        }
    }

    public static boolean isDataConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}

