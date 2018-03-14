package in.co.medibox.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import in.co.medibox.R;
import in.co.medibox.model.NavDrawerItem;

@SuppressLint("NewApi")
public class NavDrawerListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<NavDrawerItem> navDrawerItems;
    private Typeface font_edittext;
    private Typeface font_button;

    public NavDrawerListAdapter(Context context, ArrayList<NavDrawerItem> navDrawerItems) {
        this.context = context;
        this.navDrawerItems = navDrawerItems;
    }

    @Override
    public int getCount() {
        return navDrawerItems.size();
    }

    @Override
    public Object getItem(int position) {
        return navDrawerItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.list_item_drawer, null);
        }

        RelativeLayout rl = (RelativeLayout) convertView.findViewById(R.id.relativeLayout_ListItem);
        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.title);

        TextView txtCount = (TextView) convertView.findViewById(R.id.counter);

        imgIcon.setImageResource(navDrawerItems.get(position).getIcon());
        txtTitle.setText(navDrawerItems.get(position).getTitle());
        txtTitle.setTypeface(font_edittext);

        switch (position) {
            case 0:
                //		rl.setBackground(context.getResources().getDrawable(R.drawable.list_selector_personalwall));
                break;
            case 1:
                //		rl.setBackground(context.getResources().getDrawable(R.drawable.list_selector_personalwall));
                break;
            case 2:
                //		rl.setBackground(context.getResources().getDrawable(R.drawable.list_selector_businesswall));
                break;
            case 3:
                //	    rl.setBackground(context.getResources().getDrawable(R.drawable.list_selector_find));
                break;
            case 4:
                //		rl.setBackground(context.getResources().getDrawable(R.drawable.list_selector_classified));
                break;
            case 5:
                //		rl.setBackground(context.getResources().getDrawable(R.drawable.list_selector_personalwall));
                break;
            default:
                break;
        }

        // displaying count
        // check whether it set visible or not
        if (navDrawerItems.get(position).getCounterVisibility()) {
            txtCount.setText(navDrawerItems.get(position).getCount());
        } else {
            // hide the counter view
            txtCount.setVisibility(View.GONE);
        }
        return convertView;
    }
}
