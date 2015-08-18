package com.znsoftech.fragments;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
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

import com.znsoftech.utils.Config;
import com.znsoftech.utils.DBHelper;
import com.znsoftech.utils.GPSTracker;
import com.znsoftech.utils.ToastClass;
import com.znsoftech.www.surveyapp.R;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class GeneralInfoOne extends Fragment {

    String spinnerValue[]=new String[]{"Male", "Female"};

    final int CAMERA_CAPTURE = 1;
    final int PIC_CROP = 2;
    private Uri picUri;
    ImageButton imageButton;
    GPSTracker gps;
    String photoPath=null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.general_info_fragment_one, container, false);

        Button next=(Button)view.findViewById(R.id.next);
        imageButton =(ImageButton)view.findViewById(R.id.imageButton);
        final EditText firstName=(EditText)view.findViewById(R.id.editText13);
        final EditText middleName=(EditText)view.findViewById(R.id.editText14);
        final EditText lastName=(EditText)view.findViewById(R.id.editText15);
        final EditText emailID=(EditText)view.findViewById(R.id.editText16);
        final EditText mobileNumber=(EditText)view.findViewById(R.id.editText17);
        final EditText phoneNumber=(EditText)view.findViewById(R.id.editText18);
        final EditText address=(EditText)view.findViewById(R.id.editText19);
        final EditText city=(EditText)view.findViewById(R.id.editText20);
        final EditText state=(EditText)view.findViewById(R.id.editText21);
        final EditText country=(EditText)view.findViewById(R.id.editText22);
        final EditText zipcode=(EditText)view.findViewById(R.id.editText23);

        final Spinner spinner=(Spinner)view.findViewById(R.id.spinner);
        ArrayAdapter<?> adapter=new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, spinnerValue);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        gps=new GPSTracker(getActivity());

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //use standard intent to capture an image
                    Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(captureIntent, CAMERA_CAPTURE);
                } catch (ActivityNotFoundException anfe) {
                    //display an error message
                    String errorMessage = getString(R.string.error_in_photo_capturing);
                    ToastClass.showShort(getActivity(), errorMessage);
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String f_name=firstName.getText().toString();
                String m_name=middleName.getText().toString();
                String l_name=lastName.getText().toString();
                String email_id=emailID.getText().toString();
                String mobile_number=mobileNumber.getText().toString();
                String phone_number=phoneNumber.getText().toString();
                String _address=address.getText().toString();
                String _city=city.getText().toString();
                String _state=state.getText().toString();
                String _country=country.getText().toString();
                String _zipcode=zipcode.getText().toString();
                String spinner_value=spinner.getSelectedItem().toString();

                if(gps.canGetLocation()){
                    gps.getLocation();
                }else{
                    gps.showSettingsAlert();
                    return;
                }

                String latitude=gps.getLatitude()+"";
                String longitude=gps.getLongitude()+"";

                if(f_name.length()<2 && m_name.length()<2 && l_name.length()<2){
                    ToastClass.showShort(getActivity(),getString(R.string.enter_consumer_name));
                    return;
                }

                String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

                DBHelper dbHelper=new DBHelper(getActivity(),"Survey.db",null,1);
                dbHelper.insertIntoConsumer(f_name,m_name,l_name,_address,_city,_state,_country,_zipcode,mobile_number,phone_number,spinner_value,photoPath,email_id,date, Config.user_name,latitude,longitude,"");

                ToastClass.showShort(getActivity(),"Done!");
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
                photoPath=SaveToSD(thePic);
            }
        }
    }

    private String SaveToSD(Bitmap thePic){
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + File.separator+getString(R.string.app_name));
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String imageName = "Image-"+ n +".jpg";
        String filePath=myDir+imageName;
        File file = new File(filePath);
        if (file.exists ())
            file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            thePic.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return filePath;
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
        gps.stopUsingGPS();
    }

}
