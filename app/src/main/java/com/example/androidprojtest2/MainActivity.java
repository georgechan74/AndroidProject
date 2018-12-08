package com.example.androidprojtest2;

import android.app.PendingIntent;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Image;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidprojtest2.database.Identification;
import com.example.androidprojtest2.database.IdentificationViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView mStatus;
    private ImageView mFail;
    private ImageView mSuccess;
    private IdentificationViewModel mViewModel;
    private List<Identification> mAllIDs = new ArrayList<>();
    private Identification mIdentification = null;

    private final String[][] techList = new String[][] {
            new String[] {
                    NfcA.class.getName(),
                    NfcB.class.getName(),
                    NfcF.class.getName(),
                    NfcV.class.getName(),
                    IsoDep.class.getName(),
                    MifareClassic.class.getName(),
                    MifareUltralight.class.getName(), Ndef.class.getName()
            }
    };

    enum STATUS{
        SUCCESS, FAILED
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStatus = (TextView) findViewById(R.id.status);
        mFail = (ImageView) findViewById(R.id.fail);
        mSuccess = (ImageView) findViewById(R.id.success);

        mViewModel = ViewModelProviders.of(this).get(IdentificationViewModel.class);

        mViewModel.insert("XYZ");

//        mViewModel.getAllEntries().observe(this, new Observer<List<Identification>>() {
//            @Override
//            public void onChanged(@Nullable List<Identification> identifications) {
////                Log.d("sizeofdatabase", mViewModel.getAllEntries().toString());
//                updateList(identifications);
//            }
//        });
    }

    private void updateList(List<Identification> identifications) {
        mAllIDs.addAll(identifications);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Intent intent = getIntent();
//        Log.d("myIntent", ""+intent);
        // creating pending intent:
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
//        Log.d("pendingIntent", ""+pendingIntent);
        // creating intent receiver for NFC events:
        IntentFilter filter = new IntentFilter();
        filter.addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
        filter.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filter.addAction(NfcAdapter.ACTION_TECH_DISCOVERED);
        // enabling foreground dispatch for getting intent from NFC event:
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
//        Log.d("nfcAdapter", nfcAdapter+"");
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, new IntentFilter[]{filter}, this.techList);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {

            String finalMessage = "";
            Parcelable[] rawMessages =
                    intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMessages != null) {
                NdefMessage[] messages = new NdefMessage[rawMessages.length];
                for (int i = 0; i < rawMessages.length; i++) {
                    messages[i] = (NdefMessage) rawMessages[i];
                }
                NdefRecord newMessage = messages[0].getRecords()[0];
                byte[] getPayLoad = messages[0].getRecords()[0].getPayload();
                String editMessage = newMessage.toString();
                int startingNumber = editMessage.indexOf("E") + 1;
                String readPayLoad = "";
                Log.d("length", newMessage.toString());
                for(int i = startingNumber; i < editMessage.length(); i++) {
                    readPayLoad += editMessage.charAt(i);
                }
                finalMessage = convertHexToString(readPayLoad);
                Log.d("readPayLoad", readPayLoad);
//                Log.d("payload", messages[0].getRecords()[0].toString());
                // Process the messages array.
            }

//            mStatus.setText(
//                    "NFC Tag\n" + ByteArrayToHexString(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)) +
//                            "\nMessage in Tag: " + finalMessage);

            outputResult(finalMessage);
        }
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // disabling foreground dispatch:
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcAdapter.disableForegroundDispatch(this);
    }

    private String ByteArrayToHexString(byte [] inarray) {
        int i, j, in;
        String [] hex = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
        String out= "";

        for(j = 0 ; j < inarray.length ; ++j)
        {
            in = (int) inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        Log.d("output", out);
        return out;
    }

    public String convertHexToString(String hex){

        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();

        //49204c6f7665204a617661 split into two characters 49, 20, 4c...
        for( int i=0; i<hex.length()-1; i+=2 ){

            //grab the hex in pairs
            String output = hex.substring(i, (i + 2));
            //convert hex to decimal
            int decimal = Integer.parseInt(output, 16);
            //convert the decimal to character
            sb.append((char)decimal);

            temp.append(decimal);
        }
//        System.out.println("Decimal : " + temp.toString());

        return sb.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();
        if (itemThatWasClickedId == R.id.history) {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void outputResult(String output) {
//        Log.d("outputresult", output + " test");
//        Identification identification = null;
        GetIdentification getIdentification = new GetIdentification();
        getIdentification.execute(output);
//        try {
//            //asynctask here
////            identification = mViewModel.checkDatabase(output);
//            Toast.makeText(this, "Success! Card in database. Activity logged.", Toast.LENGTH_LONG).show();
//            mSuccess.setVisibility(View.VISIBLE);
//        }
//        catch (NullPointerException n)
//        {
//            Toast.makeText(this, "Failed! Card not in database. Activity logged. ", Toast.LENGTH_LONG).show();
//            mFail.setVisibility(View.VISIBLE);
//        }
//        Log.d("outputresult", mIdentification.getDescription());
//        Log.d("outputresult", output);
//        if (mIdentification != null)
//        {
//            Toast.makeText(this, "Success! Card in database. Activity logged.", Toast.LENGTH_LONG).show();
//            mSuccess.setVisibility(View.VISIBLE);
//        }
//        else
//        {
//            Toast.makeText(this, "Failed! Card not in database. Activity logged. ", Toast.LENGTH_LONG).show();
//            mFail.setVisibility(View.VISIBLE);
//        }
        Sleeper sleeper = new Sleeper();
        sleeper.execute();
    }

    class GetIdentification extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            mIdentification = mViewModel.checkDatabase(strings[0]);
            Log.d("theCard", mIdentification.getDescription());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (mIdentification != null)
            {
                Toast.makeText(MainActivity.this, "Success! Card in database. Activity logged.", Toast.LENGTH_LONG).show();
                mSuccess.setVisibility(View.VISIBLE);
            }
            else
            {
                Toast.makeText(MainActivity.this, "Failed! Card not in database. Activity logged. ", Toast.LENGTH_LONG).show();
                mFail.setVisibility(View.VISIBLE);
            }
        }
    }

    class Sleeper extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mFail.setVisibility(View.GONE);
            mSuccess.setVisibility(View.GONE);
            mStatus.setText("Awaiting Scan...");
        }
    }
}
