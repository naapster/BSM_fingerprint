package com.example.fingerprint;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

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
    String ivString;
    byte [] iv;
    byte [] iv2;
    String loadFromPref2;
    SecretKey key;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        oneview = findViewById(R.id.textView3);
        secondview = findViewById(R.id.textView4);
        edittext = findViewById(R.id.editText);
        button = findViewById(R.id.button);


        button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                String text = edittext.getText().toString();

                try {
                    MainActivity.keyStore.load(null);
                } catch (CertificateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                try {
                    key = (SecretKey) MainActivity.keyStore.getKey(MainActivity.KEY_NAME, null);
                } catch (KeyStoreException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (UnrecoverableKeyException e) {
                    e.printStackTrace();
                }
                try {
                    MainActivity.cipher.init(Cipher.ENCRYPT_MODE, key);
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                }
                iv2 = MainActivity.cipher.getIV ();
                    ivString = Base64.getEncoder().encodeToString(iv2);
                    SharedPreferences.Editor editor = MainActivity.utilsIv.edit();
                    editor.putString("IV", ivString);
                    editor.commit();

                try {
                    szyfr = MainActivity.cipher.doFinal(text.getBytes("UTF-8"));
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                secret = Base64.getEncoder().encodeToString(szyfr);
                    SharedPreferences.Editor editor2 = MainActivity.utilsNote.edit();
                    editor2.putString("Note", secret);
                    editor2.commit();
                    Toast.makeText(Note.this, "You changed your note", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Note.this, MainActivity.class);
                    startActivity(intent);







/*                SharedPreferences.Editor editor = MainActivity.utilsNote.edit();
                editor.putString("Note", secret);
                editor.commit();
                Toast.makeText(Note.this, "You changed your note", Toast.LENGTH_SHORT).show();*/

            }

        });

                loadFromPref = MainActivity.utilsNote.getString("Note","");
                loadFromPref2 = MainActivity.utilsIv.getString("IV","");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    iv = Base64.getDecoder().decode(loadFromPref2);
                }
                final GCMParameterSpec spec = new GCMParameterSpec(128, iv);


                try {
                    MainActivity.keyStore.load(null);
                } catch (CertificateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }


                try {
                    key = (SecretKey) MainActivity.keyStore.getKey(MainActivity.KEY_NAME,
                            null);
                } catch (KeyStoreException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (UnrecoverableKeyException e) {
                    e.printStackTrace();
                }
                try {
                    MainActivity.cipher.init(Cipher.DECRYPT_MODE, key, spec);
                } catch (InvalidAlgorithmParameterException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                }
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        decodedData = MainActivity.cipher.doFinal(Base64.getDecoder().decode(loadFromPref));
                    }
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                }


                try {
                    unencryptedString = new String(decodedData, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                oneview.setText(unencryptedString);





        String loadFromPref = MainActivity.utilsNote.getString("Note","");
        secondview.setText(loadFromPref);



    }
}