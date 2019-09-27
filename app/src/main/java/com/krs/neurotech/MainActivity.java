package com.krs.neurotech;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.krs.neurotech.databinding.ActivityMainBinding;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Objects;

import dmax.dialog.SpotsDialog;

import static com.krs.neurotech.Utils.appendZeros;
import static com.krs.neurotech.Utils.bytesToHex;
import static com.krs.neurotech.Utils.hexStringToByteArray;
import static java.lang.String.format;

public class MainActivity extends AppCompatActivity {

    private final int SERVERPORT = 1234;
    private final String SERVER_IP = "192.168.4.1";
    ActivityMainBinding binding = null;
    //int RESPONSE = -1;
    private ClientThread clientThread;
    private String TAG = MainActivity.class.getSimpleName();
    private String ID = "01";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences(getString(R.string.pref_key), MODE_PRIVATE);
        editor = sharedPreferences.edit();

        String pass = sharedPreferences.getString(getResources().getString(R.string.pass_key_sp), "");

        if (pass.isEmpty()) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setTitle(getResources().getString(R.string.neurotech));

        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.clear();
                editor.commit();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });


        binding.imgchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.custom_dialog);
                dialog.setTitle(getResources().getString(R.string.neurotech));
                dialog.setCancelable(false);
                final EditText edit_id = dialog.findViewById(R.id.custom_edit_id);
                edit_id.setFilters(new InputFilter[]{new InputFilter.LengthFilter(2)});
                edit_id.setSelection(edit_id.getText().length());

                final EditText edit_pass = dialog.findViewById(R.id.edit_pass);

                MaterialButton btnCancel = dialog.findViewById(R.id.btnCancel);
                MaterialButton btnOk = dialog.findViewById(R.id.btnOk);

                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String pass = edit_pass.getText().toString().trim();
                        if (pass.equalsIgnoreCase(getResources().getString(R.string.password))) {
                            String str = edit_id.getText().toString().trim();
                            setNewId(str);
                            dialog.dismiss();
                        } else {
                            Toast.makeText(MainActivity.this, "Invalid Password!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        binding.edtId.setFilters(new InputFilter[]{new InputFilter.LengthFilter(2)});
        binding.edtId.setSelection(binding.edtId.getText().length());
        binding.edtId.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    binding.llId.setBackgroundResource(R.drawable.my_custom_background);
                    String str_id = binding.edtId.getText().toString().trim();
                    if (str_id.length() != 0) {
                        setId(str_id);
                    }
                } else {
                    binding.llId.setBackgroundColor(getResources().getColor(R.color.lightGreen));
                }
            }
        });
        binding.tvId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_id = binding.edtId.getText().toString().trim();
                if (str_id.length() != 0) {
                    setId(str_id);
                }
            }
        });

        binding.edtDisplay1.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
        binding.edtDisplay1.setSelection(Objects.requireNonNull(binding.edtDisplay1.getText()).length());
        binding.edtDisplay1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    binding.llD1.setBackgroundResource(R.drawable.my_custom_background);
                    String display1 = binding.edtDisplay1.getText().toString().trim();
                    sendDisplayMsg(display1, (byte) 0x01);
                } else {
                    binding.llD1.setBackgroundColor(getResources().getColor(R.color.lightGreen));
                }
            }
        });
        binding.imgsend1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String display1 = binding.edtDisplay1.getText().toString().trim();
                sendDisplayMsg(display1, (byte) 0x01);
            }
        });


        binding.edtDisplay2.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
        binding.edtDisplay2.setSelection(Objects.requireNonNull(binding.edtDisplay2.getText()).length());
        binding.edtDisplay2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    binding.llD2.setBackgroundResource(R.drawable.my_custom_background);
                    String display2 = binding.edtDisplay2.getText().toString().trim();
                    sendDisplayMsg(display2, (byte) 0x02);
                } else {
                    binding.llD2.setBackgroundColor(getResources().getColor(R.color.lightGreen));
                }

            }
        });
        binding.imgsend2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String display2 = binding.edtDisplay2.getText().toString().trim();
                sendDisplayMsg(display2, (byte) 0x02);
            }
        });


        binding.edtDisplay3.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
        binding.edtDisplay3.setSelection(Objects.requireNonNull(binding.edtDisplay3.getText()).length());
        binding.edtDisplay3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    binding.llD3.setBackgroundResource(R.drawable.my_custom_background);
                    String display3 = binding.edtDisplay3.getText().toString().trim();
                    sendDisplayMsg(display3, (byte) 0x03);
                } else {
                    binding.llD3.setBackgroundColor(getResources().getColor(R.color.lightGreen));
                }
            }
        });
        binding.imgsend3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String display3 = binding.edtDisplay3.getText().toString().trim();
                sendDisplayMsg(display3, (byte) 0x03);
            }
        });


        binding.edtDisplay4.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
        binding.edtDisplay4.setSelection(Objects.requireNonNull(binding.edtDisplay4.getText()).length());
        binding.edtDisplay4.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    binding.llD4.setBackgroundResource(R.drawable.my_custom_background);
                    String display4 = binding.edtDisplay4.getText().toString().trim();
                    sendDisplayMsg(display4, (byte) 0x04);
                } else {
                    binding.llD4.setBackgroundColor(getResources().getColor(R.color.lightGreen));
                }
            }
        });
        binding.imgsend4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String display4 = binding.edtDisplay4.getText().toString().trim();
                sendDisplayMsg(display4, (byte) 0x04);
            }
        });


        binding.edtDisplay5.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
        binding.edtDisplay5.setSelection(Objects.requireNonNull(binding.edtDisplay5.getText()).length());
        binding.edtDisplay5.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    binding.llD5.setBackgroundResource(R.drawable.my_custom_background);
                    String display5 = binding.edtDisplay5.getText().toString().trim();
                    sendDisplayMsg(display5, (byte) 0x05);
                    hideKeyboard(binding.edtDisplay5);
                } else {
                    binding.llD5.setBackgroundColor(getResources().getColor(R.color.lightGreen));
                }
            }
        });
        binding.imgsend5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String display5 = binding.edtDisplay5.getText().toString().trim();
                sendDisplayMsg(display5, (byte) 0x05);
            }
        });


        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        binding.btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!binding.btnConnect.getText().toString().toLowerCase().contains("dis")) {
                    Connect();
                } else {
                    Disconnect();
                }
            }
        });
        Connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Disconnect();
    }

    private void Disconnect() {
        if (binding.btnConnect.getText().toString().toLowerCase().contains("dis")) {
            if (clientThread.socket != null) {
                try {
                    Log.i("INFO", "closing the socket");
                    clientThread.socket.close();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.btnConnect.setText(getResources().getString(R.string.connect));
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
            binding.btnConnect.setText(getResources().getString(R.string.disconnect));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_CANCELED) {
            Connect();
        }
    }

    private void hideKeyboard(View view) {
        InputMethodManager manager = (InputMethodManager) view.getContext()
                .getSystemService(INPUT_METHOD_SERVICE);
        if (manager != null)
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
            Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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
            }, 10000);
        }
    }

    private void setNewId(final String str_id) {

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
                        setId(str_id);
                    }
                }, 10000);
            }
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void sendDisplayMsg(String display, byte num) {
        try {
            if (!display.isEmpty()) {
                display = display.replace(".", "");
                display = Integer.toString(Integer.valueOf(display), 16);
                display = display.toUpperCase();
                String result = appendZeros(display, 4);

                byte[] msg1 = hexStringToByteArray(appendZeros(ID, 2));
                byte fcode = 0x06;
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
                        binding.btnConnect.setText(getResources().getString(R.string.connect));
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
