package com.example.sideloader;


import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class AddItem extends Activity {

    private static final String LOG_TAG = AddItem.class.getSimpleName();
    private static final int requestCode = 100;

    private EditText _name;
    private EditText _desc;
    private EditText _unitPrice;
    private EditText _taxName;
    private EditText _taxRate;
    private EditText _quantity;

    private Item _item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate AddItem activity");
        Fabric.with(this, new Crashlytics());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        _name = findViewById(R.id.editName);
        _desc = findViewById(R.id.editDescription);
        _unitPrice = findViewById(R.id.editUnitPrice);
        _taxName = findViewById(R.id.editTaxName);
        _taxRate = findViewById(R.id.editTaxRate);
        _quantity = findViewById(R.id.editQuantity);
    }

    public void addItem(View view) {
        Log.d(LOG_TAG, "addItem onClick");
        if(validateFields()) {
            Intent intent = this.getIntent();
            intent.putExtra(Item.class.getSimpleName(), Item.Converter.toBundle(_item));
            this.setResult(RESULT_OK, intent);
            Log.d(LOG_TAG, _name.getText().toString());
            finish();
        } else {
            Toast toast = Toast.makeText(this, "Required field missing!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private Boolean validateFields() {
        String name = _name.getText().toString();
        String description = _desc.getText().toString();
        String unitPrice = _unitPrice.getText().toString();
        String taxName = _taxName.getText().toString();
        String taxRate = _taxRate.getText().toString();
        String quantity = _quantity.getText().toString();

        if(TextUtils.isEmpty(name) || TextUtils.isEmpty(unitPrice) || TextUtils.isEmpty(quantity)) {
            return false;
        }
        if(TextUtils.isEmpty(taxName) || TextUtils.isEmpty(taxRate)) {
            _item = new Item(name, description, Double.valueOf(quantity), Double.valueOf(unitPrice));
        } else {
            _item = new Item(name, description, Double.valueOf(quantity), taxRate, taxName, Double.valueOf(unitPrice));
        }
        return true;
    }
}
