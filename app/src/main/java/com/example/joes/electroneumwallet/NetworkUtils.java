package com.example.joes.electroneumwallet;

import android.os.AsyncTask;


import org.json.JSONException;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;


/**
 * Created by Joes on 31/1/2018.
 */

class NetworkUtils {
    // URL Needed for Data Gathering
    private static String ELECTRONEUM_WALLET_ADDRESS = "etnkDUYrWk78m2LcTiB2WoZrmW25igTvj1oHTQ7fLyJqewp8Bs9BPGm6KEmEFyyjSrEmc2bTgexjcYGqNBmQZkyt3qVSomQt3s";
    private static String BASE_ELECTRONEUM  = "https://api.etn.spacepools.org/v1";
    private static String STATS_MINING_ADDRESS = BASE_ELECTRONEUM + "/stats/address/" + ELECTRONEUM_WALLET_ADDRESS;
    private static String ELECTRONEUM_PRICE_TICKER = "https://api.coinmarketcap.com/v1/ticker/electroneum/";


    private static String TEMPORARY_URL = "";
    private static final String DONE_URL = "DONE";


    static void GetNetworkData() throws MalformedURLException {
        URL STATS_MINING_ADDRESS_URL = new URL(STATS_MINING_ADDRESS);
        URL ETN_PRICE_TICKER_URL = new URL(ELECTRONEUM_PRICE_TICKER);

        new MiningAsyncTask().execute(STATS_MINING_ADDRESS_URL);
        new MiningAsyncTask().execute(ETN_PRICE_TICKER_URL);
    }




     static class MiningAsyncTask extends AsyncTask<URL, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            TEMPORARY_URL = "";
            MainActivity.CleanUIData();
        }

        @Override
        protected String doInBackground(URL... urls) {
            URL searchUrl = urls[0];
            String WebResult = "";
            try {
                WebResult =  getResponseFromHttpUrl(searchUrl);
                TEMPORARY_URL = searchUrl.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return WebResult;
        }

        @Override
        protected void onPostExecute(String s) {

            if (TEMPORARY_URL.equals(STATS_MINING_ADDRESS)) {
                JSONUtil.GetMiningData(s);

            }
            else if (TEMPORARY_URL.equals(ELECTRONEUM_PRICE_TICKER)) {
                try {
                    JSONUtil.GetCurrencyData(s);
                    TEMPORARY_URL = DONE_URL;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (TEMPORARY_URL.equals(DONE_URL)) {
                MainActivity.UpdateUIWithData();
            }

        }
    }
    private static String getResponseFromHttpUrl(URL url) throws IOException {
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
