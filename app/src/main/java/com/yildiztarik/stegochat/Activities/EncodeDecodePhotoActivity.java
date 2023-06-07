package com.yildiztarik.stegochat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.annotation.Nullable;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;

import android.os.Environment;
import android.provider.MediaStore;

import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.squareup.picasso.Picasso;

import com.yildiztarik.stegochat.R;
import com.yildiztarik.stegochat.Utils.AES256;
import com.yildiztarik.stegochat.Utils.Constants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class EncodeDecodePhotoActivity extends AppCompatActivity {
    boolean decodeReceivedModeEnabled;
    String myFilePath, encodedMessage, receiverId, imageURL, imageRefPath;

    Bitmap photoBitmap;
    Uri imageUri;

    ImageView selectImage;
    EditText editTextKey, editTextMessage;
    Button buttonEncodeDecode;
    Switch controllerSwitch;

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    FirebaseAuth auth;
    FirebaseUser user;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encode_decode_photo);
        decodeReceivedModeEnabled = false;
        Intent intent = getIntent();
        receiverId = intent.getStringExtra(Constants.OTHER_ID);
        initComponents();
        setOnClick();
        try {
            if (!intent.getStringExtra(Constants.IMAGE_URL).equals("")) {
                selectImage.setImageDrawable(null);
                selectImage.setBackgroundResource(0);
                imageURL = intent.getStringExtra(Constants.IMAGE_URL);
                Picasso.get().load(imageURL).into(selectImage);
                imageUri = Uri.parse(imageURL);
                decodeReceivedImageMode();
                getImageRefPath2(imageURL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void initComponents() {
        setFirebaseItems();
        controllerSwitch = findViewById(R.id.switch_item);
        selectImage = findViewById(R.id.select_image);
        editTextKey = findViewById(R.id.key_edittext);
        editTextMessage = findViewById(R.id.message_edittext);
        buttonEncodeDecode = findViewById(R.id.encode_image_button);
        buttonEncodeDecode.setText("ENCODE AND SEND IMAGE");
    }

    void setOnClick() {
        controllerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    controllerSwitch.setText("DECODE");
                    buttonEncodeDecode.setText("DECODE");
                    editTextMessage.setEnabled(false);
                    editTextMessage.setHint("disabled");

                } else {
                    controllerSwitch.setText("ENCODE");
                    buttonEncodeDecode.setText("ENCODE AND SEND IMAGE");
                    editTextMessage.setEnabled(true);
                    editTextMessage.setHint("Enter the text to be encrypted.");

                }
            }
        });

        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int permission = ActivityCompat.checkSelfPermission(EncodeDecodePhotoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (permission != PackageManager.PERMISSION_GRANTED) {
                    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    ActivityCompat.requestPermissions(EncodeDecodePhotoActivity.this, permissions, 1);
                }
                if (ContextCompat.checkSelfPermission(EncodeDecodePhotoActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(EncodeDecodePhotoActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                } else {
                    Intent image = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(image, 2);
                }
            }
        });

        buttonEncodeDecode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (controllerSwitch.isChecked()) {
                    if (editTextKey.getText().toString().length() < 8) {
                        Toast.makeText(EncodeDecodePhotoActivity.this, "Lütfen en az 8 haneli bir anahtar giriniz.", Toast.LENGTH_SHORT).show();
                    } else if (imageUri == null) {
                        Toast.makeText(EncodeDecodePhotoActivity.this, "Lütfen fotoğraf seçin.", Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            if (decodeReceivedModeEnabled) {
                                getimagem();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    if (imageUri == null) {
                        Toast.makeText(EncodeDecodePhotoActivity.this, "Bir fotoğraf seçmeniz gerekiyor!", Toast.LENGTH_SHORT).show();
                    } else if (editTextMessage.getText().length() == 0) {
                        Toast.makeText(EncodeDecodePhotoActivity.this, "Şifrelemek istediğiniz mesajı henüz girmediniz!", Toast.LENGTH_SHORT).show();
                    } else if (editTextKey.getText().length() < 8) {
                        Toast.makeText(EncodeDecodePhotoActivity.this, "Lütfen en az 8 haneli anahtar belirleyin!", Toast.LENGTH_SHORT).show();
                    } else {
                        encodedMessage = AES256.encrypt(editTextMessage.getText().toString(), editTextKey.getText().toString());
                        Toast.makeText(EncodeDecodePhotoActivity.this, encodedMessage, Toast.LENGTH_SHORT).show();
                        try {
                            photoBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                            addTextToImageFirebase2(photoBitmap, encodedMessage);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        });
    }

    private void decodeReceivedImageMode() {
        selectImage.setClickable(false);
        controllerSwitch.setChecked(true);
        controllerSwitch.setClickable(false);
        decodeReceivedModeEnabled = true;
    }

    void getimagem() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference fileRef = storageRef.child(imageRefPath);
        String originalFileName = fileRef.getName();
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "myapp");

        if (!directory.exists()) {
            directory.mkdirs();
        }
        File file = new File(directory, originalFileName);
        if (file.exists()) {
            file.delete();
        }

        fileRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                myFilePath = file.getAbsolutePath();
                try (InputStream inputStream = new FileInputStream(file)) {

                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    decodeReceivedImage3(myFilePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                exception.printStackTrace();
            }
        });


    }

    private void decodeReceivedImage3(String filePath) throws IOException {

        Uri uri = FileProvider.getUriForFile(this, "com.yildiztarik.stegoochat.fileprovider", new File(filePath));
        String extracted_text = null;
        try {
            extracted_text = extractTextFromImage(uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String user_key = editTextKey.getText().toString();
        String mesaj = AES256.decrypt(extracted_text, user_key);
        if (mesaj != null) {
            Toast.makeText(EncodeDecodePhotoActivity.this, "Gizli mesaj: " + mesaj, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(EncodeDecodePhotoActivity.this, "Şifre yok ya da anahtar hatalı.", Toast.LENGTH_SHORT).show();
        }
    }

    public String extractTextFromImage(Uri myUri) throws Exception {
        String[] projection = {MediaStore.Images.Media.DISPLAY_NAME};
        Cursor cursor = getContentResolver().query(myUri, projection, null, null, null);
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
        cursor.moveToFirst();
        String imageName = cursor.getString(columnIndex);
        int location1 = imageName.indexOf('_');
        int location2 = imageName.indexOf('.');
        int bytes_of_original_photo = Integer.parseInt(imageName.substring(location1 + 1, location2));
        FileInputStream fileInputStream = new FileInputStream(myFilePath);
        long fileSize = new File(myFilePath).length();
        byte[] textInBytes = new byte[(int) (fileSize - bytes_of_original_photo)];
        fileInputStream.skip(bytes_of_original_photo);
        fileInputStream.read(textInBytes);
        fileInputStream.read(textInBytes);
        String extractedText = new String(textInBytes, StandardCharsets.UTF_8);
        fileInputStream.close();
        return extractedText;
    }


    private String getImageRefPath2(String firebaseImageURL) {
        Uri uri = Uri.parse(firebaseImageURL);
        String path = uri.getPath();
        if (path != null) {
            if (path.startsWith("/")) {
                path = path.substring(1);
            }

            String[] pathParts = path.split("/");

            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 4; i < pathParts.length; i++) {
                if (i != 4) {
                    stringBuilder.append("/");
                }
                stringBuilder.append(pathParts[i]);
            }
            imageRefPath = stringBuilder.toString();
            return imageRefPath;
        }

        return "";
    }

    private void setFirebaseItems() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
    }

    private String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date today = Calendar.getInstance().getTime();
        return dateFormat.format(today);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                selectImage.setImageDrawable(null);
                selectImage.setBackgroundResource(0);
                selectImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    void clean() {
        imageUri = null;
        editTextKey.setText("");
        editTextMessage.setText("");
        selectImage.setImageBitmap(null);
    }

    public void addTextToImageFirebase2(Bitmap image, String text) {
        Uri mYimageUri = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        byte[] textBytes = text.getBytes(StandardCharsets.UTF_8);
        byte[] newImageBytes = new byte[imageBytes.length + textBytes.length];
        System.arraycopy(imageBytes, 0, newImageBytes, 0, imageBytes.length);
        System.arraycopy(textBytes, 0, newImageBytes, imageBytes.length, textBytes.length);
        try {
            File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES); //or getExternalFilesDir(null); for external storage
            String fileName = Long.toString(System.currentTimeMillis()).replaceAll(":", ".") + "_" + imageBytes.length + ".jpg";
            File file = new File(directory, fileName);
            mYimageUri = Uri.fromFile(file);
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                fos.write(newImageBytes);
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Toast.makeText(this, fileName + " Dosyası olusturuldu...", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String messageId = databaseReference.child(Constants.CHILD_MESSAGES).child(user.getUid()).child(receiverId).push().getKey();
        storageReference.child(Constants.S_CHILD_IMAGE_MESSAGES).child(messageId).child(mYimageUri.getLastPathSegment()).putFile(mYimageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    task.getResult().getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Map messageItem = new HashMap();
                            messageItem.put(Constants.CHILD_MESSAGE_TYPE, Constants.CHILD_MESSAGE_TYPE_IMG);
                            messageItem.put(Constants.CHILD_MESSAGE_DATE, getDate());
                            messageItem.put(Constants.CHILD_MESSAGE_TEXT, uri.toString());
                            messageItem.put(Constants.CHILD_MESSAGE_FROM, user.getUid());
                            databaseReference.child(Constants.CHILD_MESSAGES).child(user.getUid()).child(receiverId).child(messageId).setValue(messageItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        databaseReference.child(Constants.CHILD_MESSAGES).child(receiverId).child(user.getUid()).child(messageId).setValue(messageItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(EncodeDecodePhotoActivity.this, "Image sent!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    });
                } else {
                }
            }
        });

    }
}