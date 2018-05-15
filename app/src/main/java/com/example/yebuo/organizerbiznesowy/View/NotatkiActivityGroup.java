package com.example.yebuo.organizerbiznesowy.View;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * Created by Yebuo on 02.05.2018.
 */

public class NotatkiActivityGroup extends AppCompatActivity {

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
    EditText editTextNazwa;
    EditText editTextTresc;
    TextView textViewNazwa;
    TextView textViewTresc;
    private String projekt;


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if(v.getId() == R.id.itemsListView){
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_list_notatki, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()){
            case R.id.editNotatka:
                LayoutInflater inflater = NotatkiActivityGroup.this.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
                alert.setView(dialogView);
                editTextNazwa = dialogView.findViewById(R.id.editNazwa);
                editTextTresc = dialogView.findViewById(R.id.editTresc);
                dRef.child("projekty").child(projekt).child("zasoby").child("notatki").child(lResources.get(info.position).getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            editTextNazwa.setText(dataSnapshot.child("nazwa").getValue().toString());
                            editTextTresc.setText(dataSnapshot.child("url").getValue().toString());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                alert.setMessage("");
                alert.setTitle("Edytuj notatkę");


                alert.setPositiveButton("Anuluj", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });

                alert.setNegativeButton("Zapisz", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
//                        String key = String.valueOf(dRef.child("projekty").child(lProjekty.get(listView.getSelectedItemPosition()).getUid()));
                        String key = String.valueOf(dRef.child("projekty").child(projekt).child("zasoby").child("notatki").child(lResources.get(info.position).getUid()).getKey());
                        DatabaseReference tempRef = dRef.child("projekty").child(projekt).child("zasoby").child("notatki").child(key);
                        tempRef.child("nazwa").setValue(editTextNazwa.getText().toString());
                        tempRef.child("url").setValue(editTextTresc.getText().toString());
                        finish();
                    }
                });

                alert.show();
                return true;
            case R.id.deleteNotatka:
                dRef.child("projekty").child(projekt).child("zasoby").child("notatki").child(lResources.get(info.position).getUid()).removeValue();
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

        lResources = new ArrayList<>();
        lResourcesNames = new ArrayList<>();
        listView = findViewById(R.id.itemsListView);
        projekt = getIntent().getExtras().getString("projekt");

        FloatingActionButton fab = findViewById(R.id.fab);
        alert = new AlertDialog.Builder(this);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Wprowadź wartości", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                LayoutInflater inflater = NotatkiActivityGroup.this.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
                alert.setView(dialogView);
                editTextNazwa = dialogView.findViewById(R.id.editNazwa);
                editTextTresc = dialogView.findViewById(R.id.editTresc);
                textViewNazwa = dialogView.findViewById(R.id.showNazwa);
                textViewTresc = dialogView.findViewById(R.id.showTresc);
                textViewNazwa.setVisibility(View.INVISIBLE);
                textViewTresc.setVisibility(View.INVISIBLE);
                alert.setMessage("");
                alert.setTitle("Nowa notatka");


                alert.setPositiveButton("Anuluj", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });

                alert.setNegativeButton("Zapisz", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String key = dRef.child("projekty").child(projekt).child("zasoby").child("notatki").push().getKey();
                        DatabaseReference tempRef = dRef.child("projekty").child(projekt).child("zasoby").child("notatki").child(key);
                        tempRef.setValue(new Resource(editTextNazwa.getText().toString(), editTextTresc.getText().toString(), key));
                        finish();
                    }
                });

                alert.show();
            }
        });

//        if (!(lResources == null || lResources.isEmpty())) {
//            for (int i = 0; i <lResources.size(); i++){
//                lResourcesNames.add(lResources.get(i).getNazwa());
//            }
//            adapter = new ArrayAdapter<>(this, R.layout.item, R.id.listItem, lResourcesNames);
//            listView.setAdapter(adapter);
//        }

        dRef.child("projekty").child(projekt).child("zasoby").child("notatki").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Resource resource = new Resource();
                    resource.setUid(snapshot.getKey());
                    resource.setUrl(snapshot.child("url").getValue().toString());
                    resource.setNazwa(snapshot.child("nazwa").getValue().toString());
//                    resource.setOsobaEmail(snapshot.child("osobaEmail").getValue().toString());
//                    resource.setStan(snapshot.child("stan").getValue().toString());
                    lResources.add(resource);
                }
                if (!(lResources == null || lResources.isEmpty())) {
                    for (int i = 0; i < lResources.size(); i++){
                        lResourcesNames.add(lResources.get(i).getNazwa());
                    }
                    adapter = new ArrayAdapter<String>(NotatkiActivityGroup.this, R.layout.item, R.id.listItem, lResourcesNames){
                        @NonNull
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            View view = super.getView(position, convertView, parent);
                            TextView textViewItem = view.findViewById(R.id.listItem);
//                            textViewItem.setBackgroundColor(Color.YELLOW);

                            return view;
                        }
                    };
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
                LayoutInflater inflater = NotatkiActivityGroup.this.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
                alert.setView(dialogView);
                textViewNazwa = dialogView.findViewById(R.id.showNazwa);
                textViewTresc = dialogView.findViewById(R.id.showTresc);
                editTextNazwa = dialogView.findViewById(R.id.editNazwa);
                editTextTresc = dialogView.findViewById(R.id.editTresc);
                editTextNazwa.setVisibility(View.INVISIBLE);
                editTextTresc.setVisibility(View.INVISIBLE);
                textViewTresc.setMovementMethod(new ScrollingMovementMethod());
                dRef.child("projekty").child(projekt).child("zasoby").child("notatki").child(lResources.get(i).getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            textViewNazwa.setText(dataSnapshot.child("nazwa").getValue().toString());
                            textViewTresc.setText(dataSnapshot.child("url").getValue().toString());
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


}
