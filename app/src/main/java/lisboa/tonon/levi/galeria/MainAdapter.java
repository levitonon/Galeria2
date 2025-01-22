package lisboa.tonon.levi.galeria;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import util.Util;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.PhotoViewHolder> {

    MainActivity mainActivity;
    List<String> photos;

    public MainAdapter(MainActivity mainActivity, List<String> photos) {
        this.mainActivity = mainActivity;
        this.photos = photos;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla o layout para o item da lista
        View itemView = LayoutInflater.from(mainActivity).inflate(R.layout.list_item, parent, false);

        // Retorna o ViewHolder com a view
        return new PhotoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        ImageView imPhoto = holder.imPhoto;

        // Dimensões do item
        int w = (int) mainActivity.getResources().getDimension(R.dimen.itemWidth);
        int h = (int) mainActivity.getResources().getDimension(R.dimen.itemHeight);

        // Cria um objeto Util para carregar a imagem
        Util utils = new Util();

        // Carrega a imagem na resolução especificada
        Bitmap bitmap = utils.getBitmap(photos.get(position), w, h);

        // Define o Bitmap carregado no ImageView
        imPhoto.setImageBitmap(bitmap);

        // Define o listener de clique para cada item
        imPhoto.setOnClickListener(v -> mainActivity.startPhotoActivity(photos.get(position)));
    }

    // Retorna o número de itens na lista
    @Override
    public int getItemCount() {
        return photos.size();
    }

    // ViewHolder que mantém a referência dos itens de cada view
    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imPhoto;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            // Inicializa o ImageView a partir do layout do item
            imPhoto = itemView.findViewById(R.id.imItem);
        }
    }
}
