package com.example.android.googlechallenge;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.googlechallenge.Adapters.ThoughtRecyclerAdapter;
import com.example.android.googlechallenge.Models.Thought;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ThoughtsActivity extends AppCompatActivity {
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private ThoughtRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thoughts);
        // setup the add btn
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ThoughtsActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
        // setup the recycler view
        RecyclerView recyclerView = findViewById(R.id.recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        List<Thought> mThoughts = new ArrayList<>();
        adapter = new ThoughtRecyclerAdapter(this, mThoughts);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        ThoughtRecyclerAdapter.ThoughtItemsDivider divider = new ThoughtRecyclerAdapter.ThoughtItemsDivider(this);
        recyclerView.addItemDecoration(divider);

        mAuth = FirebaseAuth.getInstance();
        populateData();
    }

    private void populateData() {
        if (mAuth.getCurrentUser() != null) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("thoughts").child(mAuth.getCurrentUser().getUid());

            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<Thought> thoughts = new ArrayList<>();
                    adapter.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        postSnapshot.getChildren();
                        Thought thought = postSnapshot.getValue(Thought.class);
                        thoughts.add(thought);
                    }
                    if (!thoughts.isEmpty()) {
                        adapter.addAll(thoughts);
                        findViewById(R.id.error_msg).setVisibility(View.GONE);
                    } else
                        findViewById(R.id.error_msg).setVisibility(View.VISIBLE);
                    findViewById(R.id.progress_bar).setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    TextView errorTv = findViewById(R.id.error_msg);
                    errorTv.setText(databaseError.getMessage());
                }
            });
        } else {
            Intent loginIntent = new Intent(this, MainActivity.class);
            startActivity(loginIntent);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.thoughts_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        mAuth.signOut();
        mGoogleSignInClient.revokeAccess();
        finish();
    }
}
