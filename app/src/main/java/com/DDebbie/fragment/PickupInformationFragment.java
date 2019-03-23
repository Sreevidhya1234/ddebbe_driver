package com.DDebbie.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.DDebbie.R;
import com.DDebbie.util.IOUtils;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PickupInformationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PickupInformationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PickupInformationFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public PickupInformationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PickupInformationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PickupInformationFragment newInstance(String param1, String param2) {
        PickupInformationFragment fragment = new PickupInformationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mListener.onPickupInfoFragmentInteraction();
    }

    private View view;
    private ImageView mImgNavigate;
    private FragmentManager fragmentManager;
    private Button mBtnArrived;
    private FragmentTransaction fragmentTransaction;
    public static final String TAG = PickupInformationFragment.class.getSimpleName();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_pickup_information, container, false);
        IOUtils.DONT_SHOW = true;
        mImgNavigate = (ImageView) view.findViewById(R.id.imgNavigate);
        mImgNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG,"click==>_navigate==>");

                PickupNavigationFragment pickupNavigationFragment = new PickupNavigationFragment();
                fragmentManager = getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, pickupNavigationFragment, pickupNavigationFragment.getClass().getName());
                fragmentTransaction.addToBackStack(pickupNavigationFragment.getClass().getName());
                fragmentTransaction.commit();
            }
        });

        mBtnArrived = (Button) view.findViewById(R.id.btnArrived);
        mBtnArrived.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JobPreviewFragment jobPreviewFragment = new JobPreviewFragment();
                fragmentManager =getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, jobPreviewFragment , jobPreviewFragment.getClass().getName());
                fragmentTransaction.addToBackStack(jobPreviewFragment.getClass().getName());
                fragmentTransaction.commit();
            }
        });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onPickupInfoFragmentInteraction();
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
        void onPickupInfoFragmentInteraction();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.pickup_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_info:
                break;

        }
        return super.onOptionsItemSelected(item);

    }
}
