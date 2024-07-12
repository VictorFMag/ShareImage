package br.ifmg.edu.bsi.progmovel.shareimage1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class NovoTextoActivity extends AppCompatActivity {

    public static String EXTRA_TEXTO_ATUAL_BAIXO = "br.ifmg.edu.bsi.progmovel.shareimage1.texto_atual_baixo";
    public static String EXTRA_NOVO_TEXTO_BAIXO = "br.ifmg.edu.bsi.progmovel.shareimage1.novo_texto_baixo";

    public static String EXTRA_TEXTO_ATUAL_CIMA = "br.ifmg.edu.bsi.progmovel.shareimage1.texto_atual_cima";
    public static String EXTRA_NOVO_TEXTO_CIMA = "br.ifmg.edu.bsi.progmovel.shareimage1.novo_texto_cima";

    public static String EXTRA_FONTE_ATUAL = "br.ifmg.edu.bsi.progmovel.shareimage1.fonte_atual";
    public static String EXTRA_NOVA_FONTE = "br.ifmg.edu.bsi.progmovel.shareimage1.nova_fonte";

    private EditText inputTxtBaixo;
    private EditText inputFontSize;
    private EditText inputTxtCima;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_texto);

        inputTxtBaixo = findViewById(R.id.inputTxtBaixo);
        inputTxtCima = findViewById(R.id.inputTxtCima);
        inputFontSize = findViewById(R.id.inputFontSize);

        Intent intent = getIntent();
        String textoAtualBaixo = intent.getStringExtra(EXTRA_TEXTO_ATUAL_BAIXO);
        String textoAtualCima = intent.getStringExtra(EXTRA_TEXTO_ATUAL_CIMA);
        float fonteAtual = intent.getFloatExtra(EXTRA_FONTE_ATUAL, 64);

        inputTxtBaixo.setText(textoAtualBaixo);
        inputTxtCima.setText(textoAtualCima);
        inputFontSize.setText(String.valueOf(fonteAtual));
    }

    public void enviarNovoTexto(View v) {
        String novoTextoBaixo = inputTxtBaixo.getText().toString();
        String novaFonte = inputFontSize.getText().toString();
        String novoTextoCima = inputTxtCima.getText().toString();

        Intent intent = new Intent();

        intent.putExtra(EXTRA_NOVO_TEXTO_BAIXO, novoTextoBaixo);
        intent.putExtra(EXTRA_NOVA_FONTE, novaFonte);
        intent.putExtra(EXTRA_NOVO_TEXTO_CIMA, novoTextoCima);

        setResult(RESULT_OK, intent);
        finish();
    }
}