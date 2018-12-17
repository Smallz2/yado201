package app.yado.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import app.yado.R;
import app.yado.Utils.Utils;


public class On_Boarding_Page_Activity extends AppCompatActivity implements View.OnClickListener {
    // Global Variables
    static final String TAG = On_Boarding_Page_Activity.class.getSimpleName();

    Button mSkipBtn, mFinishBtn;
    ImageView zero, one, two;
    ImageView[] indicators;
    int page; // to track page position


    /**
     * The {@link androidx.viewpager.widget.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on__boarding__page_);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // Find Views
        mSkipBtn = findViewById(R.id.intro_btn_skip);
        mFinishBtn = findViewById(R.id.intro_btn_finish);

        zero = findViewById(R.id.intro_indicator_0);
        one = findViewById(R.id.intro_indicator_1);
        two = findViewById(R.id.intro_indicator_2);

        indicators = new ImageView[]{zero, one, two};

        mViewPager.setCurrentItem(page);
        updateIndicators(page);

        //View Pager OnChange Listener
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                page = position;
                updateIndicators(page);

                mSkipBtn.setVisibility(position == 2 ? View.GONE : View.VISIBLE);
                mFinishBtn.setVisibility(position == 2 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // Set onClick
        mFinishBtn.setOnClickListener(this);
        mSkipBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.intro_btn_finish:
                //Update 1st time pre (SignIn)

                Utils.saveSharedSetting(On_Boarding_Page_Activity.this, MainActivity.PREF_USER_FIRST_TIME, "false");
                finish();

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.intro_btn_skip:

                //Update 1st time pre (SignIn)
                Utils.saveSharedSetting(On_Boarding_Page_Activity.this, MainActivity.PREF_USER_FIRST_TIME, "false");
                finish();

                Intent intent2 = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent2);
                //Todo: Make This more friendly
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_on__boarding__page_, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        ImageView img;

        int[] backgrounds = new int[] {
                R.drawable.ic_create_onboarding,
                R.drawable.ic_accept_onboarding,
                R.drawable.ic_relax_onboarding,
        };



        String[] introHeadings = {
          "Post a Task",
          "Choose a Doer",
          "Relax",
        };

        String[] introDescriptions = {
                "Simple and easy task creation",
                "Selects a doer from applicants who requested to complete your task",
                "That's it, you can now relax and let the doer complete your task",
        };

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        private static Fragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_on__boarding__page_, container, false);
            TextView textView = rootView.findViewById(R.id.section_label);
            TextView introDescriptionTextView = rootView.findViewById(R.id.introPageDescription);
            if (getArguments() != null) {
                textView.setText(introHeadings[getArguments().getInt(ARG_SECTION_NUMBER)-1]);
                introDescriptionTextView.setText(introDescriptions[getArguments().getInt(ARG_SECTION_NUMBER)-1]);
            }
            img = rootView.findViewById(R.id.section_img);
            img.setBackgroundResource(backgrounds[getArguments().getInt(ARG_SECTION_NUMBER) - 1]);


            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public androidx.fragment.app.Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }


        @Override
        public int getCount() {
            // Show 4 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Post a Task";
                case 1:
                    return "Choose a Doer";
                case 2:
                    return "Relax";
//                case 3:
//                    return "Grow your profile";
            }
            return null;
        }
    }

    public void updateIndicators(int position) {
        for (int i = 0; i < indicators.length; i++) {
            indicators[i].setBackgroundResource(
                    i == position ? R.drawable.indicator_selected : R.drawable.indicator_unselected
            );
        }
    }
}
