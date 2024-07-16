package br.ifmg.edu.bsi.progmovel.shareimage1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia;
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.exifinterface.media.ExifInterface;

import java.io.BufferedOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;

/**
 * Activity que cria uma imagem com um texto e imagem de fundo.
 */
public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private MemeCreator memeCreator;
    private int textoSelecionado = 0; // 0 para nada, 1 para texto de cima, 2 para texto de baixo

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);

        Bitmap imagemFundo = BitmapFactory.decodeResource(getResources(), R.drawable.fry_meme);

        memeCreator = new MemeCreator("Texto de baixo!", "Texto de cima!", Color.WHITE, Color.WHITE,
                imagemFundo, getResources().getDisplayMetrics(), 64);
        mostrarImagem();

        // Adicionando o ouvinte de toque à imagem
        imageView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                float x = event.getX();
                float y = event.getY();

                if (textoSelecionado == 0) {
                    // Não há texto selecionado
                    showTextSelectionDialog();
                }
                else if (textoSelecionado == 1) {
                    // Texto de cima
                    memeCreator.setPosicaoTextoCima(new PointF(x, y));
                    textoSelecionado = 0;
                } else if (textoSelecionado == 2) {
                    // Texto de baixo
                    memeCreator.setPosicaoTextoBaixo(new PointF(x, y));
                    textoSelecionado = 0;
                }
                mostrarImagem();
            }
            return true;
        });
    }

    // Seleção de texto
    private void showTextSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecione o texto");

        builder.setPositiveButton("Texto de cima", (dialog, which) -> textoSelecionado = 1);

        builder.setNegativeButton("Texto de baixo", (dialog, which) -> textoSelecionado = 2);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Mudança de template
    public void setViewTemplateActivity(View view) {
        Intent intent = new Intent(this, TemplateActivity.class);
        changeTemplate.launch(intent);
    }

    private final ActivityResultLauncher<Intent> changeTemplate = registerForActivityResult(new StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        if (intent != null) {

                            byte[] novoTemplate = intent.getByteArrayExtra(TemplateActivity.EXTRA_NOVO_TEMPLATE);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(novoTemplate, 0, novoTemplate.length);

                            memeCreator.setFundo(bitmap);
                            mostrarImagem();
                        }
                    }
                }
            });

    // Mudança de textos
    public void iniciarMudarTexto(View v) {
        Intent intent = new Intent(this, NovoTextoActivity.class);
        intent.putExtra(NovoTextoActivity.EXTRA_TEXTO_ATUAL_BAIXO, memeCreator.getTextoBaixo());
        intent.putExtra(NovoTextoActivity.EXTRA_TEXTO_ATUAL_CIMA, memeCreator.getTextoCima());
        intent.putExtra(NovoTextoActivity.EXTRA_FONTE_ATUAL, memeCreator.getTamanhoFonte());

        startNovoTexto.launch(intent);
    }

    private final ActivityResultLauncher<Intent> startNovoTexto = registerForActivityResult(new StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        if (intent != null) {

                            String novoTextoBaixo = intent.getStringExtra(NovoTextoActivity.EXTRA_NOVO_TEXTO_BAIXO);
                            String novoTextoCima = intent.getStringExtra(NovoTextoActivity.EXTRA_NOVO_TEXTO_CIMA);
                            String novoTamanhoFonte = intent.getStringExtra(NovoTextoActivity.EXTRA_NOVA_FONTE);

                            memeCreator.setTextoBaixo(novoTextoBaixo);
                            memeCreator.setTextoCima(novoTextoCima);

                            memeCreator.setTamanhoFonte(Float.valueOf(novoTamanhoFonte));
                            mostrarImagem();
                        }
                    }
                }
            });

    // Mudança de cores
    private final ActivityResultLauncher<Intent> startNovaCor = registerForActivityResult(new StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        if (intent != null) {
                            String novaCorBaixo = intent.getStringExtra(NovaCorActivity.EXTRA_NOVA_COR_BAIXO);
                            if (novaCorBaixo == null || novaCorBaixo.isEmpty() || novaCorBaixo.equals("")) {
                                Toast.makeText(MainActivity.this, "Cor desconhecida. Usando preto no lugar.", Toast.LENGTH_SHORT).show();
                                novaCorBaixo = "BLACK";
                            }
                            String novaCorCima = intent.getStringExtra(NovaCorActivity.EXTRA_NOVA_COR_CIMA);
                            if (novaCorCima == null || novaCorCima.isEmpty() || novaCorCima.equals("")) {
                                Toast.makeText(MainActivity.this, "Cor desconhecida. Usando preto no lugar.", Toast.LENGTH_SHORT).show();
                                novaCorCima = "BLACK";
                            }
                            memeCreator.setCorTextoBaixo(Color.parseColor(novaCorBaixo.toUpperCase()));
                            memeCreator.setCorTextoCima(Color.parseColor(novaCorCima.toUpperCase()));
                            mostrarImagem();
                        }
                    }
                }
            });

    public void iniciarMudarCor(View v) {
        Intent intent = new Intent(this, NovaCorActivity.class);
        intent.putExtra(NovaCorActivity.EXTRA_COR_ATUAL_BAIXO, converterCor(memeCreator.getCorTextoBaixo()));
        intent.putExtra(NovaCorActivity.EXTRA_COR_ATUAL_CIMA, converterCor(memeCreator.getCorTextoCima()));

        startNovaCor.launch(intent);
    }

    public String converterCor(int cor) {
        switch (cor) {
            case Color.BLACK:
                return "BLACK";
            case Color.WHITE:
                return "WHITE";
            case Color.BLUE:
                return "BLUE";
            case Color.GREEN:
                return "GREEN";
            case Color.RED:
                return "RED";
            case Color.YELLOW:
                return "YELLOW";
        }
        return null;
    }

    // Mudança de Background
    private final ActivityResultLauncher<PickVisualMediaRequest> startImagemFundo = registerForActivityResult(new PickVisualMedia(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    if (result == null) {
                        return;
                    }
                    try (ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(result, "r")) {
                        Bitmap imagemFundo = MediaStore.Images.Media.getBitmap(MainActivity.this.getContentResolver(), result);
                        memeCreator.setFundo(imagemFundo);

                        // descobrir se é preciso rotacionar a imagem
                        FileDescriptor fd = pfd.getFileDescriptor();
                        ExifInterface exif = new ExifInterface(fd);
                        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                            memeCreator.rotacionarFundo(90);
                        }

                        mostrarImagem();
                    } catch (IOException e) {
                        Toast.makeText(MainActivity.this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            });

    public void iniciarMudarFundo(View v) {
        startImagemFundo.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    // Geração de imagem
    public void mostrarImagem() {
        imageView.setImageBitmap(memeCreator.getImagem());
    }

    // Compartilhamento de imagem
    private ActivityResultLauncher<String> startWriteStoragePermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    if (!result) {
                        Toast.makeText(MainActivity.this, "Sem permissão para salvar a imagem", Toast.LENGTH_SHORT).show();
                    } else {
                        iniciarCompartilharImagem();
                    }
                }
            });

    public void iniciarCompartilharImagem() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            compartilharImagem();
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                startWriteStoragePermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            } else {
                compartilharImagem();
            }
        }
    }

    private void compartilharImagem() {
        String nomeArquivo = System.currentTimeMillis() + "_meme.jpg";
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, nomeArquivo);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/memes");
            contentValues.put(MediaStore.Images.Media.IS_PENDING, true);
        }

        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        try (BufferedOutputStream out = new BufferedOutputStream(getContentResolver().openOutputStream(uri))) {
            memeCreator.getImagem().compress(Bitmap.CompressFormat.JPEG, 90, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        contentValues.clear();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.Images.Media.IS_PENDING, false);
            getContentResolver().update(uri, contentValues, null, null);
        }

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setType("image/jpeg");

        startActivity(Intent.createChooser(intent, "Compartilhar via: "));
    }
}
