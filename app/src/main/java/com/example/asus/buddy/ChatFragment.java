package com.example.asus.buddy;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChatFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */


public class ChatFragment extends Fragment  {
    FragmentTransaction transaction;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    RecyclerView re;
    ProgressDialog progress;
    private List<Groups> mygroups=new ArrayList<>();
    private List<String> mDatakey=new ArrayList<>();
    private ArrayList<String>  groupids=new ArrayList<>();



    formadapterv1 cc;
    View v;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // In
        // flate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_chat, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        re=(RecyclerView)view.findViewById(R.id.my_recycler_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);


        re.setLayoutManager(layoutManager);


        loadData();
        Log.i("TEST - MY GROUPS:",mygroups.toString());

        cc=new formadapterv1( mygroups,getActivity());
        re.setHasFixedSize(true);
        re.setAdapter(cc);

        re.setItemAnimator(new DefaultItemAnimator());
        re.getAdapter().notifyDataSetChanged();



        return view;



    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }


    private void loadData() {


        auth=FirebaseAuth.getInstance();

        String userid = auth.getCurrentUser().getUid();


        DatabaseReference usrgroup = FirebaseDatabase.getInstance().getReference().child("UsersGroup").child(userid);

        usrgroup.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                for (DataSnapshot single:dataSnapshot.getChildren()){
                    String temp=single.getKey().toString();

                    Log.e("First", single.getKey().toString());
                    getgroupinfo(temp);


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });






    }



    private void getgroupinfo(String temp) {

        DatabaseReference groupinfo = FirebaseDatabase.getInstance().getReference().child("Groups").child(temp);
        groupinfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Log.e("First", dataSnapshot1.child("CreatedTime").getValue().toString());


                if (dataSnapshot.getValue()!=null){
                    String displaytime=dataSnapshot.child("CreatedTime").getValue(String.class);
                    String displayid=dataSnapshot.child("Id").getValue().toString();
                    String displaypath=dataSnapshot.child("ImageUrlPath").getValue().toString();
                    String displayleader=dataSnapshot.child("LeaderId").getValue().toString();
                    String displaytitle=dataSnapshot.child("Title").getValue().toString();
                    mygroups.add(new Groups(displaytime,displayid,displaypath,displayleader,displaytitle));
                }




                cc.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        FloatingActionButton fab=(FloatingActionButton)getActivity().findViewById(R.id.addgroup);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).add(R.id.content,new CreateGroupFragment(),
                        "Fragmentcreate").addToBackStack("Fragmentcreate").commit();


            }
        });

    }
}
