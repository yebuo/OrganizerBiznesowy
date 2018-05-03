package com.example.yebuo.organizerbiznesowy.View;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.yebuo.organizerbiznesowy.Model.Resource;
import com.example.yebuo.organizerbiznesowy.R;
import com.example.yebuo.organizerbiznesowy.Model.User;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private NavigationView mNavigationView;
    GoogleSignInAccount account;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    private List<Resource> lResources;
    ListView listView;
    private ArrayAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listView = findViewById(R.id.itemsListView);

        account = GoogleSignIn.getLastSignedInAccount(this);

//        FloatingActionButton fab = findViewById(R.id.fab);
//        alert = new AlertDialog.Builder(this);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//                final EditText edittext = new EditText(MainActivity.this);
//                alert.setMessage("Enter Your Message");
//                alert.setTitle("Enter Your Title");
//
//                alert.setView(edittext);
//
//                alert.setPositiveButton("Yes Option", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        //What ever you want to do with the value
//                        //Editable YouEditTextValue = edittext.getText();
//                        //OR
//                        //String YouEditTextValue = edittext.getText().toString();
//                    }
//                });
//
//                alert.setNegativeButton("No Option", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        // what ever you want to do with No option.
//                    }
//                });
//
//                alert.show();
//            }
//        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        updateUI(account);

        lResources = new ArrayList<>();
//
//        lResources.add("a");
//        lResources.add("b");
//        lResources.add("c");
//
//
//        adapter = new ArrayAdapter<>(this, R.layout.item, R.id.listItem, lResources);
//        listView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int idx = item.getItemId();

        if (idx == R.id.notatkiUser) {
            loadNotatki();
        } else if (idx == R.id.listyUser) {
            getData();
        } else if (idx == R.id.plikiUser) {
            loadFiles();
        } else if (idx == R.id.grupy) {

        } else if (idx == R.id.notatkiGrup) {

        } else if (idx == R.id.listyGrup) {

        } else if (idx == R.id.plikiGrup) {

        }else if (idx == R.id.nav_share) {

        } else if (idx == R.id.nav_send) {
            signOut();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void updateUI(GoogleSignInAccount user){
        TextView mail = mNavigationView.getHeaderView(0).findViewById(R.id.mailTextView);
        mail.setText(user.getEmail());
        TextView userName = mNavigationView.getHeaderView(0).findViewById(R.id.userTextView);
        userName.setText(user.getDisplayName());
    }

    private void signOut(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>(){
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });
    }

    private void getData(){
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                User user = dataSnapshot.child("osoby").child("O1").child("dane").getValue(User.class);
                TextView textView = findViewById(R.id.mainTextView);
                textView.setText(user.getTelefon());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });
    }

    public void loadNotatki(){
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lResources = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.child("osoby").child(account.getId()).child("zasoby").child("notatki").getChildren()) {
                    Resource exercise = snapshot.getValue(Resource.class);

                    lResources.add(exercise);

                }
                Intent intent = new Intent(MainActivity.this, NotatkiActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("notatki", (ArrayList<? extends Parcelable>) lResources); //to debug change here list of exercises
                intent.putExtras(bundle);
                startActivity(intent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void loadFiles(){
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lResources = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.child("osoby").child(account.getId()).child("zasoby").child("pliki").getChildren()) {
                    Resource exercise = snapshot.getValue(Resource.class);

                    lResources.add(exercise);

                }
                Intent intent = new Intent(MainActivity.this, PlikiActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("pliki", (ArrayList<? extends Parcelable>) lResources); //to debug change here list of exercises
                intent.putExtras(bundle);
                startActivity(intent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
