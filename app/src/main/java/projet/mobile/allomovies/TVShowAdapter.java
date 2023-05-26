package projet.mobile.allomovies;

import android.content.Intent;
import android.graphics.Movie;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class TVShowAdapter extends RecyclerView.Adapter<TVShowAdapter.ViewHolder> {
    private List<TVShow> tvShows;
    private Context context;

    public TVShowAdapter(Context context, List<TVShow> tvShows) {
        this.context = context;
        this.tvShows = tvShows;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tv_show, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TVShow tvShow = tvShows.get(position);

        String imageUrl = tvShow.getPosterUrl();
        Picasso.get().load(imageUrl).into(holder.imageViewPoster);

        holder.textViewTitle.setText(tvShow.getName());
        holder.textViewDescription.setText(tvShow.getOverview());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Créez une intention pour lancer l'activité de détail
                Intent intent = new Intent(context, DetailActivity.class);

                // Passez l'ID du film à l'intention
                intent.putExtra("movie_id", tvShow.getId());

                // Démarrez l'activité de détail
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (tvShows != null) {
            return tvShows.size();
        } else {
            return 0;
        }
    }

    public void setTVShows(List<TVShow> tvShows) {
        // Définissez la liste des séries télévisées
        this.tvShows = tvShows;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewPoster;
        TextView textViewTitle;
        TextView textViewDescription;

        public ViewHolder(View itemView) {
            super(itemView);
            imageViewPoster = itemView.findViewById(R.id.image_view);
            textViewTitle = itemView.findViewById(R.id.tv_name);
            textViewDescription = itemView.findViewById(R.id.tv_overview);
        }
    }
}

