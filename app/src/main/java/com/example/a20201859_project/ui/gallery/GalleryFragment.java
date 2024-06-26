package com.example.a20201859_project.ui.gallery;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.a20201859_project.databinding.FragmentGalleryBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class GalleryFragment extends Fragment {
    SQLiteDatabase sqlDB;
    myDBHelper myHelper;
    String ValiedUrl = "https://quasarzone.com/bbs/qb_saleinfo/views/\\d+";

    private FragmentGalleryBinding binding;

    @SuppressLint("SetJavaScriptEnabled")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);
        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        FloatingActionButton Crawling = binding.startCrawling;

        WebView webView = binding.webview;
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://quasarzone.com/bbs/qb_saleinfo");
        //웹뷰에서 이전 페이지 기록이 있으면 뒤로가기 키로 이전 페이지로 이동
        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    WebView webView = (WebView) v;
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        if (webView.canGoBack()) {
                            webView.goBack();
                            return true;
                        }
                    }
                }
                return false;
            }
        });

        myHelper = new myDBHelper(getActivity());


        //버튼을 누르면 현재 페이지에서 크롤링 시작
        Crawling.setOnClickListener(new View.OnClickListener() {
            String URL;
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(),"성공1", Toast.LENGTH_SHORT).show();
                String PageUrl = webView.getUrl();

                //유효한 URL이면 크롤링 실행
                if(PageUrl.matches(ValiedUrl)) {
                    makeThread(PageUrl);
                    Toast.makeText(getContext(),"추가되었습니다", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(),"URL이 유효하지 않습니다", Toast.LENGTH_SHORT).show();
                }


            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    //크롤링 처리 스레드
    public void makeThread(String PageUrl) {
        new Thread() {
            @Override
            public void run() {

                Document doc = null;
                try {
                    doc = Jsoup.connect(PageUrl).get(); //Jsoup 라이브러리 사용

                    //특가 진행 상태와 제품 이름 가져오기
                    Elements crawledDetail = doc.select("div.common-view-area h1.title");
                    String status = crawledDetail.text().split(" ", 2)[0];
                    String name = crawledDetail.text().split(" ", 2)[1];

                    //특가 가격 로드
                    Elements crawledPrice = doc.select("table.market-info-view-table span.text-orange");
                    String price = crawledPrice.text();
                    Log.d("Price", price);

                    //특가 사이트 url 가져오기
                    Element crawledUrl = doc.selectFirst("table.market-info-view-table");
                    String url = crawledUrl.select("tbody tr").select("td").get(0).text();

                    SQLInsert(status, name, url, PageUrl, price); //DB에 삽입
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    //DB 삽입 함수
    public void SQLInsert(String status, String name, String url, String purl, String price) {
        sqlDB = myHelper.getWritableDatabase();
        sqlDB.execSQL("INSERT INTO groupTBL (Status, Name, Url, Purl, Price) VALUES ('" + status +
                "', '" + name + "', '" + url + "', '" + purl + "', '" + price + "');");
        sqlDB.close();
    }

    public static class myDBHelper extends SQLiteOpenHelper {
        public myDBHelper(Context context) {
            super(context, "groupDB", null, 1);
        }

        //Id번호, 상태, 상품명, 상품Url, 특가사이트Url 저장
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE groupTBL ( Id INTEGER PRIMARY KEY AUTOINCREMENT, Status TEXT, Name TEXT, Url TEXT, Purl TEXT, Price TEXT);");
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS groupTBL");
            onCreate(db);
        }
    }
}