package com.DDebbie.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.DDebbie.R;
import com.DDebbie.adapter.TripInfoListAdapter;
import com.DDebbie.entity.Earnings;
import com.DDebbie.entity.RideDataPojo;
import com.DDebbie.entity.TripInfo;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TripInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TripInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TripInfoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    public ArrayList<Earnings> mArrayList;

    public TripInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TripInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TripInfoFragment newInstance(String param1, String param2) {
        TripInfoFragment fragment = new TripInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    int position;
    private View view;
    private ListView listView;
    private ArrayList<RideDataPojo> arrayList;
    private TripInfoListAdapter adapter;
    private TextView txtGrandTotal, txtDate, txtTripN, txtTripBill;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_trip_info, container, false);
        init();
        mArrayList = (ArrayList<Earnings>) getArguments().getSerializable("Data");

        position = getArguments().getInt("Position", 0);

        arrayList = mArrayList.get(position).getRideData();

        txtGrandTotal.setText(mArrayList.get(position).getCurrencySymbol() + mArrayList.get(position).getTotalEarns());
        txtDate.setText("" + mArrayList.get(position).getEarningDate());


        if (mArrayList.get(position).rideData.size() > 0) {
            String ride = "";
            String amount = "";
            for (int arrIndx = 0; arrIndx < mArrayList.get(position).rideData.size(); arrIndx++) {

                ride = ride + "\n" + mArrayList.get(position).rideData.get(arrIndx).getRideId();
                amount = amount + "\n" + mArrayList.get(position).rideData.get(arrIndx).getAmount();



            }
            txtTripN.setText("Trip ID: " + ride);
            txtTripBill.setText(mArrayList.get(position).getCurrencySymbol() + amount);

        } else {

        }


        // adapter = new TripInfoListAdapter(arrayList, getActivity());
        // listView.setAdapter(adapter);


        return view;
    }

    private void init() {
        listView = (ListView) view.findViewById(R.id.listTripInfo);
        txtGrandTotal = (TextView) view.findViewById(R.id.txtGrandTotal);
        txtDate = (TextView) view.findViewById(R.id.txtDate);
        txtTripN = (TextView) view.findViewById(R.id.txtTripN);
        txtTripBill = (TextView) view.findViewById(R.id.txtTripBill);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
