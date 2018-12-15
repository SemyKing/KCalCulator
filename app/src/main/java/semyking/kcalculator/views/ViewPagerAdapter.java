package semyking.kcalculator.views;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

//    public Integer getPosition(final Fragment fragment) {
//        if (mFragmentList.size() > 0) {
//            for (int i = 0; i < mFragmentList.size(); i++) {
//                if (mFragmentList.get(i) == fragment)
//                    return i;
//            }
//        }
//        return null;
//    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment) {
        mFragmentList.add(fragment);
    }
}
