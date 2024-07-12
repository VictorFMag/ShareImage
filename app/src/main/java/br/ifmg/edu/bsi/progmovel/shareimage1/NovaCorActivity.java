package br.ifmg.edu.bsi.progmovel.shareimage1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class NovaCorActivity extends AppCompatActivity {

    public static String EXTRA_COR_ATUAL_BAIXO = "br.ifmg.edu.bsi.progmovel.shareimage1.cor_atual_baixo";
    public static String EXTRA_NOVA_COR_BAIXO = "br.ifmg.edu.bsi.progmovel.shareimage1.nova_cor_baixo";
    public static String EXTRA_COR_ATUAL_CIMA = "br.ifmg.edu.bsi.progmovel.shareimage1.cor_atual_cima";
    public static String EXTRA_NOVA_COR_CIMA = "br.ifmg.edu.bsi.progmovel.shareimage1.nova_cor_cima";

    private EditText inputCorBaixo;
    private EditText inputCorCima;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nova_cor);

        inputCorBaixo = findViewById(R.id.inputCorBaixo);
        inputCorCima = findViewById(R.id.inputCorCima);

        Intent intent = getIntent();
        String corAtualBaixo = intent.getStringExtra(EXTRA_COR_ATUAL_BAIXO);
        String corAtualCima = intent.getStringExtra(EXTRA_COR_ATUAL_CIMA);
        inputCorBaixo.setText(corAtualBaixo);
        inputCorCima.setText(corAtualCima);
    }

    public void enviarNovaCor(View v) {
        String novaCorBaixo = inputCorBaixo.getText().toString();
        String novaCorCima = inputCorCima.getText().toString();

        Intent intent = new Intent();

        intent.putExtra(EXTRA_NOVA_COR_BAIXO, novaCorBaixo);
        intent.putExtra(EXTRA_NOVA_COR_CIMA, novaCorCima);

        setResult(RESULT_OK, intent);
        finish();
    }
}