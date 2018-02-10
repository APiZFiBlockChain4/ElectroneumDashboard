package com.example.joes.electroneumwallet;

import android.graphics.Color;
import android.os.AsyncTask;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Scanner;

/**
 * Created by Joes on 31/1/2018.
 */

public class NetworkUtils {
    private static String ETN_WALLET = "etnkDUYrWk78m2LcTiB2WoZrmW25igTvj1oHTQ7fLyJqewp8Bs9BPGm6KEmEFyyjSrEmc2bTgexjcYGqNBmQZkyt3qVSomQt3s";
    private static String BASE_MINING_ADDRESS  = "https://api.etn.spacepools.org/v1";
    private static String STATS_MINING_ADDRESS = BASE_MINING_ADDRESS + "/stats/address/" + ETN_WALLET;
    private static String STATS_MINING_POOL = BASE_MINING_ADDRESS + "/poolStats";
    private static String ETN_PRICE_TICKER = "https://api.coinmarketcap.com/v1/ticker/electroneum/";

    private static String ETN_to_USD = "";
    private static String ETN_to_BTC = "";
    private static String ETN_Price_Change = "";

    private static String Mine_HashRate = "";
    private static String Mine_Balance = "";
    private static String Mine_LastUpdate = "";

    private static String Mine_Balance_USD = "";
    private static String Mine_Balance_BTC = "";


    private static  String STATS_RESULT = "";

    public static int Counter = 0;

    public static void GetNetworkData() throws MalformedURLException {
        URL STATS_MINING_ADDRESS_URL = new URL(STATS_MINING_ADDRESS);
        URL STATS_MINING_POOL_URL = new URL(ETN_PRICE_TICKER);

        new MiningAsyncTask().execute(STATS_MINING_ADDRESS_URL);
        new MiningAsyncTask().execute(STATS_MINING_POOL_URL);
    }
    static class MiningAsyncTask extends AsyncTask<URL, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            MainActivity.Current_ETN_Price_USD_TextView.setText("");
            MainActivity.Current_ETN_Price_BTC_TextView.setText("");
            MainActivity.Current_ETN_Price_Change.setText("");
            MainActivity.Current_ETN_MineSpeed.setText("");
            MainActivity.Current_ETN_Balance.setText("");
            MainActivity.Current_ETN_TimeDif.setText("");
        }

        @Override
        protected String doInBackground(URL... urls) {
            URL searchUrl = urls[0];
            String WebResult = "";
            try {
                WebResult = getResponseFromHttpUrl(searchUrl);
                Counter++;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return WebResult;
        }

        @Override
        protected void onPostExecute(String s) {

            if (Counter == 1) {
                GetMiningData(s);

            }
            else if (Counter == 2) {
                try {
                    GetCurrencyData(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (Counter == 2) {
                MainActivity.Current_ETN_Price_USD_TextView.setText(ETN_to_USD);
                MainActivity.Current_ETN_Price_BTC_TextView.setText(ETN_to_BTC);
                MainActivity.Current_ETN_Price_Change.setText(ETN_Price_Change);
                MainActivity.Current_ETN_MineSpeed.setText(Mine_HashRate);
                MainActivity.Current_ETN_Balance.setText(Mine_Balance);
                MainActivity.Current_ETN_TimeDif.setText(Mine_LastUpdate);
                Double Mine_Balance_Float = Double.parseDouble(Mine_Balance.substring(0, Mine_Balance.length()-4));
                Double ETN_to_USD_Float = Double.parseDouble(ETN_to_USD) * Mine_Balance_Float;
                Double ETN_to_BTC_Float = Double.parseDouble(ETN_to_BTC) * Mine_Balance_Float;
                DecimalFormat df = new DecimalFormat("#");
                df.setMaximumFractionDigits(8);
                MainActivity.Unpaid_USD_Balance.setText(String.valueOf(ETN_to_USD_Float) + " USD");
                MainActivity.Unpaid_BTC_Balance.setText("0" + String.valueOf(df.format( ETN_to_BTC_Float)) + " BTC");


            }

        }
    }

    static void GetMiningData(String FullJsonResponse) {
        try {
            JSONObject BaseJsonResponse = new JSONObject(FullJsonResponse);
            JSONObject StatsJson = BaseJsonResponse.getJSONObject("stats");
            if (StatsJson.has("hashrate")) {
                Mine_HashRate = StatsJson.getString("hashrate");
                Mine_HashRate = Mine_HashRate.substring(0, Mine_HashRate.length() - 1);
            }
            else {
                Mine_HashRate = "0";
            }



            Mine_Balance = StatsJson.getString("balance");
            Double TempBalance = Double.parseDouble(Mine_Balance)/100;
            Mine_Balance = TempBalance + " ETN";

            Mine_LastUpdate = StatsJson.getString("lastShare");
            long LastUpdateMinute = (System.currentTimeMillis()/1000 - Long.parseLong(Mine_LastUpdate))/60;
            Mine_LastUpdate = "Last Update " + LastUpdateMinute + " Minute";


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    static void GetCurrencyData(String FullJsonResponse) throws JSONException {
        JSONArray BaseJsonResponse = new JSONArray(FullJsonResponse);
        JSONObject FirstObject = BaseJsonResponse.getJSONObject(0);
        ETN_to_USD = FirstObject.getString("price_usd");
        ETN_to_BTC = FirstObject.getString("price_btc");
        ETN_Price_Change = FirstObject.getString("percent_change_1h") + " %";
        if (ETN_Price_Change.substring(0, 1).equals("-")) {
            MainActivity.Current_ETN_Price_Change.setTextColor(Color.parseColor("#E56487"));
        }
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
