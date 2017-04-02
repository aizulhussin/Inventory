package com.example.android.inventory;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventory.data.InventoryContract.InventoryEntry;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InventoryEditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private EditText productNameEditText;
    private EditText productPriceEditText;
    private TextView productQuantityTextView;
    private ImageView productImageView;
    private int productQuantity = 0;
    private Uri currentUri = null;
    private Uri imageUri = null;
    private String mCurrentPhotoPath;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 2;

    private String LOG_CAT = InventoryEditorActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_editor);


        //get current Uri for edit
        Intent intent = getIntent();
        currentUri = intent.getData();

        //get input reference
        productNameEditText = (EditText)findViewById(R.id.product_name_edit_text);
        productPriceEditText = (EditText)findViewById(R.id.price_edit_text);
        productQuantityTextView = (TextView)findViewById(R.id.quantity_text_view);
        productImageView = (ImageView)findViewById(R.id.product_image_view);


        productImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //TODO load camera to capture photo and save the Uri
                capturePhoto();
            }
        });


    }


    private void capturePhoto(){

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                imageUri = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            productImageView.setImageBitmap(imageBitmap);
        }else if(requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK){

            //do something
            Log.d(LOG_CAT,"Image URI "+imageUri.toString());
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_save:
                save();
                return true;
            case R.id.action_delete:
                delete();
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void addQuantity(View view){

        productQuantity++;
        productQuantityTextView.setText(productQuantity);

    }

    public void decreaseQuantity(View view){

        productQuantity--;
        productQuantityTextView.setText(productQuantity);
    }

    public void orderProduct(View view){

        //TODO call intent to email order
    }


    private void save(){

        String productNameString = productNameEditText.getText().toString();
        String priceString = productPriceEditText.getText().toString();
        String quantityString = productQuantityTextView.getText().toString();

        //TODO add validations

        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT_NAME,productNameString);
        values.put(InventoryEntry.COLUMN_PRICE,priceString);
        values.put(InventoryEntry.COLUMN_QUANTITY,quantityString);

        if(imageUri!=null) {
            values.put(InventoryEntry.COLUMN_IMAGE_PATH, imageUri.toString());
        }


        if(currentUri==null) {

            //add

            Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);

            if (newUri != null) {
                Toast.makeText(this, "Item saved", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error saving item", Toast.LENGTH_SHORT).show();
            }
        }else{

            //update
            int rowsUpdated = getContentResolver().update(currentUri,values,null,null);

            if(rowsUpdated > 0){
                Toast.makeText(this,"Item saved",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,"Error saving item",Toast.LENGTH_SHORT).show();
            }
        }

    }


    private void delete(){

        int rowsDeleted = getContentResolver().delete(currentUri,null,null);

        if (rowsDeleted == 0) {
            Toast.makeText(this, "Item deleted",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,"Delete unsuccessful",
                    Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRICE,
                InventoryEntry.COLUMN_QUANTITY,
                InventoryEntry.COLUMN_IMAGE_PATH

        };

        return new CursorLoader(this,currentUri,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor.moveToFirst()) {

            String productName = cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME));
            String productPrice = cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_PRICE));
            String productQuantity = cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_QUANTITY));
            String productImagePath = cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_IMAGE_PATH));

            productNameEditText.setText(productName);
            productPriceEditText.setText(productPrice);
            productQuantityTextView.setText(productQuantity);
            //productImageView.setImageURI(Uri.parse(productImagePath));

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        productNameEditText.setText("");
        productPriceEditText.setText("");
        productQuantityTextView.setText("");
        //productImageView.setImageURI(null);
    }
}
