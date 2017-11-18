package com.example.edward_liao.cranemachine;


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
import com.google.zxing.integration.android.*;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class Pay extends AppCompatActivity {
    BluetoothManager btManager;
    BluetoothAdapter btAdapter;
    BluetoothLeScanner btScanner;
    BluetoothGatt bluetoothGatt;

    private final static int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    Boolean btScanning = false;
    int deviceIndex = 0;
    //存放藍芽設備的陣列
    ArrayList<BluetoothDevice> devicesDiscovered = new ArrayList<BluetoothDevice>();

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


    private Button button_qrcode, button_yes, button_cancel;
    private TextView textView_enter;
    int money_pay, enter;
    private Activity pay;


    // 10秒之後將停止搜尋
    private Handler mHandler = new Handler();
    private static final long SCAN_PERIOD = 10000;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        first();


        btManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
        btScanner = btAdapter.getBluetoothLeScanner();

        if (btAdapter != null && !btAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();


    }

    String MacAddress;

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
//印出讀取到的值
            System.out.print(scanContent);


//            String str = "{\n" + "\"MacAddress\": \"DC:36:99:F8:D2:3C\",\n" + "\"Price\": 50\n" + "}";

            try {
                JSONObject jsonObject = new JSONObject(scanContent);

                MacAddress = jsonObject.getString("MacAddress");


            } catch (JSONException e) {
                e.printStackTrace();

            }
            startScanning();


            String temp = "金額為" + enter + "元";
            textView_enter.setText(temp);


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

            if (result.getDevice().getAddress().toString().equals(MacAddress)) {
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

            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);


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

            //讀取到值，在這裏讀數據
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }


        }


    };
    String get_info_from_BLE;

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        System.out.print("目前連接：");
        System.out.println(characteristic.getUuid());

        get_info_from_BLE = characteristic.getValue().toString();

        System.out.print("讀到的值是");
        System.out.println(get_info_from_BLE);


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
        System.out.println("＊＊＊開始掃描＊＊＊");
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
        System.out.println("＊＊＊停止掃描＊＊＊");
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

        toServer();


        //mac address !!!!!!!!!
        temp = devicesDiscovered.get(connectNO).getAddress();

        if (temp.equals(MacAddress)) {

            toServer();

            button_yes.setVisibility(View.VISIBLE);


        }

    }

    String success_info;





    public void get_info_from_BLE() {

        BluetoothGattCharacteristic myCharacteristic = bluetoothGatt.getService(RX_SERVICE_UUID).getCharacteristic(TX_CHAR_UUID);


    }


    //禁用系統返回鍵
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return false;
    }


    public void first() {

        this.pay = this;

        button_qrcode = (Button) findViewById(R.id.button_qrcode);
        button_yes = (Button) findViewById(R.id.button_yes);
        button_cancel = (Button) findViewById(R.id.button_cancel);
        textView_enter = (TextView) findViewById(R.id.textView_enter);
    }


    public void setButton_qrcode(View view) {
        IntentIntegrator scanIntegrator = new IntentIntegrator(pay);
        scanIntegrator.initiateScan();
        button_qrcode.setVisibility(View.INVISIBLE);

    }


    public void setButton_yes(View view) {

        setStatus();

    }


    //回到功能頁面
    public void setButton_cancel(View view) {
        setBack();
    }


    String Status_temp;

    //傳送付款資訊給server
    public void toServer() {


        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                // TODO Auto-generated method stub

                try {
                    //建立要傳送的JSON物件
                    JSONObject json = new JSONObject();
                    json.put("data", "0bb685c846654441db1a1ba03aa2a9f0531db1c49b5c02d8d1d37c2ff94eab73");


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
                    int Balance = responseJSON.getInt("balance");

                    Status_temp = Status;


                    if (Status.equals("Success")) {
                        GlobalVariable gv = (GlobalVariable) getApplicationContext();

                        money_pay = Balance;

                        gv.setMoney_total(money_pay);


                        System.out.println(Status);
                        System.out.print("目前餘額：");
                        System.out.println(money_pay);


                    } else {
                        textView_enter.setText("交易失敗");
                    }


                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return null;
            }
        }.execute(null, null, null);

    }

    public void setStatus() {

        System.out.println("付款成功");

        write();


//                中斷藍牙裝置
        bluetoothGatt.disconnect();
        setBack();


    }

    //    資訊寫入ＢＬＥ讓販賣機動
    public void write() {
        BluetoothGattCharacteristic myCharacteristic = bluetoothGatt.getService(RX_SERVICE_UUID).getCharacteristic(RX_CHAR_UUID);
      /*  char a = (char) 0x07;
        int[] stringValueOf = {0x07, 0x58, 0x83, 0x13, 0x7f, 0x4f, 0x11, 0x3e, 0x67, 0x10, 0xdf, 0xe5, 0x6d, 0x01, 0xe5, 0x75};
        byte data[] = new byte[16];
        for (int i = 0; i < 16; i++) {
            data[i] = (byte) stringValueOf[i];

        }
*/
        // System.out.print(data);
        myCharacteristic.setValue("1111111111111111");
        System.out.println("Start! ");
        bluetoothGatt.writeCharacteristic(myCharacteristic);
        System.out.println("RX:" + myCharacteristic);


    }


    //回到功能頁面
    public void setBack() {


        finish();
        Intent back = new Intent(this, FunctionActivity.class);
        startActivity(back);
    }

    public void disconnectDeviceSelected() {
        bluetoothGatt.disconnect();
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
                Uri.parse("android-app://com.example.edward_liao.cranemachine/http/host/path")
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
                Uri.parse("android-app://com.example.edward_liao.cranemachine/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

}