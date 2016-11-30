package com.shanejansen.portablesecurity.ui.main.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import com.shanejansen.portablesecurity.R;

/**
 * Created by shane on 11/10/15.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {
  private TransactionInterface mTransactionInterface;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    try {
      mTransactionInterface = (TransactionInterface) getActivity();
    } catch (ClassCastException e) {
      throw new ClassCastException(
          getActivity().toString() + " must implement TransactionInterface");
    }
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_home, container, false);

    // Finding views
    Button btnPathChecker = (Button) v.findViewById(R.id.bthPathChecker);
    btnPathChecker.setOnClickListener(this);
    Button btnSpotlightToggle = (Button) v.findViewById(R.id.btnSpotlightToggle);
    btnSpotlightToggle.setOnClickListener(this);
    Button btnAirConditioner = (Button) v.findViewById(R.id.btnAirConditioner);
    btnAirConditioner.setOnClickListener(this);

    return v;
  }

  @Override public void onPause() {
    super.onPause();
  }

  @Override public void onClick(View v) {
    switch (v.getId()) {
      case R.id.bthPathChecker:
        mTransactionInterface.addFragment(new BluetoothFragment(), true);
        break;
      case R.id.btnSpotlightToggle:
        Toast.makeText(getActivity(), "Not yet implemented.", Toast.LENGTH_SHORT).show();
        break;
      case R.id.btnAirConditioner:
        Toast.makeText(getActivity(), "Not yet implemented.", Toast.LENGTH_SHORT).show();
        break;
    }
  }
}
