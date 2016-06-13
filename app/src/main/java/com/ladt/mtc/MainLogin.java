package com.ladt.mtc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.ladt.mtc.LoginActivity;

public class MainLogin extends Activity
{
    private TextView tv;
    public static final int RESULT_Main = 1;

    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);

        //Appel de la page de Login
        startActivityForResult(new Intent(MainLogin.this, LoginActivity.class), RESULT_Main);

        tv = new TextView(this);
        setContentView(tv);
    }

    private void startup(Intent i)
    {
        // Récupère l'identifiant
        //int user = i.getIntExtra("userid",-1);

        //Affiche les identifiants de l'utilisateur
        // tv.setText("UserID: "+String.valueOf(user)+" logged in");
        Intent intent = new Intent(MainLogin.this, MainActivity.class);
        startActivity(intent);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == RESULT_Main && resultCode == RESULT_CANCELED) {
            Toast.makeText(getBaseContext(), "ARF!", Toast.LENGTH_LONG).show();
            startup(data);
        }
        else {
            Toast.makeText(getBaseContext(), "BRAVO!", Toast.LENGTH_LONG).show();
            startup(data);
        }
    }
}