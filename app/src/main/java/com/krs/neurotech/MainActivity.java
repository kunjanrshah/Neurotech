package com.krs.neurotech;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

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
    private String id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setTitle("NeuroTech Computer Systems");

        binding.edtId.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    id = binding.edtId.getText().toString().trim();
                    id = Integer.toString(Integer.valueOf(id), 16);
                    id = id.toUpperCase();
                    if (id.length() == 1) {
                        id = "0" + id;
                    }
                    String fcode = " 03";
                    String sadd = " 00 00";
                    String eadd = " 00 06";
                    String str = id + fcode + sadd + eadd;
                    if (null != clientThread) {
                        clientThread.sendMessage(str);
                    }
                }
            }
        });

        binding.edtDisplay1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        String display1 = binding.edtDisplay1.getText().toString().trim();
                        sendDisplayMsg(display1,"01");
                    }
            }
        });

        binding.edtDisplay2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        String display2 = binding.edtDisplay2.getText().toString().trim();
                        sendDisplayMsg(display2,"02");
                    }

            }
        });

        binding.edtDisplay3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                        String display3 = binding.edtDisplay3.getText().toString().trim();
                        sendDisplayMsg(display3,"03");
                }
            }
        });

        binding.edtDisplay4.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        String display4 = binding.edtDisplay4.getText().toString().trim();
                        sendDisplayMsg(display4,"04");
                    }
            }
        });

        binding.edtDisplay5.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        String display5 = binding.edtDisplay5.getText().toString().trim();
                        sendDisplayMsg(display5,"05");
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

                // if (null != clientThread) {
                //clientThread.sendMessage("Disconnect");
                /*if(clientThread.socket!=null)
                {
                    Log.d(TAG,"isConnected: "+clientThread.socket.isConnected());
                    Log.d(TAG,"get channel: "+clientThread.socket.getChannel().toString());
                }*/

                try {
                    /*if (!clientThread.socket.isClosed()) {
                        clientThread.socket.close();
                    }*/
                    clientThread = new ClientThread();
                    thread = new Thread(clientThread);
                    thread.start();
                    binding.btnConnect.setEnabled(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                /*try {

                        if (!clientThread.socket.isClosed()) {
                            clientThread.socket.close();
                        }
                        if (thread.isAlive()) {
                            thread.stop();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                 //   clientThread = null;
               // }
                thread.start();*/
            }
        });

        clientThread = new ClientThread();
        thread = new Thread(clientThread);
        thread.start();
        binding.btnConnect.setEnabled(false);
    }

    private void sendDisplayMsg(String display,String num)
    {
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
            result.insert(2, " ");
        }
        String fcode = " 06";
        String index = " 00 "+num+" ";
        String str = id + fcode + index + result;

        if (null != clientThread) {
            clientThread.sendMessage(str);
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
