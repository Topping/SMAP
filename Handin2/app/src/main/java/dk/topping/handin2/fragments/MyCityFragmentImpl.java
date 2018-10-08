package dk.topping.handin2.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

import dk.topping.handin2.R;
import dk.topping.handin2.externalservices.OpenWeatherMapApiWrapper;
import dk.topping.handin2.models.CityWeatherData;
import dk.topping.handin2.util.HardcodedCities;

public class MyCityFragmentImpl extends Fragment implements MyCityFragment {

    private final String TAG = this.getClass().getSimpleName();

    private OpenWeatherMapApiWrapper api;
    private TextView cityName;
    private TextView cityTemperature;
    private ImageView weatherIcon;
    private Button editButton;

    private MyCityFragmentListener mListener;

    public MyCityFragmentImpl() {
        // Required empty public constructor
    }
    public static MyCityFragmentImpl newInstance() {
        MyCityFragmentImpl fragment = new MyCityFragmentImpl();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = new OpenWeatherMapApiWrapper(getContext().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_city, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cityName = view.findViewById(R.id.myCityCityName);
        cityTemperature = view.findViewById(R.id.myCityTemperature);
        weatherIcon = view.findViewById(R.id.myCityWeatherIcon);
        editButton = view.findViewById(R.id.myCityEditButton);
        editButton.setOnClickListener(v -> onEditButtonClicked());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MyCityFragmentListener) {
            mListener = (MyCityFragmentListener) context;
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

    @Override
    public void setCityInformation(CityWeatherData data) {
        if(data != null) {
            Log.d(TAG, "Setting My City Data");
            cityName.setText(data.getCityName());
            cityTemperature.setText(String.valueOf(data.getWeatherDetails().getTemp()));
            String iconId = data.getWeatherDescription().get(0).getIconId();
            api.getWeatherIcon(iconId, bitmap -> weatherIcon.setImageBitmap(bitmap));
        }
    }

    private void onEditButtonClicked() {
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View selectCityView = inflater.inflate(R.layout.fragment_select_city, null);

        new AlertDialog.Builder(getContext(), R.style.MyAlertDialogStyle)
                .setTitle(getString(R.string.cityselection_dialog))
                .setView(selectCityView)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    EditText cityNameEditText = ((Dialog)dialog).findViewById(R.id.selectCityCityName);
                    if(cityNameEditText != null) {
                        mListener.setMyCityName(cityNameEditText.getText().toString());
                    }
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {

                })
                .create()
                .show();
    }

    public interface MyCityFragmentListener {
        void setMyCityName(String cityName);
        void refreshMyCity();
    }
}
