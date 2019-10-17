package com.krs.neurotech;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.arthurivanets.bottomsheets.BottomSheet;
import com.google.android.material.snackbar.Snackbar;
import com.krs.neurotech.databinding.ActivityHomeBinding;

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


public class HomeActivity extends Activity {

    private final int SERVERPORT = 1234;
    private final String SERVER_IP = "192.168.4.1";
    ActivityHomeBinding binding = null;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ClientThread clientThread;
    private String TAG = HomeActivity.class.getSimpleName();
    private String ID = "01";
    private boolean isConnected = false;
    private BottomSheet bottomSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);
        setTitle("");

        sharedPreferences = getSharedPreferences(getString(R.string.pref_key), MODE_PRIVATE);
        editor = sharedPreferences.edit();

        String pass = sharedPreferences.getString(getResources().getString(R.string.pass_key_sp), "");

        if (pass.isEmpty()) {
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        binding.llLogout.setOnClickListener(v -> {
            editor.clear();
            editor.commit();
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
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

        binding.edtId.setFilters(new InputFilter[]{new InputFilter.LengthFilter(2)});
        binding.edtId.setSelection(binding.edtId.getText().length());
        binding.btnGo.setOnClickListener(v -> {
            binding.edtId.setTextColor(getResources().getColor(android.R.color.black));
            String str_id = binding.edtId.getText().toString().trim();
            if (str_id.length() != 0) {
                setId(str_id);
            }
        });

        Connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Disconnect();
    }

    private void showCustomBottomSheet() {
        bottomSheet = new SimpleCustomBottomSheet(this);
        bottomSheet.show();
    }

    private void Disconnect() {
        if (isConnected) {
            if (clientThread.socket != null) {
                try {
                    Log.i("INFO", "closing the socket");
                    clientThread.socket.close();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.imgWifi.setBackground(getResources().getDrawable(R.drawable.no_wifi));
                            binding.tvNet.setText("Wifi");
                            isConnected = false;
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
        if (Utils.checkWifiOnAndConnected(this)) {
            clientThread = null;
            Thread thread;
            clientThread = new ClientThread();
            thread = new Thread(clientThread);
            thread.start();
            binding.imgWifi.setBackground(getResources().getDrawable(R.drawable.wifi));
            String ssid = Utils.getSSID(this);
            binding.tvNet.setText(ssid);
            isConnected = true;
        } else {
            Snackbar snackbar = Snackbar
                    .make(binding.llParent, "Connect your Wifi First...", Snackbar.LENGTH_LONG)
                    .setAction("CONNECT", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), 0);
                        }
                    });
            snackbar.show();
        }
    }


    private void setId(String str_id) {

        try {
            str_id = Integer.toString(Integer.valueOf(str_id), 16);
            str_id = str_id.toUpperCase();
            String result_id = appendZeros(str_id, 2);

            byte fcode = 0x03;
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
                // RESPONSE = 1;
            }
        } catch (Exception e) {
            Toast.makeText(HomeActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
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

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                outputStream.write(msg1);
                outputStream.write(msg2);
                outputStream.write(msg3);

                byte[] msg = outputStream.toByteArray();

                if (null != clientThread) {
                    clientThread.sendMessage(msg);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                Log.e(TAG, "server ip: " + SERVER_IP);

                while ((read = is.read(buffer)) != -1) {
                    Log.e(TAG, "message from server: " + bytesToHex(buffer));
                    String result = bytesToHex(buffer);
                    Log.e(TAG, "Result: " + result);
                    Log.e(TAG, "ID: " + ID);
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

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                binding.edtDisplay1.setText(MessageFormat.format("{0}", d1));
                                binding.edtDisplay2.setText(MessageFormat.format("{0}", d2));
                                binding.edtDisplay3.setText(MessageFormat.format("{0}", d3));
                                binding.edtDisplay4.setText(MessageFormat.format("{0}", d4));
                                binding.edtDisplay5.setText(MessageFormat.format("{0}", d5));
                            }
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.imgWifi.setBackground(getResources().getDrawable(R.drawable.no_wifi));
                        binding.tvNet.setText("Wifi");
                        isConnected = false;
                    }
                });
                Log.e(TAG, "Exception:");
            }
        }


        void sendMessage(final byte[] message) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (null != socket) {
                            Log.e(TAG, "sendMessage: " + Arrays.toString(message));
                            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                            out.write(message);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Exception: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
