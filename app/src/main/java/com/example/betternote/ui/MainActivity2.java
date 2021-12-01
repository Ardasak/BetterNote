package com.example.betternote.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.betternote.R;
import com.example.betternote.ui.home.HomeFragment;

public class MainActivity2 extends AppCompatActivity {
    EditText titleedit;
    EditText noteedit;
    Button button;
    SQLiteDatabase database;
    Intent intent;
    Cursor cursor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        titleedit = findViewById(R.id.titleedit);


        noteedit = findViewById(R.id.noteedit);
        intent = getIntent();
        String state = intent.getStringExtra("state");
        button = findViewById(R.id.button);
        if(state.equals("new_note")){
            titleedit.setText("");
            noteedit.setText("");

        }
        else {
            String title = intent.getStringExtra("title");
            String note = intent.getStringExtra("note");
            button.setText("Update");

            titleedit.setText(title);
            noteedit.setText(note);

        }
    }
    public boolean check_counts(){
        int s = HomeFragment.arrayAdapter.getCount();
        database = openOrCreateDatabase("Notes", MODE_PRIVATE, null);
        cursor = database.rawQuery("SELECT * FROM notes", null);
        while (cursor.moveToNext()){
            int index= cursor.getColumnIndex("note");
            if(cursor.getString(index).equals(noteedit.getText().toString())){
                return false;
            }
        }
        for(int i = 0;i<s;i++){
            if(HomeFragment.arrayAdapter.getItem(i).equals(titleedit.getText().toString())){
                return false;
            }
        }
        return true;
    }

    public void update(String title, String note, String old_title){
        String sqlString = "UPDATE notes SET title = ?, note = ? WHERE title = ?";
        SQLiteStatement sqLiteStatement = database.compileStatement(sqlString);
        sqLiteStatement.bindString(1, title);
        sqLiteStatement.bindString(2, note);
        sqLiteStatement.bindString(3, old_title);
        sqLiteStatement.execute();
    }
    public void insert(String title, String note){
        String sqlString = "INSERT INTO notes (title,note) VALUES (?, ?)";
        SQLiteStatement sqLiteStatement = database.compileStatement(sqlString);
        sqLiteStatement.bindString(1, title);
        sqLiteStatement.bindString(2, note);
        sqLiteStatement.execute();
    }
    public void execute(View view){
        String old_title = intent.getStringExtra("title");
        boolean check = check_counts();
        if(check) {
            String title = titleedit.getText().toString();

            String note = noteedit.getText().toString();
            if(title.trim().equals("")){
                title = note;
            }
            database = this.openOrCreateDatabase("Notes", MODE_PRIVATE, null);
            database.execSQL("CREATE TABLE IF NOT EXISTS notes (title VARCHAR, note VARCHAR)");
            if(button.getText().toString()=="Update"){
                update(title,note,old_title);

            }
            else {
                insert(title,note);
            }
            Intent intent = new Intent(MainActivity2.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);


        }
        else{
            if(titleedit.getText().toString().equals(old_title)){

                database = this.openOrCreateDatabase("Notes", MODE_PRIVATE, null);
                database.execSQL("CREATE TABLE IF NOT EXISTS notes (title VARCHAR, note VARCHAR)");
                String title = titleedit.getText().toString();

                String note = noteedit.getText().toString();
                if(title.trim().equals("")){
                    title = note;
                }
                update(title,note,old_title);
                Intent intent = new Intent(MainActivity2.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            else{
                show_alert("Error","You can't have the same 2 titles at the same time.","OK");
            }
        }

    }
    public void show_alert(String title, String message, String button_text){
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity2.this);
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setPositiveButton(button_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });

        alert.show();
    }
}