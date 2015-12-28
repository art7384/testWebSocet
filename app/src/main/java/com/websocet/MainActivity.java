package com.websocet;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpGet;
import com.koushikdutta.async.http.WebSocket;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mTxt;
    private EditText mEd;
    private Button mBtn;

    private WebSocket mWebSocket = null;

    private Handler mHandler = new Handler();

    private WebSocket.StringCallback mStringCallbacknew = new WebSocket.StringCallback() {
        @Override
        public void onStringAvailable(String s) {
            Log.d("", "I got a string: " + s);
        }
    };

    private WebSocket.PongCallback mPongCallback = new WebSocket.PongCallback() {
        @Override
        public void onPongReceived(String s) {
            Log.d("", "PongCallback");
        }
    };

    private CompletedCallback mCompletedCallback = new CompletedCallback() {
        @Override
        public void onCompleted(Exception ex) {
            Log.d("", "CompletedCallback");
        }
    };

    private DataCallback mDataCallback = new DataCallback() {
        @Override
        public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
            Log.d("", "I got some bytes!");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTxt = (TextView) findViewById(R.id.txt);
        mEd = (EditText) findViewById(R.id.ed);
        mBtn = (Button) findViewById(R.id.btn);

        mBtn.setOnClickListener(this);


        mTxt.setText("Соединение...");
        startSocet();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn: {
                mWebSocket.send(mEd.getText().toString());
                break;
            }
        }
    }

    private void startSocet() {

        AsyncHttpGet get = new AsyncHttpGet("http://game.4zdev.ru:8090");
        get.addHeader("X-token", "tocken");
        AsyncHttpClient.getDefaultInstance().websocket(get, "http", new AsyncHttpClient.WebSocketConnectCallback() {

            @Override
            public void onCompleted(Exception ex, final WebSocket webSocket) {
                if (ex != null) {
                    ex.printStackTrace();
                    mTxt.setText("ERROR: " + ex.getMessage());
                    return;
                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mWebSocket = webSocket;
                        mTxt.setText("Соединение установлено!");

                        webSocket.setStringCallback(mStringCallbacknew);
                        webSocket.setPongCallback(mPongCallback);
                        webSocket.setClosedCallback(mCompletedCallback);
                        webSocket.setDataCallback(mDataCallback);

                        webSocket.send("test");
                        webSocket.send("test".getBytes());
                    }
                });


            }

        });


    }

}
