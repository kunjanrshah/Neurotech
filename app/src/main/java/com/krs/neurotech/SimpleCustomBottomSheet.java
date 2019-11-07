package com.krs.neurotech;

import android.app.Activity;
import android.content.Context;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.arthurivanets.bottomsheets.BaseBottomSheet;
import com.arthurivanets.bottomsheets.config.BaseConfig;
import com.arthurivanets.bottomsheets.config.Config;

public class SimpleCustomBottomSheet extends BaseBottomSheet {

    private Activity hostActivity;
    private IchangeId ichangeId;

    public SimpleCustomBottomSheet(Activity hostActivity) {

        this(hostActivity, new Config.Builder(hostActivity).build());
        this.hostActivity=hostActivity;
        ichangeId= (IchangeId) hostActivity;
    }

    public SimpleCustomBottomSheet(@NonNull Activity hostActivity, @NonNull BaseConfig config) {
        super(hostActivity, config);
    }

    interface IchangeId{
        void setNewId(String str);
        void closeBottomSheet();
    }

    @NonNull
    @Override
    public final View onCreateSheetContentView(@NonNull Context context) {


       View view=  LayoutInflater.from(context).inflate(R.layout.view_simple_custom_bottom_sheet, this, false);
       ImageView imgClose=view.findViewById(R.id.imgClose);
        EditText edtId=view.findViewById(R.id.edtId);
        EditText edtPass=view.findViewById(R.id.edtPass);

        Button btnCancel=view.findViewById(R.id.btnCancel);
        Button btnChange=view.findViewById(R.id.btnChange);

        edtPass.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
        edtPass.setSelection(edtId.getText().length());

        edtId.setFilters(new InputFilter[]{new InputFilter.LengthFilter(2)});
        edtId.setSelection(edtId.getText().length());

        btnCancel.setOnClickListener(v -> {
            ichangeId.closeBottomSheet();
        });

        btnChange.setOnClickListener(v -> {
            String pass1 = edtPass.getText().toString().trim();
            if (pass1.equalsIgnoreCase(getResources().getString(R.string.change_id_password))) {
                String str = edtId.getText().toString().trim();
                ichangeId.setNewId(str);
                ichangeId.closeBottomSheet();
            } else {
                Toast.makeText(hostActivity, "Invalid Password!", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
