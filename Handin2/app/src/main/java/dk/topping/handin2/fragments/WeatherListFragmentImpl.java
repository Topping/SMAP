package dk.topping.handin2.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import dk.topping.handin2.DetailsActivity;
import dk.topping.handin2.R;
import dk.topping.handin2.adapters.WeatherListAdapter;
import dk.topping.handin2.models.CityWeatherData;


public class WeatherListFragmentImpl extends Fragment implements WeatherListFragment, SwipeRefreshLayout.OnRefreshListener {

    private final String TAG = this.getClass().getSimpleName();

    private WeatherListFragmentListener mListener;
    private ListView weatherListView;
    private List<CityWeatherData> cityWeatherDataList = new ArrayList<>();
    private WeatherListAdapter adapter;
    private SwipeRefreshLayout weatherListRefreshLayout;

    public WeatherListFragmentImpl() {
        // Required empty public constructor
    }
    public static WeatherListFragmentImpl newInstance() {
        WeatherListFragmentImpl fragment = new WeatherListFragmentImpl();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weather_list, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        weatherListView = view.findViewById(R.id.fragmentWeatherListView);
        adapter = new WeatherListAdapter(cityWeatherDataList, this.getContext());
        weatherListView.setAdapter(adapter);
        weatherListRefreshLayout = view.findViewById(R.id.fragmentWeatherListPullRefresh);
        weatherListRefreshLayout.setOnRefreshListener(this);
        weatherListView.setOnItemClickListener((adapterView, view1, i, l) -> {
            CityWeatherData data = (CityWeatherData) adapterView.getAdapter().getItem(i);
            if(data != null) {
                Log.d(TAG, "Clicked on city: " + data.getCityName());
                mListener.goToDetails(data.getCityName());
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof WeatherListFragmentListener) {
            mListener = (WeatherListFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement WeatherListFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    // Called when swiping down to refresh.
    @Override
    public void onRefresh() {
        mListener.refreshWeatherData();
    }


    //Interface methods used by underlying activity to call functions on the fragment

    // Update the data displayed in the listview
    @Override
    public void updateWeatherList(List<CityWeatherData> data) {
        cityWeatherDataList.clear();
        cityWeatherDataList.addAll(data);
        adapter.notifyDataSetChanged();
        weatherListRefreshLayout.setRefreshing(false);
    }

    // Used to enable the stupid hack for Snackbars on android version 4.X
    // https://issuetracker.google.com/issues/64285517
    @Override
    public void disableSwipeRefresh() {
        weatherListRefreshLayout.setEnabled(false);
    }

    // Used to enable the stupid hack for Snackbars on android version 4.X
    @Override
    public void enableSwipeRefresh() {
        weatherListRefreshLayout.setEnabled(true);
    }

    // When no internet connection is broadcast, stop the refreshing from the activity.
    @Override
    public void stopRefreshing() {
        weatherListRefreshLayout.setRefreshing(false);
    }

    public interface WeatherListFragmentListener {
        void refreshWeatherData();
        void goToDetails(String cityName);
    }
}
