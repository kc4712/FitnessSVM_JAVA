package kr.co.greencomm.ibody24.coach.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import kr.co.greencomm.ibody24.coach.R;
import kr.co.greencomm.ibody24.coach.base.CoachBaseActivity;
import kr.co.greencomm.ibody24.coach.tab.ActivityTabHome;
import kr.co.greencomm.ibody24.coach.tab.ActivityTabSetting;


/**
 * Created by young on 2015-09-02.
 */
public class ActivityInformation
        extends CoachBaseActivity
        implements View.OnClickListener {
    private Button m_btn_close;
    private TextView m_txt_title;
    //private TextView m_txt_main;
    private WebView m_web_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.coachplus_information);
        //setApplicationStatus(ApplicationStatus.InformationScreen);

        m_btn_close = (Button) findViewById(R.id.information_btn_close);
        m_btn_close.setOnClickListener(this);

        m_txt_title = (TextView) findViewById(R.id.information_txt_title);
        //m_txt_main = (TextView) findViewById(R.id.txt_main);
        //textLoadDisplay(m_txt_main, R.raw.manual);
        m_web_main = (WebView) findViewById(R.id.information_web_main);

        ///웹뷰 자바 스크립트 허용///
        m_web_main.getSettings().setJavaScriptEnabled(true);
        //m_web_main.getSettings().setAllowFileAccessFromFileURLs(true);
        //m_web_main.getSettings().setLoadsImagesAutomatically(true);
        //m_web_main.getSettings().setAllowFileAccess(true);
        ///웹뷰 다른 브라우저 사용 불가///
        class WVC extends WebViewClient {
            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        }
        m_web_main.setWebViewClient(new WVC());
        ///까지

        Intent intent = getIntent();
        String title = intent.getExtras().getString("title");
        m_txt_title.setText(title);
        String addr = intent.getExtras().getString("address");
        Log.i("Webview",addr);
        m_web_main.loadUrl(addr);

//        if (intent.getExtras().containsKey("rawfile")) {
//            int docId = intent.getExtras().getInt("rawfile");
//            textLoadDisplay(m_web_main, docId);
//        } else {
//            String addr = intent.getExtras().getString("address");
//            m_web_main.loadUrl(addr);
//        }
    }

    private void textLoadDisplay(TextView view, int docId) {
        InputStream in = getResources().openRawResource(docId);
        if (in == null) return;
        try {
            InputStreamReader reader = new InputStreamReader(in, "utf-8");
            BufferedReader buffer = new BufferedReader(reader);
            StringBuilder sb = new StringBuilder();
            String sline;
            while ((sline = buffer.readLine()) != null) {
                sb.append(sline);
            }
            in.close();
            view.setText(getHtml(sb.toString()));
        } catch (Exception e) {
        }
    }

    private void textLoadDisplay(WebView view, int docId) {
        InputStream in = getResources().openRawResource(docId);
        if (in == null) return;
        try {
            InputStreamReader reader = new InputStreamReader(in, "utf-8");
            BufferedReader buffer = new BufferedReader(reader);
            StringBuilder sb = new StringBuilder();
            String sline;
            while ((sline = buffer.readLine()) != null) {
                sb.append(sline);
            }
            in.close();
            view.loadData(sb.toString(), "text/html", "UTF-8");
        } catch (Exception e) {
        }
    }

    @Override
    public void onClick(View v) {
        if (m_popup_mode_home) {
            m_popup_mode_home = false;
            ActivityTabHome.popActivity();
            return;
        }
        if (m_popup_mode) {
            ActivityTabSetting.popActivity();
            return;
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        m_popup_mode_home = false;
    }

    @Override
    public void run(Intent intent) {

    }
}
