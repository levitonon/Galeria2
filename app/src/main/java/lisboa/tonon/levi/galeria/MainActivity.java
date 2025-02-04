package lisboa.tonon.levi.galeria;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import android.Manifest;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import util.Util;

public class MainActivity extends AppCompatActivity {

    List<String> photos = new ArrayList<>();

    MainAdapter mainAdapter;

    static int RESULT_REQUEST_PERMISSION = 2;
    // Identificação para retorno de requisições de permissão.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Configura da Toolbar 
        Toolbar toolbar = findViewById(R.id.tbMain);
        setSupportActionBar(toolbar);

        // Obtém referência ao diretório de imagens definido pelo sistema.
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        // Lista todos os arquivos presentes nesse diretório.
        File[] files = dir.listFiles();

        // Itera pelos arquivos, adicionando cada caminho ao ArrayList de imagens.
        for(int i = 0; i < files.length; i++) {
            photos.add(files[i].getAbsolutePath());
        }

        mainAdapter = new MainAdapter(MainActivity.this, photos);

        RecyclerView rvGallery = findViewById(R.id.rvGallery);
        rvGallery.setAdapter(mainAdapter);

        // Descobre a largura de cada item
        float w = getResources().getDimension(R.dimen.itemWidth);

        // Cálculo do número de colunas que cabem na tela.
        int numberOfColumns = Util.calculateNoOfColumns(MainActivity.this, w);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, numberOfColumns);
        rvGallery.setLayoutManager(gridLayoutManager);

        // Monta uma lista de permissões que serão verificadas
        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.CAMERA);

        // Chama o método que verifica se as permissões já foram concedidas, caso contrário solicita.
        checkForPermissions(permissions);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_tb, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // É chamado quando o usuário seleciona um item do menu.
        switch (item.getItemId()) {
            case R.id.opCamera:
                // Se o usuário clicou no ícone de câmera, chamam o método de tirar foto.
                dispatchTakePictureIntent();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void startPhotoActivity(String photoPath) {
        Intent i = new Intent(MainActivity.this, PhotoActivity.class);
        i.putExtra("photo_path", photoPath);
        startActivity(i);
    }

    // Código para identificar quando a câmera retornou a foto.
    static int RESULT_TAKE_PICTURE = 1;

    String currentPhotoPath;
    // Armazena o caminho do arquivo gerado no momento em que a foto for tirada.

    private void dispatchTakePictureIntent() {
        // Criação de um arquivo temporário onde a câmera salvará a foto.
        File f = null;
        try {
            f = createImageFile();
        } catch (IOException e) {
            Toast.makeText(MainActivity.this, "Não foi possível criar o arquivo", Toast.LENGTH_LONG).show();
            return;
        }

        currentPhotoPath = f.getAbsolutePath();
        if(f != null) {
            Uri fUri = FileProvider.getUriForFile(
                    MainActivity.this, 
                    "trindade.daniel.galeria.fileprovider", 
                    f
            );

            // Cria uma intent para iniciar a câmera do dispositivo.
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            // Diz à câmera para salvar a foto no local especificado.
            i.putExtra(MediaStore.EXTRA_OUTPUT, fUri);

            startActivityForResult(i, RESULT_TAKE_PICTURE);
        }
    }

    private File createImageFile() throws IOException {
        // Gera um nome único com base na data/hora
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp;

        // Local onde o arquivo temporário será salvo
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File f = File.createTempFile(imageFileName, ".jpg", storageDir);
        return f;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_TAKE_PICTURE) {
            if(resultCode == Activity.RESULT_OK) {
                photos.add(currentPhotoPath);
                mainAdapter.notifyItemInserted(photos.size()-1);
            } else {
                File f = new File(currentPhotoPath);
                f.delete();
            }
        }
    }
//Verifica permissoes
    private void checkForPermissions(List<String> permissions) {
        List<String> permissionsNotGranted = new ArrayList<>();
        for(String permission : permissions) {
            if(!hasPermission(permission)) {
                permissionsNotGranted.add(permission);
            }
        }
        //Solicita permissões caso não tenha
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(permissionsNotGranted.size() > 0) {
                requestPermissions(
                    permissionsNotGranted.toArray(new String[permissionsNotGranted.size()]),
                    RESULT_REQUEST_PERMISSION
                );
            }
        }
    }
    // Verifica se a permissão informada está concedida pelo usuário.
    private boolean hasPermission(String permission) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ActivityCompat.checkSelfPermission(MainActivity.this, permission)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, 
                                          @NonNull String[] permissions, 
                                          @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        final List<String> permissionsRejected = new ArrayList<>();
        // Verifica permissões negadas.
        if(requestCode == RESULT_REQUEST_PERMISSION) {
            for(String permission : permissions) {
                if(!hasPermission(permission)) {
                    permissionsRejected.add(permission);
                }
            }
        }

        // Informa permissões negadas e dá opção de nunca mais perguntar
        if(permissionsRejected.size() > 0) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                    new AlertDialog.Builder(MainActivity.this)
                        .setMessage("Para usar essa app é preciso conceder essas permissões")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(
                                    permissionsRejected.toArray(new String[permissionsRejected.size()]),
                                    RESULT_REQUEST_PERMISSION
                                );
                            }
                        }).create().show();
                }
            }
        }
    }
}
