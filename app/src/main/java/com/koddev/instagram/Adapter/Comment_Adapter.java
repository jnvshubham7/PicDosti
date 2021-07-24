package com.koddev.instagram.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.koddev.instagram.Main_Activity;
import com.koddev.instagram.Model.Comment;
import com.koddev.instagram.Model.User;
import com.koddev.instagram.R;

import java.util.List;

public class Comment_Adapter extends RecyclerView.Adapter<Comment_Adapter.ImageViewHolder> {

    private final Context mContext;
    private final List<Comment> mComment;
    private final String postid;

    private FirebaseUser firebaseUser;

    public Comment_Adapter(Context context, List<Comment> comments, String postid){
        mContext = context;
        mComment = comments;
        this.postid = postid;
    }

    @NonNull
    @Override
    public Comment_Adapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Comment_Adapter.ImageViewHolder holder, final int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Comment comment = mComment.get(position);

        holder.comment.setText(comment.getComment());
        getUserInfo(holder.image_profile, holder.username, comment.getPublisher());

        holder.username.setOnClickListener(view -> {

            Intent intent = new Intent(mContext, Main_Activity.class);
            intent.putExtra("publisherid", comment.getPublisher());
            mContext.startActivity(intent);
        });

        holder.image_profile.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, Main_Activity.class);
            intent.putExtra("publisherid", comment.getPublisher());
            mContext.startActivity(intent);
        });

        holder.itemView.setOnLongClickListener(view -> {
            if (comment.getPublisher().equals(firebaseUser.getUid())) {

                    AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                    alertDialog.setTitle("Do you want to delete?");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "No",
                            (dialog, which) -> dialog.dismiss());
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                            (dialog, which) -> {
                                FirebaseDatabase.getInstance().getReference("Comments")
                                        .child(postid).child(comment.getCommentid())
                                        .removeValue().addOnCompleteListener(task -> {
                                            if (task.isSuccessful()){
                                                Toast.makeText(mContext, "Deleted!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                dialog.dismiss();
                            });
                    alertDialog.show();
                }
            return true;
        });

    }

    @Override
    public int getItemCount() {
        return mComment.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {

        public ImageView image_profile;
        public TextView username, comment;

        public ImageViewHolder(View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.username);
            comment = itemView.findViewById(R.id.comment);
        }
    }

    private void getUserInfo(final ImageView imageView, final TextView username, String publisherid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(publisherid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                Glide.with(mContext).load(user.getImageurl()).into(imageView);
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
