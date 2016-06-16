package com.ladt.mtc;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.*;


public class MainActivity extends AppCompatActivity {

    @BindView(R.id.username)
    TextView _usernameText;
    @BindView(R.id.btn_code_barre)
    Button _codebarreButton;
    @BindView(R.id.btn_mes_tickets)
    Button _mesticketsButton;
    @BindView(R.id.btn_log_out)
    Button _logoutButton;
    @BindView(R.id.btn_scan)
    Button _scanButton;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Intent intentOrigin = getIntent();
        username = intentOrigin.getStringExtra("username");

        _codebarreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CodeBarreActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        _mesticketsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MesTicketsActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        _scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ScanTicket.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        _logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }


}
