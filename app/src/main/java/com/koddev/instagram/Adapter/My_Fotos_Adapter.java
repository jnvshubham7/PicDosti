package com.koddev.instagram.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.koddev.instagram.Fragments.Post_Detail_Fragment;
import com.koddev.instagram.Model.Post;
import com.koddev.instagram.R;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class My_Fotos_Adapter extends RecyclerView.Adapter<My_Fotos_Adapter.ImageViewHolder> {

    private final Context mContext;
    private final List<Post> mPosts;

    public My_Fotos_Adapter(Context context, List<Post> posts){
        mContext = context;
        mPosts = posts;
    }

    @NonNull
    @Override
    public My_Fotos_Adapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.fotos_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final My_Fotos_Adapter.ImageViewHolder holder, final int position) {

        final Post post = mPosts.get(position);

        Glide.with(mContext).load(post.getPostimage()).into(holder.post_image);

        holder.post_image.setOnClickListener(view -> {
            SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
            editor.putString("postid", post.getPostid());
            editor.apply();

            ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new Post_Detail_Fragment()).commit();
        });

    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {

        public ImageView post_image;


        public ImageViewHolder(View itemView) {
            super(itemView);

            post_image = itemView.findViewById(R.id.post_image);

        }
    }
}