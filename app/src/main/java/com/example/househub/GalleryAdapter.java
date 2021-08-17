package com.example.househub;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewAdapter> {

    private List<String> images;
    private Context context;

    public GalleryAdapter(Context context, List<String> images) {
        this.context = context;
        this.images = images;
    }

    @NonNull
    @NotNull
    @Override
    public GalleryViewAdapter onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_image, parent, false);

        return new GalleryViewAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull GalleryAdapter.GalleryViewAdapter holder, int position) {
        String currentImage = images.get(position);

        Glide.with(context).load(images.get(position)).into(holder.imageView);


        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ImageActivity.class);
                intent.putExtra("path", currentImage);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class GalleryViewAdapter extends RecyclerView.ViewHolder{

        ImageView imageView;

        public GalleryViewAdapter(@NonNull @NotNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image_gallery);
        }
    }
}
