package com.scottlindley.Bundle.DetailView;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.scottlindley.Bundle.NetworkConnectionDetector;
import com.scottlindley.Bundle.R;


public class ExpandedNewsFragment extends Fragment {
    private static final String ARG_URL = "url";

    private WebView mWebView;

    private String mURL;


    public ExpandedNewsFragment() {
        // Required empty public constructor
    }

    public static ExpandedNewsFragment newInstance(String link) {
        ExpandedNewsFragment fragment = new ExpandedNewsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_URL, link);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mURL = getArguments().getString(ARG_URL);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_expanded_news, container, false);
        mWebView = (WebView)rootView.findViewById(R.id.article_webView);
        mWebView.getSettings().setJavaScriptEnabled(true);
        //Only attempt to load the url in the webView if there is a network connection
        NetworkConnectionDetector detector = new NetworkConnectionDetector(getContext());
        if(detector.isConnected()){
            mWebView.setWebChromeClient(new WebChromeClient());
            mWebView.setWebViewClient(new WebViewClient());
            mWebView.loadUrl(mURL);
        } else {
            Toast.makeText(getContext(), "No Network Detected", Toast.LENGTH_SHORT).show();
        }
        return rootView;
    }
}
