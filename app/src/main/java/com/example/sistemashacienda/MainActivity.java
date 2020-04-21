package com.example.sistemashacienda;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        obtenerToken();
        Button ir_webview= (Button) findViewById(R.id.ir_webview);
        ir_webview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Webview.class);
                startActivityForResult(intent, 0);
            }
        });

    }

    private void obtenerToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(MainActivity.this,  new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String mToken = instanceIdResult.getToken();
                EditText mitoken= (EditText) findViewById(R.id.token);
                mitoken.setText(mToken);
                Toast.makeText(MainActivity.this, mToken, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
