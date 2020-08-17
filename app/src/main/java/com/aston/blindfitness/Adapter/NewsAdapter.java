package com.aston.blindfitness.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.aston.blindfitness.Model.Article;
import com.aston.blindfitness.Utils.NewsUtils;
import com.aston.blindfitness.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.List;

/**
 * Adapter class for our News API
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyHolder> {

    private List<Article> articles;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public NewsAdapter(List<Article> articles, Context context) {
        this.articles = articles;
        this.context = context;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.news_item, parent, false);
        return new MyHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holders, int position) {
        final MyHolder holder = holders;
        Article model = articles.get(position);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(NewsUtils.getRandomDrawbleColor());
        requestOptions.error(NewsUtils.getRandomDrawbleColor());
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.centerCrop();

        Glide.with(context)
                .load(model.getUrlToImage())
                .apply(requestOptions)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.image);

        holder.title.setText(model.getTitle());
        holder.description.setText(model.getDescription());
        holder.source.setText(model.getSource().getName());
        holder.time.setText("\u2022 " + NewsUtils.DateToTimeFormat(model.getPublishedAt()));
        holder.publishedAt.setText(NewsUtils.DateFormat(model.getPublishedAt()));
        holder.author.setText(model.getAuthor());
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title, description, author, publishedAt, source, time;
        ImageView image;
        ProgressBar progressBar;
        OnItemClickListener onItemClickListener;

        public MyHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            itemView.setOnClickListener(this);
            title = itemView.findViewById(R.id.news_title);
            description = itemView.findViewById(R.id.news_description);
            author = itemView.findViewById(R.id.news_author);
            publishedAt = itemView.findViewById(R.id.news_publishedAt);
            source = itemView.findViewById(R.id.news_source);
            time = itemView.findViewById(R.id.news_time);
            image = itemView.findViewById(R.id.news_image);
            progressBar = itemView.findViewById(R.id.progress_load_photo);

            this.onItemClickListener = onItemClickListener;
        }

        @Override
        public void onClick(View view) {
            onItemClickListener.onItemClick(view, getAdapterPosition());
        }
    }
}
