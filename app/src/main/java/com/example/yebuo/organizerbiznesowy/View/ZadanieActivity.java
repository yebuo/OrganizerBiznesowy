package com.example.yebuo.organizerbiznesowy.View;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.yebuo.organizerbiznesowy.Model.Resource;
import com.example.yebuo.organizerbiznesowy.Model.Zadanie;
import com.example.yebuo.organizerbiznesowy.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Yebuo on 02.05.2018.
 */

public class ZadanieActivity extends AppCompatActivity {

    private ArrayAdapter adapter;
    AlertDialog.Builder alert;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final FirebaseStorage storage = FirebaseStorage.getInstance();
    DatabaseReference dRef = database.getReference();
    StorageReference sRef = storage.getReference();
    GoogleSignInAccount account;

    private List<Zadanie> lZadania;
    private List<String> lZadaniaNames;
    ListView listView;
    EditText editTextNazwa;
    EditText editTextTresc;
    TextView textViewNazwa;
    TextView textViewTresc;
    private String projekt;
    String osoba;
    int listViewCounter = 0;


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if(v.getId() == R.id.itemsListView){
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_list_zadanie, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()){
            case R.id.editZadanie:
                LayoutInflater inflater = ZadanieActivity.this.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
                alert.setView(dialogView);
                editTextNazwa = dialogView.findViewById(R.id.editNazwa);
                editTextTresc = dialogView.findViewById(R.id.editTresc);
                dRef.child("projekty").child(projekt).child("zadania").child(lZadania.get(info.position).getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            editTextNazwa.setText(dataSnapshot.child("osobaEmail").getValue().toString());
                            editTextTresc.setText(dataSnapshot.child("tresc").getValue().toString());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                alert.setMessage("");
                alert.setTitle("Edytuj zadanie");


                alert.setPositiveButton("Anuluj", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });

                alert.setNegativeButton("Zapisz", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
//                        String key = String.valueOf(dRef.child("projekty").child(lProjekty.get(listView.getSelectedItemPosition()).getUid()));
                        String key = String.valueOf(dRef.child("projekty").child(projekt).child("zadania").child(lZadania.get(info.position).getUid()).getKey());
                        DatabaseReference tempRef = dRef.child("projekty").child(projekt).child("zadania").child(key);
                        tempRef.child("osobaEmail").setValue(editTextNazwa.getText().toString());
                        tempRef.child("tresc").setValue(editTextTresc.getText().toString());
                        finish();
                    }
                });

                alert.show();
                return true;
            case R.id.deleteZadanie:
                dRef.child("projekty").child(projekt).child("zadania").child(lZadania.get(info.position).getUid()).removeValue();
                return true;
            case R.id.zmienStan:
                dRef.child("projekty").child(projekt).child("zadania").child(lZadania.get(info.position).getUid()).child("stan").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String stan =  dataSnapshot.getValue().toString();
                        if (Objects.equals(stan, "0")) {
                            dRef.child("projekty").child(projekt).child("zadania").child(lZadania.get(info.position).getUid()).child("stan").setValue(1);
//                            listView.getChildAt(info.position).setBackgroundColor(Color.GREEN);
                            finish();
                        }
                        else if (Objects.equals(stan, "1")) {
                            dRef.child("projekty").child(projekt).child("zadania").child(lZadania.get(info.position).getUid()).child("stan").setValue(0);
//                            listView.getChildAt(info.position).setBackgroundColor(Color.RED);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notatki);
        account = GoogleSignIn.getLastSignedInAccount(this);

        lZadania = new ArrayList<>();
        lZadaniaNames = new ArrayList<>();
        listView = findViewById(R.id.itemsListView);
//        lZadania = getIntent().getExtras().getParcelableArrayList("zadania");
        projekt = getIntent().getExtras().getString("projekt");

        FloatingActionButton fab = findViewById(R.id.fab);
        alert = new AlertDialog.Builder(this);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Wprowadź wartości", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                LayoutInflater inflater = ZadanieActivity.this.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
                alert.setView(dialogView);
                editTextNazwa = dialogView.findViewById(R.id.editNazwa);
                editTextTresc = dialogView.findViewById(R.id.editTresc);
                textViewNazwa = dialogView.findViewById(R.id.showNazwa);
                textViewTresc = dialogView.findViewById(R.id.showTresc);
                textViewNazwa.setVisibility(View.INVISIBLE);
                textViewTresc.setVisibility(View.INVISIBLE);
                alert.setMessage("");
                alert.setTitle("Nowe zadanie");


                alert.setPositiveButton("Anuluj", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });

                alert.setNegativeButton("Zapisz", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dRef.child("osoby").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    if (snapshot.child("dane").child("email").getValue().toString().toLowerCase().equals(editTextNazwa.getText().toString().toLowerCase())){
                                        osoba = snapshot.getKey();
                                    }
                                }
                                String key = dRef.child("projekty").child(projekt).child("zadania").push().getKey();
                                DatabaseReference tempRef = dRef.child("projekty").child(projekt).child("zadania").child(key);
                                Zadanie zadanie = new Zadanie();
                                zadanie.setOsoba(osoba);
                                zadanie.setOsobaEmail(editTextNazwa.getText().toString().toLowerCase());
                                zadanie.setUid(key);
                                zadanie.setStan("0");
                                zadanie.setTresc(editTextTresc.getText().toString());
                                tempRef.setValue(zadanie);
                                finish();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                });

                alert.show();
            }
        });

        dRef.child("projekty").child(projekt).child("zadania").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Zadanie zadanie = new Zadanie();
                    zadanie.setUid(snapshot.getKey());
                    zadanie.setTresc(snapshot.child("tresc").getValue().toString());
                    zadanie.setOsoba(snapshot.child("osoba").getValue().toString());
                    zadanie.setOsobaEmail(snapshot.child("osobaEmail").getValue().toString());
                    zadanie.setStan(snapshot.child("stan").getValue().toString());
                    lZadania.add(zadanie);
                }
                if (!(lZadania == null || lZadania.isEmpty())) {
                    for (int i = 0; i < lZadania.size(); i++){
                        lZadaniaNames.add(lZadania.get(i).getUid());
                    }
                    adapter = new ArrayAdapter<>(ZadanieActivity.this, R.layout.item, R.id.listItem, lZadaniaNames);
                    listView.setAdapter(adapter);
//                    koloruj();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        registerForContextMenu(listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                LayoutInflater inflater = ZadanieActivity.this.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
                alert.setView(dialogView);
                textViewNazwa = dialogView.findViewById(R.id.showNazwa);
                textViewTresc = dialogView.findViewById(R.id.showTresc);
                editTextNazwa = dialogView.findViewById(R.id.editNazwa);
                editTextTresc = dialogView.findViewById(R.id.editTresc);
                editTextNazwa.setVisibility(View.INVISIBLE);
                editTextTresc.setVisibility(View.INVISIBLE);
                textViewTresc.setMovementMethod(new ScrollingMovementMethod());
                koloruj();
                dRef.child("projekty").child(projekt).child("zadania").child(lZadania.get(i).getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            textViewNazwa.setText(dataSnapshot.child("osobaEmail").getValue().toString());
                            textViewTresc.setText(dataSnapshot.child("tresc").getValue().toString());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                alert.show();
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void koloruj(){
        for (listViewCounter = 0; listViewCounter < listView.getCount(); listViewCounter++){
            if (lZadania.get(listViewCounter).getStan() == "0")
                listView.getChildAt(listViewCounter).setBackgroundColor(Color.RED);
            if (lZadania.get(listViewCounter).getStan() == "1")
                listView.getChildAt(listViewCounter).setBackgroundColor(Color.GREEN);
//            if (lZadania.get(listViewCounter).getStan() == "2")
//                listView.getChildAt(listViewCounter).setBackgroundColor(Color.GREEN);
        }
        listViewCounter = 0;
    }
}
