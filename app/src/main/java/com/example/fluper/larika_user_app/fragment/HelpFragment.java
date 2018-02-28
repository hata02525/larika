package com.example.fluper.larika_user_app.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.fluper.larika_user_app.R;

/**
 * A simple {@link Fragment} subclass.
 */

public class HelpFragment extends Fragment {

   private View view ;
    ListView listview;

    public HelpFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_help, container, false);

        listview=(ListView)view.findViewById(R.id.help_list);

        HelpListAdapter helpadapter=new HelpListAdapter(getActivity());
        listview.setAdapter(helpadapter);



        return view;
    }

}
