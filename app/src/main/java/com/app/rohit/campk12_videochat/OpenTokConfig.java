package com.app.rohit.campk12_videochat;


import android.webkit.URLUtil;

import com.opentok.OpenTok;
import com.opentok.Role;
import com.opentok.TokenOptions;
import com.opentok.exception.InvalidArgumentException;
import com.opentok.exception.OpenTokException;
import com.opentok.util.Crypto;

import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Random;

public class OpenTokConfig {
    // *** Fill the following variables using your own Project info from the OpenTok dashboard  ***
    // ***                      https://dashboard.tokbox.com/projects                           ***

    // Replace with your OpenTok API key
    public static final String API_KEY = "46035482";
   public static final String apiSecret = "4461dcb871f5cf5f7ef62ad6ebcd7c37ce0f62d4";
   public static final OpenTok opentok = new OpenTok(Integer.parseInt(API_KEY), apiSecret);



   public static String generated_session_id;
    public  static String generated_token;






    public static String generateToken(TokenOptions tokenOptions) throws OpenTokException {

        // Token format
        //
        // | ------------------------------  tokenStringBuilder ----------------------------- |
        // | "T1=="+Base64Encode(| --------------------- innerBuilder --------------------- |)|
        //                       | "partner_id={apiKey}&sig={sig}:| -- dataStringBuilder -- |

        if (tokenOptions == null) {
            throw new InvalidArgumentException("Token options cannot be null");
        }

        Role role = tokenOptions.getRole();
        double expireTime = tokenOptions.getExpireTime(); // will be 0 if nothing was explicitly set
        String data = tokenOptions.getData();             // will be null if nothing was explicitly set
        Long create_time = new Long(System.currentTimeMillis() / 1000).longValue();

        StringBuilder dataStringBuilder = new StringBuilder();
        Random random = new Random();
        int nonce = random.nextInt();
        dataStringBuilder.append("session_id=");
        dataStringBuilder.append(SESSION_ID);
        dataStringBuilder.append("&create_time=");
        dataStringBuilder.append(create_time);
        dataStringBuilder.append("&nonce=");
        dataStringBuilder.append(nonce);
        dataStringBuilder.append("&role=");
        dataStringBuilder.append(role);

        double now = System.currentTimeMillis() / 1000L;
        if (expireTime == 0) {
            expireTime = now + (60*60*24); // 1 day
        } else if(expireTime < now-1) {
            throw new InvalidArgumentException(
                    "Expire time must be in the future. relative time: "+ (expireTime - now));
        } else if(expireTime > (now + (60*60*24*30) /* 30 days */)) {
            throw new InvalidArgumentException(
                    "Expire time must be in the next 30 days. too large by "+ (expireTime - (now + (60*60*24*30))));
        }
        // NOTE: Double.toString() would print the value with scientific notation
        dataStringBuilder.append(String.format("&expire_time=%.0f", expireTime));

        if (data != null) {
            if(data.length() > 1000) {
                throw new InvalidArgumentException(
                        "Connection data must be less than 1000 characters. length: " + data.length());
            }
            dataStringBuilder.append("&connection_data=");
            try {
                dataStringBuilder.append(URLEncoder.encode(data, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new InvalidArgumentException(
                        "Error during URL encode of your connection data: " +  e.getMessage());
            }
        }


        StringBuilder tokenStringBuilder = new StringBuilder();
        try {
            tokenStringBuilder.append("T1==");

            StringBuilder innerBuilder = new StringBuilder();
            innerBuilder.append("partner_id=");
            innerBuilder.append(API_KEY);

            innerBuilder.append("&sig=");

            innerBuilder.append(Crypto.signData(dataStringBuilder.toString(), apiSecret));
            innerBuilder.append(":");
            innerBuilder.append(dataStringBuilder.toString());

            String abc = new String(Base64.encodeBase64(
                    innerBuilder.toString().getBytes("UTF-8")
            ));

            tokenStringBuilder.append(
                    abc
                            .replace("+", "-")
                            .replace("/", "_")
            );

            // if we only wanted Java 7 and above, we could DRY this into one catch clause
        } catch (SignatureException e) {
            throw new OpenTokException("Could not generate token, a signing error occurred.", e);
        } catch (NoSuchAlgorithmException e) {
            throw new OpenTokException("Could not generate token, a signing error occurred.", e);
        } catch (InvalidKeyException e) {
            throw new OpenTokException("Could not generate token, a signing error occurred.", e);
        } catch (UnsupportedEncodingException e) {
            throw new OpenTokException("Could not generate token, a signing error occurred.", e);
        }

        return tokenStringBuilder.toString();
    }




    // Replace with a generated Session ID
    public static String SESSION_ID ;
    // Replace with a generated token (from the dashboard or using an OpenTok server SDK)
    public static String TOKEN ;

    /*                           ***** OPTIONAL *****
     If you have set up a server to provide session information replace the null value
     in CHAT_SERVER_URL with it.

     For example: "https://yoursubdomain.com"
    */
    public static final String CHAT_SERVER_URL = null;
    public static final String SESSION_INFO_ENDPOINT = CHAT_SERVER_URL + "/session";


    // *** The code below is to validate this configuration file. You do not need to modify it  ***

    public static String webServerConfigErrorMessage;
    public static String hardCodedConfigErrorMessage;

    public static boolean areHardCodedConfigsValid() {
        if (OpenTokConfig.API_KEY != null && !OpenTokConfig.API_KEY.isEmpty()
                && OpenTokConfig.SESSION_ID != null && !OpenTokConfig.SESSION_ID.isEmpty()
                && OpenTokConfig.TOKEN != null && !OpenTokConfig.TOKEN.isEmpty()) {
            return true;
        }
        else {

            hardCodedConfigErrorMessage = "API KEY, SESSION ID and TOKEN in OpenTokConfig.java cannot be null or empty.";
            return false;
        }
    }

    public static boolean isWebServerConfigUrlValid(){
        if (OpenTokConfig.CHAT_SERVER_URL == null || OpenTokConfig.CHAT_SERVER_URL.isEmpty()) {
            webServerConfigErrorMessage = "CHAT_SERVER_URL in OpenTokConfig.java must not be null or empty";
            return false;
        } else if ( !( URLUtil.isHttpsUrl(OpenTokConfig.CHAT_SERVER_URL) || URLUtil.isHttpUrl(OpenTokConfig.CHAT_SERVER_URL)) ) {
            webServerConfigErrorMessage = "CHAT_SERVER_URL in OpenTokConfig.java must be specified as either http or https";
            return false;
        } else if ( !URLUtil.isValidUrl(OpenTokConfig.CHAT_SERVER_URL) ) {
            webServerConfigErrorMessage = "CHAT_SERVER_URL in OpenTokConfig.java is not a valid URL";
            return false;
        } else {
            return true;
        }
    }
}
