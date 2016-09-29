package com.apps.jorge.monkeybisfire.util;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class Image {


    public static final int ACTION_CAPTURE_IMAGE = 1325;
    public static final int ACTION_PICK_IMAGE = 1326;

    public static final String LOCAL_RESOURCE_PATH =
            Environment.getExternalStorageDirectory() + "/jxx/";
    public static final String THUMBNAIL_PATH = LOCAL_RESOURCE_PATH + ".thumbnails/";
    public static final String PROFILE_THUMBNAIL_PATH = LOCAL_RESOURCE_PATH + ".profile/";


    public static Intent captureImage(String fileName, Context context) {

        File folder = new File(Image.LOCAL_RESOURCE_PATH);

        String mPathImage = Image.LOCAL_RESOURCE_PATH + fileName;

        if (!folder.exists())
            folder.mkdirs();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(mPathImage);
        Uri mImageCaptureUri = Uri.fromFile(file);
        try {
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    mImageCaptureUri);
            return intent;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return intent;
    }

    public static Intent pickImage() {

        Intent mediaIntent = new Intent(Intent.ACTION_GET_CONTENT);
        mediaIntent.setType("image/*");
        return mediaIntent;
    }



    public static boolean validateFields(EditText textField) {

        if (textField.getText().toString().length() == 0) {
            textField.setError("Required");
            return false;
        } else return true;
    }


    public static Bitmap getImage(String path) {
        try {
            File imageFile = new File(path);
            Bitmap bitmap = BitmapFactory.decodeFile(imageFile
                    .getAbsolutePath());

            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap reduceImage(String originalPath) {
        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        o.inPurgeable = true;
        o.inInputShareable = true;
        BitmapFactory.decodeFile(originalPath, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 320;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inPurgeable = true;
        o2.inInputShareable = true;
        o2.inSampleSize = scale;
        Bitmap bitmapScaled = null;
        bitmapScaled = BitmapFactory.decodeFile(originalPath, o2);

        return bitmapScaled;
    }


    public static boolean saveImage(Bitmap bitmap, String imageName) {
        try {
            FileOutputStream outActual = new FileOutputStream(new File(LOCAL_RESOURCE_PATH + imageName));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outActual);
            outActual.flush();
            outActual.close();

            FileOutputStream outProfile = new FileOutputStream(new File(PROFILE_THUMBNAIL_PATH + imageName));
            Bitmap profileBitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, true);
            profileBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outProfile);
            outProfile.flush();
            outProfile.close();

            FileOutputStream outThumbnail = new FileOutputStream(new File(THUMBNAIL_PATH + imageName));
            Bitmap thumbnailBitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
            thumbnailBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outThumbnail);
            outThumbnail.flush();
            outThumbnail.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean saveImage(String path, String imageName) {
        try {
            Bitmap bitmap = reduceImage(path);

            if (bitmap != null) {
                FileOutputStream outOriginal = new FileOutputStream(new File(LOCAL_RESOURCE_PATH + imageName));
                //  Bitmap originalBitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outOriginal);
                outOriginal.flush();
                outOriginal.close();

                FileOutputStream outProfile = new FileOutputStream(new File(PROFILE_THUMBNAIL_PATH + imageName));
                Bitmap profileBitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, true);
                profileBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outProfile);
                outProfile.flush();
                outProfile.close();

                FileOutputStream outThumbnail = new FileOutputStream(new File(THUMBNAIL_PATH + imageName));
                Bitmap thumbnailBitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
                thumbnailBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outThumbnail);
                outThumbnail.flush();
                outThumbnail.close();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean saveThumbnail(Bitmap bitmap, String imagePath) {
        try {
            File file = new File(imagePath);
            FileOutputStream out = new FileOutputStream(file);
            Bitmap thumbnail = Bitmap.createScaledBitmap(bitmap, 30, 30, true);
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }



    public static String getRealPathFromURI(Context context, Uri contentURI) {
        String result = "";
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(contentURI, null, null, null, null);
            if (cursor == null) { // Source is Dropbox or other similar local file path
                result = contentURI.getPath();
            } else {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                result = cursor.getString(idx);
                cursor.close();
            }
        } catch (Exception e) {
            if (cursor != null) {
                cursor.close();
            }
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return result;
    }




    public static boolean createLocalResourceDirectory(Context context) {
        try {
            File localResourceDirectory = new File(Environment.getExternalStorageDirectory() + "/jxx");
            if (!localResourceDirectory.exists()) {
                boolean directoryCreated = localResourceDirectory.mkdir();
                if (!directoryCreated) {
                    Toast.makeText(context, "Folder not created", Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    return true;
                }
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean createThumbNailDirectory(Context context) {
        try {
            File localResourceDirectory = new File(Environment.getExternalStorageDirectory() + "/.jxx/.thumbnails");
            if (!localResourceDirectory.exists()) {
                boolean directoryCreated = localResourceDirectory.mkdir();
                if (!directoryCreated) {
                    Toast.makeText(context, "Please ensure External Storage on your device!", Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    return true;
                }
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean createProfileImageDirectory(Context context) {
        try {
            File localResourceDirectory = new File(Environment.getExternalStorageDirectory() + "/.jxx/.profile");
            if (!localResourceDirectory.exists()) {
                boolean directoryCreated = localResourceDirectory.mkdir();
                if (!directoryCreated) {
                    Toast.makeText(context, "Please ensure External Storage on your device!", Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    return true;
                }
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


}
