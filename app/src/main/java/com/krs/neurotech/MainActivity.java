package com.krs.neurotech;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Objects;

import dmax.dialog.SpotsDialog;

import static java.lang.String.format;

public class MainActivity extends AppCompatActivity {

    private static final int SERVERPORT = 1234;
    ActivityMainBinding binding = null;
    private ClientThread clientThread;
    private String TAG = MainActivity.class.getSimpleName();
    int RESPONSE = -1;
    private String ID = "01";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setTitle("NeuroTech");

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
                MaterialButton btnCancel = dialog.findViewById(R.id.btnCancel);
                MaterialButton btnOk = dialog.findViewById(R.id.btnOk);

                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String str = edit_id.getText().toString().trim();
                        setNewId(str);
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

        binding.edtDisplay1.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
        binding.edtDisplay1.setSelection(Objects.requireNonNull(binding.edtDisplay1.getText()).length());
        binding.edtDisplay1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String display1 = binding.edtDisplay1.getText().toString().trim();
                    sendDisplayMsg(display1, "01");
                }
            }
        });
        binding.imgsend1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String display1 = binding.edtDisplay1.getText().toString().trim();
                sendDisplayMsg(display1, "01");
            }
        });


        binding.edtDisplay2.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
        binding.edtDisplay2.setSelection(Objects.requireNonNull(binding.edtDisplay2.getText()).length());
        binding.edtDisplay2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String display2 = binding.edtDisplay2.getText().toString().trim();
                    sendDisplayMsg(display2, "02");
                }

            }
        });
        binding.imgsend2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String display2 = binding.edtDisplay2.getText().toString().trim();
                sendDisplayMsg(display2, "02");
            }
        });


        binding.edtDisplay3.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
        binding.edtDisplay3.setSelection(Objects.requireNonNull(binding.edtDisplay3.getText()).length());
        binding.edtDisplay3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String display3 = binding.edtDisplay3.getText().toString().trim();
                    sendDisplayMsg(display3, "03");
                }
            }
        });
        binding.imgsend3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String display3 = binding.edtDisplay3.getText().toString().trim();
                sendDisplayMsg(display3, "03");
            }
        });


        binding.edtDisplay4.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
        binding.edtDisplay4.setSelection(Objects.requireNonNull(binding.edtDisplay4.getText()).length());
        binding.edtDisplay4.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String display4 = binding.edtDisplay4.getText().toString().trim();
                    sendDisplayMsg(display4, "04");
                }
            }
        });
        binding.imgsend4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String display4 = binding.edtDisplay4.getText().toString().trim();
                sendDisplayMsg(display4, "04");
            }
        });


        binding.edtDisplay5.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
        binding.edtDisplay5.setSelection(Objects.requireNonNull(binding.edtDisplay5.getText()).length());
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
        binding.imgsend5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String display5 = binding.edtDisplay5.getText().toString().trim();
                sendDisplayMsg(display5, "05");
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
                RESPONSE = 1;
            }
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void save() {
        String fcode = " 06";
        String raw_id = " 00 07";
        String data1 = " 00 ";
        String str = ID + fcode + raw_id + data1 + ID;

        if (null != clientThread) {
            clientThread.sendMessage(str);
            final AlertDialog alertDialog = new SpotsDialog.Builder().setContext(this).build();
            alertDialog.setCancelable(false);
            alertDialog.setTitle("NeuroTech");
            alertDialog.setMessage("Loading...");
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
            if (new_id.length() == 1) {
                new_id = "0" + new_id;
            }
            String fcode = " 06";
            String raw_id = " 00 06";
            String data1 = " 00 ";
            String str = ID + fcode + raw_id + data1 + new_id;

            if (null != clientThread) {
                clientThread.sendMessage(str);
                ID = new_id;
                final AlertDialog alertDialog = new SpotsDialog.Builder().setContext(this).build();
                alertDialog.setCancelable(false);
                alertDialog.setTitle("NeuroTech");
                alertDialog.setMessage("Loading...");
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

    private void sendDisplayMsg(String display, String num) {
        try {
            if (!display.isEmpty()) {
                display = display.replace(".", "");
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
                } else {
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

    private class ClientThread implements Runnable {

        private Socket socket;
        private InputStream is;

        @Override
        public void run() {

            try {
                //192.168.43.95
                String SERVER_IP = "192.168.4.1";//192.168.1.103  //
                Log.e(TAG, "server ip: " + SERVER_IP);
                InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
                socket = new Socket(serverAddr, SERVERPORT);
                is = socket.getInputStream();
                byte[] buffer = new byte[1024];
                int read;
                String message;
                while ((read = is.read(buffer)) != -1) {
                    message = new String(buffer, 0, read);
                    System.out.print(message);
                    System.out.flush();
                    Log.e(TAG, "message from server: " + message);


                    if (RESPONSE == 1) {

                        String msg = message.substring(9, 38);

                        String display1 = msg.substring(0, 5).replace(" ", "");
                        final int d1 = Integer.parseInt(display1, 16);

                        String display2 = msg.substring(6, 11).replace(" ", "");
                        final int d2 = Integer.parseInt(display2, 16);

                        String display3 = msg.substring(12, 17).replace(" ", "");
                        final int d3 = Integer.parseInt(display3, 16);

                        String display4 = msg.substring(18, 23).replace(" ", "");
                        final int d4 = Integer.parseInt(display4, 16);


                        String display5 = msg.substring(24, 29).replace(" ", "");
                        final int d5 = Integer.parseInt(display5, 16);
                        RESPONSE = -1;

                        Log.v(TAG, msg);
                        Log.v(TAG, "" + d1);
                        Log.v(TAG, "" + d2);
                        Log.v(TAG, "" + d3);
                        Log.v(TAG, "" + d4);
                        Log.v(TAG, "" + d5);

                        runOnUiThread(new Runnable() {
                            @SuppressLint("DefaultLocale")
                            @Override
                            public void run() {
                                binding.edtDisplay1.setText(String.format("%d", d1));
                                binding.edtDisplay2.setText(String.format("%d", d2));
                                binding.edtDisplay3.setText(String.format("%d", d3));
                                binding.edtDisplay4.setText(String.format("%d", d4));
                                binding.edtDisplay5.setText(String.format("%d", d5));
                            }
                        });

                    }

                }


            } catch (UnknownHostException e1) {
                e1.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.btnConnect.setText(getResources().getString(R.string.connect));
                    }
                });

                Log.e(TAG, "UnknownHostException: ");
            } catch (IOException e1) {
                e1.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.btnConnect.setText(getResources().getString(R.string.connect));
                    }
                });
                Log.e(TAG, "IOException:");
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
