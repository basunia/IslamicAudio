package mahmud.picosoft.islamiclecturecollection.tabfragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import mahmud.picosoft.islamiclecturecollection.BaseFragment;
import mahmud.picosoft.islamiclecturecollection.R;

/**
 * Created by Mahmud Basunia on 7/23/2017.
 */

public class DownloadFragment extends BaseFragment {

    private List<Fragment> mFragments = new ArrayList<>();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getChildFragmentManager().registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
                super.onFragmentCreated(fm, f, savedInstanceState);
                mFragments.add(f);
            }

            @Override
            public void onFragmentDestroyed(FragmentManager fm, Fragment f) {
                super.onFragmentDestroyed(fm, f);
                mFragments.remove(f);
            }
        }, false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            Iterator<Fragment> iterator = mFragments.iterator();
            while (iterator.hasNext()) {
                Fragment fragment = iterator.next();
                iterator.remove();
                getChildFragmentManager().beginTransaction().remove(fragment).commitNow();
            }
            final View view = inflater.inflate(R.layout.fragment_download, container, false);
            return view;
        } catch (Exception e) {
            Toast.makeText(getActivity(), "View not found", Toast.LENGTH_SHORT).show();
        }

        return null;
    }
}
