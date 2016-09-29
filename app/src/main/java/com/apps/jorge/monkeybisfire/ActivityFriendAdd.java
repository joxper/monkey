package com.apps.jorge.monkeybisfire;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.apps.jorge.monkeybisfire.adapter.FriendsListAdapter;
import com.apps.jorge.monkeybisfire.data.Constant;
import com.apps.jorge.monkeybisfire.model.Friend;
import com.apps.jorge.monkeybisfire.util.Image;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class ActivityFriendAdd extends AppCompatActivity {
	private Toolbar mToolbar;
    private EditText nameEditText, emailEditText,
            cityEditText;
    private ImageView photoEdit;
    private Spinner countryList, categoryList;
    private String photoName;
    public FloatingActionButton save;
    private ArrayList<Map<String, Object>> mapArrayList;
    static ArrayList<HashMap<String, Object>> list;
    static ArrayList<HashMap<String, Object>> list1;

    public FriendsListAdapter mAdapter;

    private StorageReference storageRef;
    private StorageReference photoRef;
    private UploadTask uploadTask;

    private String qrData;

    public static String KEY_FRIEND = "com.apps.jorge.monkeybisfire.FRIEND";
    private Friend friend;



	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_add);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mAdapter = new FriendsListAdapter(this);

        // initialize conversation data
        Intent intent = getIntent();
        Friend friendEdit = (Friend) intent.getExtras().getSerializable(KEY_FRIEND);
        friend = new Friend(friendEdit.getKey().toString(),friendEdit.getName().toString(),friendEdit.getCity().toString(),friendEdit.getPhoto().toString());
        Log.i(Constant.TAG, "friend save: "+friend.getKey());

//        Bundle extras = getIntent().getExtras();
//        if (extras != null) {
//            this.qrData = extras.getString("qrData");
//            //The key argument here must match that used in the other activity
//        }

        //Storage Reference
        // Create a storage reference from our app
        storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://monkeybisfire.appspot.com");

        setReference();

        photoName = createPhotoName();


        if (friend != null) {
            // pre-populate
            nameEditText.setText(friend.getName());
            cityEditText.setText(friend.getCity());
            photoName = friend.getPhoto().toString();
            photoEdit.setImageBitmap(BitmapFactory.decodeFile(Image.LOCAL_RESOURCE_PATH + photoName));
        }

    }

    private String createPhotoName(){
        photoName = new Date().getTime() + ".JPG";
        Log.i("image before", ""+photoName);
        Image.createLocalResourceDirectory(this);
        //Image.createProfileImageDirectory(this);
        return photoName;
    }
    public void setReference() {
        photoEdit = (ImageView) findViewById(R.id.photo);
        nameEditText = (EditText) findViewById(R.id.input_name);
        emailEditText = (EditText) findViewById(R.id.input_email);
        cityEditText = (EditText) findViewById(R.id.input_city);
        photoEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageUploadOption();
            }
        });
        save = (FloatingActionButton) findViewById(R.id.btn_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save(friend);
            }
        });
        //name.setText(qrData);
    }



    public void showImageUploadOption() {

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                this);
        //  builderSingle.setIcon(R.drawable.icon);
        builderSingle.setTitle("Select Image");
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.select_dialog_item);

        arrayAdapter.add("Choose From Gallery");
        arrayAdapter.add("Capture From Camera");

        builderSingle.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }
        );

        builderSingle.setAdapter(arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                startActivityForResult(Image.pickImage(), Image.ACTION_PICK_IMAGE);
                                break;
                            case 1:
                                startActivityForResult(Image.captureImage(photoName,
                                        ActivityFriendAdd.this), Image.ACTION_CAPTURE_IMAGE);
                                break;
                        }
                    }
                }
        );
        builderSingle.show();
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        switch (reqCode) {
            case 123:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        mapArrayList.addAll((ArrayList<Map<String, Object>>)
                                data.getSerializableExtra("data"));
                        //   reloadData();
                        Log.d("map is", mapArrayList.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case Image.ACTION_CAPTURE_IMAGE:
                photoName = createPhotoName();
                if (resultCode == Activity.RESULT_OK) {
                    Image.saveImage(Image.LOCAL_RESOURCE_PATH + photoName, photoName);
                    photoEdit.setImageBitmap(BitmapFactory.decodeFile(Image.LOCAL_RESOURCE_PATH + photoName));
                }
                break;
            case Image.ACTION_PICK_IMAGE:
                photoName = createPhotoName();
                if (resultCode == Activity.RESULT_OK) {
                    Uri photoUri = data.getData();
                    Log.d("", "imageUri = " + Image.getRealPathFromURI(this, data.getData()));
                    try {
                        Image.saveImage(Image.getRealPathFromURI(this, photoUri), photoName);
                        photoEdit.setImageBitmap(BitmapFactory.decodeFile(Image.LOCAL_RESOURCE_PATH + photoName));
                        Log.i("image middle", ""+photoName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    public boolean save(Friend friend) {
        Log.i(Constant.TAG, "friend save: "+friend.getKey());
        if (Image.validateFields(nameEditText)) {
            photoRef = storageRef.child("photos/"+photoName);
            String name = nameEditText.getText().toString();
            String city = cityEditText.getText().toString();
            String photo = photoName;

            Uri file = Uri.fromFile(new File(Image.LOCAL_RESOURCE_PATH + photoName));
            uploadTask = photoRef.putFile(file);

            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Log.e(Constant.TAG, "onFailure: ", exception );
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Log.i(Constant.TAG, ""+downloadUrl);
                }
            });
            if (friend != null) {
                mAdapter.update(friend, name, city, photo);
                Log.i(Constant.TAG, "friend save: "+friend.getKey());
            }
            else{
                mAdapter.add(new Friend(name, city, photo));
            }
            Intent intent = new Intent(ActivityFriendAdd.this, ActivityMain.class);
            startActivity(intent);

        }
        return true;
    }


    @Override
    public void onBackPressed() {
        File file = new File(Image.LOCAL_RESOURCE_PATH, photoName);
        if (file.exists())
            file.delete();
        super.onBackPressed();
    }
}
