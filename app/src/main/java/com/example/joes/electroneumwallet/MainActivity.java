package com.example.joes.electroneumwallet;


import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import java.net.MalformedURLException;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    private static TextView Current_ETN_Price_USD_TextView;
    private static TextView Current_ETN_Price_BTC_TextView;
    private static TextView Current_ETN_Price_Change;
    private static TextView Current_ETN_MineSpeed;
    private static TextView Current_ETN_Balance;
    private static TextView Current_ETN_TimeDif;
    private static TextView Unpaid_USD_Balance;
    private static TextView Unpaid_BTC_Balance;
    private static TextView Address;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupSharedPreferences();
        Current_ETN_Price_USD_TextView = findViewById(R.id.tv_current_price_usd);
        Current_ETN_Price_BTC_TextView = findViewById(R.id.tv_current_price_btc);
        Current_ETN_Price_Change = findViewById(R.id.tv_current_price_changes);
        Current_ETN_MineSpeed = findViewById(R.id.tv_unpaid_speed);
        Current_ETN_Balance = findViewById(R.id.tv_unpaid_balance_etn);
        Current_ETN_TimeDif = findViewById(R.id.tv_unpaid_balance_time);
        Unpaid_USD_Balance = findViewById(R.id.tv_unpaid_balance_usd);
        Unpaid_BTC_Balance = findViewById(R.id.tv_unpaid_balance_btc);


        try {
            NetworkUtils.GetNetworkData();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String AddressName = sharedPreferences.getString(getString(R.string.pref_wallet_address_key), getString(R.string.pref_wallet_address_default));
        Log.i("Address Status", AddressName);
        Address = findViewById(R.id.Address);
        NetworkUtils.URLCreator(AddressName);
        Address.setText(AddressName);

    }

     public static void UpdateUIWithData() {
        Current_ETN_Price_USD_TextView.setText(JSONUtil.ETN_to_USD);
        Current_ETN_Price_BTC_TextView.setText(JSONUtil.ETN_to_BTC);
        Current_ETN_Price_Change.setText(JSONUtil.ETN_Price_Change);
        Current_ETN_MineSpeed.setText(JSONUtil.Mine_HashRate);
        Current_ETN_Balance.setText(JSONUtil.Mine_Balance);
        Current_ETN_TimeDif.setText(JSONUtil.Mine_LastUpdate);
         Log.i("Mine Lenght", "AA" + String.valueOf(JSONUtil.Mine_Balance));
        Double Mine_Balance_Float = Double.parseDouble(JSONUtil.Mine_Balance.substring(0, JSONUtil.Mine_Balance.length()-4));

        Double ETN_to_USD_Float = Double.parseDouble(JSONUtil.ETN_to_USD) * Mine_Balance_Float;
        Double ETN_to_BTC_Float = Double.parseDouble(JSONUtil.ETN_to_BTC) * Mine_Balance_Float;
        DecimalFormat df = new DecimalFormat("#");
        df.setMaximumFractionDigits(8);
        Unpaid_USD_Balance.setText(String.valueOf(ETN_to_USD_Float) + " USD");
        Unpaid_BTC_Balance.setText("0" + String.valueOf(df.format( ETN_to_BTC_Float)) + " BTC");
    }

    public static void CleanUIData() {
        Current_ETN_Price_USD_TextView.setText("");
        Current_ETN_Price_BTC_TextView.setText("");
        Current_ETN_Price_Change.setText("");
        Current_ETN_MineSpeed.setText("");
        Current_ETN_Balance.setText("");
        Current_ETN_TimeDif.setText("");
        Unpaid_USD_Balance.setText("");
        Unpaid_BTC_Balance.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int IDGathered = item.getItemId();
        if (IDGathered == R.id.action_refresh) {
            try {
                NetworkUtils.GetNetworkData();
                return true;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        else if (IDGathered == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
