package mahmud.picosoft.islamiclecturecollection;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import mahmud.picosoft.islamiclecturecollection.activity.AboutActivity;
import mahmud.picosoft.islamiclecturecollection.tabfragment.DownloadFragment;
import mahmud.picosoft.islamiclecturecollection.tabfragment.ForegroundActivity;
import mahmud.picosoft.islamiclecturecollection.tabfragment.FavoriteFragment;
import mahmud.picosoft.islamiclecturecollection.adapter.TabPagerAdapter;

/**
 * Created by Mahmud Basunia on 7/23/2017.
 */

public class MainActivity extends AppCompatActivity {

    TabPagerAdapter pagerAdapter;
    ViewPager viewPager;
    TabLayout tabLayout;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.view_tab);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setIcon(R.drawable.icon2);
            //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        pagerAdapter = new TabPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new ForegroundActivity(), "Lecture");
        pagerAdapter.addFragment(new FavoriteFragment(), "Favorite");
        pagerAdapter.addFragment(new DownloadFragment(), "Download");

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(pagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        getMenuInflater().inflate(R.menu.foreground, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {

            case R.id.share:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent
                        .putExtra(
                                Intent.EXTRA_TEXT,
                                "Islamic Authentic Lecture Collections. Download it now http://goo.gl/YNeLhY");
                startActivity(Intent.createChooser(shareIntent, "Share this app.."));
                break;

            case R.id.rate:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=mahmud.picosoft.islamiclecturecollection"));
                startActivity(intent);
                /*Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://goo.gl/YNeLhY"));
                startActivity(browserIntent);*/
                break;
            case R.id.more:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                /*Intent browserIntent2 = new Intent(
                Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/developer?id=PicoSoft"));
                startActivity(browserIntent2);*/
                break;
        }
        return true;
    }
}
