package com.gm.rxdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    static final String TAG = "RxDemo";

    void demo0() {
        Observable.just("one", "two", "three", "four", "five")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.i(TAG, Thread.currentThread().getId() + " : " + s);
                    }
                });
    }

    void demo1() {
        Observable.interval(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .take(5)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        Log.i(TAG, Thread.currentThread().getId() + " : " + aLong.toString());
                    }
                });
    }

    String searchWikipedia(String term) {
        try {
            URL url = new URL("https://en.wikipedia.org/w/api.php?action=opensearch&format=json&search=" +
                                       URLEncoder.encode(term, "UTF-8"));

            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String inputLine;
            StringBuilder out = new StringBuilder();
            try {
                while ((inputLine = in.readLine()) != null) {
                    out.append(inputLine);
                }
                return new GsonBuilder()
                        .setPrettyPrinting()
                        .create()
                        .toJson(new JsonParser().parse(out.toString()));

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        demo0();
        demo1();

        ((EditText) findViewById(R.id.editText1)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(final Editable s) {
                Observable.create(new OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        /*
                         *  TODO:
                         *  Complete this function by call searchWikipedia
                         *  pass return JSON string to onNext function
                         * */
                        String result =  searchWikipedia(s.toString());
                        subscriber.onNext(result);
                        subscriber.onCompleted();
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<String>() {
                            @Override
                            public void call(String s) {
                        /*
                         * TODO:
                         * set string to the textView
                         * */
                         ((TextView) findViewById(R.id.TextView1)).setText(s);
                            }
                        });

            }
        });

    }

}
