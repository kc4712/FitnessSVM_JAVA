package kr.co.greencomm.ibody24.coach.utils;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;

/**
 * Created by young on 2015-08-25.
 */
public class TabListener<T extends Fragment>
    implements ActionBar.TabListener
{
    private final Activity m_activity;
    private final String m_tag;
    private final Class<T> m_class;
    private Fragment m_fragment;

    public TabListener(Activity activity, String tag, Class<T> cls) {
        this.m_activity = activity;
        this.m_tag = tag;
        this.m_class = cls;

        m_fragment = m_activity.getFragmentManager().findFragmentByTag(m_tag);
        if (m_fragment != null && m_fragment.isDetached() == false) {
            FragmentTransaction ft = m_activity.getFragmentManager().beginTransaction();
            ft.detach(m_fragment);
            ft.commit();
        }
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        if (m_fragment == null) {
            m_fragment = Fragment.instantiate(m_activity, m_class.getName(), null);
            ft.add(android.R.id.content, m_fragment, m_tag);
        }
        else {
            ft.attach(m_fragment);
        }
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        if (m_fragment != null) {
            ft.detach(m_fragment);
        }
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
    }
}
