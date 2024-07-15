package br.ifmg.edu.bsi.progmovel.shareimage1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

public class TemplateActivity extends AppCompatActivity {

    public static String EXTRA_NOVO_TEMPLATE = "br.ifmg.edu.bsi.progmovel.shareimage1.novo_template";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template);
    }

    public void enviarNovoTemplate(View view) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_NOVO_TEMPLATE, converterDrawableParaBytearray(view));

        setResult(RESULT_OK, intent);
        finish();
    }

    private byte[] converterDrawableParaBytearray(View view) {
        ImageView imageView = view.findViewById(view.getId());
        Drawable drawable = imageView.getDrawable();

        Bitmap bitmap;
        if (drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        } else {
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        }

        // Converte o Bitmap para um byte array
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        return byteArray;
    }
}