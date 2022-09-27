package com.example.jsoup;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MainActivity extends AppCompatActivity {

    String BASE_URL = "https://egov.uok.edu.in/Results/Default.aspx";
    private WebView WebView;
    String html = "Loading...";
    final String mime = "text/html";
    final String encoding = "utf-8";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WebView = findViewById(R.id.webview);
        WebView.setWebChromeClient(new WebChromeClient());
        WebView.getSettings().setJavaScriptEnabled(true);
        WebView.getSettings().setDomStorageEnabled(true);

        //Display "Loading..." message while waiting
        WebView.loadData(html, mime, encoding);
        //Invoke the AsyncTask
        new GetData().execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class GetData extends AsyncTask<Void, Void, String> {

        // This is run in a background thread
        @Override
        protected String doInBackground(Void... params) {
            try {
                Document doc  = Jsoup.connect(BASE_URL).sslSocketFactory(socketFactory()).get();
                Elements ele = doc.select("div.cont");
                html = ele.toString();
                return html;
            } catch (Exception e) {
                Log.d("APP", e.toString());
            }
            return "error";
        }

        // This runs in UI when background thread finishes
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("APP", "1");
            //WebView.loadData(result, mime, encoding);
            WebView.loadUrl("https://www.bgsbu.ac.in/results");
        }
    }


    public SSLSocketFactory socketFactory() {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};

        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            return sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException("Failed to create a SSL socket factory", e);
        }
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

}