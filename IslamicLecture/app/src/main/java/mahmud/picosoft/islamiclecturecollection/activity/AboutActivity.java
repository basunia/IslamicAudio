package mahmud.picosoft.islamiclecturecollection.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import mahmud.picosoft.islamiclecturecollection.R;

/**
 * Created by Mahmud Basunia on 7/21/2017.
 */

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activivty_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            //getSupportActionBar().setIcon(R.drawable.icon2);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "mahmud.basunia@gmail.com" });
                intent.putExtra(Intent.EXTRA_SUBJECT, "For Developing Android App");
                intent.putExtra(Intent.EXTRA_TEXT, "Please specify what can I do for you?");
                startActivity(Intent.createChooser(intent, ""));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(AboutActivity.this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
