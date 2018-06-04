package com.example.yebuo.organizerbiznesowy.View;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.yebuo.organizerbiznesowy.Model.Projekt;
import com.example.yebuo.organizerbiznesowy.Model.Resource;
import com.example.yebuo.organizerbiznesowy.Model.Zadanie;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
    ListView listProjView;
    private ArrayAdapter adapter;
    private List<Projekt> lProjekty;
    private List<Zadanie> lZadania;
    private List<String> lProjektyNames;
    AlertDialog.Builder alert;
    String projekt;


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

//        Button btnCreateNotification = (Button) findViewById(R.id.btnCreateNotification);
//        btnCreateNotification.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                createNotification();
//            }
//        });


            Intent alarmIntent = new Intent(this, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 20);
            calendar.set(Calendar.MINUTE, 00);
            calendar.set(Calendar.SECOND, 00);

            manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);




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
            loadProjekt();
        } else if (idx == R.id.notatkiGrup) {
            loadNotatkiGrup();
        } else if (idx == R.id.listyGrup) {
            loadZadania();
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
                //TextView textView = findViewById(R.id.mainTextView);
                //textView.setText(user.getTelefon());
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

    public void loadProjekt(){
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lProjekty = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.child("projekty").getChildren()) {
                    for(DataSnapshot s : snapshot.child("osoby").getChildren()){
                        if (Objects.equals(s.getKey(), account.getId())){
                            Projekt projekt = new Projekt();
                            projekt.setDaneTermRozp(snapshot.child("dane").child("termrozp").getValue(String.class));
                            projekt.setDaneTermZak(snapshot.child("dane").child("termzak").getValue(String.class));
                            projekt.setDaneTytul(snapshot.child("dane").child("tytul").getValue(String.class));
                            projekt.setUid(snapshot.getKey());
                            lProjekty.add(projekt);
                        }
                    }

                }
                Intent intent = new Intent(MainActivity.this, ProjektActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("projekty", (ArrayList<? extends Parcelable>) lProjekty); //to debug change here list of exercises
                intent.putExtras(bundle);
                startActivity(intent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void loadZadania(){
        lProjektyNames = new ArrayList<>();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lProjekty = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.child("projekty").getChildren()) {
                    for(DataSnapshot s : snapshot.child("osoby").getChildren()){
                        if (Objects.equals(s.getKey(), account.getId())){
                            Projekt projekt = new Projekt();
                            projekt.setDaneTermRozp(snapshot.child("dane").child("termrozp").getValue(String.class));
                            projekt.setDaneTermZak(snapshot.child("dane").child("termzak").getValue(String.class));
                            projekt.setDaneTytul(snapshot.child("dane").child("tytul").getValue(String.class));
                            projekt.setUid(snapshot.getKey());
                            lProjekty.add(projekt);
                        }
                    }
                }
                alert = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.custom_dialog_zadania, null);
                alert.setView(dialogView);
                listProjView = dialogView.findViewById(R.id.itemsProjListView);
                alert.setMessage("");
                alert.setTitle("Wybierz projekt");

                ArrayAdapter tempAdapter;
                if (!(lProjekty != null && lProjekty.isEmpty())) {
                    for (int i = 0; i <lProjekty.size(); i++){
                        lProjektyNames.add(lProjekty.get(i).getDaneTytul());
                    }
                    tempAdapter = new ArrayAdapter<>(MainActivity.this, R.layout.item, R.id.listItem, lProjektyNames);
                    listProjView.setAdapter(tempAdapter);
                }

                listProjView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        lZadania = new ArrayList<>();
                        projekt = lProjekty.get(i).getUid();
                        myRef.child("projekty").child(lProjekty.get(i).getUid()).child("zadania").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                    Zadanie zadanie = new Zadanie();
                                    zadanie.setUid(snapshot.getKey());
                                    zadanie.setTresc(snapshot.child("tresc").getValue().toString());
                                    zadanie.setOsoba(snapshot.child("osoba").getValue().toString());
                                    lZadania.add(zadanie);
                                }
                                Intent intent = new Intent(MainActivity.this, ZadanieActivity.class);
                                Bundle bundle = new Bundle();
//                        bundle.putParcelableArrayList("zadania", (ArrayList<? extends Parcelable>) lZadania);
//                        bundle.putParcelableArrayList("projekt", (ArrayList<? extends Parcelable>) lZadania);
                                bundle.putString("projekt", projekt);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });


                alert.show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void loadNotatkiGrup(){
        lProjektyNames = new ArrayList<>();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lProjekty = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.child("projekty").getChildren()) {
                    for(DataSnapshot s : snapshot.child("osoby").getChildren()){
                        if (Objects.equals(s.getKey(), account.getId())){
                            Projekt projekt = new Projekt();
                            projekt.setDaneTermRozp(snapshot.child("dane").child("termrozp").getValue(String.class));
                            projekt.setDaneTermZak(snapshot.child("dane").child("termzak").getValue(String.class));
                            projekt.setDaneTytul(snapshot.child("dane").child("tytul").getValue(String.class));
                            projekt.setUid(snapshot.getKey());
                            lProjekty.add(projekt);
                        }
                    }
                }
                alert = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.custom_dialog_zadania, null);
                alert.setView(dialogView);
                listProjView = dialogView.findViewById(R.id.itemsProjListView);
                alert.setMessage("");
                alert.setTitle("Wybierz projekt");

                ArrayAdapter tempAdapter;
                if (!(lProjekty != null && lProjekty.isEmpty())) {
                    for (int i = 0; i <lProjekty.size(); i++){
                        lProjektyNames.add(lProjekty.get(i).getDaneTytul());
                    }
                    tempAdapter = new ArrayAdapter<>(MainActivity.this, R.layout.item, R.id.listItem, lProjektyNames);
                    listProjView.setAdapter(tempAdapter);
                }

                listProjView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        lResources = new ArrayList<>();
                        projekt = lProjekty.get(i).getUid();
                        myRef.child("projekty").child(lProjekty.get(i).getUid()).child("zasoby").child("notatki").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                    Resource resource = new Resource();
                                    resource.setUid(snapshot.getKey());
                                    resource.setUrl(snapshot.child("url").getValue().toString());
                                    resource.setNazwa(snapshot.child("nazwa").getValue().toString());
                                    lResources.add(resource);
                                }
                                Intent intent = new Intent(MainActivity.this, NotatkiActivityGroup.class);
                                Bundle bundle = new Bundle();
//                        bundle.putParcelableArrayList("zadania", (ArrayList<? extends Parcelable>) lZadania);
//                        bundle.putParcelableArrayList("projekt", (ArrayList<? extends Parcelable>) lZadania);
                                bundle.putString("projekt", projekt);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });


                alert.show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

//    private void createNotification() {
//
//        Intent intent = new Intent(this, MainActivity.class);
//        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
//
//        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_menu_grup);
//
//        Notification noti = new NotificationCompat.Builder(this)
//                .setContentTitle("Nowa wiadomość")
//                .setContentText("Temat wiadomości")
//                .setTicker("Masz wiadomość")
//                .setSmallIcon(android.R.drawable.ic_dialog_info)
//                .setLargeIcon(icon)
//                .setAutoCancel(true)
//                .setContentIntent(pIntent)
//                .build();
//
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//
//        notificationManager.notify(0, noti);
//    }
}
