package com.example.yebuo.organizerbiznesowy.View;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.example.yebuo.organizerbiznesowy.Model.Resource;
import com.example.yebuo.organizerbiznesowy.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yebuo on 02.05.2018.
 */

public class NotatkiActivity extends AppCompatActivity {

    private ArrayAdapter adapter;
    AlertDialog.Builder alert;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    GoogleSignInAccount account;

    private List<Resource> lResources;
    private List<String> lResourcesNames;
    ListView listView;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notatki);
        account = GoogleSignIn.getLastSignedInAccount(this);

        lResources = new ArrayList<>();
        lResourcesNames = new ArrayList<>();
        listView = findViewById(R.id.itemsListView);
        lResources = getIntent().getExtras().getParcelableArrayList("notatki");

        FloatingActionButton fab = findViewById(R.id.fab);
        alert = new AlertDialog.Builder(this);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                LayoutInflater inflater = NotatkiActivity.this.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
                alert.setView(dialogView);
                final EditText editTextNazwa = dialogView.findViewById(R.id.editNazwa);
                final EditText editTextTresc = dialogView.findViewById(R.id.editTresc);
                alert.setMessage("");
                alert.setTitle("Nowa notatka");


                alert.setPositiveButton("Anuluj", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });

                alert.setNegativeButton("Zapisz", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        ref.child("osoby").child(account.getId()).child("zasoby").child("notatki").push().setValue(new Resource(editTextNazwa.getText().toString(), editTextTresc.getText().toString()));
                        finish();
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
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
