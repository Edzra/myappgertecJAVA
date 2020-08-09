package com.example.myfirstapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //Váriaveis de Lib

    //ZXing
    private IntentIntegrator qrScan;
    private ArrayList<String> arrayListTipo;



    //Views

    private Button btnLerQRCodeEBarcode;
    private Button btnImpressao;
    private PrintActivity P;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Auxiliar para ultilizar os metodos da classe de Impressao
        P = new PrintActivity(this);
        //Views da Tela Principal
        btnLerQRCodeEBarcode = (Button) findViewById(R.id.btn_lerqrcodeebarcode);
        btnImpressao = (Button) findViewById(R.id.btn_Impressao);

        btnLerQRCodeEBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCamera();
            }
        });

        btnImpressao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i;
                i = new Intent(MainActivity.this,PrintActivity.class);
                startActivity(i);
            }
        });




    }



    //funcoes de leitor de codigo de barras e qrcode
    //funcao que inicia a camera,
    private void startCamera(){
        this.arrayListTipo = new ArrayList<String>();


        arrayListTipo.add("QR_CODE");
        arrayListTipo.add("EAN_8");
        arrayListTipo.add("EAN_13");

        qrScan = new IntentIntegrator(this);
        qrScan.setPrompt("Digitalizar o código ");
        qrScan.setBeepEnabled(true);
        qrScan.setBarcodeImageEnabled(true);
        qrScan.setTimeout(30000);

        qrScan.initiateScan(this.arrayListTipo);
    }
    //funcao que lida com o resultado do que foi lido e identificado pela camera como codigo valido
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (intentResult != null) {
            //if qrcode has nothing in it
            if (intentResult.getContents() == null) {
                System.out.println("Não foi possivel achar o qrcode");
                // Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                try {
                    P.PrintBarCode(intentResult.getContents(),intentResult.getFormatName(),300,300);
                    System.out.println(intentResult.getContents());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
            System.out.println(": Não foi possível fazer a leitura.\n");
        }
    }

}
