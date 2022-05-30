package com.malikendsley.quipswap;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.FirebaseDatabase;
import com.malikendsley.firebaseutils.FirebaseHandler;
import com.malikendsley.firebaseutils.interfaces.GetRequestsListener;
import com.malikendsley.firebaseutils.interfaces.RequestClickListener;
import com.malikendsley.firebaseutils.secureadapters.SecureRequestAdapter;
import com.malikendsley.firebaseutils.secureschema.ExpandableListItem;

import java.util.ArrayList;

public class FriendRequestsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    TextView noFriendRequestsFlavor;
    //recycler
    RecyclerView requestRecycler;
    SecureRequestAdapter requestAdapter;
    //firebase setup
    ArrayList<ExpandableListItem> friendRequestList = new ArrayList<>();
    FirebaseHandler mdb2 = new FirebaseHandler(FirebaseDatabase.getInstance().getReference(), this);

    SwipeRefreshLayout swipeRefresh;

    View aboutView;
    View legalView;
    AlertDialog aboutDialog;
    AlertDialog legalDialog;

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_requests);


        //retrieve friend requests and populate
        updateFriends();

        //element setup
        findViewById(R.id.friendRequestsBackButton).setOnClickListener(view -> finish());
        noFriendRequestsFlavor = findViewById(R.id.noPendingRequestsFlavor);

        //friend request recycler setup
        requestRecycler = findViewById(R.id.requestList);
        requestRecycler.setHasFixedSize(true);
        requestRecycler.setLayoutManager(new LinearLayoutManager(this));

        //dialog views
        aboutView = getLayoutInflater().inflate(R.layout.about_dialog, null);
        legalView = getLayoutInflater().inflate(R.layout.legal_dialog, null);

        //create alertdialogs
        AlertDialog.Builder aboutBuilder = new AlertDialog.Builder(this);
        aboutBuilder.setView(aboutView);
        AlertDialog.Builder legalBuilder = new AlertDialog.Builder(this).setView(legalView);
        legalBuilder.setView(legalView);

        aboutDialog = aboutBuilder.create();
        legalDialog = legalBuilder.create();


        //about dialog buttons
        Button emailMeButton = aboutView.findViewById(R.id.emailMeButton);
        Button linkedInButton = aboutView.findViewById(R.id.linkedInButton);

        //legal dialog buttons
        Button privacyPolicyButton = legalView.findViewById(R.id.privacyPolicyButton);
        Button dataInquiryButton = legalView.findViewById(R.id.dataInquiryButton);

        privacyPolicyButton.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse("https://www.privacypolicies.com/live/c6452f28-3848-4b4a-8b8f-3798bcb59022"));
            startActivity(intent);
        });


        dataInquiryButton.setOnClickListener(view -> {
            String mailto = "mailto:malik.s.endsley@gmail.com" +
                    "?cc=" +
                    "&subject=" + Uri.encode("Data Inquiry - QuipSwap") +
                    "&body=" + Uri.encode("");
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse(mailto));

            try {
                startActivity(emailIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "Error opening email app", Toast.LENGTH_SHORT).show();
            }
        });

        linkedInButton.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse("https://www.linkedin.com/in/malik-endsley/"));
            startActivity(intent);
        });

        emailMeButton.setOnClickListener(view -> {
            String mailto = "mailto:malik.s.endsley@gmail.com" +
                    "?cc=" +
                    "&subject=" + Uri.encode("Feedback About QuipSwap") +
                    "&body=" + Uri.encode("");
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse(mailto));

            try {
                startActivity(emailIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "Error opening email app", Toast.LENGTH_SHORT).show();
            }
        });

        requestAdapter = new SecureRequestAdapter(friendRequestList, new RequestClickListener() {
            @Override
            public void onAcceptClicked(int position) {
                acceptFriend(position);
            }

            @Override
            public void onDenyClicked(int position) {
                denyFriend(position);
            }
        });
        requestRecycler.setAdapter(requestAdapter);

        swipeRefresh = findViewById(R.id.friendRequestSwipe);
        swipeRefresh.setOnRefreshListener(this);

    }

    //add options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_friend_requests, menu);

        return true;
    }

    //handle menu select
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.friendRequestsAppBar:
                finish();
                break;
            case R.id.legalOption:
                legalDialog.show();
                break;
            case R.id.aboutUsOption:
                aboutDialog.show();
                break;
            case R.id.settingsOption:
                Intent myIntent = new Intent(this, SettingsActivity.class);
                startActivity(myIntent);
                break;
        }
        return true;
    }

    void acceptFriend(int position) {
        String user = (String) friendRequestList.get(position).getObject();
        mdb2.acceptFriend(user, () -> deleteFriend(position));
        Toast.makeText(this, "Request Accepted", Toast.LENGTH_SHORT).show();
    }

    void denyFriend(int position) {
        String user = (String) friendRequestList.get(position).getObject();
        mdb2.denyFriend(user);
        Toast.makeText(this, "Request Denied", Toast.LENGTH_SHORT).show();
        deleteFriend(position);
    }

    void deleteFriend(int position) {
        friendRequestList.remove(position);
        requestAdapter.notifyItemRemoved(position);
        noFriendRequestsFlavor.setVisibility(friendRequestList.isEmpty() ? View.VISIBLE : View.GONE);

    }

    @Override
    public void onRefresh() {
        updateFriends();
    }

    void updateFriends(){
        mdb2.getFriendRequests(new GetRequestsListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onRequests(ArrayList<String> requestList) {
                if(swipeRefresh.isRefreshing()){
                    swipeRefresh.setRefreshing(false);
                }
                if (requestList.isEmpty()) {
                    noFriendRequestsFlavor.setVisibility(View.VISIBLE);
                } else {
                    noFriendRequestsFlavor.setVisibility(View.GONE);
                    friendRequestList.clear();
                    for (String username : requestList) {
                        friendRequestList.add(new ExpandableListItem(username));
                    }
                    requestAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onGetFail(Exception e) {
                if(swipeRefresh.isRefreshing()){
                    swipeRefresh.setRefreshing(false);
                }
                noFriendRequestsFlavor.setVisibility(View.VISIBLE);
                Toast.makeText(FriendRequestsActivity.this, "Trouble connecting to the database", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
