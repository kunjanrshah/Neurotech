package com.krs.neurotech;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.krs.neurotech.databinding.ActivityMainBinding;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    public static final int SERVERPORT = 1234;
    public static String SERVER_IP = "192.168.4.1";//192.168.43.95
    ActivityMainBinding binding = null;
    private ClientThread clientThread;
    private Thread thread;
    private String TAG = MainActivity.class.getSimpleName();
    private String ID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setTitle("NeuroTech Computer Systems");

        binding.imgchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.custom_dialog);
                dialog.setTitle("NeuroTech");
                dialog.setCancelable(false);
                final EditText edit_id = dialog.findViewById(R.id.custom_edit_id);
                edit_id.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
                edit_id.setSelection(edit_id.getText().length());
                MaterialButton btnCancel = (MaterialButton) dialog.findViewById(R.id.btnCancel);
                MaterialButton btnOk = (MaterialButton) dialog.findViewById(R.id.btnOk);


                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String str = edit_id.getText().toString().trim();
                        setId(str);
                        dialog.dismiss();

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

        binding.edtId.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
        binding.edtId.setSelection(binding.edtId.getText().length());
        binding.edtId.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String str_id = binding.edtId.getText().toString().trim();
                    if (str_id.length() != 0) {
                        setId(str_id);
                    }
                }
            }
        });
        binding.edtId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() != 0) {
                    setId(s.toString());
                }
            }
        });


        binding.edtDisplay1.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
        binding.edtDisplay1.setSelection(binding.edtDisplay1.getText().length());
        binding.edtDisplay1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String display1 = binding.edtDisplay1.getText().toString().trim();
                    sendDisplayMsg(display1, "01");
                }
            }
        });
        binding.edtDisplay1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() != 0) {
                    sendDisplayMsg(s.toString(), "01");
                }
            }
        });


        binding.edtDisplay2.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
        binding.edtDisplay2.setSelection(binding.edtDisplay2.getText().length());
        binding.edtDisplay2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String display2 = binding.edtDisplay2.getText().toString().trim();
                    sendDisplayMsg(display2, "02");
                }

            }
        });
        binding.edtDisplay2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() != 0) {
                    sendDisplayMsg(s.toString(), "02");
                }
            }
        });


        binding.edtDisplay3.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
        binding.edtDisplay3.setSelection(binding.edtDisplay3.getText().length());
        binding.edtDisplay3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String display3 = binding.edtDisplay3.getText().toString().trim();
                    sendDisplayMsg(display3, "03");
                }
            }
        });
        binding.edtDisplay3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() != 0) {
                    sendDisplayMsg(s.toString(), "03");
                }
            }
        });


        binding.edtDisplay4.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
        binding.edtDisplay4.setSelection(binding.edtDisplay4.getText().length());
        binding.edtDisplay4.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String display4 = binding.edtDisplay4.getText().toString().trim();
                    sendDisplayMsg(display4, "04");
                }
            }
        });
        binding.edtDisplay4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() != 0) {
                    sendDisplayMsg(s.toString(), "04");
                }
            }
        });


        binding.edtDisplay5.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
        binding.edtDisplay5.setSelection(binding.edtDisplay5.getText().length());
        binding.edtDisplay5.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String display5 = binding.edtDisplay5.getText().toString().trim();
                    sendDisplayMsg(display5, "05");
                    hideKeyboard(binding.edtDisplay5);
                }
            }
        });
        binding.edtDisplay5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() != 0) {
                    sendDisplayMsg(s.toString(), "05");
                }
            }
        });


        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != clientThread) {
                    clientThread.sendMessage("");
                }
            }
        });

        binding.btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (Utils.checkWifiOnAndConnected(MainActivity.this)) {
                        clientThread = new ClientThread();
                        thread = new Thread(clientThread);
                        thread.start();
                        binding.btnConnect.setEnabled(false);
                    } else {
                        Snackbar snackbar = Snackbar
                                .make(binding.llParent, "Connect your Wifi First...", Snackbar.LENGTH_LONG)
                                .setAction("CONNECT", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                    }
                                });
                        snackbar.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        if (Utils.checkWifiOnAndConnected(this)) {
            clientThread = new ClientThread();
            thread = new Thread(clientThread);
            thread.start();
            binding.btnConnect.setEnabled(false);
        } else {
            Snackbar snackbar = Snackbar
                    .make(binding.llParent, "Connect your Wifi First...", Snackbar.LENGTH_LONG)
                    .setAction("CONNECT", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    });
            snackbar.show();
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
            if (str_id.length() == 1) {
                str_id = "0" + str_id;
            }
            String fcode = " 03";
            String sadd = " 00 00";
            String eadd = " 00 06";
            String str = str_id + fcode + sadd + eadd;
            ID = str_id;
            if (null != clientThread) {
                clientThread.sendMessage(str);
            }
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void sendDisplayMsg(String display, String num) {
        try {
            if (!display.isEmpty()) {
                display = display.replaceAll(".", "");
                display = Integer.toString(Integer.valueOf(display), 16);
                display = display.toUpperCase();
                StringBuilder result = new StringBuilder();
                if (display.length() != 4) {
                    int len = display.length();
                    len = 4 - len;
                    for (int i = 0; i < len; i++) {
                        result.append("0");
                    }
                    result.append(display);
                }
                result.insert(2, " ");
                String fcode = " 06";
                String index = " 00 " + num + " ";
                String str = ID + fcode + index + result;

                if (null != clientThread) {
                    clientThread.sendMessage(str);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != clientThread) {
            clientThread = null;
        }
    }

    class ClientThread implements Runnable {

        private Socket socket;

        @Override
        public void run() {

            try {
                Log.e(TAG, "server ip: " + SERVER_IP);
                InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
                socket = new Socket(serverAddr, SERVERPORT);
                InputStream is = socket.getInputStream();
                byte[] buffer = new byte[1024];
                int read;
                String message = null;

                while ((read = is.read(buffer)) != -1) {
                    message = new String(buffer, 0, read);
                    System.out.print(message);
                    System.out.flush();
                    Log.e(TAG, "message from server: " + message);
                }


            } catch (UnknownHostException e1) {
                e1.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.btnConnect.setEnabled(true);
                    }
                });

                Log.e(TAG, "UnknownHostException: ");
            } catch (IOException e1) {
                e1.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.btnConnect.setEnabled(true);
                    }
                });
                Log.e(TAG, "IOException:");
            }
        }

        void sendMessage(final String message) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (null != socket) {
                            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                            Log.e(TAG, "sendMessage: " + message);
                            out.println(message);
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
