package com.example.fingerprint;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.UnsupportedEncodingException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

public class Note extends AppCompatActivity {

    TextView oneview;
    TextView secondview;
    EditText edittext;
    Button button;
    private byte[] szyfr;
    byte [] decodedData;
    String unencryptedString;
    String secret;
    String loadFromPref;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        oneview = findViewById(R.id.textView3);
        secondview = findViewById(R.id.textView4);
        edittext = findViewById(R.id.editText);
        button = findViewById(R.id.button);

        /*loadFromPref = MainActivity.utilsNote.getString("Note", "");*/
/*        try {
            decodedData = MainActivity.cipher2.doFinal(Base64.getDecoder().decode(loadFromPref));
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        try {
            unencryptedString = new String(decodedData, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }*/


/*        oneview.setText(loadFromPref);
        secondview.setText(unencryptedString)*/;

        button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                String text = edittext.getText().toString();
                try {
                    szyfr = MainActivity.cipher.doFinal(text.getBytes("UTF-8"));
                    secret = java.util.Base64.getEncoder().encodeToString(szyfr);
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
/*                try {
                    MainActivity.Decrypt();
                } catch (KeyStoreException e) {
                    e.printStackTrace();
                } catch (CertificateException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (UnrecoverableKeyException e) {
                    e.printStackTrace();
                } catch (InvalidAlgorithmParameterException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                }
                try {
                    decodedData = MainActivity.cipher2.doFinal(Base64.getDecoder().decode(secret));
                    unencryptedString = new String(decodedData, "UTF-8");
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                oneview.setText(unencryptedString)*/;
                secondview.setText(secret);
/*                SharedPreferences.Editor editor = MainActivity.utilsNote.edit();
                editor.putString("Note", secret);
                editor.commit();
                Toast.makeText(Note.this, "You changed your note", Toast.LENGTH_SHORT).show();*/

            }
        });


    }
}