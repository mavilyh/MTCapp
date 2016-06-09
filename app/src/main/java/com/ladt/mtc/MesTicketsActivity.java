package com.ladt.mtc;

/**
 * Created by zdl on 23/05/2016.
 */
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.*;

public class MesTicketsActivity extends AppCompatActivity {
    @BindView(R.id.btn_retour2)
    Button _retourButton;
    @BindView(R.id.mes_tickets)
    TextView _mes_ticketsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mestickets);
        ButterKnife.bind(this);

        _retourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Main activity
                finish();
            }
        });
    }
}