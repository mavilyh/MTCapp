package com.ladt.mtc;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.ladt.mtc.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class MesTicketsActivity extends Activity {

    private ListView maListViewPerso;
    ImageView imag;
    ImageView imag2;
    ProgressDialog pDialog;
    Bitmap bitmap;
    Button load_img;

    List<String> tableauImages=  new ArrayList<String>();
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        //load_img = (Button)findViewById(R.id.load);
        //imag = (ImageView)findViewById(R.id.imag);
        /*load_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                new listImages().execute();
            }
        });*/

        new listImages().execute();
    }


    private class listImages extends AsyncTask<Void, Void, List<String>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<String> doInBackground(Void... arg0) {
            try {
                //StringBuffer result = new StringBuffer();
                System.out.println("DOINBACKGROUND");
                URL url = new URL("http://10.43.1.252:8888/AndroidFileUpload/imgList.php");
                URLConnection conn = url.openConnection();
                HttpURLConnection httpConn = (HttpURLConnection) conn;
                httpConn.setRequestMethod("GET");
                httpConn.connect();
                if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    System.out.println("YAYAAAAAA");
                    InputStream in = httpConn.getInputStream();
                    InputStreamReader reader = new InputStreamReader(in);
                    BufferedReader buffer = new BufferedReader(reader);
                    String inputLine;
                    System.out.println("YEYEEEEE");
                    int lineCount = 0;
                    while ((lineCount < 10) && ((inputLine = buffer.readLine()) != null)) {
                        tableauImages.add(inputLine);
                        lineCount++;
                    }

                    buffer.close();
                    httpConn.disconnect();


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return tableauImages;
        }

        @Override
        protected void onPostExecute(List<String> myList) {
            if (myList == null)
                System.out.println("NULL");
            else {
                setContentView(R.layout.main);
                System.out.println("OK GOOD");
                tableauImages = myList;
                System.out.println(tableauImages.get(0));
                //Récupération de la listview créée dans le fichier main.xml
                maListViewPerso = (ListView) findViewById(R.id.listviewperso);

                //Création de la ArrayList qui nous permettra de remplire la listView
                ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();

                //On déclare la HashMap qui contiendra les informations pour un item
                HashMap<String, String> map;

                //Création d'une HashMap pour insérer les informations du premier item de notre listView
                for (String s: tableauImages) {
                    map = new HashMap<String, String>();
                    map.put("titre", s);
                    listItem.add(map);
                }


                //Création d'un SimpleAdapter qui se chargera de mettre les items présent dans notre list (listItem) dans la vue affichageitem
                SimpleAdapter mSchedule = new SimpleAdapter (MesTicketsActivity.this, listItem, R.layout.affichageitem,
                        new String[] {"titre", "description"}, new int[] { R.id.titre, R.id.description});

                //On attribut à notre listView l'adapter que l'on vient de créer
                maListViewPerso.setAdapter(mSchedule);
                //Enfin on met un écouteur d'évènement sur notre listView
                maListViewPerso.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                        //on récupère la HashMap contenant les infos de notre item (titre, description, img)
                        HashMap<String, String> map = (HashMap<String, String>) maListViewPerso.getItemAtPosition(position);
                        Intent intent = new Intent(MesTicketsActivity.this, AffichageActivity.class);
                        String str = map.get("titre");
                        System.out.println("Maintactivite "+str);
                        intent.putExtra("myString", str);
                        startActivity(intent);
                        //on créer une boite de dialogue
                        //Context mContext = MainActivity.this;

                        //Dialog dialog = new Dialog(mContext);

                        //dialog.setContentView(R.layout.special_dialog);
                        //on attribut un titre à notre boite de dialogue
                        //dialog.setTitle("Sélection Item");
                        //on insère un message à notre boite de dialogue, et ici on affiche le titre de l'item cliqué
                        //dialog.setMessage("Votre choix : "+map.get("titre"));
                        //on indique que l'on veut le bouton ok à notre boite de dialogue
                        //adb.setPositiveButton("Ok", null);
                        //new LoadImage().execute("http://192.168.1.19:8888/AndroidFileUpload/uploads/"+map.get("titre"));
                        //on affiche la boite de dialogue
                        //adb.show();
                    }
                });
            }
        }



    }
/*
    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MesTicketsActivity.this);
            pDialog.setMessage("Loading Image ....");
            pDialog.show();

        }
        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream)new URL(args[0]).getContent());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {

            if(image != null){
                pDialog.dismiss();
                Context mContext = MainActivity.this;
                Dialog dialog = new Dialog(mContext);
                dialog.setContentView(R.layout.special_dialog);
                ImageView imag2 = (ImageView) dialog.findViewById(R.id.image);
                System.out.println("FINALLY");
                imag2.setImageBitmap(image);
                //pDialog.dismiss();

            }else{

                pDialog.dismiss();
                Toast.makeText(MainActivity.this, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();

            }
        }
    }*/
}