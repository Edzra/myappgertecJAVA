package com.example.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.google.zxing.integration.android.IntentIntegrator;

import java.util.ArrayList;

import br.com.gertec.gedi.GEDI;
import br.com.gertec.gedi.enums.GEDI_PRNTR_e_Alignment;
import br.com.gertec.gedi.enums.GEDI_PRNTR_e_BarCodeType;
import br.com.gertec.gedi.enums.GEDI_PRNTR_e_Status;
import br.com.gertec.gedi.exceptions.GediException;
import br.com.gertec.gedi.impl.Gedi;
import br.com.gertec.gedi.interfaces.IGEDI;
import br.com.gertec.gedi.interfaces.IPRNTR;
import br.com.gertec.gedi.structs.GEDI_PRNTR_st_BarCodeConfig;
import br.com.gertec.gedi.structs.GEDI_PRNTR_st_PictureConfig;
import br.com.gertec.gedi.structs.GEDI_PRNTR_st_StringConfig;

public class PrintActivity extends AppCompatActivity {

    //Váriaveis de Lib
    private IGEDI iGedi = null;
    private IPRNTR iPrint = null;
    private GEDI_PRNTR_st_PictureConfig pictureConfig;
    private GEDI_PRNTR_st_StringConfig stringConfig;
    private GEDI_PRNTR_e_Status status;

    private IntentIntegrator qrScan;
    private ArrayList<String> arrayListTipo;
    //Váriaveis Nativas
    private static boolean isPrinterInit = false;

    //Views
    EditText text;

    // A funcao imprimir foi implementada e esta comentada, nao foi chamada nenhum botao por que assim como no propio app da gertec, apenas imprimiria um blankline ou uma imagem no meu repositorio;
    private Button printTextButton;
    private Button printBarCodeButton;

    private CheckBox italic;
    private CheckBox negrito;
    private CheckBox sublinhado;

    private RadioButton btn_esquerda;
    private RadioButton btn_direita;
    private RadioButton btn_centro;

    RadioGroup group;

    private Spinner tipo;
    ArrayAdapter<CharSequence> tipoAdapter;

    private Spinner sizes;
    ArrayAdapter<CharSequence> sizesAdapter;


    //construtor padrao (ultilizado nas chamadas intents diretas pra essa atividade;
    public PrintActivity(){}

    //construtor
    public PrintActivity(Activity activity){
        this.threadCreate(activity);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.print_activity);



        //Inicia nova thread para o inicialização da GEDI
        threadCreate(this);
        //Atribuicao dos botoes
        printBarCodeButton= (Button)findViewById(R.id.printBarCode);
        printTextButton = (Button) findViewById(R.id.printText);

        italic = (CheckBox) findViewById(R.id.italic);
        negrito = (CheckBox) findViewById(R.id.negrito);
        sublinhado = (CheckBox) findViewById(R.id.sublinhado);

        text = (EditText) findViewById(R.id.mensagem);

        btn_direita = (RadioButton) findViewById(R.id.btn_direita);
        btn_esquerda = (RadioButton) findViewById(R.id.btn_esquerda);
        btn_centro = (RadioButton) findViewById(R.id.btn_centralizado);

        tipo = (Spinner) findViewById(R.id.Tipo);
        sizes = (Spinner) findViewById(R.id.Tamanho);

        tipoAdapter = ArrayAdapter.createFromResource(this,R.array.Fontes,android.R.layout.simple_spinner_item);
        sizesAdapter = ArrayAdapter.createFromResource(this,R.array.Tamanhos,android.R.layout.simple_spinner_item);

        tipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sizesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        tipo.setAdapter(tipoAdapter);
        sizes.setAdapter(sizesAdapter);




        printTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    PrintText(text.getText().toString());
                } catch (GediException e) {
                    e.printStackTrace();
                }
            }
        });
        printBarCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    if(tipo.getSelectedItemId() == 0) {
                        PrintBarCode(text.getText().toString(), "QR_CODE", 300, 300);
                    }
                    else if(tipo.getSelectedItemId() == 1){
                        PrintBarCode(text.getText().toString(), "EAN_8", 300, 300);
                    }else{
                        PrintBarCode(text.getText().toString(), "EAN_13", 300, 300);
                    }

                }catch (GediException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void threadCreate(Activity activity){
        new Thread(() -> {
            iGedi = new Gedi(activity);
            this.iGedi = GEDI.getInstance(activity);
            this.iPrint = this.iGedi.getPRNTR();
            Looper.prepare();
            try {
                new Thread().sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    //funcao que configura a string de acordo com as opcoes selecionadas
    public void parseStringConfig(String texto) throws GediException{

        int id;
        stringConfig = new GEDI_PRNTR_st_StringConfig(new Paint());

        //config padrao da string
        this.stringConfig.paint.setTextSize(50);
        this.stringConfig.paint.setTextAlign(Paint.Align.CENTER);

        //checagem pelas config selecionadas



        if(this.sizes.getSelectedItemId() == 0){
            this.stringConfig.paint.setTextSize(50);
        }

        else if(this.sizes.getSelectedItemId() == 1){
            this.stringConfig.paint.setTextSize(75);

        }
        else if(this.sizes.getSelectedItemId() == 2){
            this.stringConfig.paint.setTextSize(100);
        }

        if(negrito.isChecked()){
            this.stringConfig.paint.setFakeBoldText(true);
        }
        if(sublinhado.isChecked()){
            this.stringConfig.paint.setUnderlineText(true);
        }
        //        if(italic.isChecked()){
        //            this.stringConfig.paint.set
        //        }

        if(btn_esquerda.isChecked()){
            this.stringConfig.paint.setTextAlign(Paint.Align.LEFT);
        }
        else if(btn_direita.isChecked()){
            this.stringConfig.paint.setTextAlign(Paint.Align.RIGHT);
        }
        else if(btn_centro.isChecked()){
            this.stringConfig.paint.setTextAlign(Paint.Align.CENTER);
        }




    }
    //Checa o estado o atual da impressora, retorna um true se tudo estiver nos conformes
    public boolean isPrinterOK() throws GediException {


        if(this.status.getValue() == 0){
            return  true;
        }else {
            return  false;
        }
    }

    //Inicializa a impressora da GEDI, também checa se ja esta inicializada, se não estiver ; inicializa e salva essa informação em uma varável;
    public void printerInitialize() throws GediException {
        try{
            //this.iPrint.Init();
            if(this.iPrint != null && isPrinterInit == false){

                isPrinterInit = true;
                this.iPrint.Init();
                this.status = this.iPrint.Status();
            }
        }catch (GediException e){
            e.printStackTrace();
            throw new GediException(e.getErrorCode());
        }
    }

    //Funcao principal de imprimir texto
    public void PrintText(String texto) throws GediException{
        try{
            //inicializa a impressora
            this.printerInitialize();
            //após verificar se o estado da impressora é o esperado -> imprime e seta a varivel de guarda da iniciliazação da impressora pra false preparando-a para nova impressao
            if(isPrinterOK()){
                parseStringConfig(texto);
                this.iPrint.DrawStringExt(this.stringConfig, texto);
                this.iPrint.DrawBlankLine(200);
                this.iPrint.Output();
                this.isPrinterInit = false;
            }

        }catch (GediException e){
            throw new GediException((e.getErrorCode()));
        }
    }

    //Funcao de imprimir codigo de barras
    public void PrintBarCode(String texto, String barCodeType, int height, int widht) throws GediException{
        try{
            GEDI_PRNTR_st_BarCodeConfig barCodeConfig = new GEDI_PRNTR_st_BarCodeConfig();
            //configurando o tipo de codigo de barras
            barCodeConfig.barCodeType = GEDI_PRNTR_e_BarCodeType.valueOf(barCodeType);

            //setando altura
            barCodeConfig.height = height;
            //setando largura
            barCodeConfig.width = widht;
            //inicializa a impressora
            printerInitialize();
            //inseri as configuração na impressora
            this.iPrint.DrawBarCode(barCodeConfig,texto);
            this.iPrint.DrawBlankLine(150);
            //após verificar se o estado da impressora é o esperado -> imprime e seta a varivel de guarda da iniciliazação da impressora pra false preparando-a para nova impressao
            if(isPrinterOK()) {
                this.iPrint.Output();
                this.isPrinterInit = false;
            }
        }catch (IllegalArgumentException e){
            throw new IllegalArgumentException(e);
        } catch (GediException e) {
            throw new GediException(e.getErrorCode());
        }

    }

    public void PrintImage(String imagem, int height, int width) throws GediException{
        int id = 0;
        Bitmap bmp;
        try {
            //instancia o objeto da GEDI responsavel pela configuracao de imagem pra impressao
            pictureConfig = new GEDI_PRNTR_st_PictureConfig();

            //setando o alinhamento
            pictureConfig.alignment = GEDI_PRNTR_e_Alignment.valueOf("CENTER");

            //setando a altura
            pictureConfig.height = height;
            //setando a largura
            pictureConfig.width = width;


            id = this.getApplicationContext().getResources().getIdentifier(
                    imagem, "drawable",
                    this.getApplicationContext().getPackageName());
            bmp = BitmapFactory.decodeResource(this.getApplicationContext().getResources(), id);


            //inserindo a imagem impressora e formatando
            this.iPrint.DrawPictureExt(pictureConfig, bmp);
            this.iPrint.DrawBlankLine(50);

            //incializando a impressora
            printerInitialize();
            //após verificar se o estado da impressora é o esperado -> imprime e seta a varivel de guarda da iniciliazação da impressora pra false preparando-a para nova impressao
            if(isPrinterOK()) {
                this.iPrint.Output();
                this.isPrinterInit = false;
            }

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e);
        } catch (GediException e) {
            throw new GediException(e.getErrorCode());
        }

    }


}
