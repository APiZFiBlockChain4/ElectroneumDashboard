package com.example.joes.electroneumwallet;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    public static TextView Current_ETN_Price_USD_TextView;
    public static TextView Current_ETN_Price_BTC_TextView;
    public static TextView Current_ETN_Price_Change;
    public static TextView Current_ETN_MineSpeed;
    public static TextView Current_ETN_Balance;
    public static TextView Current_ETN_TimeDif;
    public static TextView Unpaid_USD_Balance;
    public static TextView Unpaid_BTC_Balance;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Current_ETN_Price_USD_TextView = (TextView) findViewById(R.id.tv_current_price_usd);
        Current_ETN_Price_BTC_TextView = (TextView) findViewById(R.id.tv_current_price_btc);
        Current_ETN_Price_Change = (TextView) findViewById(R.id.tv_current_price_changes);
        Current_ETN_MineSpeed = (TextView) findViewById(R.id.tv_unpaid_speed);
        Current_ETN_Balance = (TextView) findViewById(R.id.tv_unpaid_balance_etn);
        Current_ETN_TimeDif = (TextView) findViewById(R.id.tv_unpaid_balance_time);
        Unpaid_USD_Balance = (TextView) findViewById(R.id.tv_unpaid_balance_usd);
        Unpaid_BTC_Balance = (TextView) findViewById(R.id.tv_unpaid_balance_btc);


        try {
            NetworkUtils.GetNetworkData();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }


}
