package com.example.a20201859_project.ui.home;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a20201859_project.MyAdapter;
import com.example.a20201859_project.PaintTitle;
import com.example.a20201859_project.databinding.FragmentHomeBinding;
import com.example.a20201859_project.ui.gallery.GalleryFragment;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    GalleryFragment.myDBHelper myHelper;

    RecyclerView recyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    SQLiteDatabase db;

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        myHelper = new GalleryFragment.myDBHelper(getActivity());
        recyclerView = binding.recyclerView;
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);


        Button btnRefresh = binding.btnrefresh;

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<PaintTitle> myDataset = new ArrayList<PaintTitle>();
                db = myHelper.getReadableDatabase();
                Cursor cursor;
                cursor = db.rawQuery("SELECT * FROM groupTBL;", null);

                String strNames = "상태: ";
                String strNumbers = "제품명: ";

                Log.d("mark4653", "완료표시1");
                while (cursor.moveToNext()) {
                    myDataset.add(new PaintTitle(cursor.getString(1), cursor.getString(2), cursor.getString(3)));
                }
                Log.d("mark4653", "완료표시2");
                mAdapter = new MyAdapter(myDataset);
                Log.d("mark4653", "완료표시3");


                recyclerView.setAdapter(mAdapter);
                Log.d("mark4653", "완료표시4");

            }
        });


        //homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    //데이터베이스에서 데이터 로드 후 리스트 생성
    public void loadData() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}