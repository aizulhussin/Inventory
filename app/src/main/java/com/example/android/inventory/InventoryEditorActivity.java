package com.example.android.inventory;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventory.data.InventoryContract.InventoryEntry;

public class InventoryEditorActivity extends AppCompatActivity {


    private EditText productNameEditText;
    private EditText productPriceEditText;
    private TextView productQuantityTextView;

    private int productQuantity = 0;

    private Uri currentUri = null;


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


    private void save(){

        String productNameString = productNameEditText.getText().toString();
        String priceString = productPriceEditText.getText().toString();
        String quantityString = productQuantityTextView.getText().toString();

        //TODO add validations

        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT_NAME,productNameString);
        values.put(InventoryEntry.COLUMN_PRICE,priceString);
        values.put(InventoryEntry.COLUMN_QUANTITY,quantityString);
        values.put(InventoryEntry.COLUMN_IMAGE_PATH,"");

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

}
