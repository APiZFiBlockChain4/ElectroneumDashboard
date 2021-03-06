package com.example.joes.electroneumwallet;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;


import org.json.JSONException;


import java.io.File;
import java.io.FileNotFoundException;
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
    private static String STATS_MINING_ADDRESS;
    private static String ELECTRONEUM_PRICE_TICKER;

    private static String TEMPORARY_URL = "";
    private static final String DONE_URL = "DONE";
    private static final String ERROR_INFO = "{\"error\":\"not found\"}";
    private static boolean ERROR_TICKER;
    private static int NetworkCode;


    static void GetNetworkData() throws MalformedURLException {

        Log.i("Current Info", "Address: " + STATS_MINING_ADDRESS);
        URL STATS_MINING_ADDRESS_URL = new URL(STATS_MINING_ADDRESS);
        URL ETN_PRICE_TICKER_URL = new URL(ELECTRONEUM_PRICE_TICKER);

        new MiningAsyncTask().execute(STATS_MINING_ADDRESS_URL);
        new MiningAsyncTask().execute(ETN_PRICE_TICKER_URL);
    }

    static void URLCreator(String ELECTRONEUM_WALLET_ADDRESS) {
        String BASE_ELECTRONEUM  = "https://api.etn.spacepools.org/v1";
        ERROR_TICKER = false;

        STATS_MINING_ADDRESS = BASE_ELECTRONEUM + "/stats/address/" + ELECTRONEUM_WALLET_ADDRESS;
        ELECTRONEUM_PRICE_TICKER = "https://api.coinmarketcap.com/v1/ticker/electroneum/";
    }




     static class MiningAsyncTask extends AsyncTask<URL, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            TEMPORARY_URL = "";
            NetworkCode = 0;
            Log.i("WebResult", "ON PRE");
        }

        @Override
        protected String doInBackground(URL... urls) {
            URL searchUrl = urls[0];
            String WebResult = "";
            Log.i("WebResult", "DO IN");
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
            // If Wallet is Invalid
            Log.i("Result", s);
            if (s.equals(ERROR_INFO)) {
                MainActivity.ShowInvalidWallet();
                ERROR_TICKER = true;
            }
            //If Error 503
            if (NetworkCode == HttpURLConnection.HTTP_UNAVAILABLE) {
                try {
                    Thread.sleep(0);
                    NetworkUtils.GetNetworkData();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
            Log.i("HTTP", String.valueOf(NetworkCode));
            Log.i("Errror Ticker", TEMPORARY_URL);

            Log.i("temp Data", TEMPORARY_URL);
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
            Log.i("Errror Ticker", String.valueOf(ERROR_TICKER));

            if (TEMPORARY_URL.equals(DONE_URL) && !ERROR_TICKER) {
                MainActivity.UpdateUIWithData();

                MainActivity.ShowResult();

            }

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
        } catch (FileNotFoundException e) {
            NetworkCode = HttpURLConnection.HTTP_UNAVAILABLE;
            InputStream er = urlConnection.getErrorStream();
            Scanner erscan = new Scanner(er);
            return  erscan.next();
        }
        finally {
            urlConnection.disconnect();
        }

    }

}
