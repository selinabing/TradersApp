package teamcool.tradego.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.messenger.MessengerUtils;
import com.facebook.messenger.ShareToMessengerParams;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import teamcool.tradego.Clients.ParseClient;
import teamcool.tradego.Fragments.AlertDeleteFragment;
import teamcool.tradego.Models.Item;
import teamcool.tradego.R;

public class DetailsActivity extends AppCompatActivity {

    private static final String EXTRA_PROTOCOL_VERSION = "com.facebook.orca.extra.PROTOCOL_VERSION";
    private static final String EXTRA_APP_ID = "com.facebook.orca.extra.APPLICATION_ID";
    private static final int PROTOCOL_VERSION = 20150314;
    private static final String YOUR_APP_ID = "528996547286241";
    private static final int SHARE_TO_MESSENGER_REQUEST_CODE = 1;
    String ownerId = "";

    @BindView(R.id.btnMessenger) View btnMessenger;
    Item item;
    String itemId;
    /*
    @BindView(R.id.tvItemDescription) TextView tvItemDescription;
    @BindView(R.id.tvItemName) TextView tvItemName;
    @BindView(R.id.tvItemStatus) TextView tvItemStatus;
    @BindView(R.id.etPrice) EditText etPrice;
    @BindView(R.id.tvItemNegotiable) EditText tvItemNegotiable;*/

    TextView tvItemDescription;
    TextView tvItemName;
    TextView tvItemStatus;
    TextView tvItemPrice;
    TextView tvItemNegotiable;
    TextView tvItemCategory;
    ImageView ivItem1;
    ImageView ivItem2;

    private static final int REQUEST_CODE_SHARE_TO_MESSENGER = 15251;
    ParseClient parseClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowHomeEnabled(true);

        itemId = getIntent().getStringExtra("item_id");
        
        btnMessenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri messenger = Uri.parse((new StringBuilder("http://m.me/")).append(Uri.encode("kinjal.shah.7505")).toString());
                Intent i = new Intent(Intent.ACTION_VIEW, messenger);
                startActivity(i);

            }
        });

        populateItemDetails();

    }

    private void populateItemDetails() {

        parseClient = new ParseClient();
        item = parseClient.queryItemBasedonObjectID(itemId);

        tvItemDescription = (TextView) findViewById(R.id.tvItemDescription);
        tvItemName = (TextView) findViewById(R.id.tvItemName);
        tvItemStatus = (TextView) findViewById(R.id.tvItemStatus);
        tvItemPrice = (TextView) findViewById(R.id.tvItemPrice);
        tvItemNegotiable = (TextView) findViewById(R.id.tvItemNegotiable);
        tvItemCategory = (TextView) findViewById(R.id.tvItemCategory);
        ivItem1 = (ImageView) findViewById(R.id.ivItem1);
        ivItem2 = (ImageView) findViewById(R.id.ivItem2);



        tvItemDescription.setText("Item description: " + item.getDescription());
        tvItemName.setText(item.getItem_name());
        tvItemStatus.setText("Status: " + item.getStatus());
        String price = String.valueOf(item.getPrice());
        tvItemPrice.setText("Price: " + price);
        tvItemNegotiable.setText("Negotiable: " + item.getNegotiable());
        tvItemCategory.setText("Category: " + item.getCategory());
        ParseUser owner = item.getParseUser("owner");

        ownerId = "546811442193809"; //hardcoded luis' fb-id for debugging purpose

        ivItem1.setImageBitmap(decodeBase64(item.getImage1()));
        ivItem2.setImageBitmap(decodeBase64(item.getImage2()));

    }

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        Item item_2 = parseClient.queryItemBasedonObjectID(itemId);
        List<Item> items = parseClient.queryItemsOnUser(ParseUser.getCurrentUser());

        for(Item item: items) {
            String obj = item.getObjectId();
            if (obj.equals(itemId)) {
                getMenuInflater().inflate(R.menu.menu_detail_activity, menu);
                return true;
            }
        }
        return true;
    }


    public void onEditItem(MenuItem item) {

        Intent i = new Intent(DetailsActivity.this, AddItemActivity.class);
        i.putExtra("item_id", itemId);
        startActivity(i);
    }

    public void onDeleteItem(MenuItem item) {
        showAlertDialog();
    }

    public void showAlertDialog() {
        AlertDeleteFragment frag = AlertDeleteFragment.newInstance(itemId);
        frag.show(getSupportFragmentManager(), "fragment_alert");
    }

    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
        }
        return super.onOptionsItemSelected(item);
    }
}
