package com.scottlindley.touchmelabs.MainView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scottlindley.touchmelabs.ContentDBHelper;
import com.scottlindley.touchmelabs.ModelObjects.CardContent;
import com.scottlindley.touchmelabs.ModelObjects.CurrentWeather;
import com.scottlindley.touchmelabs.R;
import com.scottlindley.touchmelabs.RecyclerViewComponents.CardRecyclerViewAdapter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.util.List;

/**
 * "Home screen" fragment. Main purpose is to display the RecyclerView of {@link CardContent} objects.
 */

public class CardListFragment extends Fragment {
    TwitterLoginButton mLoginButton;

    private OnFragmentInteractionListener mListener;
    private CurrentWeather mWeather;

    private CardRecyclerViewAdapter mAdapter;
    private List<CardContent> mCardList;

    public CardListFragment() {}

    public static CardListFragment newInstance() {
        CardListFragment fragment = new CardListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_card_list, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLoginButton = (TwitterLoginButton) getView().findViewById(R.id.twitter_login_button);
        mLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                requestDataRefresh(getContext());
            }

            @Override
            public void failure(TwitterException exception) {
                exception.printStackTrace();
            }
        });


        SharedPreferences preferences = getContext().getSharedPreferences("mWeather", Context.MODE_PRIVATE);
        String cityName = preferences.getString("city name", "error");
        String cityConditions = preferences.getString("city conditions", "error");
        String cityTemp = preferences.getString("city temp", "error");

        mWeather = new CurrentWeather(cityName, cityConditions, cityTemp);

        mCardList = ContentDBHelper.getInstance(getContext()).getCardList(mWeather);

        RecyclerView cardRecycler = (RecyclerView)getView().findViewById(R.id.fragment_recyclerview);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        cardRecycler.setLayoutManager(manager);
        mAdapter = new CardRecyclerViewAdapter(mCardList);
        cardRecycler.setAdapter(mAdapter);

        setUpBroadcastReceiverForRecyclerView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setUpBroadcastReceiverForRecyclerView() {
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                mCardList.clear();
                mCardList.addAll(ContentDBHelper.getInstance(getContext()).getCardList(mWeather));

                mAdapter.notifyDataSetChanged();
                
            }
        };
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, new IntentFilter("card list"));
    }

    public void requestDataRefresh(Context context){
        ContentDBHelper.getInstance(context).refreshDB();
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mLoginButton.onActivityResult(requestCode, resultCode, data);
    }

}
