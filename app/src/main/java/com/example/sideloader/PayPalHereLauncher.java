package com.example.sideloader;

import android.content.Intent;
import android.app.Activity;
import android.os.Bundle;
//copied from git
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.text.MessageFormat;

import io.fabric.sdk.android.Fabric;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;


//called when activity is created
public class PayPalHereLauncher extends Activity {

    private static final String LOG_TAG = PayPalHereLauncher.class.getSimpleName();
    public static final String MARKET_URL = "market://details?id=com.paypal.here";
    private static final String PPH_URL_STRING = "paypalhere://takePayment/v2?accepted={0}&returnUrl={1}&invoice={2}&step=choosePayment&payerPhone={3}";
    private static final String RETURN_URL = "sideloader://handleresponse/?Type={Type}&InvoiceId={InvoiceId}&Tip={Tip}&Email={Email}&TxId={TxId}&GrandTotal={GrandTotal}";
    private static final String ACCEPTED_PAYMENT_TYPES = "cash,card,paypal";
    private static final String RESPONSE_HOST = "handleresponse";

    private Invoice _invoice;

    private EditText editInvoiceNum;
    private EditText editPaymentTerms;
    private EditText editDiscount;
    private EditText editCurrencyCode;
    private EditText editMerchantEmail;
    private EditText editPayerEmail;
    private EditText editPayerPhone;

    private TextView _itemCount;

    private static final int ADD_ITEM_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_pay_pal_here_launcher);

        editInvoiceNum = findViewById(R.id.editInvoiceNum);
        editPaymentTerms = findViewById(R.id.editPaymentTerms);
        editDiscount = findViewById(R.id.editDiscount);
        editCurrencyCode = findViewById(R.id.editCurrencyCode);
        editMerchantEmail = findViewById(R.id.editMerchantEmail);
        editPayerEmail = findViewById(R.id.editPayerEmail);
        editPayerPhone = findViewById(R.id.editPayerPhone);

        _itemCount = (TextView) findViewById(R.id.itemCount);

        //figure out what this does
       // initializeDefaultDataList();
        System.out.println(savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleOpenURL(intent);
        System.out.println(intent);
    }

    private void initializeDefaultDataList() {
        _invoice = new Invoice();
        _invoice.addItemWithName("Shirt", "I love shirts", "8.50", 9.99, "Sales Tax", 1.0);
        _itemCount.setText(String.valueOf(_invoice.getItemsCount()));
    }

    private void handleOpenURL(Intent intent) {
        Uri data = intent.getData();
        if(data == null) {
            return;
        }

        String host = data.getHost();
        if(RESPONSE_HOST.equals(host)) {
            String response = data.toString();
            AlertDialog.Builder responseDialog = new AlertDialog.Builder(this);
            responseDialog.setMessage(response).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            responseDialog.show();
        }
    }

    public void addItem(View view) {
        Intent intent = new Intent(this, AddItem.class);
        startActivityForResult(intent, ADD_ITEM_REQUEST_CODE);
    }

    //processes info from addItem ^
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ADD_ITEM_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Bundle bundle = data.getBundleExtra(Item.class.getSimpleName());
            Item item = Item.Converter.fromBundle(bundle);
            if(_invoice != null) {
                _invoice.addItem(item);
            } else {
                Toast toast = Toast.makeText(this, "Invoice is empty!", Toast.LENGTH_SHORT);
                toast.show();
                Toast toast2 = Toast.makeText(this, "starting initializedDefaultDataList()", Toast.LENGTH_SHORT);
                toast2.show();

                initializeDefaultDataList();
            }
            _itemCount.setText(String.valueOf(_invoice));//add .getItemsCount()
        }
    }


    public void launchPayPalHere(View view) {
        _invoice.setPayerEmail(editPayerEmail.getText().toString());
        _invoice.setMerchantEmail(editMerchantEmail.getText().toString());
        _invoice.setCurrencyCode(editCurrencyCode.getText().toString());
        _invoice.setDiscountPercent(editDiscount.getText().toString());
        _invoice.setNumber(editInvoiceNum.getText().toString());
        _invoice.setPaymentTerms(editPaymentTerms.getText().toString());
        String phone = editPayerPhone.getText().toString();

        String urlEncodedReturnUrl = Uri.encode(RETURN_URL);
        String urlEncodedPaymentTypes = Uri.encode(ACCEPTED_PAYMENT_TYPES);

        String urlEncodedInvoice = Invoice.Converter.serializeToUrlEncodedJsonString(_invoice);
        String pphUrl = MessageFormat.format(PPH_URL_STRING, urlEncodedPaymentTypes, urlEncodedReturnUrl, urlEncodedInvoice, phone);

        Intent pphIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(pphUrl));
        PackageManager packageManager = getPackageManager();
        ResolveInfo resolveInfo = packageManager.resolveActivity(pphIntent, PackageManager.MATCH_DEFAULT_ONLY);
        if(resolveInfo == null) {
            AlertDialog.Builder pphNotFoundDialog = new AlertDialog.Builder(this);
            pphNotFoundDialog.setTitle("PayPal Here app not found!").setMessage("Install from Google Play");
            pphNotFoundDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(MARKET_URL)));
                    dialogInterface.dismiss();
                }
            });
            pphNotFoundDialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            pphNotFoundDialog.show();
        } else {
            startActivity(pphIntent);
        }
    }
}
