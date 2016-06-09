package com.ladt.mtc;

import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.io.UnsupportedEncodingException;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.*;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private static String Username;
    private HashMap<String, String> session =new HashMap<String, String>();

    @BindView(R.id.input_username)
    EditText _usernameText;
    @BindView(R.id.input_password)
    EditText _passwordText;
    @BindView(R.id.btn_login)
    Button _loginButton;
    @BindView(R.id.link_signup)
    TextView _signupLink;

   /* StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

    StrictMode.setThreadPolicy(policy); */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        Toast.makeText(getBaseContext(), "CREATION", Toast.LENGTH_LONG).show();

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new myTask().execute();
                /*Thread thread = new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            login();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                });

                thread.start();*/


                //login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }
     private class myTask extends AsyncTask<Void, Void, String>{
        @Override
        protected String doInBackground(Void... params){
            Toast.makeText(getBaseContext(), "DO IN BACKGROUND", Toast.LENGTH_LONG).show();
            //login();
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

            String username = _usernameText.getText().toString();
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

            return "OK";
        }
         @Override
         protected void onProgressUpdate(Void... progress){
             super.onProgressUpdate();
             final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                     R.style.AppTheme);
             progressDialog.setIndeterminate(true);
             progressDialog.setMessage("Authenticating...");
             progressDialog.show();
         }

         /*@Override
         protected void onPostExecute(String result){
             super.onPostExecute(result);
         }*/
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

}