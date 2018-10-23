package com.example.a503_25.a1023android;

        import android.app.ProgressDialog;
        import android.content.Intent;
        import android.os.Handler;
        import android.os.Message;
        import android.support.v4.widget.SwipeRefreshLayout;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.ListAdapter;
        import android.widget.ListView;
        import android.widget.ProgressBar;

        import org.json.JSONArray;
        import org.json.JSONObject;

        import java.io.BufferedReader;
        import java.io.InputStreamReader;
        import java.net.HttpURLConnection;
        import java.net.URL;
        import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    SwipeRefreshLayout swipeRefreshLayout;

    //ListView 출력관련 변수
    ListView listView;
    ArrayAdapter adapter;
    ArrayList<String> nameList;

    //상세보기를 위해서 id를 저장할 List
    ArrayList<String> idList;

    //데이터를 다운로드 받는 동안 보여질 대화상자
    ProgressDialog progressDialog;

    //리스트 뷰의 데이터를 다시 출력하는 핸들러
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message message){
            adapter.notifyDataSetChanged();
            progressDialog.dismiss();
            swipeRefreshLayout.setRefreshing(false);

        }
    };

    class ThreadEx extends Thread{
        @Override
        public void run(){
            StringBuilder sb = new StringBuilder();
            //다운로드 받는 코드
            try{
                //다운로드 받을 주소 생성
                URL url = new URL("http://192.168.0.235:8080/android/listitem");
                //connection 연결
                HttpURLConnection con = (HttpURLConnection)((URL) url).openConnection();
                con.setUseCaches(false);
                con.setConnectTimeout(20000);
                //문자열을 다운로드 받을 스트림 만들기
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                //문자열을 다운로드 받아서 sb에 추가하기
                while (true){
                    String line = br.readLine();
                    if(line == null) {
                        break;
                    }else {
                        sb.append(line + "\n");
                    }
                }

                br.close();
                con.disconnect();



            }catch (Exception e){
                Log.e("다운로드 실패:",e.getMessage());
            }

            //파싱하는 코드
            try{
                //전체 문자열을 배열로 변경
                JSONArray ar = new JSONArray(sb.toString());
                //배열순회
                //이전에 있던내용을 삭제 새로만드는거다/
                nameList.clear();
                idList.clear();
                for(int i=0;i<ar.length();i=i+1){
                    JSONObject object = ar.getJSONObject(i);
                    // {"itemid":1,"itemname":"Lemon","price":0,"description":null,"pictureurl":null}
                    //이부분이 들어오게 된다.
                    //객체에서 itemname의 값을 가져와서 nameList에 추가
                    nameList.add(object.getString("itemname"));
                    idList.add(object.getString("itemid"));
                    //핸들러 호출 - 다시 출력
                    handler.sendEmptyMessage(0);
                }




            }catch (Exception e){
                Log.e("파싱에러:",e.getMessage());
            }


        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new ThreadEx().start();
            }
        });

        nameList = new ArrayList<>();
        idList = new ArrayList<>();

        adapter = new ArrayAdapter<>(MainActivity.this,android.R.layout.simple_list_item_1,nameList);
        listView = (ListView)findViewById(R.id.itemlist);
        listView.setAdapter(adapter);

        //리스트 뷰에서 항목을 클릭했을 때 수행할 내용
        listView.setOnItemClickListener(new ListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this,DetailActivity.class);
                intent.putExtra("itemid",idList.get(position));
                startActivity(intent);
            }
        });


    }

    //액티비티가 실행될때 호출되는 메소드
    @Override
    protected void onResume() {
        super.onResume();
        progressDialog = progressDialog.show(this,"","다운로드 중");
        ThreadEx th = new ThreadEx();
        th.start();
    }
}
