package com.example.vision.login;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.L;
import com.example.vision.R;
import com.example.vision.fragment.TipsModeAdpater;
import com.google.android.material.card.MaterialCardView;
import com.skydoves.transformationlayout.TransformationLayout;

import java.util.ArrayList;

public class LoginAdapter extends RecyclerView.Adapter<LoginAdapter.LoginViewHolder> {

    public interface ItemClickListener{
        void onItemClickListener(String UserName);
    }

    ArrayList<String> UserList = new ArrayList<>();
    private ItemClickListener listener;

    public LoginAdapter(ArrayList<String> UserList){
        this.UserList = UserList;
    }

    public void setUserList(ArrayList<String> userList){
        this.UserList = (ArrayList<String>) userList.clone();
    }
    public void setListener(ItemClickListener listener) { this.listener = listener; }

    @NonNull
    @Override
    public LoginViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // load
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.item_user_list, parent, false);
        return new LoginAdapter.LoginViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LoginViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // set listener
        Log.v("LoginAdapter", UserList.get(position));
        holder.text_num.setText(String.valueOf(position+1));
        holder.text_name.setText(UserList.get(position));
        holder.card_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginActivity.userDBManager.update(
                        UserList.get(position),
                        LoginActivity.userDBManager.getPassword(UserList.get(position)),
                        LoginActivity.userDBManager.getConfig(UserList.get(position)),
                        "0"
                        );
                UserList.remove(position);
                LoginAdapter.this.notifyDataSetChanged();
            }
        });
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClickListener(UserList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return UserList.size();
    }

    class LoginViewHolder extends RecyclerView.ViewHolder{
        //找到组件
        private TextView text_name, text_num;
        private MaterialCardView card_close;
        private MaterialCardView card;

        public LoginViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card);
            text_name = itemView.findViewById(R.id.text_name);
            text_num = itemView.findViewById(R.id.text_num);
            card_close = itemView.findViewById(R.id.card_close);
        }
    }


}
