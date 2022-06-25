package com.example.vision.fragment;

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

public class SettingFragment extends Fragment {

    private SettingViewModel mViewModel;
    private RecyclerView recyclerView;

    public static SettingFragment newInstance() {
        return new SettingFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SettingViewModel.class);
        // TODO: Use the ViewModel
        recyclerView = getView().findViewById(R.id.setting_recyclerView);
        LinearLayoutManager horizonLinearLayoutManager = new LinearLayoutManager(this.getContext());
        horizonLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(horizonLinearLayoutManager);
        recyclerView.setLayoutManager(horizonLinearLayoutManager);
        recyclerView.setAdapter(new SettingModeAdapter(getContext()));
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.top = 20;
                outRect.bottom = 20;
            }
        });
    }

}