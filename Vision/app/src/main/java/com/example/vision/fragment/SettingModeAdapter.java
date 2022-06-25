package com.example.vision.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vision.MainActivity;
import com.example.vision.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingModeAdapter extends RecyclerView.Adapter<SettingModeAdapter.SettingViewHolder> {
    String TAG = "SettingModeAdapter";
    private Context context;
    String[] names = {"视力设置","声音","距离","帮助",
                        "色盲设置","帮助",
                        "散光设置","帮助",
                        "其他设置","关于我们",
                        "连接设置","帮助"};
    String[] values = {"null","开","0.5米","",
                        "null","",
                        "null","",
                        "null","",
                        "null",""};
    final String[] Distance=new String[]{"0.5米","0.8米","1.0米"};

    int[] pics = {0, R.drawable.pic_volume,R.drawable.pic_distance,R.drawable.ic_baseline_help_outline_24,
                    0,R.drawable.ic_baseline_help_outline_24,
                    0,R.drawable.ic_baseline_help_outline_24,
                    0,R.drawable.pic_aboutme,
                    0,R.drawable.ic_baseline_help_outline_24};

    //开----swich 空---不显示


    SettingModeAdapter(Context context){
        this.context = context;
    }


    @NonNull
    @Override
    public SettingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.item_setting_mode,parent,false);
        return new SettingModeAdapter.SettingViewHolder(itemView);
//        return new LinearViewHolder(LayoutInflater.from(context).inflate(R.layout.setting_mode_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull SettingViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if(values[position].equals("null")==true){//代表标题
            Log.v(TAG,"1");
            //只显示head
            holder.item_card.setClickable(false);
            holder.item_head.setText(names[position]);
            holder.item_head.setVisibility(View.VISIBLE);
            holder.item_value.setVisibility(View.INVISIBLE);
            holder.item_pic.setVisibility(View.INVISIBLE);
            holder.item_switch.setVisibility(View.INVISIBLE);
            holder.item_name.setVisibility(View.INVISIBLE);
        }else if(values[position].equals("开")){
            Log.v(TAG,"2");
            //显示pic name switch
            //隐藏head value
            holder.item_head.setVisibility(View.INVISIBLE);
            holder.item_value.setVisibility(View.INVISIBLE);

            holder.item_pic.setImageResource(pics[position]);
            holder.item_name.setText(names[position]);
//            holder.item_switch.setChecked(values[position].equals("开"));
            holder.item_switch.setChecked(MainActivity.settingConfig.getUseAudio());
            holder.item_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    MainActivity.settingConfig.setUseAudio(b);
                }
            });

        }else if(values[position].equals("")){
            Log.v(TAG,"3");
            //显示pic name
            //隐藏head value switch
            holder.item_head.setVisibility(View.INVISIBLE);
            holder.item_value.setVisibility(View.INVISIBLE);
            holder.item_switch.setVisibility(View.INVISIBLE);

            holder.item_pic.setImageResource(pics[position]);
            holder.item_name.setText(names[position]);
        }else{
            Log.v(TAG,"4");
            //显示pic name value
            //隐藏head switch
            holder.item_head.setVisibility(View.INVISIBLE);
            holder.item_switch.setVisibility(View.INVISIBLE);

            holder.item_pic.setImageResource(pics[position]);
            holder.item_name.setText(names[position]);
            holder.item_value.setText(values[position]);
            holder.item_value.setText(Distance[MainActivity.settingConfig.getDistance()]);
        }

        holder.item_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                switch (position){
                    case 0://视力设置
                        break;
                    case 1://声音
                        break;
                    case 2://距离
                        AlertDialog.Builder builder1=new AlertDialog.Builder(context);
                        builder1.setTitle("请选择您想要进行测试时的距离");

                        int initPos=0;
                        if(Distance[1].equals(holder.item_value.getText().toString())) initPos=1;
                        else if(Distance[2].equals(holder.item_value.getText().toString())) initPos=2;
                        builder1.setSingleChoiceItems(Distance, initPos, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                holder.item_value.setText(Distance[which]);
                                MainActivity.settingConfig.setDistance(which);
                            }
                        });
                        builder1.setCancelable(true);
                        builder1.show();
                        break;
                    case 3://帮助
                        builder.setTitle("视力的帮助");
                        builder.setMessage(R.string.vision_help);
                        builder.setNeutralButton("我知道了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                Toast.makeText(context,"那么请开始测试吧！",Toast.LENGTH_SHORT).show();
                            }
                        });
                        builder.show();
                        break;
                    case 4://色盲设置
                        break;
                    case 5://帮助
                        builder.setTitle("色盲的帮助");
                        builder.setMessage(R.string.achromate_help);
                        builder.setNeutralButton("我知道了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                Toast.makeText(context,"那么请开始测试吧！",Toast.LENGTH_SHORT).show();
                            }
                        });
                        builder.show();
                        break;
                    case 6://散光设置
                        break;
                    case 7://帮助
                        builder.setTitle("散光的帮助");
                        builder.setMessage(R.string.vision_help);
                        builder.setNeutralButton("我知道了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                Toast.makeText(context,"那么请开始测试吧！",Toast.LENGTH_SHORT).show();
                            }
                        });
                        builder.show();
                        break;
                    case 8://其他设置
                        break;
                    case 9://关于我们
                        builder.setTitle("关于我们");
                        builder.setMessage("Design By Mike Dean©");
                        builder.setNeutralButton("我知道了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                Toast.makeText(context,"那么请开始测试吧！",Toast.LENGTH_SHORT).show();
                            }
                        });
                        builder.show();
                        break;
                    case 10:
                        break;
                    case 11:
                        builder.setTitle("连接帮助");
                        builder.setMessage(R.string.connect_help);
                        builder.setNeutralButton("我知道了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                Toast.makeText(context,"那么请开始测试吧！",Toast.LENGTH_SHORT).show();
                            }
                        });
                        builder.show();
                        break;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return names.length;
    }

    class SettingViewHolder extends RecyclerView.ViewHolder{
        //找到组件
        private ImageView item_pic;
        private TextView item_name,item_value, item_head;
        private SwitchMaterial item_switch;
        private MaterialCardView item_card;


        public SettingViewHolder(@NonNull View itemView) {
            super(itemView);
            item_pic = itemView.findViewById(R.id.setting_item_pic);//图片
            item_name = itemView.findViewById(R.id.setting_item_name);//设置选项的名称
            item_value = itemView.findViewById(R.id.setting_item_value);//设置选项的当前值
            item_head = itemView.findViewById(R.id.setting_item_head);//设置名字
            item_switch = itemView.findViewById(R.id.setting_item_switch);//设置开关
            item_card = itemView.findViewById(R.id.setting_item_background);
        }
    }

}
