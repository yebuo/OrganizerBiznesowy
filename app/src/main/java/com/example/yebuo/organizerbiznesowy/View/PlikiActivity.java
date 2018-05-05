package com.example.yebuo.organizerbiznesowy.View;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yebuo.organizerbiznesowy.Model.Resource;
import com.example.yebuo.organizerbiznesowy.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yebuo on 02.05.2018.
 */

public class PlikiActivity extends AppCompatActivity {

    private ArrayAdapter adapter;
    AlertDialog.Builder alert;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final FirebaseStorage storage = FirebaseStorage.getInstance();
    DatabaseReference dRef = database.getReference();
    StorageReference sRef = storage.getReference();
    GoogleSignInAccount account;

    private List<Resource> lResources;
    private List<String> lResourcesNames;
    ListView listView;

    public static final int REQUEST_CODE = 1234;
    private Uri uri;
    TextView filenameTextView;


    File file = null;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if(v.getId() == R.id.itemsListView){
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_list_pliki, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()){
            case R.id.deletePlik:
                dRef.child("osoby").child(account.getId()).child("zasoby").child("pliki").child(lResources.get(info.position).getUid()).removeValue();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pliki);
        account = GoogleSignIn.getLastSignedInAccount(this);

        lResources = new ArrayList<>();
        lResourcesNames = new ArrayList<>();
        listView = findViewById(R.id.itemsListView);
        lResources = getIntent().getExtras().getParcelableArrayList("pliki");

//        Button buttonFileExplorer = findViewById(R.id.buttonSearchFile);
//        buttonFileExplorer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                fileView(view);
//            }
//        });

        FloatingActionButton fab = findViewById(R.id.fab);
        alert = new AlertDialog.Builder(this);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                LayoutInflater inflater = PlikiActivity.this.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.custom_dialog_pliki, null);
                alert.setView(dialogView);
                final EditText editTextNazwa = dialogView.findViewById(R.id.editNazwa);
                final EditText editTextTresc = dialogView.findViewById(R.id.editTresc);
                alert.setMessage("");
                alert.setTitle("Dodaj plik");
                Button buttonFileExplorer = dialogView.findViewById(R.id.buttonSearchFile);
                filenameTextView = dialogView.findViewById(R.id.filenameTextView);
                buttonFileExplorer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fileView(view);
                    }
                });

                alert.setPositiveButton("Anuluj", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });

                alert.setNegativeButton("Zapisz", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        uploadFile(view);
                        //ref.child("osoby").child(account.getId()).child("zasoby").child("notatki").push().setValue(new Resource(editTextNazwa.getText().toString(), editTextTresc.getText().toString()));
                        //finish();
                    }
                });

                alert.show();
            }
        });

//        lResourcesNames.add("x");
//        lResourcesNames.add("y");
//        lResourcesNames.add("x");
//
//        adapter = new ArrayAdapter<>(this, R.layout.item, R.id.listItem, lResourcesNames);
//        listView.setAdapter(adapter);

        if (!(lResources != null && lResources.isEmpty())) {
            for (int i = 0; i <lResources.size(); i++){
                lResourcesNames.add(lResources.get(i).getNazwa());
            }
            adapter = new ArrayAdapter<>(this, R.layout.item, R.id.listItem, lResourcesNames);
            listView.setAdapter(adapter);
        }

        registerForContextMenu(listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    file = File.createTempFile("image"+ System.currentTimeMillis(), "jpg");
                } catch (IOException e) {
                    e.printStackTrace();
                }sRef.child("files").child(account.getId()).child((String)adapterView.getItemAtPosition(i))
                .getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        ImageView img = findViewById(R.id.mainImageView);
                        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                        img.setImageBitmap(bitmap);
                        FileOutputStream fileOutputStream;
                        try {
                            fileOutputStream = openFileOutput(file.getName(), Context.MODE_PRIVATE);
                            fileOutputStream.write(("asd").getBytes());
                            fileOutputStream.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                // getDownloadUrl().toString()
                //Uri tempStor = sRef.child("files").child(account.getId()).child(adapterView.getItemAtPosition(i).toString()).getDownloadUrl();
//                tempStor.getFile() .addOnSuccessListener(new OnSuccessListener<Uri>() {
//                    @Override
//                    public void onSuccess(Uri uri) {
//
//                    }
//                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void fileView(View v){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            startActivityForResult(Intent.createChooser(intent, "Wybierz plik"), REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null){
            uri = data.getData();
            filenameTextView.setText(getFileName(uri));
            //uploadFile(this.getCurrentFocus());
        }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public String getFileExt(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void uploadFile(View v){
        if (uri != null){
            StorageReference sRef = this.sRef.child("files").child(account.getId()).child(getFileName(uri));
            sRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getApplicationContext(), "Wysłano", Toast.LENGTH_SHORT).show();
                    String key = dRef.child("osoby").child(account.getId()).child("zasoby").child("pliki").push().getKey();
                    DatabaseReference tempRef = dRef.child("osoby").child(account.getId()).child("zasoby").child("pliki").child(key);
                    tempRef.setValue(new Resource(getFileName(uri), taskSnapshot.getDownloadUrl().toString(),key));
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(getApplicationContext(), "Niepowodzenie", Toast.LENGTH_SHORT).show();
                }
            })
            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                }
            });
        }
    }
}
