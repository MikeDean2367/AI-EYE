package com.example.vision.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vision.R;
import com.example.vision.TipsPageActivity;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.skydoves.transformationlayout.TransformationLayout;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class TipsModeAdpater extends RecyclerView.Adapter<TipsModeAdpater.TipsViewHolder> {

    ArrayList<ArticleData> articleData = new ArrayList<>();
    String TAG = "TipsModeAdapter";
    Activity activity;
    int MAX_LENGTH = 10;

    void setActivity(Activity activity){this.activity = activity;}

    void setArticleData(ArrayList<ArticleData> articleData){this.articleData = articleData; }

    @NonNull
    @Override
    public TipsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.item_tips_mode, parent, false);
        return new TipsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TipsViewHolder holder, int position) {
        holder.ArticleImg.setImageResource(articleData.get(position).getImageResource());
        holder.ArticleTitle.setText(articleData.get(position).getTitle());
        String url = articleData.get(position).getUrl();
        String title = articleData.get(position).getTitle();
        if (title.length()>MAX_LENGTH){
            title = title.substring(0, MAX_LENGTH) + "...";
        }
        String finalTitle = title;
        holder.CardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = holder.transformationLayout.withActivity(activity, "transitionNameA");
                Intent intent = new Intent(activity, TipsPageActivity.class);
                intent.putExtra("TransformationParams", holder.transformationLayout.getParcelableParams());
                intent.putExtra("url",url);
                intent.putExtra("title", finalTitle);
                activity.startActivity(intent, bundle);
            }
        });
    }

    @Override
    public int getItemCount() { return articleData.size(); }

    class TipsViewHolder extends RecyclerView.ViewHolder{
        //找到组件
        private ImageView ArticleImg;
        private TextView ArticleTitle;
        private MaterialCardView CardView;
        private TransformationLayout transformationLayout;

        public TipsViewHolder(@NonNull View itemView) {
            super(itemView);
            ArticleImg = itemView.findViewById(R.id.item_img);
            ArticleTitle = itemView.findViewById(R.id.article_title);
            CardView = itemView.findViewById(R.id.item_card);
            transformationLayout = itemView.findViewById(R.id.item_transition);
        }
    }
}
