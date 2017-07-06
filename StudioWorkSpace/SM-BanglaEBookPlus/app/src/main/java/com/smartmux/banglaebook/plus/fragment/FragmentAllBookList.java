package com.smartmux.banglaebook.plus.fragment;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.smartmux.banglaebook.plus.R;
import com.smartmux.banglaebook.plus.adapter.BookListAdapter;
import com.smartmux.banglaebook.plus.model.ItemRow;
import com.smartmux.banglaebook.plus.util.Constant;
import com.smartmux.banglaebook.plus.util.EBookPreferenceUtils;
import com.smartmux.banglaebook.plus.util.JSONParser;
import com.smartmux.banglaebook.plus.util.ProgressHUD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by tanvir-android on 4/25/16.
 */
public class FragmentAllBookList extends Fragment {

    private PullToRefreshListView pullRefreshListview;
    TextView no_book;
    ListView bookListView;
    JSONArray books = null;
    BookListAdapter bookGridAdapter;
    ArrayList<ItemRow> bookList = new ArrayList<ItemRow>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booklist, container,
                false);

        no_book = (TextView)view.findViewById(R.id.on_mybook);
        no_book.setVisibility(View.GONE);
        pullRefreshListview = (PullToRefreshListView) view.findViewById(R.id.book_list);
        pullRefreshListview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        bookListView = pullRefreshListview.getRefreshableView();
        pullRefreshListview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        pullRefreshListview
                .setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {

                    @Override
                    public void onRefresh(
                            PullToRefreshBase<ListView> refreshView) {

                        String label = "Checking Book";
                        refreshView.getLoadingLayoutProxy()
                                .setLastUpdatedLabel(label);

                        // Do work to refresh the list here.
                        new GetNewDataTask().execute();
                    }
                });
        loadAllBooks();


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        bookGridAdapter = new BookListAdapter(getActivity(),
                R.layout.item_row, bookList);
        bookListView.setAdapter(bookGridAdapter);
        bookGridAdapter.notifyDataSetChanged();
    }

    public void loadAllBooks() {

        boolean isLocalAvailable = EBookPreferenceUtils.getBoolPref(
                getActivity(), Constant.PREF_NAME,
                Constant.ISLOCALAVAILABLE, false);

        if (bookListView.getChildCount() == 0) {

            if (isLocalAvailable) {

                String eBookJsondata = EBookPreferenceUtils.getStringPref(
                        getActivity(), Constant.PREF_NAME,
                        Constant.EBOOK_JSON, "");
                JSONObject json;
                try {
                    json = new JSONObject(eBookJsondata);
                    setDataFromJson(json);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } else {

                new SM_AsyncTaskForGetBook().execute(Constant.EBOOK_URL);
            }

        } else {

            int position = EBookPreferenceUtils.getIntPref(
                    getActivity(), Constant.PREF_NAME,
                    Constant.EBOOK_POSITION, 0);
            bookListView.setSelection(position - 1);
        }

    }

    private void setDataFromJson(JSONObject json) {

        if(bookList!=null && bookList.size()>0){
            bookList.clear();
        }

        try {
            // Getting JSON Array
            books = json.getJSONArray(Constant.TAG_RADIOS);

            for (int i = 0; i < books.length(); i++) {
                JSONObject c = books.getJSONObject(i);
                String bengaliTitle = c.getString(Constant.TAG_BTITLE);
                String title = c.getString(Constant.TAG_ETITLE);
                String image = c.getString(Constant.TAG_THUMBURL);
                String pdfUrl = c.getString(Constant.TAG_PDFURL);
                String pdfPage = c.getString(Constant.TAG_PAGES);
                String pdfAuthor = c.getString(Constant.TAG_AUTHOR);
                String pdfSize = c.getString(Constant.TAG_SIZE);

                ItemRow item = new ItemRow(bengaliTitle, title, pdfPage, image,
                        pdfAuthor, pdfSize, pdfUrl, false);
                bookList.add(item);

            }

            Log.e("setDataFromJson", "" + bookList.size());

//			bookGridAdapter = new BookListAdapter(MainActivity.this,
//					R.layout.item_row, bookList);
//			bookListView.setAdapter(bookGridAdapter);
//			int position = EBookPreferenceUtils.getIntPref(
//					getApplicationContext(), Constant.PREF_NAME,
//					Constant.EBOOK_POSITION, 0);
//            bookListView.setSelection(position - 1);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    class SM_AsyncTaskForGetBook extends AsyncTask<String, String, JSONObject> {

        ProgressHUD mProgressHUD;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressHUD = ProgressHUD.show(getActivity(),
                    "Loading Books...", true);

        }

        @Override
        protected JSONObject doInBackground(String... params) {
            // Creating new JSON Parser
            JSONParser jParser = new JSONParser();

            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(Constant.EBOOK_URL);


            return json;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            mProgressHUD.setMessage(values[0]);
            super.onProgressUpdate(values);
        }

        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);
            if (mProgressHUD.isShowing()) {

                mProgressHUD.dismiss();
            }
            if(result != null){
                EBookPreferenceUtils.saveBoolPref(getActivity(),
                        Constant.PREF_NAME, Constant.ISLOCALAVAILABLE, true);
                EBookPreferenceUtils.saveStringPref(getActivity(),
                        Constant.PREF_NAME, Constant.EBOOK_JSON, result.toString());

                setDataFromJson(result);


            }
        }
    }



    private class GetNewDataTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            JSONParser jParser = new JSONParser();

            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(Constant.EBOOK_URL);
            EBookPreferenceUtils.saveBoolPref(getActivity(),
                    Constant.PREF_NAME, Constant.ISLOCALAVAILABLE, true);
            EBookPreferenceUtils.saveStringPref(getActivity(),
                    Constant.PREF_NAME, Constant.EBOOK_JSON, json.toString());

            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            // Call onRefreshComplete when the list has been refreshed.
            pullRefreshListview.onRefreshComplete();
            // pullRefreshGridView.onRefreshComplete();
            String eBookJsondata = EBookPreferenceUtils.getStringPref(
                    getActivity(), Constant.PREF_NAME,
                    Constant.EBOOK_JSON, "");
            JSONObject json;
            try {
                json = new JSONObject(eBookJsondata);
                // Getting JSON Array
                books = json.getJSONArray(Constant.TAG_RADIOS);

                for (int i = 0; i < books.length(); i++) {
                    JSONObject c = books.getJSONObject(i);
                    String bengaliTitle = c.getString(Constant.TAG_BTITLE);
                    String title = c.getString(Constant.TAG_ETITLE);
                    String image = c.getString(Constant.TAG_THUMBURL);
                    String pdfUrl = c.getString(Constant.TAG_PDFURL);
                    String pdfPage = c.getString(Constant.TAG_PAGES);
                    String pdfAuthor = c.getString(Constant.TAG_AUTHOR);
                    String pdfSize = c.getString(Constant.TAG_SIZE);

                    ItemRow item = new ItemRow(bengaliTitle, title, pdfPage,
                            image, pdfAuthor, pdfSize, pdfUrl, false);
                    bookList.add(item);

                }


//				bookGridAdapter = new BookListAdapter(MainActivity.this,
//						R.layout.item_row, bookList);
//				// listView.setAdapter(menuAdapter);
//                bookListView.setAdapter(bookGridAdapter);
//				bookGridAdapter.notifyDataSetChanged();

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }



}