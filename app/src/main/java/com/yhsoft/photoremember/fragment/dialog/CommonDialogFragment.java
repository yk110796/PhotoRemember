package com.yhsoft.photoremember.fragment.dialog;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.yhsoft.photoremember.R;
import com.yhsoft.photoremember.util.DebugLog;

import java.util.ArrayList;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.graphics.Color.TRANSPARENT;
import static android.widget.AdapterView.OnItemClickListener;
import static com.yhsoft.photoremember.R.dimen;
import static com.yhsoft.photoremember.R.dimen.common_line_height;
import static com.yhsoft.photoremember.R.id;
import static com.yhsoft.photoremember.R.id.common_dialog_list;
import static com.yhsoft.photoremember.R.id.dialog_menu_text;
import static com.yhsoft.photoremember.R.layout;
import static com.yhsoft.photoremember.R.layout.layout_common_dialog;
import static com.yhsoft.photoremember.R.layout.layout_common_dialog_item;
import static com.yhsoft.photoremember.util.DebugLog.e;

/**
 * Created by James on 2/28/15.
 */
public class CommonDialogFragment extends DialogFragment {
    private static CommonDialogFragment mInstance = null;
    private ArrayList<String> menu;
    private ListView menuListView;
    private static DialogSelectListener mListener = null;

    public interface DialogSelectListener {
        void onItemSelected(int position);
    }

    public static CommonDialogFragment getInstance(ArrayList<String> list, DialogSelectListener listener) {
        if (mInstance == null) {
            mInstance = new CommonDialogFragment();
        }
        if (mListener != null) {
            mListener = null;
        }
        mListener = listener;
        Bundle arg = new Bundle();
        arg.putStringArrayList("menu", list);
        mInstance.setArguments(arg);

        return mInstance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        menu = getArguments().getStringArrayList("menu");
        View view = inflater.inflate(layout_common_dialog, container, false);
        view.setBackgroundColor(TRANSPARENT);
        menuListView = (ListView) view.findViewById(common_dialog_list);
        if (menu != null) {
            menuListView.setAdapter(new SelectAdapter(getActivity(), layout_common_dialog_item, menu));
        } else {
            e("Invalid state");
        }

        menuListView.setMinimumHeight((int) getActivity().getResources().getDimension(common_line_height));

        menuListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (mListener != null) {
                    mListener.onItemSelected(position);
                }
                getActivity().getSupportFragmentManager().beginTransaction().remove(CommonDialogFragment.this).commit();
            }
        });
        return view;
    }

    private class SelectAdapter extends ArrayAdapter<String> {
        private ArrayList<String> menuList;

        public SelectAdapter(Context context, int resource, ArrayList<String> list) {
            super(context, resource, list);
            menuList = list;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(layout_common_dialog_item, null);
            }
            TextView item = (TextView) view.findViewById(dialog_menu_text);
            item.setText(menuList.get(position));
            return view;
        }
    }
}
