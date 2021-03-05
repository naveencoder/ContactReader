package com.example.usercontact

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.usercontact.adapter.MyAdapter
import com.example.usercontact.listener.OnCallListener
import com.example.usercontact.listener.SharedPreference
import com.example.usercontact.model.Contact
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import kotlin.collections.ArrayList

@RequiresApi(Build.VERSION_CODES.M)
class MainActivity : AppCompatActivity(), OnCallListener<Contact> {

    private var mySession: SharedPreference? = null
    val packagedata: ArrayList<String> = ArrayList()
    private val PERMISSIONS_REQUEST_READ_CONTACTS = 100
    var checkResume = false
    var checklist = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        EventBus.getDefault().register(this)
//        Log.e("Checking", "onCreate: ")
        initization()
        loadContacts()

    }

    private fun initization() {
        mySession = SharedPreference(this)
    }

    private fun loadContacts() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
            || checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.WRITE_CALL_LOG
                ),
                PERMISSIONS_REQUEST_READ_CONTACTS
            )
        } else {
            recyclerView.layoutManager = LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false
            )
            val adapter = MyAdapter(getContacts())
            recyclerView.adapter = adapter
            adapter.setListener(this)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.READ_CALL_LOG,
                        Manifest.permission.WRITE_CALL_LOG
                    ), PERMISSIONS_REQUEST_READ_CONTACTS
                )
                //callback onRequestPermissionsResult
            } else {
                recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                val adapter = MyAdapter(getContacts())
                recyclerView.adapter = adapter
                adapter.setListener(this)
                progressBar.visibility = View.GONE
            }
        } else {
            recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            val adapter = MyAdapter(getContacts())
            recyclerView.adapter = adapter
            adapter.setListener(this)
            progressBar.visibility = View.GONE
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadContacts()
            } else {
//                showToast("Permission must be granted in order to display contacts information")
            }
        }
    }

    private fun getContacts(): ArrayList<Contact> {
        val contacts = ArrayList<Contact>()
        val cursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )

        if (cursor!!.count > 0) {
            while (cursor.moveToNext()) {
                val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                val name =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val phoneNumber =
                    (cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))).toInt()

                if (phoneNumber > 0) {
                    val cursorPhone = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
                        arrayOf(id),
                        null
                    )

                    if (cursorPhone!!.count > 0) {
                        while (cursorPhone.moveToNext()) {
                            val phoneNumValue = cursorPhone.getString(
                                cursorPhone.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER
                                )
                            )
                            if(!checklist.contains(phoneNumValue)){
                                checklist.add(phoneNumValue)
                                contacts.add(Contact(name, phoneNumValue))
                            }
                        }
                    }
                    cursorPhone.close()
                }
            }
        } else {
            showToast("No contacts available!")
        }
        cursor.close()
        return contacts
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onMessage(contact: Contact, status: String) {
        val contactdata = contact.number+","+contact.name
        if (status.equals("check")){
            if (!packagedata.containsAll(listOf(contactdata))) {
                packagedata.add(contactdata)
            }
        }else {
            packagedata.remove(contactdata)
        }
        mySession!!.putPhoneNumber(packagedata)
    }

    override fun onResume() {
        super.onResume()
        Log.e("Check", "onResume: ")
    }

    override fun onPause() {
        super.onPause()
        val pref3 = PreferenceManager.getDefaultSharedPreferences(this)
        if (pref3.getBoolean("pauseStateVLC", false)) {
            checkResume = true
            pref3.edit().putBoolean("pauseStateVLC", false).apply()
        } else checkResume = false
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEventAddCarts(number: String) {

        val newnumber = number.replace("+91","")
        for (i in 0 until mySession!!.phoneNumber.size){
            val sessionnumber = mySession!!.phoneNumber[i]
                if (sessionnumber.contains(newnumber)){
                    optionsAlert(sessionnumber)
                }
        }
    }

    private fun optionsAlert(str_usernumber: String) {
        val dialog = Dialog(this, R.style.AppTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.activity_notification_page)
        val cancelimage = dialog.findViewById(R.id.cancelimage) as ImageView
        val username = dialog.findViewById(R.id.username) as TextView
        val usernumber = dialog.findViewById(R.id.usernumber) as TextView
        val namenuber = str_usernumber.split(",")
        username.text = namenuber[1]
        usernumber.text = namenuber[0]
        cancelimage.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}
