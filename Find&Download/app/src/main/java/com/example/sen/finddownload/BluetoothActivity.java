package com.example.sen.finddownload;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.crypto.SecretKey;

public class BluetoothActivity extends AppCompatActivity {

    private final static int DISCOVER_DURATION = 300;
    private final static int REQUEST_BLUE = 1;
    private final static int PORT = 8080;
    private final static String SERVER_LOCATION = "172.20.10.7";
    private final static String messageToServer = "2-1-1.pdf";
    private static BluetoothAdapter mBluetoothAdapter;
    private static ArrayAdapter<String> adapter;
    private static BluetoothDevice device;
    int currentBatteryRemaining;


    private BroadcastReceiver batteryChangedReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            if(Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {

                int level = intent.getIntExtra("level", 0);
                int scale = intent.getIntExtra("scale", 100);
                currentBatteryRemaining = level * 100 / scale;
//                Log.v("battery", Integer.toString(currentBatteryRemaining));
            }
        }
    };

    public final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ListView listView = (ListView) findViewById(R.id.btList);
            Toast.makeText(BluetoothActivity.this, "File Downloaded!", Toast.LENGTH_LONG).show();
//            Set<BluetoothDevice> paireDevices = mBluetoothAdapter.getBondedDevices();
//            int count = 0;
//            if (paireDevices.size() > 0) {
//                String[] data = new String[paireDevices.size()];
//                for (BluetoothDevice bluetoothDevice : paireDevices) {
//                    data[count++] = bluetoothDevice.getName() + ":" + bluetoothDevice.getAddress();
//                }
//                adapter = new ArrayAdapter<String>(BluetoothActivity.this, android.R.layout.simple_expandable_list_item_1, data);
//
//                listView.setAdapter(adapter);

            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mDeviceList.add(device.getName() + "\n" + device.getAddress());
                Log.i("BT", device.getName() + "\n" + device.getAddress());
                assert listView != null;
                adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, mDeviceList);
                listView.setAdapter(adapter);
            }


//            assert listView != null;
//            listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    String s = adapter.getItem(position);
//                    String address = s.substring(s.indexOf(":")+1).trim();
//
//                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
//
//                    try {
//                        BluetoothSocket socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
//                        socket.connect();
//                        OutputStream os = socket.getOutputStream();
//                        String message  = "hello";
//                        os.write(message.getBytes());
//
//
//                        BluetoothServerSocket serverSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("BT",
//                                UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
//                        socket = serverSocket.accept();
//                        InputStream is = socket.getInputStream();
//
//                    }
//                    catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            });
        }
    };


    Runnable r = new Runnable() {
        @Override
        public void run() {
            try {
                FileDownload.downloadFile(SERVER_LOCATION, PORT, messageToServer, getPrivateKeys(), getPublicKeys(), getAESKeys());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private ArrayList<String> mDeviceList = new ArrayList<String>();

    public PrivateKey getPrivateKeys() throws Exception {
        AssetManager assetManager = getAssets();
        PrivateKey privatekey = (PrivateKey) new ObjectInputStream(assetManager.open("private.key")).readObject();
        byte[] s = Serialization.serialize(privatekey);
        Log.v("privatekey", Integer.toString(s.length));
        return privatekey;
    }

    public PublicKey getPublicKeys() throws Exception {
        AssetManager assetManager = getAssets();
        PublicKey publicDSkey = (PublicKey) new ObjectInputStream(assetManager.open("publicDS.key")).readObject();
        byte[] b = Serialization.serialize(publicDSkey);
        Log.v("publicDSkey", Integer.toString(b.length));
        return publicDSkey;
    }

    public SecretKey getAESKeys() throws Exception {
        AssetManager assetManager = getAssets();
        SecretKey AESkey = (SecretKey) new ObjectInputStream(assetManager.open("AES.key")).readObject();
        return AESkey;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);


        registerReceiver(batteryChangedReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        Button mButton = (Button) findViewById(R.id.find);
        assert mButton != null;
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discoverDevice();
            }
        });

        Button downloadButton = (Button) findViewById(R.id.download);
        assert downloadButton != null;
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread t = new Thread(r);
                t.start();
            }
        });

        Button wButton = (Button) findViewById(R.id.transfer);
        assert wButton != null;
        wButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transferFiles();
            }
        });

        Button mergeButton = (Button) findViewById(R.id.merge);
        assert mergeButton != null;
        mergeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<File> listFile = new ArrayList<File>();
                try {
                    // move file first
                    OutputStream os = new FileOutputStream(new File("/storage/emulated/0/2.pdf"));

                    for (int i = 1; i <= 2; i++) {
                        String path = "/storage/emulated/0/2-" + i;
                        listFile.add(new File(path));
                        System.out.println(path);
                    }
                    Log.d("Total size", Integer.toString(MergeFile.mergeFiles(listFile, os)));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void discoverDevice() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Bluetooth is not supported on this device", Toast.LENGTH_LONG).show();
        } else {
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
            }
            mBluetoothAdapter.startDiscovery();
        }

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
    }

    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    public void transferFiles() {
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            Toast.makeText(BluetoothActivity.this, "Bluetooth is not supported", Toast.LENGTH_LONG).show();
        } else {
            enableBluetooth();
        }
    }

    public void enableBluetooth() {
        Intent discoveryIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoveryIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVER_DURATION);

        startActivityForResult(discoveryIntent, REQUEST_BLUE);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == DISCOVER_DURATION && requestCode == REQUEST_BLUE) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("text/plain");
            System.out.println(Environment.getExternalStorageDirectory().getAbsoluteFile());
            File file = new File("/storage/emulated/0/12.pdf");
            Log.d("FIlE", file.getAbsolutePath().toString() + " " + file.length());
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));

            PackageManager pm = getPackageManager();
            List<ResolveInfo> appList = pm.queryIntentActivities(intent, 0);

            if (appList.size() > 0) {
                for (ResolveInfo info : appList) {
                    Log.d("TTT", info.activityInfo.packageName.toString());
                }
                String packageName = null;
                String className = null;
                boolean found = false;
                for (ResolveInfo info : appList) {
                    packageName = info.activityInfo.packageName;
                    if (packageName.equals("com.android.bluetooth")) {
                        className = info.activityInfo.name;
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    Toast.makeText(BluetoothActivity.this, "Bluetooth have not been found!", Toast.LENGTH_LONG).show();
                } else {
                    intent.setClassName(packageName, className);
                    long start = System.nanoTime();
                    startActivity(intent);
                    long end = System.nanoTime();
                    Log.v("1", Long.toString((end - start) / 1000000) + " ms");
                }
            } else {
                Toast.makeText(BluetoothActivity.this, "Bluetooth is cancelled", Toast.LENGTH_LONG).show();
            }
        }
    }
}

