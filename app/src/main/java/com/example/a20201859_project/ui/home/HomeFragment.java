package com.example.a20201859_project.ui.home;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class HomeFragment extends Fragment {
    GalleryFragment.myDBHelper myHelper;
    RecyclerView recyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    SQLiteDatabase db, db_remove;

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        myHelper = new GalleryFragment.myDBHelper(getActivity());

        //RecyclerView 설정
        recyclerView = binding.recyclerView;
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        //새로고침, 초기화 버튼 설정
        Button btnRefresh = binding.btnrefresh;
        Button btnInit = binding.btninit;

        //현재 데이터를 갱신하여 특가가 종료되었는지 확인 후 종료되었다면 삭제
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db = myHelper.getReadableDatabase();
                Cursor cursor;
                cursor = db.rawQuery("SELECT * FROM groupTBL;", null);
                while (cursor.moveToNext()) {
                    CountDownLatch latch = new CountDownLatch(1); //Latch 선언
                    //각 DB의 소개페이지를 넘겨 상태를 재확인
                    checkDB(cursor.getInt(0), cursor.getString(4), latch);
                    try {
                        latch.await(); //Latch를 사용해 스레드에서 작업이 전부 완료될 때까지 대기
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                cursor.close();
                db.close();
                loadData(); //작업이 완료되었으면 리스트 갱신
            }
        });

        //DB에 있는 리스트를 전부 초기화
        btnInit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db_remove = myHelper.getWritableDatabase();
                myHelper.onUpgrade(db_remove, 1, 2);
                db_remove.close();
                loadData();
            }
        });

        loadData();

        return root;
    }

    //데이터베이스에서 데이터 로드 후 RecyclerView에 표시
    public void loadData() {
        ArrayList<PaintTitle> myDataset = new ArrayList<PaintTitle>();
        db = myHelper.getReadableDatabase();
        Cursor cursor;
        cursor = db.rawQuery("SELECT * FROM groupTBL;", null);
        while (cursor.moveToNext()) {
            myDataset.add(new PaintTitle(cursor.getString(1), cursor.getString(2),
                    cursor.getString(3), cursor.getString(5)));
        }

        cursor.close();
        db.close();
        mAdapter = new MyAdapter(myDataset);
        recyclerView.setAdapter(mAdapter);
    }

    //DB 갱신용 함수
    public void checkDB(int id, String PageUrl, CountDownLatch latch) {
        new Thread() {
            @Override
            public void run() {
                Document doc = null;
                try {
                    doc = Jsoup.connect(PageUrl).get();

                    //특가 진행 상태 가져오기
                    Elements crawledDetail = doc.select("div.common-view-area h1.title");
                    String status = crawledDetail.text().split(" ", 2)[0];

                    //특가가 종료되었다면 해당 Id에 해당하는 데이터를 DB에서 삭제
                    if ("종료".equals(status) == true) {
                        db_remove = myHelper.getWritableDatabase();
                        db_remove.execSQL("DELETE FROM groupTBL WHERE Id = '" + id + "';");
                        db_remove.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally { //작업이 전부 끝나면 latch 감소
                    latch.countDown();
                }
            }
        }.start();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}