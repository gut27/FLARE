package com.dlk.dlk.NFC_Write;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dlk.dlk.PreferenceManager;
import com.dlk.dlk.R;

public class NFCWritePassword extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    String nfcwrite_information = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfcwritepassword);


//        Intent password = new Intent(this.getIntent());
//        nfcwrite_information = password.getStringExtra("password");

        Context context = getApplicationContext();
        nfcwrite_information = "FLARE_Exit,"
                +PreferenceManager.getString(context,"password");

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Intent intent = new Intent(this, getClass()).addFlags(
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

    }
    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);

        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        NdefMessage message = getNdefMessage(nfcwrite_information);
        write(message, tagFromIntent);

    }

    private NdefMessage getNdefMessage(String text){
        byte[] textBytes = text.getBytes();
        NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,"text/plain".getBytes(), new byte[] {}, textBytes);
        NdefMessage message = new NdefMessage(textRecord);
        return message;

    }

    private boolean write(NdefMessage message, Tag tagFromIntent) {
        try {
            Ndef ndef = Ndef.get(tagFromIntent);
            if (ndef != null) {
                ndef.connect();
                ndef.writeNdefMessage(message);
                ndef.close();
                Toast.makeText(this, "태그에 쓰기 성공", Toast.LENGTH_LONG).show();
                return false;
            }
            return false;
        } catch (Exception e) {
            Toast.makeText(this, "태그에 쓰기 실패했습니다.!", Toast.LENGTH_LONG).show();
            return false;
        }
    }
    public void onResume () {
        super.onResume();
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }
}
