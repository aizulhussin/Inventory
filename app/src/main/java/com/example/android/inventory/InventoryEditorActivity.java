package com.example.android.inventory;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventory.data.InventoryContract.InventoryEntry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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

    private String LOG_TAG = InventoryEditorActivity.class.getSimpleName();
    private static final int PICK_IMAGE_REQUEST = 0;
    private static final int SEND_MAIL_REQUEST = 1;
    private static int INVENTORY_EDITOR_LOADER=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_editor);


        //get current Uri for edit
        Intent intent = getIntent();
        currentUri = intent.getData();

        //get input reference
        productNameEditText = (EditText) findViewById(R.id.product_name_edit_text);
        productPriceEditText = (EditText) findViewById(R.id.price_edit_text);
        productQuantityTextView = (TextView) findViewById(R.id.quantity_text_view);
        productImageView = (ImageView) findViewById(R.id.product_image_view);

        if(currentUri!=null) {
            getSupportLoaderManager().initLoader(INVENTORY_EDITOR_LOADER, null, this);
        }

        productImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openImageSelector();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
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


    public void addQuantity(View view) {

        productQuantity++;
        productQuantityTextView.setText(Integer.toString(productQuantity));

    }

    public void decreaseQuantity(View view) {

        productQuantity--;
        productQuantityTextView.setText(Integer.toString(productQuantity));
    }

    public void orderProduct(View view) {

        //TODO call intent to email order
    }


    private void save() {

        String productNameString = productNameEditText.getText().toString();
        String priceString = productPriceEditText.getText().toString();
        String quantityString = productQuantityTextView.getText().toString();

        //TODO add validations

        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT_NAME, productNameString);
        values.put(InventoryEntry.COLUMN_PRICE, priceString);
        values.put(InventoryEntry.COLUMN_QUANTITY, quantityString);

        if (imageUri != null) {
            Log.i(LOG_TAG, "Uri: " + imageUri.toString());
            values.put(InventoryEntry.COLUMN_IMAGE_PATH, imageUri.toString());
        }


        if (currentUri == null) {

            //add

            Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);

            if (newUri != null) {
                Toast.makeText(this, "Item saved", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error saving item", Toast.LENGTH_SHORT).show();
            }
        } else {

            //update
            int rowsUpdated = getContentResolver().update(currentUri, values, null, null);

            if (rowsUpdated > 0) {
                Toast.makeText(this, "Item saved", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error saving item", Toast.LENGTH_SHORT).show();
            }
        }

    }


    private void delete() {

        int rowsDeleted = getContentResolver().delete(currentUri, null, null);

        if (rowsDeleted > 0) {
            Toast.makeText(this, "Item deleted",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Delete unsuccessful",
                    Toast.LENGTH_SHORT).show();
        }

        Intent intent = new Intent(InventoryEditorActivity.this,InventoryActivity.class);
        startActivity(intent);

        finish();


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

        return new CursorLoader(this, currentUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor.moveToFirst()) {

            Log.d(LOG_TAG,"Cursor Move To First");

            String productName = cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME));
            String productPrice = cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_PRICE));
            String productQuantityString = cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_QUANTITY));
            String productImagePath = cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_IMAGE_PATH));

            productNameEditText.setText(productName);
            productPriceEditText.setText(productPrice);
            productQuantityTextView.setText(productQuantityString);
            productImageView.setImageURI(Uri.parse(productImagePath));

            productQuantity = Integer.parseInt(productQuantityString);

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        productNameEditText.setText("");
        productPriceEditText.setText("");
        productQuantityTextView.setText("");
        //productImageView.setImageURI(null);
    }


    public void openImageSelector() {
        Intent intent;

        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }

        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code READ_REQUEST_CODE.
        // If the request code seen here doesn't match, it's the response to some other intent,
        // and the below code shouldn't run at all.

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.  Pull that uri using "resultData.getData()"

            if (resultData != null) {
                imageUri = resultData.getData();
                Log.i(LOG_TAG, "Uri: " + imageUri.toString());

                //mTextView.setText(mUri.toString());
                productImageView.setImageBitmap(getBitmapFromUri(imageUri));
            }
        } else if (requestCode == SEND_MAIL_REQUEST && resultCode == Activity.RESULT_OK) {

        }
    }

    public Bitmap getBitmapFromUri(Uri uri) {

        if (uri == null || uri.toString().isEmpty())
            return null;

        // Get the dimensions of the View
        int targetW = productImageView.getWidth();
        int targetH = productImageView.getHeight();

        InputStream input = null;
        try {
            input = this.getContentResolver().openInputStream(uri);

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();

            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            input = this.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();
            return bitmap;

        } catch (FileNotFoundException fne) {
            Log.e(LOG_TAG, "Failed to load image.", fne);
            return null;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to load image.", e);
            return null;
        } finally {
            try {
                input.close();
            } catch (IOException ioe) {

            }
        }
    }


}
