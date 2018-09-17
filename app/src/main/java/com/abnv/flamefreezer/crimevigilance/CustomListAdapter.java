package com.abnv.flamefreezer.crimevigilance;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Abhishek Abhinav on 09-07-2017.
 */

public class CustomListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] itemname;
    private final Integer[] imgid,ext;

    public CustomListAdapter(Activity context, String[] itemname, Integer[] imgid, Integer[] ext) {
        super(context, R.layout.mylist, itemname);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.itemname=itemname;
        this.imgid=imgid;
        this.ext= ext;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.mylist, null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        ImageView extraimg = (ImageView) rowView.findViewById(R.id.selectme);

        txtTitle.setText(itemname[position]);
        imageView.setImageResource(imgid[position]);
        extraimg.setImageResource(ext[position]);
        return rowView;

    };
}
