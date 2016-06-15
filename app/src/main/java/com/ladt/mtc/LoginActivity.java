package com.ladt.mtc;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ladt.mtc.R;

public class LoginActivity extends Activity {
    // Lien vers votre page php sur votre serveur
    private static final String UPDATE_URL = "http://10.43.4.183:8888/AndroidFileUpload/auth.php";

    public ProgressDialog progressDialog;

    private EditText UserEditText;

    private EditText PassEditText;

    private static final int REQUEST_SIGNUP = 0;


    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // initialisation d'une progress bar
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        // Récupération des éléments de la vue définis dans le xml
        UserEditText = (EditText) findViewById(R.id.username);

        PassEditText = (EditText) findViewById(R.id.password);
        Button button = (Button) findViewById(R.id.okbutton);
        TextView link = (TextView) findViewById(R.id.link_signup);

        // Définition du listener du bouton
        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                int usersize = UserEditText.getText().length();

                int passsize = PassEditText.getText().length();
                // si les deux champs sont remplis
                if (usersize > 0 && passsize > 0) {

                    progressDialog.show();

                    String user = UserEditText.getText().toString();

                    String pass = PassEditText.getText().toString();
                    // On appelle la fonction doLogin qui va communiquer avec le PHP
                    doLogin(user, pass);

                } else
                    createDialog("Error", "Please enter Username and Password");

            }

        });

        //button = (Button) findViewById(R.id.cancelbutton);
        // Création du listener du bouton cancel (on sort de l'appli)
        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                quit(false, null);
            }

        });

        link.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    private void quit(boolean success, Intent i) {
        // On envoie un résultat qui va permettre de quitter l'appli
        setResult((success) ? Activity.RESULT_OK : Activity.RESULT_CANCELED, i);
        finish();

    }

    private void createDialog(String title, String text) {
        // Création d'une popup affichant un message
        AlertDialog ad = new AlertDialog.Builder(this)
                .setPositiveButton("Ok", null).setTitle(title).setMessage(text)
                .create();
        ad.show();

    }

    private void doLogin(final String login, final String pass) {

        final String pw = md5(pass);
        // Création d'un thread
        Thread t = new Thread() {

            public void run() {

                Looper.prepare();
                // On se connecte au serveur afin de communiquer avec le PHP
                DefaultHttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 15000);

                HttpResponse response;
                HttpEntity entity;

                try {
                    // On établit un lien avec le script PHP
                    HttpPost post = new HttpPost(UPDATE_URL);

                    List<NameValuePair> nvps = new ArrayList<NameValuePair>();

                    nvps.add(new BasicNameValuePair("username", login));

                    nvps.add(new BasicNameValuePair("password", pw));

                    post.setHeader("Content-Type", "application/x-www-form-urlencoded");
                    // On passe les paramètres login et password qui vont être récupérés
                    // par le script PHP en post
                    post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
                    // On récupère le résultat du script
                    response = client.execute(post);

                    entity = response.getEntity();

                    InputStream is = entity.getContent();
                    // On appelle une fonction définie plus bas pour traduire la réponse
                    read(is);
                    is.close();

                    if (entity != null)
                        entity.consumeContent();

                } catch (Exception e) {

                    progressDialog.dismiss();
                    createDialog("Error", "Couldn't establish a connection");

                }

                Looper.loop();

            }

        };

        t.start();

    }

    private void read(InputStream in) {
        // On traduit le résultat d'un flux
        SAXParserFactory spf = SAXParserFactory.newInstance();

        SAXParser sp;

        try {

            sp = spf.newSAXParser();

            XMLReader xr = sp.getXMLReader();
            // Cette classe est définie plus bas
            LoginContentHandler uch = new LoginContentHandler();

            xr.setContentHandler(uch);

            xr.parse(new InputSource(in));

        } catch (ParserConfigurationException e) {

        } catch (SAXException e) {

        } catch (IOException e) {
        }

    }

    private String md5(String in) {

        MessageDigest digest;

        try {

            digest = MessageDigest.getInstance("MD5");

            digest.reset();

            digest.update(in.getBytes());

            byte[] a = digest.digest();

            int len = a.length;

            StringBuilder sb = new StringBuilder(len << 1);

            for (int i = 0; i < len; i++) {

                sb.append(Character.forDigit((a[i] & 0xf0) >> 4, 16));

                sb.append(Character.forDigit(a[i] & 0x0f, 16));

            }

            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;

    }

    private class LoginContentHandler extends DefaultHandler {
        // Classe traitant le message de retour du script PHP
        private boolean in_loginTag = false;
        private int userID;
        private boolean error_occured = false;

        public void startElement(String n, String l, String q, Attributes a)

                throws SAXException

        {

            if (Objects.equals(l, "login"))
                in_loginTag = true;
            if (Objects.equals(l, "error")) {

                progressDialog.dismiss();

                switch (Integer.parseInt(a.getValue("value"))) {
                    case 1:
                        createDialog("Error", "Couldn't connect to Database");
                        break;
                    case 2:
                        createDialog("Error", "Error in Database: Table missing");
                        break;
                    case 3:
                        createDialog("Error", "Invalid username and/or password");
                        break;
                }
                error_occured = true;

            }

            if (Objects.equals(l, "user") && in_loginTag && !Objects.equals(a.getValue("id"), ""))
                // Dans le cas où tout se passe bien on récupère l'ID de l'utilisateur
                userID = Integer.parseInt(a.getValue("id"));

        }

        public void endElement(String n, String l, String q) throws SAXException {
            // on renvoie l'id si tout est ok
            if (Objects.equals(l, "login")) {
                in_loginTag = false;

                if (!error_occured) {
                    progressDialog.dismiss();
                    Intent i = new Intent();
                    i.putExtra("userid", userID);
                    quit(true, i);
                }
            }
        }

        public void characters(char ch[], int start, int length) {
        }

        public void startDocument() throws SAXException {
        }

        public void endDocument() throws SAXException {
        }

    }

}
/*
    public void login() {
        //Log.d(TAG, "Login");

        Toast.makeText(getBaseContext(), "BRAVO VOUS AVEZ CLIQUE", Toast.LENGTH_LONG).show();

        if (!validate()) {
            onLoginFailed();
        }

        _loginButton.setEnabled(false);

        /*final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();*/

       /* String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        DefaultHttpClient mHttpClient = new DefaultHttpClient();
        HttpPost mPost = new HttpPost("http://10.43.6.254:8888/web/php/login.php");

        List<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
        pairs.add(new BasicNameValuePair("username", username));
        pairs.add(new BasicNameValuePair("password", password));

        try {
            mPost.setEntity(new UrlEncodedFormEntity(pairs, HTTP.UTF_8));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            HttpResponse response = mHttpClient.execute(mPost);
            int res = response.getStatusLine().getStatusCode();

            if (res == 200) {
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    String info = EntityUtils.toString(entity);
                    System.out.println("info-----------"+info);
                    //以下主要是对服务器端返回的数据进行解析

                    JSONObject jsonObject=null;
                    //flag为登录成功与否的标记,从服务器端返回的数据
                    String flag="";
                    String name="";
                    String userid="";
                    String sessionid="";
                    try {
                        jsonObject = new JSONObject(info);
                        flag = jsonObject.getString("flag");
                        name = jsonObject.getString("name");
                        userid = jsonObject.getString("userid");
                        sessionid = jsonObject.getString("sessionid");

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    //根据服务器端返回的标记,判断服务端端验证是否成功

                    if(flag.equals("success")){
                        //为session传递相应的值,用于在session过程中记录相关用户信息
                        session.put("s_userid", userid);
                        session.put("s_username", name);
                        session.put("s_sessionid", sessionid);
                        onLoginSuccess();
                        setUsername(username);
                    }
                    else{
                        onLoginFailed();
                    }
                }
                else{

                    onLoginFailed();
                }

            }

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // if (username.equals("admin")&&password.equals("1234")) {
        //  Toast.makeText(getApplicationContext(), "Redirecting...",Toast.LENGTH_SHORT).show();
        //     onLoginSuccess();
        //     setUsername(username);
        // }else {
        //     onLoginFailed();
        // }

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                                R.style.AppTheme);
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }
*/
    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        Toast.makeText(getBaseContext(), "Login success", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        //  String email = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        //  if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        //      _usernameText.setError("enter a valid email address");
        //      valid = false;
        //  } else {
        //      _usernameText.setError(null);
        //  }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    public static String getUsername() {
        return Username;
    }

    public void setUsername(String Username) {
        this.Username = Username;
    }

} */