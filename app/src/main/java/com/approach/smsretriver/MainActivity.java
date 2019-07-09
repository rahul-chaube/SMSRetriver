package com.approach.smsretriver;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity  {
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    MySMSBroadcastReceiver mySMSBroadcastReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mySMSBroadcastReceiver =new MySMSBroadcastReceiver();
        AppSignatureHelper helper=new AppSignatureHelper(this);
        registerReceiver();

//        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//            @Override
//            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
//                Toast.makeText(MainActivity.this, "Verification Complete ", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onVerificationFailed(FirebaseException e) {
//                Toast.makeText(MainActivity.this, "Verification Failed ", Toast.LENGTH_SHORT).show();
//
//            }
//        };
    }

    @Override
    protected void onResume() {
        super.onResume();
//        try {
//            requestHint();
//        } catch (IntentSender.SendIntentException e) {
//            e.printStackTrace();
//        }


    }

    void registerReceiver()
    {
        SmsRetrieverClient client = SmsRetriever.getClient(this /* context */);
        Task<Void> task = client.startSmsRetriever();
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Successfully started retriever, expect broadcast intent
                // ...
                MySMSBroadcastReceiver.Listner listner=new MySMSBroadcastReceiver.Listner() {
                    @Override
                    public void onReceived(String otp) {
                        Toast.makeText(MainActivity.this, "Received SMS ", Toast.LENGTH_SHORT).show();
                        Log.e("SMS is ",otp);
                        registerReceiver();
                    }

                    @Override
                    public void timeout() {
                        Toast.makeText(MainActivity.this, "Time Out ", Toast.LENGTH_SHORT).show();
                    }
                };
                mySMSBroadcastReceiver.injectListner(listner);
                registerReceiver(mySMSBroadcastReceiver, new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION));
                Toast.makeText(MainActivity.this, "Happened Some thing Good ", Toast.LENGTH_LONG).show();
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Failed to start retriever, inspect Exception for more details
                // ...


                Toast.makeText(MainActivity.this, "Failed to retrived  ", Toast.LENGTH_SHORT).show();
                Log.e("Failed to Retrive sms "," "+e.getMessage());
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mySMSBroadcastReceiver!=null)
        unregisterReceiver(mySMSBroadcastReceiver);
    }

    public void simOnClicked(View view) {
        registerReceiver(mySMSBroadcastReceiver, new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION));

//        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                "+919819891458",        // Phone number to verify
//                60,                 // Timeout duration
//                TimeUnit.SECONDS,   // Unit of timeout
//                this,               // Activity (for callback binding)
//                mCallbacks);
    }

    public void simOnClicked2(View view) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+919819891458",        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);
    }

    public static final int RESOLVE_HINT = 10004;
    private static final int CREDENTIAL_PICKER_REQUEST = 1;  // Set to an unused request code

    // Construct a request for phone numbers and show the picker
    private void requestHint() throws IntentSender.SendIntentException {
        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();
        PendingIntent intent = Credentials.getClient(this).getHintPickerIntent(hintRequest);
        startIntentSenderForResult(intent.getIntentSender(),
                RESOLVE_HINT, null, 0, 0, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CREDENTIAL_PICKER_REQUEST:
                // Obtain the phone number from the result
                if (resultCode == RESULT_OK) {
                    Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                    // credential.getId();  <-- will need to process phone number string
                }
                break;
            // ...
        }
    }
}
