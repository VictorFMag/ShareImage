package br.ifmg.edu.bsi.progmovel.shareimage1;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.util.DisplayMetrics;

/**
 * Cria um meme com um texto e uma imagem de fundo.
 *
 * Você pode controlar o texto, a cor do texto e a imagem de fundo.
 */
public class MemeCreator {
    private String textoBaixo;
    private String textoCima;
    private int corTextoBaixo;
    private int corTextoCima;
    private Bitmap fundo;
    private DisplayMetrics displayMetrics;
    private Bitmap meme;
    private boolean dirty; // se true, significa que o meme precisa ser recriado.
    private float tamanhoFonte;

    // Variáveis para as posições dos textos
    private PointF posicaoTextoCima;
    private PointF posicaoTextoBaixo;

    public MemeCreator(String textoBaixo, String textoCima, int corTextoBaixo, int corTextoCima, Bitmap fundo, DisplayMetrics displayMetrics, float tamanhoFonte) {
        this.textoBaixo = textoBaixo;
        this.textoCima = textoCima;
        this.corTextoBaixo = corTextoBaixo;
        this.corTextoCima = corTextoCima;
        this.fundo = fundo;
        this.displayMetrics = displayMetrics;
        this.meme = criarImagem();
        this.dirty = true;
        this.tamanhoFonte = tamanhoFonte;

        float heightFactor = (float) fundo.getHeight() / fundo.getWidth();
        int width = displayMetrics.widthPixels;
        int height = (int) (width * heightFactor);

        // Setar as posições dos textos
        this.posicaoTextoCima = new PointF(width / 2, tamanhoFonte + 10);
        this.posicaoTextoBaixo = new PointF(width / 2, height - tamanhoFonte);

        // Garantir que as posições dos textos estão dentro dos limites da imagem
        if (posicaoTextoCima.y < tamanhoFonte) {
            posicaoTextoCima.y = tamanhoFonte;
        }
        if (posicaoTextoBaixo.y > height - tamanhoFonte) {
            posicaoTextoBaixo.y = height - tamanhoFonte;
        }
    }

    public PointF getPosicaoTextoCima() {
        return posicaoTextoCima;
    }

    public void setPosicaoTextoCima(PointF posicaoTextoCima) {
        this.posicaoTextoCima = posicaoTextoCima;
        dirty = true;
    }

    public PointF getPosicaoTextoBaixo() {
        return posicaoTextoBaixo;
    }

    public void setPosicaoTextoBaixo(PointF posicaoTextoBaixo) {
        this.posicaoTextoBaixo = posicaoTextoBaixo;
        dirty = true;
    }

    public float getTamanhoFonte() {
        return tamanhoFonte;
    }

    public void setTamanhoFonte(float tamanhoFonte) {
        this.tamanhoFonte = tamanhoFonte;
        dirty = true;
    }

    public String getTextoBaixo() {
        return textoBaixo;
    }

    public void setTextoBaixo(String textoBaixo) {
        this.textoBaixo = textoBaixo;
        dirty = true;
    }

    public String getTextoCima() {
        return textoCima;
    }

    public void setTextoCima(String textoCima) {
        this.textoCima = textoCima;
        dirty = true;
    }

    public int getCorTextoBaixo() {
        return corTextoBaixo;
    }

    public void setCorTextoBaixo(int corTextoBaixo) {
        this.corTextoBaixo = corTextoBaixo;
        dirty = true;
    }

    public int getCorTextoCima() {
        return corTextoCima;
    }

    public void setCorTextoCima(int corTextoCima) {
        this.corTextoCima = corTextoCima;
        dirty = true;
    }

    public Bitmap getFundo() {
        return fundo;
    }

    public void setFundo(Bitmap fundo) {
        this.fundo = fundo;
        dirty = true;
    }

    public void rotacionarFundo(float graus) {
        Matrix matrix = new Matrix();
        matrix.postRotate(graus);
        fundo = Bitmap.createBitmap(fundo, 0, 0, fundo.getWidth(), fundo.getHeight(), matrix, true);
        dirty = true;
    }

    public Bitmap getImagem() {
        if (dirty) {
            meme = criarImagem();
            dirty = false;
        }
        return meme;
    }

    protected Bitmap criarImagem() {
        if (fundo == null || displayMetrics == null) {
            return null;
        }

        float heightFactor = (float) fundo.getHeight() / fundo.getWidth();
        int width = displayMetrics.widthPixels;
        int height = (int) (width * heightFactor);

        // nao deixa a imagem ocupar mais que 60% da altura da tela.
        if (height > displayMetrics.heightPixels * 0.6) {
            height = (int) (displayMetrics.heightPixels * 0.6);
            width = (int) (height * (1 / heightFactor));
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        Paint paintTxtCima = new Paint();

        Bitmap scaledFundo = Bitmap.createScaledBitmap(fundo, width, height, true);
        canvas.drawBitmap(scaledFundo, 0, 0, new Paint());

        paint.setColor(corTextoBaixo);
        paint.setAntiAlias(true);
        paint.setTextSize(tamanhoFonte);
        paint.setTypeface(Typeface.create("sans-serif-condensed", Typeface.BOLD));
        paint.setTextAlign(Paint.Align.CENTER);

        paintTxtCima.setColor(corTextoCima);
        paintTxtCima.setAntiAlias(true);
        paintTxtCima.setTextSize(tamanhoFonte);
        paintTxtCima.setTypeface(Typeface.create("sans-serif-condensed", Typeface.BOLD));
        paintTxtCima.setTextAlign(Paint.Align.CENTER);

        // desenhar texto em cima usando a posição definida
        if (textoCima != null && posicaoTextoCima != null) {
            canvas.drawText(textoCima, posicaoTextoCima.x, posicaoTextoCima.y, paintTxtCima);
        }

        // desenhar texto embaixo usando a posição definida
        if (textoBaixo != null && posicaoTextoBaixo != null) {
            canvas.drawText(textoBaixo, posicaoTextoBaixo.x, posicaoTextoBaixo.y, paint);
        }

        return bitmap;
    }
}
