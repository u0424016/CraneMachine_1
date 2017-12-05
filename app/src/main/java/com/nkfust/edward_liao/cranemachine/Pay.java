package com.nkfust.edward_liao.cranemachine;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


import android.support.v4.app.ActivityCompat;
import android.Manifest;
import android.content.pm.PackageManager;

import static android.Manifest.permission.*;

public class Pay extends AppCompatActivity {

    private Activity Pay;


    BluetoothManager btManager;
    BluetoothAdapter btAdapter;
    BluetoothLeScanner btScanner;
    TextView textView2;

    private final static int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    Boolean btScanning = false;
    int deviceIndex = 0;
    //存放藍芽設備的陣列
    ArrayList<BluetoothDevice> devicesDiscovered = new ArrayList<BluetoothDevice>();

    Button button_qrcode;
    Button button_yes;
    Button button_back, button_back2;
    Button button_help;
    BluetoothGatt bluetoothGatt;

    String Status_temp;

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";

    public Map<String, String> uuids = new HashMap<String, String>();

    public static final UUID RX_SERVICE_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
    public static final UUID RX_CHAR_UUID = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");
    public static final UUID TX_CHAR_UUID = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");


    // 10秒之後將停止搜尋
    private Handler mHandler = new Handler();
    private static final long SCAN_PERIOD = 10000;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    int money_pay;

    String cmtoken;
    String cmuid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);


        //檢查是否取得權限

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

//沒有權限時
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(Pay.this, new String[]{Manifest.permission.CAMERA}, 1);
        } else {
            Toast.makeText(this, "請掃描QRCode!", Toast.LENGTH_SHORT).show();
        }


        this.textView2 = (TextView) findViewById(R.id.textView2);
        this.Pay = this;


        GlobalVariable gv = (GlobalVariable) getApplicationContext();

        cmtoken = gv.getCM_Token();
        cmuid = gv.getCM_ID();


        money_pay = gv.getMoney_total();


        button_yes = (Button) findViewById(R.id.sendButton);
        button_yes.setVisibility(View.INVISIBLE);
        button_yes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                toServer();

                textView2.setText("付款成功");


                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {

                        //過1秒後要做的事情

                        textView2.setText("連接中，請稍候");

//                寫入狀態給販賣機
                        write();
                        textView2.setText("已連接");

                    }
                }, 1000);


//                read();


                button_yes.setVisibility(View.INVISIBLE);
                button_back.setVisibility(View.VISIBLE);
                button_back2.setVisibility(View.INVISIBLE);


            }
        });

        button_back = (Button) findViewById(R.id.button_back);
        button_back.setVisibility(View.INVISIBLE);
        button_back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                bluetoothGatt.disconnect();
                devicesDiscovered.clear();
                setBack();


            }
        });


        button_back2 = (Button) findViewById(R.id.button_back2);
        button_back2.setVisibility(View.VISIBLE);
        button_back2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                setBack();


            }
        });

        button_qrcode = (Button) findViewById(R.id.button_qrcode);
        button_qrcode.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //先掃描QRCode
                IntentIntegrator scanIntegrator = new IntentIntegrator(Pay);
                scanIntegrator.initiateScan();
            }
        });

        button_help = (Button) findViewById(R.id.button_help);
        button_help.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                textView2.setText("請再認真找找");

            }
        });


        btManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
        btScanner = btAdapter.getBluetoothLeScanner();

        if (btAdapter != null && !btAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();


    }

    //禁用系統返回鍵
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return false;
    }


    String ScanContent;

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {


            String scanContent = scanningResult.getContents();

            if (scanContent.equals("")) {

                Toast.makeText(getApplicationContext(), "請重新掃描", Toast.LENGTH_SHORT).show();


            } else {

                String MacAddress = scanContent;


                System.out.println(MacAddress.subSequence(44, 61));

                ScanContent = MacAddress.subSequence(44, 61).toString();


                startScanning();

            }


        } else {
            Toast.makeText(getApplicationContext(), "nothing", Toast.LENGTH_SHORT).show();
        }

    }


    int connectNO;
    // Device scan callback.
    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            devicesDiscovered.add(result.getDevice());

            if (result.getDevice().getAddress().toString().equals(ScanContent)) {
                connectNO = deviceIndex;
                connectToDeviceSelected();
                stopScanning();
            } else {
                deviceIndex++;

            }

        }
    };


    // Device connect call back
    private final BluetoothGattCallback btleGattCallback = new BluetoothGattCallback() {

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            // this will get called anytime you perform a read or write characteristic operation
            Pay.this.runOnUiThread(new Runnable() {
                public void run() {
                }
            });
        }

        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
            // this will get called when a device connects or disconnects
            System.out.println(newState);
            switch (newState) {
                case 0:
                    Pay.this.runOnUiThread(new Runnable() {
                        public void run() {

                        }
                    });
                    break;
                case 2:
                    Pay.this.runOnUiThread(new Runnable() {
                        public void run() {

                        }
                    });

                    // discover services and characteristics for this device
                    bluetoothGatt.discoverServices();
                    write();
//                    read();


                    break;
                default:
                    Pay.this.runOnUiThread(new Runnable() {
                        public void run() {
                        }
                    });
                    break;
            }
        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
            // this will get called after the client initiates a 			BluetoothGatt.discoverServices() call
            Pay.this.runOnUiThread(new Runnable() {
                public void run() {
                }
            });
            displayGattServices(bluetoothGatt.getServices());

        }

        @Override
        // Result of a characteristic read operation
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                System.out.print(characteristic);

            }

        }
    };

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {


        System.out.println(characteristic.getUuid());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }

    public void startScanning() {
        System.out.println("start scanning");
        btScanning = true;
        deviceIndex = 0;
        devicesDiscovered.clear();


        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                btScanner.startScan(leScanCallback);
            }
        });


        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopScanning();
            }
        }, SCAN_PERIOD);
    }

    public void stopScanning() {
        System.out.println("stopping scanning");
        //     peripheralTextView.append("Stopped Scanning\n");
        btScanning = false;

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                btScanner.stopScan(leScanCallback);
            }
        });
    }

    String temp;

    public void connectToDeviceSelected() {

        bluetoothGatt = devicesDiscovered.get(connectNO).connectGatt(this, false, btleGattCallback);


        textView2.setText("高科大娃娃機");


        //mac address !!!!!!!!!
        temp = devicesDiscovered.get(connectNO).getAddress();

        if (temp.equals(ScanContent)) {


            if (money_pay > 19) {

                //           確認連接後連接到伺服器進行扣款
//                toServer();

                button_yes.setVisibility(View.VISIBLE);
                button_qrcode.setVisibility(View.INVISIBLE);


//                Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//
//                    @Override
//                    public void run() {
//
//                        //過1秒後要做的事情
////            扣款後將啟動按鈕顯示，關閉QRcode掃描按鈕
//
//                    }
//                }, 1000);


            } else if (money_pay < 20) {
                textView2.setText("餘額不足");
                bluetoothGatt.disconnect();
                no_money();
                button_back.setVisibility(View.VISIBLE);
                button_qrcode.setVisibility(View.INVISIBLE);

            }


        }else{

            Toast.makeText(this, "無法連接裝置，請檢查藍牙或重新啟動!", Toast.LENGTH_SHORT).show();


        }


    }


    //
    public void write() {
        BluetoothGattCharacteristic myCharacteristic = bluetoothGatt.getService(RX_SERVICE_UUID).getCharacteristic(RX_CHAR_UUID);
        char a = (char) 0x07;
        int[] stringValueOf = {0x07, 0x58, 0x83, 0x13, 0x7f, 0x4f, 0x11, 0x3e, 0x67, 0x10, 0xdf, 0xe5, 0x6d, 0x01, 0xe5, 0x75};
        byte data[] = new byte[16];
        for (int i = 0; i < 16; i++) {
            data[i] = (byte) stringValueOf[i];

        }

        // System.out.print(data);
        myCharacteristic.setValue(data);
        System.out.println("Start! ");
        bluetoothGatt.writeCharacteristic(myCharacteristic);
        System.out.println("RX:" + myCharacteristic);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {

                //過5秒後要做的事情
//            直接斷開藍牙裝置及清除列表後返回功能頁面
                disconnectDeviceSelected();
                setBack();
            }
        }, 5000);


    }
/*
    public void read() {
        BluetoothGattCharacteristic myCharacteristic = bluetoothGatt.getService(RX_SERVICE_UUID).getCharacteristic(TX_CHAR_UUID);

        bluetoothGatt.readCharacteristic(myCharacteristic);
        myCharacteristic.getValue();


        System.out.println("TX:" + myCharacteristic.getValue());


    }*/

    //            斷開藍牙裝置及清除列表
    public void disconnectDeviceSelected() {
        bluetoothGatt.disconnect();
        devicesDiscovered.clear();
    }

    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {

            final String uuid = gattService.getUuid().toString();
            System.out.println("Service discovered: " + uuid);
            Pay.this.runOnUiThread(new Runnable() {
                public void run() {
                }
            });
            new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic :
                    gattCharacteristics) {

                final String charUuid = gattCharacteristic.getUuid().toString();
                System.out.println("Characteristic discovered for service: " + charUuid);
                Pay.this.runOnUiThread(new Runnable() {
                    public void run() {
                    }
                });

            }
        }
    }


    //傳送付款資訊給server
    public void toServer() {


        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                // TODO Auto-generated method stub

                try {
                    //建立要傳送的JSON物件
                    JSONObject json = new JSONObject();
                    json.put("data", "7c4cd77c8fdb327126eac5e6d1fec59657ef2abd308152935d874340a5bba74a2bf3857b6eb49b7e9f38f26809d4a243");
                    json.put("CMtoken", cmtoken);
                    json.put("CMUID", cmuid);


                    //建立POST Request
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost("http://163.18.2.157:80/pay");
                    //JSON物件放到POST Request
                    StringEntity stringEntity = new StringEntity(json.toString());
                    stringEntity.setContentType("application/json");
                    httpPost.setEntity(stringEntity);
                    //執行POST Request
                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    //取得回傳的內容
                    HttpEntity httpEntity = httpResponse.getEntity();
                    String responseString = EntityUtils.toString(httpEntity, "UTF-8");
                    //回傳的內容轉存為JSON物件
                    JSONObject responseJSON = new JSONObject(responseString);
                    //取得Message的屬性
                    String Status = responseJSON.getString("Status");
                    String Key = responseJSON.getString("Key");
                    int Balance = responseJSON.getInt("balance");

                    Status_temp = Status;


                    if (Status.equals("Success")) {
                        GlobalVariable gv = (GlobalVariable) getApplicationContext();

                        money_pay = Balance;

                        gv.setMoney_total(money_pay);


                        System.out.println("交易狀態");
                        System.out.println(Status);
                        System.out.print("目前餘額：");
                        System.out.println(money_pay);


                    }


                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return null;
            }
        }.execute(null, null, null);

    }

    public void no_money() {
        new AlertDialog.Builder(Pay.this)
                .setTitle("錯誤")
                .setMessage("餘額不足")
                .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        disconnectDeviceSelected();
                        setBack();
                    }
                })
                .show();
    }

    public void setBack() {


        finish();
        Intent back = new Intent(this, FunctionActivity.class);
        startActivity(back);
    }

    public void setHelp(View view) {

    }

    @Override
    public void onStart() {
        super.onStart();

        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.nkfust.edward_liao.cranemachine/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.nkfust.edward_liao.cranemachine/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}