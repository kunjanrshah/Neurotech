package com.krs.neurotech;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.arthurivanets.bottomsheets.BottomSheet;
import com.google.android.material.snackbar.Snackbar;
import com.krs.neurotech.databinding.ActivityHomepageBinding;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.Arrays;

import dmax.dialog.SpotsDialog;

import static com.krs.neurotech.Utils.appendZeros;
import static com.krs.neurotech.Utils.bytesToHex;
import static com.krs.neurotech.Utils.hexStringToByteArray;
import static java.lang.String.format;


public class HomeActivity extends Activity implements SimpleCustomBottomSheet.IchangeId, KeyboardVisibilityListener {

    private final int SERVERPORT = 1234;
    private final String SERVER_IP = "192.168.4.1";
    ActivityHomepageBinding binding;
    boolean isOnTextChanged1 = false;
    boolean isOnTextChanged2 = false;
    boolean isOnTextChanged3 = false;
    boolean isOnTextChanged4 = false;
    boolean isOnTextChanged5 = false;
    //int disConnectCount=0;
    int count1 = 0;
    int count2 = 0;
    int count3 = 0;
    int count4 = 0;
    int count5 = 0;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ClientThread clientThread;
    private String TAG = HomeActivity.class.getSimpleName();
    private String ID = "01";
    private boolean isConnected = false;
    private BottomSheet bottomSheet;
    private int requestFocus = 0;
    private int requestWrite = 0;
    private boolean writeID = false;
    private String isDuplicate1 = "";
    private String isDuplicate2 = "";
    private String isDuplicate3 = "";
    private String isDuplicate4 = "";
    private String isDuplicate5 = "";
    //ByteArrayOutputStream outputStream1=null;
    private KeyboardVisibilityListener keyboardVisibilityListener;
    private boolean isKeyboardVisible = false;
    private int mInterval = 2000;
    private Handler mHandler;
    Runnable mStatusChecker1 = new Runnable() {
        @Override
        public void run() {
            String display = binding.edtDisplay1.getText().toString();
            try {
                if(count1 < 5) {
                    count1++;
                    writeToDisplay1();
                    mHandler.postDelayed(mStatusChecker1, mInterval);
                } else {
                    stopRepeatingTask1();
                    showSnackBar(display + " not updated!");
                }
            } catch (Exception e) {
                showSnackBar("Error while updating" + display + "!");
            }
        }
    };
    Runnable mStatusChecker2 = new Runnable() {
        @Override
        public void run() {
            String display = binding.edtDisplay2.getText().toString();
            try {
                if(count2 < 5) {
                    count2++;
                    writeToDisplay2();
                    mHandler.postDelayed(mStatusChecker2, mInterval);
                } else {
                    stopRepeatingTask2();
                    showSnackBar(display + " not updated!");
                }
            } catch (Exception e) {
                showSnackBar("Error while updating" + display + "!");
            }
        }
    };
    Runnable mStatusChecker3 = new Runnable() {
        @Override
        public void run() {
            String display = binding.edtDisplay3.getText().toString();
            try {
                if(count3 < 5) {
                    count3++;
                    writeToDisplay3();
                    mHandler.postDelayed(mStatusChecker3, mInterval);
                } else {
                    stopRepeatingTask3();
                    showSnackBar(display + " not updated!");
                }
            } catch (Exception e) {
                showSnackBar("Error while updating" + display + "!");
            }
        }
    };
    Runnable mStatusChecker4 = new Runnable() {
        @Override
        public void run() {
            String display = binding.edtDisplay4.getText().toString();
            try {
                if(count4 < 5) {
                    count4++;
                    writeToDisplay4();
                    mHandler.postDelayed(mStatusChecker4, mInterval);
                } else {
                    stopRepeatingTask4();
                    showSnackBar(display + " not updated!");
                }
            } catch (Exception e) {
                showSnackBar("Error while updating" + display + "!");
            }
        }
    };
    Runnable mStatusChecker5 = new Runnable() {
        @Override
        public void run() {
            String display = binding.edtDisplay5.getText().toString();
            try {
                if(count5 < 5) {
                    count5++;
                    writeToDisplay5();
                    mHandler.postDelayed(mStatusChecker5, mInterval);
                } else {
                    stopRepeatingTask5();
                    showSnackBar(display + " not updated!");
                }
            } catch (Exception e) {
                showSnackBar("Error while updating" + display + "!");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_homepage);
        setTitle("");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (ContextCompat.checkSelfPermission(
                HomeActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 555);
        }

        sharedPreferences = getSharedPreferences(getString(R.string.pref_key), MODE_PRIVATE);
        editor = sharedPreferences.edit();
        //outputStream1 = new ByteArrayOutputStream();
        String pass = sharedPreferences.getString(getResources().getString(R.string.pass_key_sp), "");

        if (pass.isEmpty()) {
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        mHandler = new Handler();
        keyboardVisibilityListener = this;
        Utils.setKeyboardVisibilityListener(this, keyboardVisibilityListener);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_homepage);
        binding.imgWifi.setBackground(null);
        binding.imgWifi.setBackground(getResources().getDrawable(R.drawable.no_wifi));

        binding.llLogout.setOnClickListener(v -> {
            String str_id = binding.edtId.getText().toString().trim();
            if (!str_id.isEmpty()) {
                int id1 = Integer.parseInt(str_id);
                if (id1 > 0) {
                    setId("" + id1, (byte) 0x04);
                }
            }
            editor.clear();
            editor.apply();
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        binding.llChangeId.setOnClickListener(v -> {
            showCustomBottomSheet();
        });

        binding.llWifi.setOnClickListener(v -> {
            if (isConnected) {
                Disconnect();
            } else {
                Connect();
            }
        });

        binding.edtDisplay1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isOnTextChanged1 = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (getCurrentFocus() == binding.edtDisplay1) {

                    if (s.length() == 5 && isOnTextChanged1 && !writeID) {
                        if (!isDuplicate1.equals(s.toString())) {
                            Log.e(TAG, "display1: " + s);
                            isDuplicate1 = s.toString();
                            isOnTextChanged1 = false;
                            startRepeatingTask1();
                        }
                    }
                }
            }
        });

        binding.edtDisplay2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isOnTextChanged2 = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() == 5 && isOnTextChanged2 && !writeID) {
                    if (!isDuplicate2.equals(s.toString())) {
                        Log.e(TAG, "display2: " + s);
                        isDuplicate2 = s.toString();
                        isOnTextChanged2 = false;
                        startRepeatingTask2();
                    }

                }
            }
        });

        binding.edtDisplay3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isOnTextChanged3 = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() == 5 && isOnTextChanged3 && !writeID) {
                    if (!isDuplicate3.equals(s.toString())) {
                        Log.e(TAG, "display3: " + s);
                        isDuplicate3 = s.toString();
                        isOnTextChanged3 = false;
                        startRepeatingTask3();
                    }

                }
            }
        });

        binding.edtDisplay4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isOnTextChanged4 = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() == 5 && isOnTextChanged4 && !writeID) {
                    if (!isDuplicate4.equals(s.toString())) {
                        Log.e(TAG, "display4: " + s);
                        isDuplicate4 = s.toString();
                        isOnTextChanged4 = false;
                        startRepeatingTask4();
                    }

                }
            }
        });

        binding.edtDisplay5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isOnTextChanged5 = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() == 5 && isOnTextChanged5 && !writeID) {
                    if (!isDuplicate5.equals(s.toString())) {
                        Log.e(TAG, "display5: " + s);
                        isDuplicate5 = s.toString();
                        isOnTextChanged5 = false;
                        startRepeatingTask5();
                    }

                }
            }
        });

        binding.edtDisplay1.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                focusToDisplay1(true);
            }
        });

        binding.edtDisplay2.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                focusToDisplay2(true);
            }
        });

        binding.edtDisplay3.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                focusToDisplay3(true);
            }
        });

        binding.edtDisplay4.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                focusToDisplay4(true);
            }
        });

        binding.edtDisplay5.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                focusToDisplay5(true);
            }
        });

        binding.llDisplay1.setOnClickListener(v -> {
            focusToDisplay1(false);
        });

        binding.llDisplay2.setOnClickListener(v -> {
            focusToDisplay2(false);
        });

        binding.llDisplay3.setOnClickListener(v -> {
            focusToDisplay3(false);
        });

        binding.llDisplay4.setOnClickListener(v -> {
            focusToDisplay4(false);
        });

        binding.llDisplay5.setOnClickListener(v -> {
            focusToDisplay5(false);
        });

        binding.edtId.setFilters(new InputFilter[]{new InputFilter.LengthFilter(2)});
        binding.edtId.setSelection(binding.edtId.getText().length());
        binding.btnGo.setOnClickListener(v -> {
            binding.edtId.setTextColor(getResources().getColor(android.R.color.black));
            String str_id = binding.edtId.getText().toString().trim();
            if (!str_id.isEmpty()) {
                int id1 = Integer.parseInt(str_id);
                if (id1 > 0) {
                    String id2 = sharedPreferences.getString("id", "");
                    if (!id2.isEmpty()) {
                        setId("" + id2, (byte) 0x04);
                    }
                    editor.putString("id", str_id);
                    editor.commit();
                    AlertChangeId(id1);
                }
            }
        });

        binding.imgPrev.setOnClickListener(v -> {

            String id = binding.edtId.getText().toString().trim();
            if (!id.isEmpty()) {
                int id1 = Integer.parseInt(id);
                if (id1 > 0) {
                    setId("" + id1, (byte) 0x04);
                    id1--;
                    AlertChangeId(id1);
                }
            }
        });

        binding.imgNext.setOnClickListener(v -> {

            String id = binding.edtId.getText().toString().trim();
            if (!id.isEmpty()) {
                int id1 = Integer.parseInt(id);
                if (id1 > 0) {
                    setId("" + id1, (byte) 0x04);
                    id1++;
                    AlertChangeId(id1);
                }
            }

        });

        Connect();
    }

    private void AlertChangeId(final int id) {
        final AlertDialog alertDialog = new SpotsDialog.Builder().setContext(this).build();
        alertDialog.setCancelable(false);
        alertDialog.setTitle(getResources().getString(R.string.neurotech));
        alertDialog.setMessage(getResources().getString(R.string.loading));
        alertDialog.show();
        new Handler().postDelayed(() -> {
            alertDialog.dismiss();
            binding.edtId.setText("" + id);
            setId("" + id, (byte) 0x03);
        }, 2000);
    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void showSoftKeyboard(EditText editText) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(editText.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Disconnect();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showCustomBottomSheet() {
        hideSoftKeyboard();
        bottomSheet = new SimpleCustomBottomSheet(this);
        bottomSheet.show();
    }

    private void Disconnect() {
        if (isConnected) {
            if (clientThread.socket != null) {
                try {

                    Log.e("INFO", "closing the socket");
                    clientThread.socket.close();
                    runOnUiThread(() -> {
                        hideSoftKeyboard();
                        binding.imgWifi.setBackground(null);
                        binding.imgWifi.setBackground(getResources().getDrawable(R.drawable.no_wifi));
                        binding.tvNet.setText(getResources().getString(R.string.wifi));

                        final Snackbar snackBar = Snackbar.make(binding.llParent, getResources().getString(R.string.disconnected_because), Snackbar.LENGTH_LONG);
                        snackBar.setAction("OK", v -> {
                            snackBar.dismiss();
                        });
                        snackBar.show();
                        isConnected = false;
                        if (mStatusChecker1 != null) {
                            mHandler.removeCallbacks(mStatusChecker1);
                        }
                        if (mStatusChecker2 != null) {
                            mHandler.removeCallbacks(mStatusChecker2);
                        }
                        if (mStatusChecker3 != null) {
                            mHandler.removeCallbacks(mStatusChecker3);
                        }
                        if (mStatusChecker4 != null) {
                            mHandler.removeCallbacks(mStatusChecker4);
                        }
                        if (mStatusChecker5 != null) {
                            mHandler.removeCallbacks(mStatusChecker5);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (clientThread.is != null) {
                try {
                    clientThread.is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (null != clientThread) {
            clientThread = null;
        }
    }

    private void Connect() {
        if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (Utils.checkWifiOnAndConnected(this)) {
                String ssid = Utils.getCurrentSsid(this);
                if (ssid.toLowerCase().contains("neurotech")) {
                    clientThread = null;
                    Thread thread;
                    clientThread = new ClientThread();
                    thread = new Thread(clientThread);
                    thread.start();
                    binding.imgWifi.setBackground(null);
                    binding.imgWifi.setBackground(getResources().getDrawable(R.drawable.wifi));

                    ssid = ssid.replaceAll("^\"|\"$", "");
                    binding.tvNet.setText(ssid);
                    isConnected = true;
                }
            } else {
                Snackbar snackbar = Snackbar
                        .make(binding.llParent, "Connect your Wifi Please", Snackbar.LENGTH_LONG)
                        .setAction("SETTING", view -> startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), 0));
                snackbar.show();
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 555);
        }
    }

    private void setId(String str_id, byte fcode) {

        try {
            str_id = Integer.toString(Integer.valueOf(str_id), 16);
            str_id = str_id.toUpperCase();
            String result_id = appendZeros(str_id, 2);

            //   byte fcode = 0x03;
            byte eadd2 = 0x06;

            byte[] msg1 = hexStringToByteArray(result_id);

            byte[] msg2 = new byte[5];
            msg2[0] = fcode;
            msg2[4] = eadd2;

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(msg1);
            outputStream.write(msg2);

            byte[] msg = outputStream.toByteArray();

            ID = result_id;
            if (null != clientThread) {
                clientThread.sendMessage(msg);
                writeID = true;
            }
        } catch (Exception e) {
            showSnackBar(getResources().getString(R.string.something_went_wrong));
            e.printStackTrace();
        }
        hideSoftKeyboard();
    }

    private void save() {
        byte fcode = 0x06;
        byte raw_id2 = 0x07;
        byte[] msg1 = hexStringToByteArray(appendZeros(ID, 2));
        byte[] msg2 = new byte[4];
        msg2[0] = fcode;
        msg2[2] = raw_id2;

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(msg1);
            outputStream.write(msg2);
            outputStream.write(msg1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] msg = outputStream.toByteArray();

        if (null != clientThread) {
            clientThread.sendMessage(msg);
            final AlertDialog alertDialog = new SpotsDialog.Builder().setContext(this).build();
            alertDialog.setCancelable(false);
            alertDialog.setTitle(getResources().getString(R.string.neurotech));
            alertDialog.setMessage(getResources().getString(R.string.loading));
            alertDialog.show();
            new Handler().postDelayed(new Runnable() {
                @SuppressLint("DefaultLocale")
                @Override
                public void run() {
                    alertDialog.dismiss();
                    String str = binding.edtId.getText().toString().trim();
                    if (!str.isEmpty()) {
                        int i = Integer.parseInt(str) + 1;
                        binding.edtId.setText(format("%d", i));
                    }
                }
            }, 2000);
        }
    }

    @Override
    public void setNewId(final String str_id) {

        try {
            String new_id = Integer.toString(Integer.valueOf(str_id), 16);
            new_id = new_id.toUpperCase();
            new_id = appendZeros(new_id, 2);

            byte fcode = 0x06;
            byte raw_id2 = 0x06;
            byte[] msg2 = new byte[4];
            msg2[0] = fcode;
            msg2[2] = raw_id2;

            byte[] msg1 = hexStringToByteArray(appendZeros(ID, 2));
            byte[] msg3 = hexStringToByteArray(new_id);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(msg1);
            outputStream.write(msg2);
            outputStream.write(msg3);
            byte[] msg = outputStream.toByteArray();

            if (null != clientThread) {
                clientThread.sendMessage(msg);
                ID = new_id;
                final AlertDialog alertDialog = new SpotsDialog.Builder().setContext(this).build();
                alertDialog.setCancelable(false);
                alertDialog.setTitle(getResources().getString(R.string.neurotech));
                alertDialog.setMessage(getResources().getString(R.string.loading));
                alertDialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        alertDialog.dismiss();
                        binding.edtId.setText(str_id);
                        setId(str_id, (byte) 0x03);
                    }
                }, 2000);
            }
        } catch (Exception e) {
            showSnackBar(getResources().getString(R.string.something_went_wrong));
            e.printStackTrace();
        }
    }

    @Override
    public void onKeyboardVisibilityChanged(boolean keyboardVisible) {
        isKeyboardVisible = keyboardVisible;
        Log.e(TAG, "keyboardVisible: " + keyboardVisible);
    }

    @Override
    public void closeBottomSheet() {
        bottomSheet.dismiss(true);
    }

    private void focusToDisplay1(boolean isFocus) {
        binding.edtDisplay1.setEnabled(true);
        binding.edtDisplay1.setClickable(true);
        isDuplicate1 = "";
        binding.edtDisplay1.requestFocus();
        writeID = false;
        binding.llDisplay1.setBackground(getResources().getDrawable(R.drawable.round_corner_light_blue));
        binding.llDisplay2.setBackground(getResources().getDrawable(R.drawable.round_corner_white));
        binding.llDisplay3.setBackground(getResources().getDrawable(R.drawable.round_corner_white));
        binding.llDisplay4.setBackground(getResources().getDrawable(R.drawable.round_corner_white));
        binding.llDisplay5.setBackground(getResources().getDrawable(R.drawable.round_corner_white));

        if (isFocus) {
            requestFocus = 1;
            requestWrite = 0;
            count1 = 0;
            final String display1 = binding.edtDisplay1.getText().toString().trim();
            sendDisplayMsg(display1, (byte) 0x09, (byte) 0x01);
        } else {
            if (!isKeyboardVisible) {
                runOnUiThread(() -> showSoftKeyboard(binding.edtDisplay1));
            }
        }

    }

    private void focusToDisplay2(boolean isFocus) {
        binding.edtDisplay2.setEnabled(true);
        binding.edtDisplay2.setClickable(true);
        isDuplicate2 = "";
        binding.edtDisplay2.requestFocus();
        writeID = false;
        binding.llDisplay1.setBackground(getResources().getDrawable(R.drawable.round_corner_white));
        binding.llDisplay2.setBackground(getResources().getDrawable(R.drawable.round_corner_light_blue));
        binding.llDisplay3.setBackground(getResources().getDrawable(R.drawable.round_corner_white));
        binding.llDisplay4.setBackground(getResources().getDrawable(R.drawable.round_corner_white));
        binding.llDisplay5.setBackground(getResources().getDrawable(R.drawable.round_corner_white));

        if (isFocus) {
            requestFocus = 2;
            requestWrite = 0;
            count2 = 0;
            final String display2 = binding.edtDisplay2.getText().toString().trim();
            sendDisplayMsg(display2, (byte) 0x09, (byte) 0x02);
        } else {
            if (!isKeyboardVisible) {
                runOnUiThread(() -> showSoftKeyboard(binding.edtDisplay2));
            }
        }
    }

    private void focusToDisplay3(boolean isFocus) {
        binding.edtDisplay3.setEnabled(true);
        binding.edtDisplay3.setClickable(true);
        isDuplicate3 = "";
        binding.edtDisplay3.requestFocus();
        writeID = false;
        binding.llDisplay1.setBackground(getResources().getDrawable(R.drawable.round_corner_white));
        binding.llDisplay2.setBackground(getResources().getDrawable(R.drawable.round_corner_white));
        binding.llDisplay3.setBackground(getResources().getDrawable(R.drawable.round_corner_light_blue));
        binding.llDisplay4.setBackground(getResources().getDrawable(R.drawable.round_corner_white));
        binding.llDisplay5.setBackground(getResources().getDrawable(R.drawable.round_corner_white));
        if (isFocus) {
            requestFocus = 3;
            requestWrite = 0;
            count3 = 0;
            final String display3 = binding.edtDisplay3.getText().toString().trim();
            sendDisplayMsg(display3, (byte) 0x09, (byte) 0x03);
        } else {
            if (!isKeyboardVisible) {
                runOnUiThread(() -> showSoftKeyboard(binding.edtDisplay3));
            }
        }
    }

    private void focusToDisplay4(boolean isFocus) {
        binding.edtDisplay4.setEnabled(true);
        binding.edtDisplay4.setClickable(true);
        isDuplicate4 = "";
        binding.edtDisplay4.requestFocus();
        writeID = false;
        binding.llDisplay1.setBackground(getResources().getDrawable(R.drawable.round_corner_white));
        binding.llDisplay2.setBackground(getResources().getDrawable(R.drawable.round_corner_white));
        binding.llDisplay3.setBackground(getResources().getDrawable(R.drawable.round_corner_white));
        binding.llDisplay4.setBackground(getResources().getDrawable(R.drawable.round_corner_light_blue));
        binding.llDisplay5.setBackground(getResources().getDrawable(R.drawable.round_corner_white));
        if (isFocus) {
            requestFocus = 4;
            count4 = 0;
            requestWrite = 0;
            final String display4 = binding.edtDisplay4.getText().toString().trim();
            sendDisplayMsg(display4, (byte) 0x09, (byte) 0x04);
        } else {
            if (!isKeyboardVisible) {
                runOnUiThread(() -> showSoftKeyboard(binding.edtDisplay4));
            }
        }
    }

    private void focusToDisplay5(boolean isFocus) {
        binding.edtDisplay5.setEnabled(true);
        binding.edtDisplay5.setClickable(true);
        isDuplicate5 = "";
        binding.edtDisplay5.requestFocus();
        writeID = false;
        binding.llDisplay1.setBackground(getResources().getDrawable(R.drawable.round_corner_white));
        binding.llDisplay2.setBackground(getResources().getDrawable(R.drawable.round_corner_white));
        binding.llDisplay3.setBackground(getResources().getDrawable(R.drawable.round_corner_white));
        binding.llDisplay4.setBackground(getResources().getDrawable(R.drawable.round_corner_white));
        binding.llDisplay5.setBackground(getResources().getDrawable(R.drawable.round_corner_light_blue));

        if (isFocus) {
            requestFocus = 5;
            requestWrite = 0;
            count5 = 0;
            final String display5 = binding.edtDisplay5.getText().toString().trim();
            sendDisplayMsg(display5, (byte) 0x09, (byte) 0x05);
        } else {
            if (!isKeyboardVisible) {
                runOnUiThread(() -> showSoftKeyboard(binding.edtDisplay5));
            }
        }
    }

    private void writeToDisplay1() {
        requestWrite = 1;
        requestFocus = 0;
        String display1 = binding.edtDisplay1.getText().toString().trim();
        sendDisplayMsg(display1, (byte) 0x06, (byte) 0x01);
    }

    private void writeToDisplay2() {
        requestWrite = 2;
        requestFocus = 0;
        String display2 = binding.edtDisplay2.getText().toString().trim();
        sendDisplayMsg(display2, (byte) 0x06, (byte) 0x02);
    }

    private void writeToDisplay3() {
        requestWrite = 3;
        requestFocus = 0;
        String display3 = binding.edtDisplay3.getText().toString().trim();
        sendDisplayMsg(display3, (byte) 0x06, (byte) 0x03);
    }

    private void writeToDisplay4() {
        requestWrite = 4;
        requestFocus = 0;
        String display4 = binding.edtDisplay4.getText().toString().trim();
        sendDisplayMsg(display4, (byte) 0x06, (byte) 0x04);
    }

    private void writeToDisplay5() {
        requestWrite = 5;
        requestFocus = 0;
        String display5 = binding.edtDisplay5.getText().toString().trim();
        sendDisplayMsg(display5, (byte) 0x06, (byte) 0x05);
    }

    void startRepeatingTask1() {
        mStatusChecker1.run();
    }

    void stopRepeatingTask1() {
        count1 = 0;
        mHandler.removeCallbacks(mStatusChecker1);
        hideSoftKeyboard();
    }

    void startRepeatingTask2() {
        mStatusChecker2.run();
    }

    void stopRepeatingTask2() {
        count2 = 0;
        mHandler.removeCallbacks(mStatusChecker2);
    }

    void startRepeatingTask3() {
        mStatusChecker3.run();
    }

    void stopRepeatingTask3() {
        count3 = 0;
        mHandler.removeCallbacks(mStatusChecker3);
    }

    void startRepeatingTask4() {
        mStatusChecker4.run();
    }

    void stopRepeatingTask4() {
        count4 = 0;
        mHandler.removeCallbacks(mStatusChecker4);
    }

    void startRepeatingTask5() {
        mStatusChecker5.run();
    }

    void stopRepeatingTask5() {
        count5 = 0;
        mHandler.removeCallbacks(mStatusChecker5);
    }

    private void sendDisplayMsg(String display, byte fcode, byte num) {
        try {
            if (!display.isEmpty()) {
                display = display.replace(".", "");
                display = Integer.toString(Integer.valueOf(display), 16);
                display = display.toUpperCase();
                String result = appendZeros(display, 4);

                byte[] msg1 = hexStringToByteArray(appendZeros(ID, 2));
                byte[] msg2 = new byte[3];
                msg2[0] = fcode;
                msg2[2] = num;
                byte[] msg3 = hexStringToByteArray(result);

               /* String num1 = result.substring(0, 2);
                String num2 = result.substring(2);
                int sum = Integer.parseInt(num1, 16) + Integer.parseInt(num2, 16);*/

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                outputStream.write(msg1);
                outputStream.write(msg2);
                outputStream.write(msg3);
                /*byte[] msg4 = hexStringToByteArray(Integer.toHexString(sum));
                outputStream.write(msg4);*/

                byte[] msg = outputStream.toByteArray();
                if (null != clientThread) {
                    clientThread.sendMessage(msg);
                } else {
                    showSnackBar("Please connect again!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showSnackBar(String msg){
        hideSoftKeyboard();
        final Snackbar snackBar = Snackbar.make(binding.llParent, msg, Snackbar.LENGTH_LONG);
        snackBar.setAction("OK", v -> {
            snackBar.dismiss();
        });
        snackBar.show();
    }

    private class ClientThread implements Runnable {

        private Socket socket;
        private DataInputStream is;

        @Override
        public void run() {

            try {
                InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
                socket = new Socket(serverAddr, SERVERPORT);
                is = new DataInputStream(socket.getInputStream());
                byte[] buffer = new byte[1024];
                int read;
                String ack_msg = "";
                while ((read = is.read(buffer)) != -1) {

                    ack_msg = new String(buffer, 0, read);
                    Log.e(TAG, "ack from server: " + ack_msg);
                    Log.e(TAG, "message from server: " + bytesToHex(buffer));

                    String result = bytesToHex(buffer);
                    String id = result.substring(0, 2);
                    String display1 = "", display2 = "", display3 = "", display4 = "", display5 = "";
                    if (ID.equalsIgnoreCase(id)) {
                        display1 = result.substring(6, 10);
                        final int d1 = Integer.parseInt(display1, 16);
                        display2 = result.substring(10, 14);
                        final int d2 = Integer.parseInt(display2, 16);
                        display3 = result.substring(14, 18);
                        final int d3 = Integer.parseInt(display3, 16);
                        display4 = result.substring(18, 22);
                        final int d4 = Integer.parseInt(display4, 16);
                        display5 = result.substring(22, 26);
                        final int d5 = Integer.parseInt(display5, 16);

                        runOnUiThread(() -> {
                            binding.edtDisplay1.setText(MessageFormat.format("{0}", d1));
                            binding.edtDisplay2.setText(MessageFormat.format("{0}", d2));
                            binding.edtDisplay3.setText(MessageFormat.format("{0}", d3));
                            binding.edtDisplay4.setText(MessageFormat.format("{0}", d4));
                            binding.edtDisplay5.setText(MessageFormat.format("{0}", d5));
                        });
                    }

                    Log.e(TAG, "read command: requestWrite: " + requestWrite + " requestFocus: " + requestFocus + " ack_msg: " + ack_msg + " writeID: " + writeID);
                    try {
                        if (!writeID) {
                            if (requestWrite != 0 && requestFocus == 0) {
                                if (!ack_msg.isEmpty()) {
                                    StringBuffer c = new StringBuffer(ack_msg);
                                    ack_msg = c.reverse().toString();
                                }
                                if (requestWrite == 1) {
                                    String d1 = binding.edtDisplay1.getText().toString().replace(".", "");
                                    if (ack_msg.equals(d1)) {
                                        requestWrite = 0;
                                        stopRepeatingTask1();
                                        runOnUiThread(() -> {
                                            binding.edtDisplay1.setEnabled(false);
                                            binding.edtDisplay1.setClickable(false);
                                            binding.llDisplay1.setBackground(getResources().getDrawable(R.drawable.round_corner_white));
                                        });
                                    }
                                }

                                if (requestWrite == 2) {
                                    String d2 = binding.edtDisplay2.getText().toString().replace(".", "");
                                    if (ack_msg.equals(d2)) {
                                        requestWrite = 0;
                                        stopRepeatingTask2();
                                        runOnUiThread(() -> {
                                            binding.edtDisplay2.setEnabled(false);
                                            binding.edtDisplay2.setClickable(false);
                                            binding.llDisplay2.setBackground(getResources().getDrawable(R.drawable.round_corner_white));
                                        });
                                    }
                                }
                                if (requestWrite == 3) {
                                    String d3 = binding.edtDisplay3.getText().toString().replace(".", "");
                                    if (ack_msg.equals(d3)) {
                                        requestWrite = 0;
                                        stopRepeatingTask3();
                                        runOnUiThread(() -> {
                                            binding.edtDisplay3.setEnabled(false);
                                            binding.edtDisplay3.setClickable(false);
                                            binding.llDisplay3.setBackground(getResources().getDrawable(R.drawable.round_corner_white));
                                        });
                                    }
                                }
                                if (requestWrite == 4) {
                                    String d4 = binding.edtDisplay4.getText().toString().replace(".", "");
                                    if (ack_msg.equals(d4)) {
                                        requestWrite = 0;
                                        stopRepeatingTask4();
                                        runOnUiThread(() -> {
                                            binding.edtDisplay4.setEnabled(false);
                                            binding.edtDisplay4.setClickable(false);
                                            binding.llDisplay4.setBackground(getResources().getDrawable(R.drawable.round_corner_white));
                                        });
                                    }
                                }
                                if (requestWrite == 5) {
                                    String d5 = binding.edtDisplay5.getText().toString().replace(".", "");
                                    if (ack_msg.equals(d5)) {
                                        requestWrite = 0;
                                        stopRepeatingTask5();
                                        runOnUiThread(() -> {
                                            binding.edtDisplay5.setEnabled(false);
                                            binding.edtDisplay5.setClickable(false);
                                            binding.llDisplay5.setBackground(getResources().getDrawable(R.drawable.round_corner_white));
                                        });
                                    }
                                }
                            }

                            if (!ack_msg.equals(String.valueOf(requestFocus)) && requestWrite == 0 && requestFocus != 0) {
                                Log.e(TAG, "focus command: requestWrite: " + requestWrite + " requestFocus: " + requestFocus);
                                if (requestFocus == 1) {
                                    requestFocus = 0;
                                    focusToDisplay1(true);
                                } else if (requestFocus == 2) {
                                    requestFocus = 0;
                                    focusToDisplay2(true);
                                } else if (requestFocus == 3) {
                                    requestFocus = 0;
                                    focusToDisplay3(true);
                                } else if (requestFocus == 4) {
                                    requestFocus = 0;
                                    focusToDisplay4(true);
                                } else if (requestFocus == 5) {
                                    requestFocus = 0;
                                    focusToDisplay5(true);
                                }
                            }
                        }
                    } catch (Exception e) {
                        showSnackBar(getResources().getString(R.string.something_went_wrong));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    hideSoftKeyboard();
                    final Snackbar snackBar = Snackbar.make(binding.llParent, getResources().getString(R.string.disconnected_because), Snackbar.LENGTH_LONG);
                    snackBar.setAction("OK", v -> {
                        snackBar.dismiss();
                    });
                    snackBar.show();
                    binding.imgWifi.setBackground(null);
                    binding.imgWifi.setBackground(getResources().getDrawable(R.drawable.no_wifi));
                    binding.tvNet.setText(getResources().getString(R.string.wifi));
                    isConnected = false;
                });
                Log.e(TAG, "Exception:" + e.getMessage());
            }
        }


        void sendMessage(final byte[] message) {
            new Thread(() -> {
                try {
                    if (null != socket) {
                        Log.e(TAG, "sendMessage: " + Arrays.toString(message));
                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        out.write(message);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Exception: " + e.getMessage());
                    e.printStackTrace();
                    //  Disconnect();
                }
            }).start();
        }
    }
}
