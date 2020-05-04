package com.example.freq;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private static final int SIGN_REQ = 111;
    private EditText input;
    private ListView listView;
    private String username ="";
    private FirebaseListAdapter<chatClass> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatingActionButton fb = (FloatingActionButton) findViewById(R.id.fab);
        listView = (ListView) findViewById(R.id.list);
        if(FirebaseAuth.getInstance().getCurrentUser()==null)
        {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(),SIGN_REQ);
        }
        else
        {
            Toast.makeText(this, "Welcome"+FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), Toast.LENGTH_SHORT).show();
            ShowChatroom();
        }
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                EditText input = (EditText)findViewById(R.id.input);
                FirebaseDatabase.getInstance().getReference().push().setValue(new chatClass(input.getText().toString(),
                        FirebaseAuth.getInstance()
                                .getCurrentUser()
                                .getDisplayName())
                );
                input.setText("");


            }
        });

    }
    public void ShowChatroom()
    {
        username = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("Main", "user id: " + username);

        adapter = new MessageClass(this, chatClass.class, R.layout.item_in_message, FirebaseDatabase.getInstance().getReference());
        listView.setAdapter(adapter);
    }
    public  String getLoggedInUserName() {
        return username;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SIGN_REQ) {
            if(resultCode == RESULT_OK) {
                Toast.makeText(this,
                        "Successfully signed in. Welcome!",
                        Toast.LENGTH_LONG)
                        .show();
                ShowChatroom();
            } else {
                Toast.makeText(this,
                        "We couldn't sign you in. Please try again later.",
                        Toast.LENGTH_LONG)
                        .show();

                // Close the app
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
      if(item.getItemId()==R.id.menu_sign_out)
          AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
              @Override
              public void onComplete(@NonNull Task<Void> task)
              {
                  Toast.makeText(MainActivity.this, "You have Sign out", Toast.LENGTH_SHORT).show();
                  finish();

              }
          });
      return true;
    }
}

