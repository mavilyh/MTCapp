package com.ladt.mtc;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.VoiceInteractor;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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
        new LoadImage().execute("http://10.43.1.252:8888/AndroidFileUpload/uploads/"+str);

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
                String serv= "http://10.43.1.252:8888/AndroidFileUpload/";
                URL url = new URL(serv+"suppr.php");
                HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
                httpCon.setDoOutput(true);
                httpCon.setRequestMethod("POST");
                httpCon.connect();
                httpCon.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                httpCon.setRequestProperty("data", args[0]);
                try( DataOutputStream wr = new DataOutputStream( httpCon.getOutputStream())) {
                    wr.write( ("data="+args[0]).getBytes( StandardCharsets.UTF_8 ) );
                    wr.flush();
                    wr.close();
                }
                resp = httpCon.getResponseMessage();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Toast.makeText(getApplicationContext(), resp, Toast.LENGTH_SHORT).show();
            return null;
        }

    }
}