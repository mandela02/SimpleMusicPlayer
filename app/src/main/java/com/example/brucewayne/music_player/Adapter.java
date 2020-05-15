package com.example.brucewayne.music_player;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Bruce Wayne on 4/25/2017.
 */
public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private ArrayList<Song> mTitle;
    private Context mContext;

    public Adapter(Context mContext, ArrayList<Song> mTitle) {
        this.mContext = mContext;
        this.mTitle = mTitle;
    }

    @Override
    public Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(Adapter.ViewHolder holder, int position) {
        Song item = mTitle.get(position);
        holder.bindData(item);
    }

    @Override
    public int getItemCount() {
        return mTitle.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.songTitle);
            itemView.setOnClickListener(this);
        }

        public void bindData(Song item) {
            mTextView.setText(item.getName());
        }

        @Override
        public void onClick(View v) {
           /* int songIndex = getAdapterPosition();
            Intent in = new Intent(v.getContext(),
                MainActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            in.putExtra("songIndex", songIndex);
            mContext.startActivity(in);*/
        }
    }
}
