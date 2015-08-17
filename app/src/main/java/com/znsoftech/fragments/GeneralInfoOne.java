package com.znsoftech.fragments;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.znsoftech.utils.ToastClass;
import com.znsoftech.www.surveyapp.R;

public class GeneralInfoOne extends Fragment {

    String spinnerValue[]=new String[]{"Male", "Female"};

    final int CAMERA_CAPTURE = 1;
    final int PIC_CROP = 2;
    private Uri picUri;
    ImageButton imageButton;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.general_info_fragment_one, container, false);

        Button next=(Button)view.findViewById(R.id.next);
        imageButton =(ImageButton)view.findViewById(R.id.imageButton);
        EditText firstName=(EditText)view.findViewById(R.id.editText13);
        EditText middleName=(EditText)view.findViewById(R.id.editText14);
        EditText lastName=(EditText)view.findViewById(R.id.editText15);
        EditText emailID=(EditText)view.findViewById(R.id.editText16);
        EditText mobileNumber=(EditText)view.findViewById(R.id.editText17);
        EditText phoneNumber=(EditText)view.findViewById(R.id.editText18);
        EditText address=(EditText)view.findViewById(R.id.editText19);
        EditText city=(EditText)view.findViewById(R.id.editText20);
        EditText state=(EditText)view.findViewById(R.id.editText21);
        EditText country=(EditText)view.findViewById(R.id.editText22);
        EditText zipcode=(EditText)view.findViewById(R.id.editText23);

        Spinner spinner=(Spinner)view.findViewById(R.id.spinner);
        ArrayAdapter<?> adapter=new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, spinnerValue);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //use standard intent to capture an image
                    Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(captureIntent, CAMERA_CAPTURE);
                }
                catch(ActivityNotFoundException anfe){
                    //display an error message
                    String errorMessage = getString(R.string.error_in_photo_capturing);
                    ToastClass.showShort(getActivity(),errorMessage);
                }
            }
        });

        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == getActivity().RESULT_OK) {
            if(requestCode == CAMERA_CAPTURE){
                picUri = data.getData();
                //imageButton.setImageURI(picUri);
                performCrop();
            }
            else if(requestCode == PIC_CROP){
                Bundle extras = data.getExtras();
                Bitmap thePic = extras.getParcelable("data");
                imageButton.setImageBitmap(thePic);
            }
        }
    }

    /**
     * Helper method to carry out crop operation
     */
    private void performCrop(){
        //take care of exceptions
        try {
            //call the standard crop action intent (the user device may not support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            //indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            //set crop properties
            cropIntent.putExtra("crop", "true");
            //indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            //indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            //retrieve data on return
            cropIntent.putExtra("return-data", true);
            //start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, PIC_CROP);
        }
        //respond to users whose devices do not support the crop action
        catch(ActivityNotFoundException anfe){
            //display an error message
            String errorMessage = getString(R.string.error_in_croping);
            ToastClass.showShort(getActivity(),errorMessage);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
