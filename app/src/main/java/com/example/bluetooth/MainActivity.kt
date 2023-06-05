package com.example.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bluetooth.databinding.ActivityMainBinding
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val REQUEST_ENABLE_BT = 1
    var deviceName: String? = null
    var deviceHardwareAddress: String? = null
    var receivedMessage = ""

    // lateinit var pairedListModel : PairedListModel
    private var mqttClient: MqttAndroidClient? = null

    var topicSubscribe = "/supro/Android"

    private val requiredPermissions = arrayOf(
        android.Manifest.permission.BLUETOOTH,
        android.Manifest.permission.BLUETOOTH_CONNECT,
        android.Manifest.permission.BLUETOOTH_ADMIN,
        android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
    )

    lateinit var bluetoothManager: BluetoothManager
    lateinit var bluetoothAdapter: BluetoothAdapter
    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)

    var pairedList = ArrayList<PairedListModel>()
    var myHardwareAddress = "30:C6:F7:30:80:12"


    lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //   val myUuid: UUID = UUID.fromString("0000110A-0000-1000-8000-00805F9B34FB")

        //val myUuid: UUID = UUID.fromString("0000111E-0000-1000-8000-00805F9B34FB")
        viewModel = ViewModelProvider(this@MainActivity)[MainActivityViewModel::class.java]

        bluetoothManager = getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager.adapter

        if (bluetoothAdapter == null) {
            Toast.makeText(this@MainActivity, "Doesn't Support Bluetooth", Toast.LENGTH_SHORT)
                .show()
        }

        connectToMQTT()


        binding.btnEnabled.setOnClickListener {

            if (!bluetoothAdapter.isEnabled) {

                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            }


        }

//        binding.pairedDevicesIst.setOnClickListener {
//            val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
//            pairedDevices?.forEach { device ->
//               deviceName = device.name
//                deviceHardwareAddress = device.address // MAC address
//
//
//
//                if (deviceHardwareAddress.equals(myHardwareAddress))
//                {
//                       class ConnectThread(device: BluetoothDevice) : Thread() {
//
//                        private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
//                            device.createRfcommSocketToServiceRecord(myUuid)
//                        }
//
//                        public override fun run() {
//                            // Cancel discovery because it otherwise slows down the connection.
//                            bluetoothAdapter?.cancelDiscovery()
//
//                            mmSocket?.let { socket ->
//                                // Connect to the remote device through the socket. This call blocks
//                                // until it succeeds or throws an exception.
//                                socket.connect()
//
//                                // The connection attempt succeeded. Perform work associated with
//                                // the connection in a separate thread.
//                             //   manageMyConnectedSocket(socket)
//                            }
//                        }
//
//                        // Closes the client socket and causes the thread to finish.
//                        fun cancel() {
//                            try {
//                                mmSocket?.close()
//                            } catch (e: IOException) {
//                                Log.e("dfgrf", "Could not close the client socket", e)
//                            }
//                        }
//                    }
//
//                }
//
//
//                pairedList.add(PairedListModel(deviceName,deviceHardwareAddress))
//
////                for (pl in pairedList) {
////                    if (pl.deviceHardwareAddress == myHardwareAddress)
////                    {
////
////
////                    }
////                }
//
//      }
//
//
//            binding.pairedListRv.layoutManager = LinearLayoutManager(this)
//            if (pairedList!=null)
//            {
//                binding.pairedListRv.adapter = PairedListAdapter(this@MainActivity,pairedList)
//                binding.pairedListRv.setHasFixedSize(true)
//
//            } else{
//                Toast.makeText(this@MainActivity, "Empty", Toast.LENGTH_SHORT).show()
//            }
//
//
//        }


        binding.pairedDevicesIst.setOnClickListener {
            val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter.bondedDevices
            pairedDevices?.forEach { device ->
                deviceName = device.name
                deviceHardwareAddress = device.address // MAC address


                pairedList.add(PairedListModel(deviceName, deviceHardwareAddress))

                binding.pairedListRv.layoutManager = LinearLayoutManager(this)
                if (pairedList != null) {
                    binding.pairedListRv.adapter = PairedListAdapter(this@MainActivity, pairedList)
                    binding.pairedListRv.setHasFixedSize(true)

                } else {
                    Toast.makeText(this@MainActivity, "Empty", Toast.LENGTH_SHORT).show()
                }

//                if(deviceHardwareAddress==myHardwareAddress)
//                {
//                    val device: BluetoothDevice? = bluetoothAdapter?.getRemoteDevice(deviceHardwareAddress)
//                    if (device != null) {
//                        // Create a BluetoothSocket for communication                                          // 94B5552C435CFA3F00000000
//                        val socket: BluetoothSocket? = device.createRfcommSocketToServiceRecord(UUID.fromString("94B5552C-435C-FA3F-0000-0000"))
//
//                        // Connect to the Bluetooth device
//                        try {
//                            socket?.connect()
//                            // Connection successful, perform further operations with the socket
//                            Toast.makeText(this@MainActivity, "Bluetooth conected successfully", Toast.LENGTH_SHORT).show()
//                            // Remember to close the socket when you're done:
//                            // socket?.close()
//                        } catch (e: IOException) {
//                            // Connection failed
//                            e.printStackTrace()
//                        }
//                    }
//                }

            }


        }

        binding.autoConnect.setOnClickListener {
            // MyAsyncTask().execute()

            for (mypl in pairedList) {
                val devicen = mypl.deviceName
                val devicemac = mypl.deviceHardwareAddress

                if (devicemac == myHardwareAddress) {

                    val device: BluetoothDevice? =
                        bluetoothAdapter.getRemoteDevice(myHardwareAddress)
                    if (device != null) {
                        // Create a BluetoothSocket for communication                                          // 94B5552C435CFA3F00000000
                        val socket: BluetoothSocket? =
                            //4fafc201-1fb5-459e-8fcc-c5c9c331914b
                            device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"))


                        // Connect to the Bluetooth device
                        try {
                            socket?.connect()

                            //   Toast.makeText(this@MainActivity, "connected", Toast.LENGTH_SHORT).show()
                            // Get the input and output streams
                            val inputStream: InputStream? = socket?.inputStream
                            val outputStream: OutputStream? = socket?.outputStream

                            // Send data to the Bluetooth device
                            val message = "Hello world!"
                            outputStream?.write(message.toByteArray())

                            // Receive data from the Bluetooth device
                            val buffer = ByteArray(1024)
                            val bytesRead = inputStream?.read(buffer)
                            if (bytesRead != null && bytesRead > 0) {
                                val receivedData = buffer.copyOfRange(0, bytesRead)
                                receivedMessage = String(receivedData)
                                // Process the received message
                                Toast.makeText(
                                    this@MainActivity,
                                    "" + receivedMessage + "Recieved from Bluetooth",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }


                            inputStream?.close()
                            outputStream?.close()
                            socket?.close()

                        } catch (e: IOException) {
                            // Connection or communication error
                            e.printStackTrace()

                        }


                        var receivedMsginPayload = receivedMessage.toByteArray()
                        if (mqttClient!!.isConnected) {
                            try {
                                mqttClient!!.publish(topicSubscribe,
                                    receivedMsginPayload,
                                    1,
                                    true,
                                    null,
                                    object : IMqttActionListener {
                                        override fun onSuccess(asyncActionToken: IMqttToken) {
                                            val msg =
                                                "Publish message: " + String(receivedMessage.toByteArray()) + " to topic: " + topicSubscribe
                                            // Log.d(MainActivity.class.getName(), msg);
                                            Toast.makeText(
                                                this@MainActivity,
                                                msg,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                        override fun onFailure(
                                            asyncActionToken: IMqttToken, exception: Throwable
                                        ) {
                                            Log.d(
                                                MainActivity::class.java.name,
                                                "Failed to publish message to topic"
                                            )
                                        }
                                    })
                            } catch (e: MqttException) {
                                throw RuntimeException(e)
                            }
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "MQTT not connected",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
                }

            }
        }


    }

//    inner class MyAsyncTask : AsyncTask<Unit, Unit, Unit>() {
//
//        override fun doInBackground(vararg params: Unit?) {
//            for (mypl in pairedList) {
//                val devicen = mypl.deviceName
//                val devicemac = mypl.deviceHardwareAddress
//
//                if (devicemac == myHardwareAddress) {
//
//                    val device: BluetoothDevice? = bluetoothAdapter.getRemoteDevice(myHardwareAddress)
//                    if (device != null) {
//                        // Create a BluetoothSocket for communication                                          // 94B5552C435CFA3F00000000
//                        val socket: BluetoothSocket? =
//                            //4fafc201-1fb5-459e-8fcc-c5c9c331914b
//                            device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"))
//
//
//                        // Connect to the Bluetooth device
//                        try {
//                            socket?.connect()
//
//                         //   Toast.makeText(this@MainActivity, "connected", Toast.LENGTH_SHORT).show()
//                            // Get the input and output streams
//                            val inputStream: InputStream? = socket?.inputStream
//                            val outputStream: OutputStream? = socket?.outputStream
//
//                            // Send data to the Bluetooth device
//                            val message = "Hello world!"
//                            outputStream?.write(message.toByteArray())
//
//                            // Receive data from the Bluetooth device
//                            val buffer = ByteArray(1024)
//                            val bytesRead = inputStream?.read(buffer)
//                            if (bytesRead != null && bytesRead > 0) {
//                                val receivedData = buffer.copyOfRange(0, bytesRead)
//                                val receivedMessage = String(receivedData)
//                                // Process the received message
//                                Toast.makeText(this@MainActivity, ""+ receivedMessage + "Recieved from Bluetooth", Toast.LENGTH_SHORT).show()
//                            }
//
//
//                            inputStream?.close()
//                            outputStream?.close()
//                            socket?.close()
//
//                        } catch (e: IOException) {
//                            // Connection or communication error
//                            e.printStackTrace()
//
//                        }
//
//
//                    }
//                }
//
//            }
//        }
//
//        override fun onPostExecute(result: Unit?) {
//            // This method runs on the main/UI thread after the background task is completed
//            // Update the UI or perform any necessary post-processing here
//        }
//    }

    private fun connectToMQTT() {

        val serverURI = "tcp://supro.shunyaekai.tech:1883"
        mqttClient = MqttAndroidClient(this@MainActivity, serverURI, "supro")
        val options = MqttConnectOptions()
        options.userName = "supro"
        options.password = "T62\$pO^GxSG94SFMvqNQgR1\$k".toCharArray()
        try {
            mqttClient!!.connect(options, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    Log.d("AndroidMqttClient", "Connection success")
                    // var topicSubscribe = "/supro/CYCLE" + Global.deviceIdStr + "/#"

                    if (mqttClient!!.isConnected) {
                        subscribeToTopic()
                        mqttClient!!.setCallback(object : MqttCallback {
                            override fun connectionLost(cause: Throwable) {
                                // Log.d("AndroidMqttClient", "Connection lost " + cause.toString());
                            }

                            @Throws(Exception::class)
                            override fun messageArrived(topic: String, message: MqttMessage) {
                                try {
                                    val payload = String(message.payload)
                                    Toast.makeText(
                                        this@MainActivity,
                                        "" + payload + "Message from mqtt",
                                        Toast.LENGTH_SHORT
                                    ).show()


                                } catch (e: Exception) {

                                }
                            }

                            override fun deliveryComplete(token: IMqttDeliveryToken) {}
                        })
                    }
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    Log.d("AndroidMqttClient", "Connection failure")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    private fun subscribeToTopic() {
        try {
            mqttClient!!.subscribe(topicSubscribe, 1, this, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    val msg = "Subscribed to: $topicSubscribe"
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {}
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                //enabled
                Toast.makeText(this@MainActivity, "Bluetooth is enabled ", Toast.LENGTH_SHORT)
                    .show()
            } else if (resultCode == RESULT_CANCELED) {
                //cancel
                Toast.makeText(this@MainActivity, "cancelled ", Toast.LENGTH_SHORT).show()

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mqttClient?.disconnect()
    }

}


