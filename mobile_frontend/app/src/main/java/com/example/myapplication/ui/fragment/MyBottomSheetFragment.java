package com.example.myapplication.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.myapplication.R;
import com.example.myapplication.network.api.HandleListener;
import com.example.myapplication.network.api.Post.PostManager;
import com.example.myapplication.network.model.dto.ResponseDTO;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class MyBottomSheetFragment extends BottomSheetDialogFragment {
    RadioButton radioButton1, radioButton2, radioButton3;
    RadioGroup radioGroup;
    PostManager postManager;
    int postId;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.edit_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        postManager = new PostManager();
        super.onViewCreated(view, savedInstanceState);
        int scope = getArguments().getInt("scope");
        postId = getArguments().getInt("postId");
        radioGroup = view.findViewById(R.id.radioGroup_scope);
        radioButton1 = view.findViewById(R.id.radio_public);
        radioButton3 = view.findViewById(R.id.radio_friend);
        radioButton2 = view.findViewById(R.id.radio_private);
        setScope(scope);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId là ID của RadioButton được chọn
                if (checkedId == R.id.radio_public) {
                    checkScope(0);
                } else if (checkedId == R.id.radio_friend) {
                    checkScope(1);
                } else if (checkedId == R.id.radio_private) {
                    checkScope(2);
                }

            }
        });
//        Button closeButton = view.findViewById(R.id.btnClose);
//        closeButton.setOnClickListener(v -> dismiss());
    }

    private void checkScope(int scope) {
        postManager.changePostScope(postId, scope, new HandleListener<ResponseDTO>() {
            @Override
            public void onSuccess(ResponseDTO responseDTO) {
                System.out.println(responseDTO.response);
            }

            @Override
            public void onFailure(String errorMessage) {
                System.out.println(errorMessage);
            }
        });
        setScope(scope);
        Bundle result = new Bundle();
        result.putInt("newScope", scope);
        // Gửi dữ liệu về Fragment cha hoặc Activity
        getParentFragmentManager().setFragmentResult("requestKey", result);
    }
    private void setScope(int scope){
        if (scope == 0) {
            radioGroup.check(R.id.radio_public);
        } else if (scope == 1) {
            radioGroup.check(R.id.radio_friend);
        } else {
            radioGroup.check(R.id.radio_private);
        }
    }

}