package lisboa.tonon.levi.galeria;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.File;

import util.Util;

public class PhotoActivity extends AppCompatActivity {

    String photoPath;
    // Caminho completo da foto

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_photo);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Configura a Toolbar
        Toolbar toolbar = findViewById(R.id.tbPhoto);
        setSupportActionBar(toolbar);

        // Pega a referência da ActionBar para habilitar o botão de retorno
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        // Extrai a string "photo_path" passada de outra tela
        photoPath = i.getStringExtra("photo_path");

        Util Utils = new Util();

        // Para exibir a imagem, obtemos o bitmap correspondente ao caminho da foto.
        Bitmap bitmap = Utils.getBitmap(photoPath);

        // Pega a referência do ImageView do layout e define o bitmap como conteúdo.
        ImageView imPhoto = findViewById(R.id.imPhoto);
        imPhoto.setImageBitmap(bitmap);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Infla o menu específico desta tela (com opção de compartilhar).
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.photo_activity_tb, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.opShare:
                // Se o usuário clicou na opção de compartilhar, chama sharePhoto().
                sharePhoto();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void sharePhoto() {
        // Método responsável por compartilhar a foto armazenada em photoPath.
        Uri photoUri = FileProvider.getUriForFile(
                PhotoActivity.this,
                "trindade.daniel.galeria.fileprovider",
                new File(photoPath)
        );

        // Cria uma Intent de envio, anexa a foto e define o tipo de arquivo.
        Intent i = new Intent(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_STREAM, photoUri);
        i.setType("image/jpeg");

        startActivity(i);
    }
}
