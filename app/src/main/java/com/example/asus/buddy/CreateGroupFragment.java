package com.example.asus.buddy;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateGroupFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreateGroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateGroupFragment extends Fragment {
    private ImageView image;
    private static final int RESULT_LOAD_IMAGE=1;
    Uri sellectedImage;
    EditText groupnme;
    private FirebaseAuth auth;
    String generatedFilePath;

    Button nextbut;
    Button imageb;
    Uri downloadUrl;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private StorageReference mStorageRef;
    private OnFragmentInteractionListener mListener;

    public CreateGroupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateGroupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateGroupFragment newInstance(String param1, String param2) {
        CreateGroupFragment fragment = new CreateGroupFragment();
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_group, container, false);
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
            Toast.makeText(context,"ChatFragment Atteched",Toast.LENGTH_SHORT).show();
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

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode==RESULT_LOAD_IMAGE&&data!=null){
            sellectedImage=data.getData();
            image.setImageURI(sellectedImage);
            if(sellectedImage!=null){
                String imgname= UUID.randomUUID().toString();
                StorageReference storageReference=mStorageRef.child("GroupImages/"+imgname+".jpg");
                storageReference.putFile(sellectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUri = taskSnapshot.getMetadata().getDownloadUrl();
                        generatedFilePath = downloadUri.toString();

                    }
                });
            }
        }
    }
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        image=(ImageView)getActivity().findViewById(R.id.imageView);
        groupnme=(EditText)getActivity().findViewById(R.id.groupnamecreate);
        auth=FirebaseAuth.getInstance();
        mStorageRef= FirebaseStorage.getInstance().getReference();

        groupnme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupnme.setText("");
            }
        });

        nextbut=(Button)getActivity().findViewById(R.id.buttonNext);
        imageb=(Button)getActivity().findViewById(R.id.buttonImage);
        imageb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galeryIntent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galeryIntent,RESULT_LOAD_IMAGE);

            }
        });


        FloatingActionButton fab=(FloatingActionButton)getActivity().findViewById(R.id.nextbutton);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String name=groupnme.getText().toString().trim();


                if (TextUtils.isEmpty(name)){
                    //name is empty
                    Toast.makeText(getActivity(),"Enter Group Name!",Toast.LENGTH_SHORT).show();
                    //shopping the function execution further
                    return;
                }

                String groupid=FirebaseDatabase.getInstance().getReference().push().getKey();
                DatabaseReference current_grup= FirebaseDatabase.getInstance().getReference().child("Groups").child(groupid);
                SimpleDateFormat bicim2=new SimpleDateFormat("yyyy-M-dd");
                String tarihSaat=bicim2.format(new Date());

                String userid=auth.getCurrentUser().getUid();
                Map newPost=new HashMap();
                newPost.put("CreatedTime",tarihSaat);
                newPost.put("Id",groupid);
                if (generatedFilePath!=null){
                    newPost.put("ImageUrlPath",generatedFilePath.toString());
                }else {
                    newPost.put("ImageUrlPath"," ");

                }

                newPost.put("LeaderId",userid);
                newPost.put("Title",name);
                CreateSecondGroupFragment fragment=new CreateSecondGroupFragment();
                current_grup.updateChildren(newPost);
                Bundle arguments = new Bundle();
                arguments.putString( "gruopid" , groupid);
                arguments.putString( "gruopname" , name);

                fragment.setArguments(arguments);
                FragmentTransaction transaction=getFragmentManager().beginTransaction();
                transaction.replace(R.id.content,fragment).commit();

            }
        });

    }


}
