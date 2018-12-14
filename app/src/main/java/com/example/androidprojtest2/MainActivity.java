package com.example.androidprojtest2;

import android.app.PendingIntent;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidprojtest2.data.ScanLog;
import com.example.androidprojtest2.data.ScanLogViewModel;
import com.example.androidprojtest2.database.IdentificationViewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MyTestTag";
    private TextView mStatus;
    private ImageView mFail;
    private ImageView mSuccess;
    private ProgressBar mProgress;
    private ProgressBar mProgress2;
    private IdentificationViewModel mViewModel;
    private ScanLogViewModel mScanLogViewModel;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mMyRef;
    private boolean mAddItem = false;
    private boolean mRemoveItem = false;
    private HashMap<String, String> mValues;
    private TextView mVerbose;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance();
        mMyRef = mDatabase.getReference();

        mStatus = (TextView) findViewById(R.id.status);
        mFail = (ImageView) findViewById(R.id.fail);
        mSuccess = (ImageView) findViewById(R.id.success);
        mProgress = (ProgressBar) findViewById(R.id.progress);
        mProgress2 = (ProgressBar) findViewById(R.id.progress2);
        mVerbose = (TextView) findViewById(R.id.verbose);
        mVerbose.setVisibility(View.GONE);

        mViewModel = ViewModelProviders.of(this).get(IdentificationViewModel.class);
        mScanLogViewModel = ViewModelProviders.of(this).get(ScanLogViewModel.class);

        mViewModel.insert("Hello!");

        mMyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
//                String value = dataSnapshot.getValue(String.class);
//                Log.d(TAG, "Value is: " + value);

                HashMap<String,String> values = new HashMap<>();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                    String description = ds.getValue(String.class);
                    values.put(ds.getKey(),ds.getValue(String.class));
                    Log.d("descriptionvalue", ds.getValue(String.class));
                }

                mValues = values;
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
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
                // Process the messages array.
            }

            decideAction(finalMessage);
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
            if (!isFinishing()) {
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                Log.d("mainactivityintent", intent.toString());
                startActivity(intent);
            }
        }
        else if (itemThatWasClickedId == R.id.addButton){
            mAddItem = !mAddItem;
            mRemoveItem = false;
//            Toast.makeText(MainActivity.this, "The next card scanned will be added to the database.", Toast.LENGTH_LONG).show();
            mVerbose.setText("The next card scanned will be added to the database.");
            mVerbose.setVisibility(View.VISIBLE);
        }
        else if (itemThatWasClickedId == R.id.removeButton){
            mAddItem = false;
            mRemoveItem = !mRemoveItem;
//            Toast.makeText(MainActivity.this, "The next card scanned will be removed from the database.", Toast.LENGTH_LONG).show();
            mVerbose.setText("The next card scanned will be removed from the database.");
            mVerbose.setVisibility(View.VISIBLE);
        }
        return super.onOptionsItemSelected(item);
    }

    private void decideAction(String output){
        if (mAddItem)
            add(output);
        else if(mRemoveItem)
            remove(output);
        else
            outputResult(output);

        mAddItem = false;  // Only add or remove one (the next) item
        mRemoveItem = false;
    }

    protected void add(String output){
        if (mValues.containsValue(output))
        {
//            Toast.makeText(MainActivity.this, "Card is already in database, cancelling add.", Toast.LENGTH_LONG).show();
            mFail.setVisibility(View.VISIBLE);
            mProgress.setVisibility(View.INVISIBLE);
            mProgress2.setVisibility(View.INVISIBLE);
            mVerbose.setText("Card is already in database, cancelling add.");
            mVerbose.setVisibility(View.VISIBLE);
            mStatus.setText("Duplicate card found");
        }

        else {
//            Toast.makeText(MainActivity.this, "Card is not in database, " +output+ " will be added." , Toast.LENGTH_LONG).show();
//            Toast.makeText(MainActivity.this, "Card is not in database and will be added." , Toast.LENGTH_LONG).show();
            mSuccess.setVisibility(View.VISIBLE);
            mProgress.setVisibility(View.INVISIBLE);
            mProgress2.setVisibility(View.INVISIBLE);
            mVerbose.setText("Card is not in database and will be added.");
            mVerbose.setVisibility(View.VISIBLE);
            mStatus.setText("Added!");
            DatabaseReference tempRef = mMyRef.child("Tag"+(mValues.size()+1));
            tempRef.setValue(output);
        }
        //makes message linger for 3 seconds
        Sleeper sleeper = new Sleeper();
        sleeper.execute();
    }

    protected void remove(String output){
        if (!mValues.containsValue(output))
        {
//            Toast.makeText(MainActivity.this, "Card is not in database, cancelling remove.", Toast.LENGTH_LONG).show();
            mFail.setVisibility(View.VISIBLE);
            mProgress.setVisibility(View.INVISIBLE);
            mProgress2.setVisibility(View.INVISIBLE);
            mVerbose.setText("Card is not in database, cancelling remove.");
            mVerbose.setVisibility(View.VISIBLE);
            mStatus.setText("Card not found");
        }
        else{
//            Toast.makeText(MainActivity.this, "Card is in the database, " +output+ " will be removed.", Toast.LENGTH_LONG).show();
//            Toast.makeText(MainActivity.this, "Card is in the database and will be removed.", Toast.LENGTH_LONG).show();
            mSuccess.setVisibility(View.VISIBLE);
            mProgress.setVisibility(View.INVISIBLE);
            mProgress2.setVisibility(View.INVISIBLE);
            mVerbose.setText("Card is in the database and will be removed.");
            mVerbose.setVisibility(View.VISIBLE);
            mStatus.setText("Removed!");
            String tagName = null;
            for(String s : mValues.keySet()){
                if(mValues.get(s).equals(output)){
                    tagName = s;
                    break;
                }
            }
            mMyRef.child(tagName).removeValue();
        }
        //makes message linger for 3 seconds
        Sleeper sleeper = new Sleeper();
        sleeper.execute();

    }

    public void outputResult(String output) {
        //see if it matches anything on database then return appropriate message
        Log.d("outoutresult", output);
        GetIdentification getIdentification = new GetIdentification();
        getIdentification.execute(output);
        if (mValues.containsValue(output)) {
//            Toast.makeText(MainActivity.this, "Card in database. Scan activity logged to history.", Toast.LENGTH_LONG).show();
            mProgress.setVisibility(View.INVISIBLE);
            mProgress2.setVisibility(View.INVISIBLE);
            mVerbose.setText("You scanned:\n"
                    + "Output: " + output + "\n"
                    + "at " + new Date() + "\n"
                    + "This card is in the database.");
            mVerbose.setVisibility(View.VISIBLE);
            mSuccess.setVisibility(View.VISIBLE);
            mStatus.setText("Success!");
        }
        else {
//            Toast.makeText(MainActivity.this, "Card not in database. Scan activity logged to history.", Toast.LENGTH_LONG).show();
            mProgress.setVisibility(View.INVISIBLE);
            mProgress2.setVisibility(View.INVISIBLE);
            mVerbose.setText("You scanned:\n"
                    + "Output: " + output + "\n"
                    + "at " + new Date() + "\n"
                    + "This card is not in the database.");
            mVerbose.setVisibility(View.VISIBLE);
            mFail.setVisibility(View.VISIBLE);
            mStatus.setText("Failed...");
        }
        //makes message linger for 3 seconds
        Sleeper sleeper = new Sleeper();
        sleeper.execute();
    }

    class GetIdentification extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(final String... strings) {
            Log.d("isitchecking", "yes");
            ScanLog scanLog = new ScanLog(strings[0], new Date());
            mScanLogViewModel.insert(scanLog);
            return null;
        }
    }

    class Sleeper extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(6000);
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
            mProgress.setVisibility(View.VISIBLE);
            mProgress2.setVisibility(View.VISIBLE);
            mVerbose.setVisibility(View.GONE);
            mStatus.setText("Awaiting Scan...");
        }
    }
}