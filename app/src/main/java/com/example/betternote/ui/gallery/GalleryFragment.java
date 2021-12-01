package com.example.betternote.ui.gallery;

import android.content.DialogInterface;
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

import com.example.betternote.R;
import com.example.betternote.databinding.FragmentGalleryBinding;

import java.util.ArrayList;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    private FragmentGalleryBinding binding;
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
        galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        titleArray = new ArrayList<>();

        database = getActivity().openOrCreateDatabase("Notes", getActivity().MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS trash (title VARCHAR, note VARCHAR)");

        listView = root.findViewById(R.id.listView);
        textView = root.findViewById(R.id.textView4);
        arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, titleArray);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                String title = titleArray.get(pos);
                Cursor cursor = database.rawQuery("SELECT * FROM trash WHERE title = ?", new String[] {title});
                int noteIx = cursor.getColumnIndex("note");
                cursor.moveToNext();
                String note = cursor.getString(noteIx);

                int position = pos;
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Delete or Recycle?");
                alert.setMessage("If you delete, you won't be able to recycle that note again.");
                alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        GalleryFragment.this.delete_note(title, position);
                        Toast.makeText(getActivity().getApplicationContext(), "Deleted the note.", Toast.LENGTH_LONG).show();
                    }
                });
                alert.setNegativeButton("Recycle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        GalleryFragment.this.recycle_note(title, note, position);
                        Toast.makeText(getContext(), "Recycled the note.", Toast.LENGTH_LONG).show();
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
    public void delete_note(String title, int position) {
        database = getActivity().openOrCreateDatabase("Notes", getActivity().MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS trash (title VARCHAR, note VARCHAR)");

        String sqlString = "DELETE FROM trash WHERE title = ?";
        SQLiteStatement sqLiteStatement = database.compileStatement(sqlString);
        sqLiteStatement.bindString(1, title);
        sqLiteStatement.execute();
        arrayAdapter.remove(arrayAdapter.getItem(position));
        get_data();
    }
    public void recycle_note(String title, String note, int position){
        database = getActivity().openOrCreateDatabase("Notes", getActivity().MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS notes (title VARCHAR, note VARCHAR)");
        database.execSQL("CREATE TABLE IF NOT EXISTS trash (title VARCHAR, note VARCHAR)");
        String sqlString = "DELETE FROM trash WHERE title = ?";
        SQLiteStatement sqLiteStatement = database.compileStatement(sqlString);
        sqLiteStatement.bindString(1,title);
        sqLiteStatement.execute();
        sqlString = "INSERT INTO notes VALUES(?,?)";
        sqLiteStatement = database.compileStatement(sqlString);
        sqLiteStatement.bindString(1,title);
        sqLiteStatement.bindString(2,note);

        sqLiteStatement.execute();
        get_data();
    }
    public void get_data() {
        try {

            arrayAdapter.clear();
            database = getActivity().openOrCreateDatabase("Notes", getActivity().MODE_PRIVATE, null);
            cursor = database.rawQuery("SELECT * FROM trash", null);
            int nameIx = cursor.getColumnIndex("title");
            if (cursor.getCount() == 0) {
                textView.setVisibility(View.VISIBLE);
                return;

            } else {
                textView.setVisibility(View.INVISIBLE);


            }
            while (cursor.moveToNext()) {
                String name = cursor.getString(nameIx);
                System.out.println(name);
                titleArray.add(name);
            }
            arrayAdapter.notifyDataSetChanged();

            cursor.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}