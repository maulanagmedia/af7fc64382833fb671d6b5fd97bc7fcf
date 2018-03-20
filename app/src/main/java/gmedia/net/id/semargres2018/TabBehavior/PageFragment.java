package gmedia.net.id.semargres2018.TabBehavior;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDelegate;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PageFragment extends Fragment {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public static final String ARG_PAGE = "ARG_PAGE";
    public static final String ARG_LAYOUT = "ARG_LAYOUT";
    private int pageNo;
    private int layoutInt;
    private View layout;

    public static PageFragment newInstance(int pageNo , int layout) {

        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNo);
        args.putInt(ARG_LAYOUT, layout);
        PageFragment fragment = new PageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();

        //initUI();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNo = getArguments().getInt(ARG_PAGE);
        layoutInt = getArguments().getInt(ARG_LAYOUT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(layoutInt, container, false);

        initUI();
        return layout;
    }

    private void initUI() {

        // define your class here
        switch (layoutInt){
            /*case R.layout.fragment_home:
                MenuHomeHandler contentHome = new MenuHomeHandler();
                contentHome.SetView(getContext(), layout);
                break;*/
            default:
                break;
        }
    }
}
