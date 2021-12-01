package com.example.betternote.ui.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.betternote.ui.MainActivity2;
import com.example.betternote.R;
import com.example.betternote.databinding.FragmentHomeBinding;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    ListView listView;
    ArrayList<String> titleArray;
    ArrayList<Integer> idArray;
    public static ArrayAdapter arrayAdapter;
    String note;
    String title;
    TextView textView;
    int position;
    SQLiteDatabase database;
    Cursor cursor;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        titleArray = new ArrayList<>();

        database = getActivity().openOrCreateDatabase("Notes",getActivity().MODE_PRIVATE,null);
        database.execSQL("CREATE TABLE IF NOT EXISTS notes (title VARCHAR, note VARCHAR)");

        listView = root.findViewById(R.id.listView);
        textView = root.findViewById(R.id.textView3);
        arrayAdapter=new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1,titleArray);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                database = getActivity().openOrCreateDatabase("Notes",getActivity().MODE_PRIVATE,null);
                database.execSQL("CREATE TABLE IF NOT EXISTS notes (title VARCHAR, note VARCHAR)");
                String title = titleArray.get(position);
                cursor = database.rawQuery("SELECT * FROM notes WHERE title = ?", new String[] {title});
                while (cursor.moveToNext()) {
                    note = cursor.getString(1);
                    System.out.println(note);
                }
                Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity2.class);
                intent.putExtra("state","old_note");
                intent.putExtra("title",title);
                intent.putExtra("note",note);

                startActivity(intent);



            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                String title = titleArray.get(pos);
                Cursor cursor = database.rawQuery("SELECT * FROM notes WHERE title = ?", new String[] {title});
                int noteIx = cursor.getColumnIndex("note");
                cursor.moveToNext();
                String note = cursor.getString(noteIx);

                int position = pos;
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Delete");
                alert.setMessage("Are you sure?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        HomeFragment.this.delete_note(title, note, position);
                        Toast.makeText(getActivity().getApplicationContext(), "Deleted the note.", Toast.LENGTH_LONG).show();
                    }
                });
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(), "Didn't delete the note.", Toast.LENGTH_LONG).show();
                    }
                });
                alert.show();
                return true;
            }
        });
        get_data();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void delete_note(String title, String note, int pos){
        database = getActivity().openOrCreateDatabase("Notes",getActivity().MODE_PRIVATE,null);
        database.execSQL("CREATE TABLE IF NOT EXISTS notes (title VARCHAR, note VARCHAR)");
        database.execSQL("CREATE TABLE IF NOT EXISTS trash (title VARCHAR, note VARCHAR)");
        String sqlString = "DELETE FROM notes WHERE title = ?";
        SQLiteStatement sqLiteStatement = database.compileStatement(sqlString);
        sqLiteStatement.bindString(1,title);
        sqLiteStatement.execute();
        sqlString = "INSERT INTO trash VALUES(?,?)";
        sqLiteStatement = database.compileStatement(sqlString);
        sqLiteStatement.bindString(1,title);
        sqLiteStatement.bindString(2,note);
        sqLiteStatement.execute();
        arrayAdapter.remove(arrayAdapter.getItem(pos));
        get_data();
    }
    public void get_data(){
        try {

            arrayAdapter.clear();
            database = getActivity().openOrCreateDatabase("Notes", getActivity().MODE_PRIVATE, null);
            cursor = database.rawQuery("SELECT * FROM notes", null);
            int nameIx = cursor.getColumnIndex("title");
            if(cursor.getCount()==0) {
                textView.setVisibility(View.VISIBLE);
                System.out.println("a");
                return;

            }
            else {
                textView.setVisibility(View.INVISIBLE);
                System.out.println("naber");

            }
            while (cursor.moveToNext()) {
                String name = cursor.getString(nameIx);
                System.out.println(name);
                titleArray.add(name);
            }
            arrayAdapter.notifyDataSetChanged();

            cursor.close();


        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}

