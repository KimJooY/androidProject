package com.example.project6;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private ArrayList<ChargerDTO> chargerList;
    private JSONArray jsonArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        String url="http://192.168.0.2:8088/chargers/all";
        NetworkTask NetworkTask=new NetworkTask(url,null);
        NetworkTask.execute();
    }



    public class NetworkTask extends AsyncTask<Void,Void,String> {
        /**
         * 비동기 처리를 위한 NetworkTask 클래스
         * 보면 AsyncTask<Params,Progress,Result>3개의 인자가 들어가는 걸 볼 수 있는데,
         * Params는 doInBackground 의 파라미터 타입이 되며, execute 메소드 인자값이 됨.
         * Progress는 doInBackground 작업 시 진행 단위 타입, onProgressUpdate의 파라미터 타입
         * Result는 doInBackgourn 리턴 값으로 onPostExecute의 파라미터 값
         *
         * 그 외 자세한 설명은 https://itmining.tistory.com/7
         */
        private String url;
        private ContentValues values;

        public NetworkTask(String url, ContentValues values)
        {
            this.url=url; this.values=values;
        }

        @Override
        protected void onPreExecute() {
            /**
             * 카카오 맵 불러오기
             * 실행전 준비단계
             */
            super.onPreExecute();
            chargerList=new ArrayList<>();
        }

        /**
         * @param params
         * @return 서버에서 받아온 result 값, 여기서는 charger
         * doInBackground 에서는 서버에 있는 정보를 받아옴.
         */
        @Override

        protected String doInBackground(Void... params)
        {
            /**
             * url에 접속해서 charger 위치를 가져오는 과정
             * String result는 모든 Json들임
             */
            String result;
            RequestHttpConnection requestHttpConnection=new RequestHttpConnection();
            result = requestHttpConnection.request(url,values);
            return result;
        }

        /**
         * @param s
         * s는 doInBackground 에서 리턴한 result 값임. 여기선 Json String이라 봐도 무방
         * JsonArray는 서버에서 받아온 JsonArray는 s 속에 json들을 jsonArray 화 시켜주는 것 같음
         * 길이만큼 불러오기 성공, list에도 정상적으로 charger의 정보가 들어가는것을 debug로 확인.
         * 또한 비동기처리로 여기서 marker를 띄워야 할 것 같음.
         */
        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            try
            {
                jsonArray= new JSONArray(s);
                makeChargerList(jsonArray);
                setMarkerOnMap(chargerList);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public void makeChargerList(JSONArray jsonArray){
            /**
             * @param jsonArray
             * onPostExecute 에서 doInbackgroung 로부터 받은 json 을
             * chargerList에 넣는 메소드
             */
            try
            {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    int id = Integer.parseInt(jsonObject.getString("id"));
                    String chargerName = jsonObject.getString("chargerName");
                    String chargerLocation = jsonObject.getString("chargerLocation");
                    String city = jsonObject.getString("city");
                    String closedDates = jsonObject.getString("closedDates");
                    String fastChargeType = jsonObject.getString("fastChargeType");
                    Integer slowNum = Integer.parseInt(jsonObject.getString("slowNum"));
                    Integer fastNum = Integer.parseInt(jsonObject.getString("fastNum"));
                    String parkingFee = jsonObject.getString("parkingFee");
                    Double lat = Double.parseDouble(jsonObject.getString("lat"));
                    Double lon = Double.parseDouble(jsonObject.getString("lon"));
                    String address = jsonObject.getString("address");
                    chargerList.add(new ChargerDTO(id, chargerName, chargerLocation, city, closedDates, fastChargeType, slowNum, fastNum, parkingFee, lat, lon, address));
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public void setMarkerOnMap(ArrayList<ChargerDTO> chargerList){
            /**
             * 맵에 마커를 표시하는 메소드
             */
            for(int i=0; i<chargerList.size(); i++)
            {
                double lat=chargerList.get(i).getLat();
                double lon=chargerList.get(i).getLon();
                LatLng latLng=new LatLng(lat, lon);
                MarkerOptions markerOptions=new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(chargerList.get(i).getChargerName());
                mMap.addMarker(markerOptions);
            }
        }

    }
}