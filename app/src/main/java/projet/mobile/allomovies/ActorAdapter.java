package projet.mobile.allomovies;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ActorAdapter extends RecyclerView.Adapter<ActorAdapter.ActorViewHolder> {
    private List<Actor> actors;


    public ActorAdapter(List<Actor> actors) {
        this.actors = actors;
    }

    @NonNull
    @Override
    public ActorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_actor, parent, false);
        return new ActorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActorViewHolder holder, int position) {
        Actor actor = actors.get(position);
        holder.bind(actor);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Récupérer le contexte de la vue
                Context context = v.getContext();

                // Récupérer l'ID de l'acteur sélectionné
                int actorId = actor.getId();
                Log.d("ActorAdapter", "Actor ID: " + actorId);

                // Ouvrir l'activité ActorDetailActivity en passant l'ID de l'acteur
                Intent intent = new Intent(context, ActorDetailActivity.class);
                intent.putExtra("actor_id", actorId);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return actors.size();
    }

    public static class ActorViewHolder extends RecyclerView.ViewHolder {
        private ImageView profile_path;
        private TextView textViewName;

        public ActorViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_path = itemView.findViewById(R.id.actor_profile_image);
            textViewName = itemView.findViewById(R.id.actor_name);
        }

        public void bind(Actor actor) {
            String name = actor.getName();
            String profilePath = "https://image.tmdb.org/t/p/w200" + actor.getProfilePath();

            // Mettre à jour les vues avec les données de l'acteur
            textViewName.setText(name);
            Picasso.get().load(profilePath).into(profile_path);
        }
    }
}