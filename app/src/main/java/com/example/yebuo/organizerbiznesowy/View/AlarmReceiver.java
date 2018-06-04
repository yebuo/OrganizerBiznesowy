package com.example.yebuo.organizerbiznesowy.View;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.example.yebuo.organizerbiznesowy.Model.Projekt;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Created by Yebuo on 04.06.2018.
 */

public class AlarmReceiver extends BroadcastReceiver {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    GoogleSignInAccount account;
    List<Projekt> lProjekty = new ArrayList<>();


    @Override
    public void onReceive(final Context context, Intent intent) {


        account = GoogleSignIn.getLastSignedInAccount(context);

        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        final Intent repeatIntent = new Intent(context, MainActivity.class);
        repeatIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);




        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
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
                Projekt shortestTerm = new Projekt();
                if (lProjekty.size() > 1) {
                    shortestTerm = lProjekty.get(0);
                    DateFormat format = new SimpleDateFormat("yyyy-mm-dd", Locale.getDefault());
                    Date date;
                    try {
                        date = format.parse(shortestTerm.getDaneTermZak());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    for (Projekt proj : lProjekty) {
                        try {
                            if (format.parse(proj.getDaneTermZak()).compareTo(format.parse(shortestTerm.getDaneTermZak())) < 0){
                                shortestTerm = proj;
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    PendingIntent pendingIntent = PendingIntent.getActivity(context,100,repeatIntent,PendingIntent.FLAG_UPDATE_CURRENT);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                            .setContentTitle(shortestTerm.getDaneTytul())
                            .setContentText("Niedługo dobiega końca - "+shortestTerm.getDaneTermZak())
                            .setSmallIcon(android.R.drawable.ic_dialog_info)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true);
                    notificationManager.notify(100,builder.build());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
