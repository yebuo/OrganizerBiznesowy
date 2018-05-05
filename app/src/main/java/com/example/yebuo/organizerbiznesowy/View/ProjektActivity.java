package com.example.yebuo.organizerbiznesowy.View;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yebuo.organizerbiznesowy.Model.Projekt;
import com.example.yebuo.organizerbiznesowy.Model.Resource;
import com.example.yebuo.organizerbiznesowy.Model.User;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Yebuo on 02.05.2018.
 */

public class ProjektActivity extends AppCompatActivity {

    private ArrayAdapter adapter;
    AlertDialog.Builder alert;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final FirebaseStorage storage = FirebaseStorage.getInstance();
    DatabaseReference dRef = database.getReference();
    StorageReference sRef = storage.getReference();
    GoogleSignInAccount account;

    private List<Projekt> lProjekty;
    private List<String> lProjektyNames;
    ListView listView;

    public static final int REQUEST_CODE = 1234;
    private Uri uri;
    TextView filenameTextView;
    EditText editTextNazwa;
    EditText editTextTermin;


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if(v.getId() == R.id.itemsListView){
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_list, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(final MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()){
            case R.id.addUser:

                finish();
                return true;
            case R.id.editProjekt:
                LayoutInflater inflater = ProjektActivity.this.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.custom_dialog_projekt, null);
                alert.setView(dialogView);
                editTextNazwa = dialogView.findViewById(R.id.editNazwa);
                editTextTermin = dialogView.findViewById(R.id.editTermin);
                dRef.child("projekty").child(lProjekty.get(info.position).getUid()).child("dane").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            editTextNazwa.setText(dataSnapshot.child("tytul").getValue().toString());
                            editTextTermin.setText(dataSnapshot.child("termzak").getValue().toString());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                alert.setMessage("");
                alert.setTitle("Edytuj projekt");


                alert.setPositiveButton("Anuluj", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });

                alert.setNegativeButton("Zapisz", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String key = String.valueOf(dRef.child("projekty").child(lProjekty.get(info.position).getUid()).getKey());
                        DatabaseReference tempRef = dRef.child("projekty").child(key).child("dane");
                        tempRef.child("tytul").setValue(editTextNazwa.getText().toString());
                        tempRef.child("termzak").setValue(editTextTermin.getText().toString());
                        finish();
                    }
                });

                alert.show();
                return true;
            case R.id.deleteUser:

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projekt);
        account = GoogleSignIn.getLastSignedInAccount(this);

        lProjekty = new ArrayList<>();
        lProjektyNames = new ArrayList<>();
        listView = findViewById(R.id.itemsListView);
        lProjekty = getIntent().getExtras().getParcelableArrayList("projekty");

        FloatingActionButton fab = findViewById(R.id.fab);
        alert = new AlertDialog.Builder(this);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                LayoutInflater inflater = ProjektActivity.this.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.custom_dialog_projekt, null);
                alert.setView(dialogView);
                final EditText editTextNazwa = dialogView.findViewById(R.id.editNazwa);
                final EditText editTextTermin = dialogView.findViewById(R.id.editTermin);
                alert.setMessage("");
                alert.setTitle("Nowy projekt");


                alert.setPositiveButton("Anuluj", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });

                alert.setNegativeButton("Zapisz", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String key = dRef.child("projekty").push().getKey();
                        DatabaseReference tempRef = dRef.child("projekty").child(key).child("dane");
                        tempRef.child("tytul").setValue(editTextNazwa.getText().toString());
                        tempRef.child("termzak").setValue(editTextTermin.getText().toString());
                        tempRef.child("termrozp").setValue(new Date().toString());
                        dRef.child("projekty").child(key).child("osoby").child(account.getId()).child("osoba").setValue(account.getId());
                        finish();
                    }
                });

                alert.show();
            }
        });

        if (!(lProjekty != null && lProjekty.isEmpty())) {
            for (int i = 0; i <lProjekty.size(); i++){
                lProjektyNames.add(lProjekty.get(i).getDaneTytul());
            }
            adapter = new ArrayAdapter<>(this, R.layout.item, R.id.listItem, lProjektyNames);
            listView.setAdapter(adapter);
        }

        registerForContextMenu(listView);

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
}
