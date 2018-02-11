package com.example.joes.electroneumwallet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Joes on 10/2/2018.
 */

class JSONUtil {
     static String ETN_to_USD = "";
     static String ETN_to_BTC = "";
     static String ETN_Price_Change = "";
     static String Mine_HashRate = "";
     static String Mine_Balance = "";
     static String Mine_LastUpdate = "";

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


            if (StatsJson.has("balance")) {
                Mine_Balance = StatsJson.getString("balance");
                Double TempBalance = Double.parseDouble(Mine_Balance) / 100;
                Mine_Balance = TempBalance + " ETN";
            } else {
                Mine_Balance = "0 ETN";
            }

            Mine_LastUpdate = StatsJson.getString("lastShare");
            long LastUpdateMinute = (System.currentTimeMillis()/1000 - Long.parseLong(Mine_LastUpdate))/60;
            Mine_LastUpdate = "Last Update " + LastUpdateMinute + " Minute";


        } catch (JSONException e) {
            Mine_HashRate = "0";
            Mine_Balance = "0 ETN";
            e.printStackTrace();
        }

    }

     static void GetCurrencyData(String FullJsonResponse) throws JSONException {
        JSONArray BaseJsonResponse = new JSONArray(FullJsonResponse);
        JSONObject FirstObject = BaseJsonResponse.getJSONObject(0);
        ETN_to_USD = FirstObject.getString("price_usd");
        ETN_to_BTC = FirstObject.getString("price_btc");
        ETN_Price_Change = FirstObject.getString("percent_change_1h") + " %";
    }


}
