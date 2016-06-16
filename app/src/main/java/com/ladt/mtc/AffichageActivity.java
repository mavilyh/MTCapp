package com.ladt.mtc;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.VoiceInteractor;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class AffichageActivity extends Activity {
    Button load_img;
    ImageView img;
    Bitmap bitmap;
    ProgressDialog pDialog;
    List<String> tableauImages = new ArrayList<String>();
    private ListView maListViewPerso;
    //List<Bitmap> myBitmapList;
    private Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.affichageimg);
        //load_img = (Button) findViewById(R.id.load);
        img = (ImageView) findViewById(R.id.imag);
        Intent intent = getIntent();
        final String str = intent.getStringExtra("myString");
        /*load_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                new LoadImage().execute("http://10.56.92.146:8888/AndroidFileUpload/uploads/"+str);
            }
        });*/
//ajout pour github
        new LoadImage().execute(Config.MY_IP+"uploads/"+str);

        deleteButton = (Button) findViewById(R.id.button_delete);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // capture picture
                new deleteTicket().execute(str);
            }
        });
    }

    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AffichageActivity.this);
            pDialog.setMessage("Loading Image ....");
            pDialog.show();

        }

        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {

            if (image != null) {
                img.setImageBitmap(image);
                pDialog.dismiss();

            } else {

                pDialog.dismiss();
                Toast.makeText(AffichageActivity.this, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();

            }
        }
    }
    private class deleteTicket extends AsyncTask <String, Void, Void> {
        //TODO: requête de suppression du ticket à envoyer au serveur + code PHP pour gérer la requête

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getApplicationContext(), "Suppression...", Toast.LENGTH_SHORT).show();
        }

         protected Void doInBackground(String... args) {
            String resp="";
            try {
               /* URL url = new URL("http://192.168.1.19:8888/AndroidFileUpload/uploads/suppr.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded mime-type");
                //List<NameValuePair> params = new ArrayList<NameValuePair>();
                HashMap<String, String> postDataParams = new HashMap<String, String>();
                postDataParams.put("data", args[0]);
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("data", args[0]);
                String query = builder.build().getEncodedQuery();


                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                //System.out.println(getPostDataString(postDataParams));
                //writer.write(getPostDataString(postDataParams));
                System.out.println(query);
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

               /* try( DataOutputStream wr = new DataOutputStream( httpCon.getOutputStream())) {
                    wr.write( postData.getBytes( StandardCharsets.UTF_8 ) );
                }
                resp = httpCon.getResponseMessage(); */

            String serv= Config.MY_IP+"uploads/";
            URL url = new URL(serv+"suppr.php");
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setRequestMethod("POST");
            httpCon.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpCon.setRequestProperty("data", args[0]);
            try( DataOutputStream wr = new DataOutputStream( httpCon.getOutputStream())) {
                wr.write( ("data="+args[0]).getBytes( StandardCharsets.UTF_8 ) );
                wr.flush();
                wr.close();
            }
                httpCon.connect();
            //resp = httpCon.getResponseMessage();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Toast.makeText(getApplicationContext(), resp, Toast.LENGTH_SHORT).show();
            return null;
        }

    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException{
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}