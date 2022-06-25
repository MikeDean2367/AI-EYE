package com.example.vision.fragment;

import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieDrawable;
import com.example.vision.DataActivity;
import com.example.vision.MainActivity;
import com.example.vision.R;
import com.example.vision.databinding.FragmentHomeBinding;
import com.example.vision.information.InformationActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private HomeViewModel mViewModel;
    private MutableLiveData<ArrayList<CardData>> arrayListMutableLiveData;
    private ArrayList<CardData> mode_Phone = new ArrayList<>(), mode_TV = new ArrayList<>();
    static public boolean isTVMode;
    static public boolean isConnect = false;
    FragmentHomeBinding binding;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //对布局需要绑定的内容进行加载
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_home,container,false);
        //获取到视图
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding.setData(mViewModel);
        binding.setLifecycleOwner(this); //数据观察 自我更新

        ZXingLibrary.initDisplayOpinion(getContext());

        //加载横向recyclerView
        RecyclerView horizonRecyclerView = getView().findViewById(R.id.recyclerView_Mode);
        //创建适配器
        HomeModeAdapter adapter = new HomeModeAdapter();
        //设置默认管理器
        LinearLayoutManager horizonLinearLayoutManager = new LinearLayoutManager(this.getContext());
        horizonLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        horizonRecyclerView.setLayoutManager(horizonLinearLayoutManager);
        //设置适配器
        adapter.setActivity(getActivity());
        adapter.setFragment(HomeFragment.this);
        adapter.setApp_intent(getActivity().getPackageManager().getLaunchIntentForPackage("com.example.handtiqu"));
        horizonRecyclerView.setAdapter(adapter);
        horizonRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.left = 20;
                outRect.right = 20;
            }
        });
//        horizonRecyclerView.item;

        //查找ViewModel 如果没有则创建
        mViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        //创建观察者 是对ViewModel中的数据进行监听
        mViewModel.getHorizonCardData().observe(this.getActivity(), new Observer<ArrayList<CardData>>() {
            @Override
            public void onChanged(ArrayList<CardData> cardData) {
                adapter.setCardData(cardData);
                adapter.notifyDataSetChanged();
            }
        });

        //初始化
        isTVMode = false;
        arrayListMutableLiveData = mViewModel.getHorizonCardData();

        CardData []cardData = {new CardData(R.drawable.mode_card_img_1),new CardData(R.drawable.mode_card_img_2),
                new CardData(R.drawable.mode_card_img_3), new CardData(R.drawable.mode_card_img_4)};
        mode_Phone.clear();
        for(int i =0;i<cardData.length;++i) {
            mode_Phone.add(cardData[i]);
        }
        mode_TV.clear();
        mode_TV.add(new CardData(R.drawable.mode_card_img_5));//连接电视
        mode_TV.add(new CardData(R.drawable.mode_card_img_6));//手势识别
        mode_TV.add(new CardData(R.drawable.mode_card_img_4));//环境亮度
        mode_TV.add(new CardData(R.drawable.mode_card_img_7));//距离测量
        arrayListMutableLiveData.setValue(mode_Phone);//系统自己创建的应该

        MotionLayout motionLayout = (MotionLayout) binding.include.getRoot();
        binding.include.getRoot().findViewById(R.id.txt_Phone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isTVMode==true){
                    arrayListMutableLiveData.setValue(mode_Phone);
                    motionLayout.setTransition(R.id.t2);
                    motionLayout.transitionToEnd();
                    isTVMode = false;
                }
            }
        });

        binding.include.getRoot().findViewById(R.id.txt_TV).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isTVMode==false){
                    arrayListMutableLiveData.setValue(mode_TV);
                    motionLayout.setTransition(R.id.t1);
                    motionLayout.transitionToEnd();
                    isTVMode = true;
                }
            }
        });

        setListener();
        mViewModel.updateInfo();
        // load lottie
        binding.lottie.playAnimation();
    }

    private void lottie2connect(boolean delay){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (delay) binding.lottieConnect.setSpeed(0.2f);
                else binding.lottieConnect.setSpeed(1f);
                binding.lottieConnect.setProgress(0.92f);
                binding.lottieConnect.setMinProgress(0.92f);
                binding.lottieConnect.setMaxProgress(1f);
                binding.lottieConnect.playAnimation();
            }
        });

    }

    private void lottie2disconnect(boolean delay){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.lottieConnect.setProgress(0.f);
                binding.lottieConnect.setMinProgress(0.f);
                binding.lottieConnect.setMaxProgress(0.92f);
                binding.lottieConnect.playAnimation();
            }
        });
//        binding.lottieConnect.setProgress(0.92f);
    }

    //■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■二维码解析结果■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode== Activity.RESULT_OK) Log.v("测试","ok");
        else Log.v("测试","no");
        if(data==null) Log.v("测试","data为空");
        else Log.v("测试","data不为空");
        if (requestCode == 111) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    Log.v("测试","bundle为空");
                    return;
                }
                Log.v("测试","bundle不为空");
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    Log.v("测试","解析成功");
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    MainActivity.IP = result.split(",")[0];
                    MainActivity.PORT = Integer.valueOf(result.split(",")[1]);
                    Log.v("HomeFragment", "hhhhhh:"+MainActivity.IP+" "+String.valueOf(MainActivity.PORT));
                    Toast.makeText(getActivity(),"正在连接，请稍候",Toast.LENGTH_LONG).show();
                    isConnect = true;
                    lottie2connect(true);
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(getActivity(), "解析二维码失败，请扫描电视端二维码！", Toast.LENGTH_LONG).show();
                    Log.v("测试","解析失败");
                }
            }
        }
    }

    private void setListener(){
        binding.dataCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = binding.itemTransition.withActivity(getActivity(), "transitionNameA");
                Intent intent = new Intent(getActivity(), DataActivity.class);
                intent.putExtra("TransformationParams", binding.itemTransition.getParcelableParams());
                getActivity().startActivity(intent, bundle);

            }
        });

        // 个人设置
        binding.cardUserPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), InformationActivity.class);
                Bundle bundle = binding.userTransition.withActivity(getActivity(), "transitionNameA");
                intent.putExtra("TransformationParams", binding.userTransition.getParcelableParams());
                startActivity(intent, bundle);
            }
        });

        // connect
        binding.cardConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isConnect) lottie2connect(false);
                else lottie2disconnect(false);
                isConnect = !isConnect;
                mViewModel.updateInfo();
            }
        });
    }
}