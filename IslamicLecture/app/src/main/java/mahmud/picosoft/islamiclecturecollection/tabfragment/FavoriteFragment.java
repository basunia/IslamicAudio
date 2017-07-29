package mahmud.picosoft.islamiclecturecollection.tabfragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mahmud.picosoft.islamiclecturecollection.BaseFragment;
import mahmud.picosoft.islamiclecturecollection.R;

/**
 * Created by Mahmud Basunia on 7/23/2017.
 */

public class FavoriteFragment extends BaseFragment {

    public static final String ARG_OBJECT = "object";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.about_me, container, false);

        //Bundle args = getArguments();
        //Toast.makeText(getActivity(), "Tab#" + args.getInt(ARG_OBJECT), Toast.LENGTH_SHORT).show();

        return view;
    }
}
