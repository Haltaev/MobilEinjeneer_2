package ru.mobilengineer.common;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ru.mobilengineer.R;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class TakeOrChoosePhotoActivity {

    private final static int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_CAMERA = 90;
    private final static int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_GALLERY = 91;

    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_IMAGE_OPEN = 2;

    public static final String TTN_TRN_PHOTOS = "TTN_TRN_PHOTOS";

    private MediaListener listener;

    public TakeOrChoosePhotoActivity(MediaListener listener) {
        this.listener = listener;
    }

    String photoPath;

    ArrayList<String> photos = new ArrayList<>();

    public void onTakePhotoBtnClick(Fragment fragment) {
        checkPermissions(fragment, true);
    }

    public void onChoosePhotoBtnClick(Fragment fragment) {
        checkPermissions(fragment, false);
    }

    private void checkPermissions(Fragment fragment, boolean isCamera) {
        if(isCamera) {
            if (ContextCompat.checkSelfPermission(fragment.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(fragment.getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                if (ActivityCompat.shouldShowRequestPermissionRationale(fragment.getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                    new AlertDialog.Builder(fragment.getContext()).setMessage(R.string.error_permission_gallery_and_camera)
                            .setPositiveButton(android.R.string.ok, (dialog, which) -> fragment.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                                    PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_CAMERA))
                            .setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss())
                            .setCancelable(false).create().show();
                } else {

                    if (permissionWasRequestedEarlier(fragment)) {

                        new AlertDialog.Builder(fragment.getContext()).setMessage(R.string.error_permission_gallery_and_camera_settings)
                                .setPositiveButton(R.string.settings, (dialog, which) -> {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    Uri uri = Uri.fromParts("package", fragment.getActivity().getPackageName(), null);
                                    intent.setData(uri);
                                    fragment.startActivity(intent);
                                }).setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss())
                                .setCancelable(false).create().show();

                    } else {
                        fragment.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                                PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_CAMERA);
                    }
                }
            } else onCameraClick(fragment);
        } else {
        if (ContextCompat.checkSelfPermission(fragment.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                if (ActivityCompat.shouldShowRequestPermissionRationale(fragment.getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                    new AlertDialog.Builder(fragment.getContext()).setMessage(R.string.error_permission_gallery)
                            .setPositiveButton(android.R.string.ok, (dialog, which) -> fragment.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_GALLERY))
                            .setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss())
                            .setCancelable(false).create().show();
                } else {

                    if (permissionWasRequestedEarlier(fragment)) {

                        new AlertDialog.Builder(fragment.getContext()).setMessage(R.string.error_permission_gallery_settings)
                                .setPositiveButton(R.string.settings, (dialog, which) -> {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    Uri uri = Uri.fromParts("package", fragment.getActivity().getPackageName(), null);
                                    intent.setData(uri);
                                    fragment.startActivity(intent);
                                }).setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss())
                                .setCancelable(false).create().show();

                    } else {
                        fragment.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_GALLERY);
                    }
                }
            }else onGalleryClick(fragment);
        }
    }

    private boolean permissionWasRequestedEarlier(Fragment fragment) {
        SharedPreferences sp = fragment.getActivity().getSharedPreferences("PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE", MODE_PRIVATE);
        return sp.getBoolean("PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE", false);
    }

    private void setPermissionWasRequestedEarlier(Fragment fragment) {
        SharedPreferences sp = fragment.getActivity().getSharedPreferences("PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE", MODE_PRIVATE);
        sp.edit().putBoolean("PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE", true).apply();
    }


    public void onRequestPermissionsResult(Fragment fragment, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_GALLERY:
                    onGalleryClick(fragment);
                    break;
                case PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_CAMERA:
                    onCameraClick(fragment);
                    break;
            }
        } else {
            Toast.makeText(fragment.getContext(), R.string.error_permission_gallery, Toast.LENGTH_SHORT).show();

            setPermissionWasRequestedEarlier(fragment);
        }
    }


    public void onActivityResult(Fragment fragment, int requestCode, int resultCode, Intent data) {
        Uri photoUri = null;
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_OPEN) {
                photoUri = data.getData();
                processPhotoUri(photoUri, fragment);
            }
            if (requestCode == REQUEST_TAKE_PHOTO) {
                processPhotoUri(photoUri, fragment);
            }
        } else Log.e("error", "onActivityResult");

    }


    private void hideLoading() {
        listener.hideLoading();
    }

    private void showLoading() {
        listener.showLoading();
    }


    private String remakeFileFromUri(Uri photoUri, Fragment fragment) {

        try {
            File file = createImageFile(fragment);

            InputStream input = fragment.getActivity().getContentResolver().openInputStream(photoUri);
            try (OutputStream output = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024]; // or other buffer size
                int read;

                while ((read = input.read(buffer)) != -1) {
                    output.write(buffer, 0, read);
                }

                output.flush();
                photoPath = file.getAbsolutePath();
                return photoPath;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    private void processPhotoUri(final Uri photoUri, Fragment fragment) {
        Observable<String> observable;

        if (photoPath == null)
            observable = Observable.fromCallable(() -> remakeFileFromUri(photoUri, fragment));
        else {
            observable = Observable.just(photoPath);
        }

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String path) {
                        try {
                            photoPath = MediaUtils.compressImage(path, fragment.getContext());
                            listener.success(photoPath);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("Error", e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private boolean sizeIsValid() {

        File file = new File(photoPath);

        long l = file.length();

        l = l / 1000;//kb
        l = l / 1000;//mb

        return l < 5;

    }

    private void onCameraClick(Fragment fragment) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(fragment.getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile(fragment);
            } catch (IOException ex) {
                Toast.makeText(fragment.getContext(), "Ошибка при создании файла  для фотографии", Toast.LENGTH_SHORT).show();
                Toast.makeText(fragment.getContext(), ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
            if (photoFile != null) {
                Uri photoUri =
                        Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT ?
                                FileProvider.getUriForFile(fragment.getContext(), "ru.mobilengineer.fileprovider", photoFile) :
                                Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                fragment.startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private void onGalleryClick(Fragment fragment) {
        photoPath = null;

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        // Only the system receives the ACTION_OPEN_DOCUMENT, so no need to test.
        fragment.startActivityForResult(intent, REQUEST_IMAGE_OPEN);
    }

    private File createImageFile(Fragment fragment) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "ttn_" + timeStamp + "_";
        File storageDir = fragment.getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        photoPath = image.getAbsolutePath();
        return image;
    }
}
