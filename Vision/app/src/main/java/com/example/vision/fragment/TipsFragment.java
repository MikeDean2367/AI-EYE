package com.example.vision.fragment;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vision.R;
import com.example.vision.databinding.FragmentTipsBinding;

import java.util.ArrayList;

public class TipsFragment extends Fragment {

    private TipsViewModel mViewModel;
    private MutableLiveData<ArrayList<ArticleData>> arrayListMutableLiveData;
    private ArrayList<ArticleData> articleData = new ArrayList<>();
    RecyclerView recyclerView;
    TipsModeAdpater adpater;

    public static TipsFragment newInstance() {
        return new TipsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tips, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(TipsViewModel.class);
        // TODO: Use the ViewModel
        recyclerView = getView().findViewById(R.id.setting_recyclerView);
        LinearLayoutManager horizonLinearLayoutManager = new LinearLayoutManager(this.getContext());
        horizonLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        adpater = new TipsModeAdpater();
        adpater.setActivity(getActivity());

        articleData.add(new ArticleData(
                "https://mp.weixin.qq.com/s?__biz=MzAxMTAwMzEyNw==&mid=2650434237&idx=1&sn=174a07e72bfb51e7cd7201a4d1ae5be6&chksm=83497f25b43ef6330a1739137d3eaa5d4636d845663a139c2633f46a0ba53c409d23dd70fc7f&mpshare=1&scene=1&srcid=05154L34x1m9kxtPJxmbbBtM&sharer_sharetime=1652618918542&sharer_shareid=037e69babd13c8c34b13df615e42b9d3&exportkey=AURK3SDdX%2BrWtX2b4%2Bp2wWc%3D&acctmode=0&pass_ticket=YwJ6aClwVOYcsmO4VaMyskzZzG7bvHrR43ZjIF6GIlqfaHOpiD1oIxHKKR5G7KDb&wx_header=0#rd",
                "儿童近视防控，这些“常识”居然都是错的！",
                R.drawable.article1));
        articleData.add(new ArticleData(
                "https://mp.weixin.qq.com/s?__biz=MjA1ODMxMDQwMQ==&mid=2657569137&idx=1&sn=2d04c201444827a9b71ad42de1d28212&chksm=49034d1f7e74c4090cafd7a50ec4c84d9dbaaa24ada7b54f959a9f75c6994e90aee907a95901&mpshare=1&scene=1&srcid=0515SQ9KnAWafeGDqfJga5AE&sharer_sharetime=1652618978816&sharer_shareid=037e69babd13c8c34b13df615e42b9d3&exportkey=AUwU023qqmhsmSiEGT37svQ%3D&acctmode=0&pass_ticket=YwJ6aClwVOYcsmO4VaMyskzZzG7bvHrR43ZjIF6GIlqfaHOpiD1oIxHKKR5G7KDb&wx_header=0#rd",
                "预防近视的关键，不是「少玩手机」！",
                R.drawable.article2));
        articleData.add(new ArticleData(
                "https://mp.weixin.qq.com/s?__biz=MzAwNjQ3NDM5OQ==&mid=2657269691&idx=2&sn=93b92f86d856f876102f9640f9c3ed57&chksm=809ac84bb7ed415db5887cecbb4076855f29223afa17c2c28f171519588b66333f22abda9aa3&mpshare=1&scene=1&srcid=0515XmrXx6kXrJuIxG9vpnkS&sharer_sharetime=1652619010326&sharer_shareid=037e69babd13c8c34b13df615e42b9d3&exportkey=AT9WNwhqpXeDkX5FjsYKCFU%3D&acctmode=0&pass_ticket=YwJ6aClwVOYcsmO4VaMyskzZzG7bvHrR43ZjIF6GIlqfaHOpiD1oIxHKKR5G7KDb&wx_header=0#rd",
                "阴天去户外，也能预防近视吗？",
                R.drawable.article3));
        articleData.add(new ArticleData(
                "https://mp.weixin.qq.com/s?__biz=MjM5OTc0MTM4NA==&mid=2693211237&idx=4&sn=e225abdf93d6b079b30b76b30c3e3903&chksm=824556c2b532dfd4beb66dc9717360d5994657cd6c70306457bafe18e908eba367412a2bf00b&mpshare=1&scene=1&srcid=0515zEzXRfORXq6Flxfvly25&sharer_sharetime=1652619046460&sharer_shareid=037e69babd13c8c34b13df615e42b9d3&exportkey=ASiDNwW2YjZ9DOsBZ%2FKZHWE%3D&acctmode=0&pass_ticket=YwJ6aClwVOYcsmO4VaMyskzZzG7bvHrR43ZjIF6GIlqfaHOpiD1oIxHKKR5G7KDb&wx_header=0#rd",
                "家长一定要牢记的保护视力24字口诀，打破你的护眼误区！",
                R.drawable.article4));
        articleData.add(new ArticleData(
                "https://mp.weixin.qq.com/s?__biz=MzIzNjA4ODE1NA==&mid=2649824258&idx=1&sn=e11e9382ade2a9580318267772d66ab3&chksm=f0d88237c7af0b212b83f8532e4c578ad855001ca5114df75c2475af121505911b2c1d783746&mpshare=1&scene=1&srcid=0515Ia3X7O87YWdOnN5KPykJ&sharer_sharetime=1652619062439&sharer_shareid=037e69babd13c8c34b13df615e42b9d3&exportkey=Aesmyo9RTamJU%2Bzv67iwdA8%3D&acctmode=0&pass_ticket=YwJ6aClwVOYcsmO4VaMyskzZzG7bvHrR43ZjIF6GIlqfaHOpiD1oIxHKKR5G7KDb&wx_header=0#rd",
                "开学在即，和大耳朵图图一起保护视力，爱护眼睛，远离近视",
                R.drawable.article5));
        articleData.add(new ArticleData(
                "https://mp.weixin.qq.com/s?__biz=MzU3MTgwMjkwMA==&mid=2247496054&idx=1&sn=2ebd3f2784e10a7813112347478b6207&chksm=fcd8250dcbafac1b65363f2eecdab7ca356bb12d10c6a6bfcca5ad8de47b7d3fb04daa1a6b6b&mpshare=1&scene=1&srcid=0515sbQtr94or6rZ7hSCr5hb&sharer_sharetime=1652619104079&sharer_shareid=037e69babd13c8c34b13df615e42b9d3&exportkey=AZE5E0cRPK25jYlD6NeDjSc%3D&acctmode=0&pass_ticket=YwJ6aClwVOYcsmO4VaMyskzZzG7bvHrR43ZjIF6GIlqfaHOpiD1oIxHKKR5G7KDb&wx_header=0#rd",
                "散光，这一篇科普给你讲透",
                R.drawable.article6));
        articleData.add(new ArticleData(
                "https://mp.weixin.qq.com/s?__biz=MzUyMjgwMTQxMQ==&mid=2247491350&idx=1&sn=eeb5860537879e6932ca75d639a17d08&chksm=f9c7077aceb08e6ce829faa59b16502a15f7c07e2e9db43940d5a8153e31c93c8352c255c186&mpshare=1&scene=1&srcid=0515LdAh4OqTqZyzD5iNkAsl&sharer_sharetime=1652619147256&sharer_shareid=037e69babd13c8c34b13df615e42b9d3&exportkey=AbDTGBkBxHpD45aBAxjId9A%3D&acctmode=0&pass_ticket=YwJ6aClwVOYcsmO4VaMyskzZzG7bvHrR43ZjIF6GIlqfaHOpiD1oIxHKKR5G7KDb&wx_header=0#rd",
                "几张图，简单判断自己是不是色盲！",
                R.drawable.article7));

        adpater.setArticleData(articleData);

        recyclerView.setLayoutManager(horizonLinearLayoutManager);
        recyclerView.setLayoutManager(horizonLinearLayoutManager);
        recyclerView.setAdapter(adpater);
//        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
//            @Override
//            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
//                super.getItemOffsets(outRect, view, parent, state);
//                outRect.top = 20;
//                outRect.bottom = 20;
//            }
//        });
         }

}