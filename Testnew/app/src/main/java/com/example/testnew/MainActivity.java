package com.example.testnew;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.io.*;
import java.net.*;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Locale;
public class MainActivity extends AppCompatActivity {
    Button b,rec;
    static TextView t;
    private static final int pic_id = 123;
    Button camera_open_id,listen;
    ImageView click_image_id;
    Socket s;
    PrintWriter pw;
    static String encodedImage;
    TextToSpeech t1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        b = findViewById(R.id.add);
        listen=findViewById(R.id.listen);
        rec=findViewById(R.id.recieve);
        camera_open_id = (Button) findViewById(R.id.image);
        click_image_id = (ImageView) findViewById(R.id.capturedimage);
        MainActivity.t = findViewById(R.id.text);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send send = new send();
                send.execute();

            }
        });

        camera_open_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent camera_intent
                        = new Intent(MediaStore
                        .ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera_intent, pic_id);
            }
        });

        rec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recieve r=new recieve();
                r.execute();
            }
        });

        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status!= TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.US);
                }
            }
        });

        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = t.getText().toString();
                int speechStatus = t1.speak(data, TextToSpeech.QUEUE_FLUSH, null);

                if (speechStatus == TextToSpeech.ERROR) {
                    Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == pic_id) {
            try {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                click_image_id.setImageBitmap(photo);
                click_image_id.setRotation(90);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.JPEG, 100,baos);
                byte[] imageBytes = baos.toByteArray();
                encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                        }
            catch (Exception e){

            }
        }
    }
}

class send extends AsyncTask<Void,Void,Void>{

    Socket s;
    PrintWriter pw;
    BufferedReader input;

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            s= new Socket("192.168.43.114",8001);
            pw=new PrintWriter(s.getOutputStream());
            input = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String x=""+MainActivity.encodedImage;
            System.out.println(x.length());
            pw.write(x);
            pw.flush();
            pw.close();
            s.close();
        }
        catch (Exception e){

        }
        return null;
    }
}


class recieve extends AsyncTask<Void,Void,Void>{

    Socket s;
    PrintWriter pw;
    BufferedReader input;

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            s= new Socket("192.168.43.114",8001);
            InputStream stream = s.getInputStream();
            byte[] data = new byte[100];
            int count = stream.read(data);
            String message=new String(data);
            MainActivity.t.setText(""+ message);
            s.close();
        }
        catch (Exception e){

        }
        return null;
    }
}
