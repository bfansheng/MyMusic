package com.bfansheng.mymusic.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.bfansheng.mymusic.MainActivity;
import com.bfansheng.mymusic.R;
import com.bfansheng.mymusic.utils.SearchAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Hp on 2016/2/17.
 */
public class SearchMusicFragment extends Fragment implements View.OnClickListener {
    private EditText inputText;
    private Button searchButton;
    private String path = "https://api.douban.com/v2/music/search?q=";
    private String finalPath;
    private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    private SearchAdapter adapter;
    private ListView listView;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_searchmusic, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        inputText = (EditText) getActivity().findViewById(R.id.input_text);
        searchButton = (Button) getActivity().findViewById(R.id.search_button);
        searchButton.setOnClickListener(this);
        listView = (ListView) getActivity().findViewById(R.id.search_list);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.search_button) {
            finalPath = path + (inputText.getText());
            //再次请求把原先数据清除
            list.clear();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("提示");
            progressDialog.setMessage("正在努力查询中...");
            progressDialog.show();
            new LoadTask().execute(finalPath);
        }
    }

    class LoadTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            return requestData();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                list = JSONAnalysis(s);
                adapter = new SearchAdapter(getActivity(), list);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                progressDialog.dismiss();
            } else if (s == null) {
                Toast.makeText(getActivity(), "请检查网络哟，亲...",
                        Toast.LENGTH_LONG).show();
            }
            progressDialog.dismiss();
        }
    }

    public String requestData() {
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(finalPath);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(8000);
            httpURLConnection.setReadTimeout(8000);
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Map<String, Object>> JSONAnalysis(String result) {
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONObject(result).getJSONArray("musics");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JSONArray authorArray = jsonObject.getJSONArray("author");
                JSONObject authorObject = authorArray.getJSONObject(0);
                String author = authorObject.getString("name");
                String title = jsonObject.optString("title");
                String image = jsonObject.optString("image");
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("title", title);
                map.put("author", author);
                map.put("image", image);
                list.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}
