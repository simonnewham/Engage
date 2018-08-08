package com.engage.simonnewham.engageapp.adapters;

import android.widget.ArrayAdapter;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.engage.simonnewham.engageapp.R;
import com.engage.simonnewham.engageapp.models.NewsListItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by simonnewham on 2018/07/27.
 */

public class NewsAdapter extends ArrayAdapter<NewsListItem>{

    private Context mContext;
    private List<NewsListItem> newsList = new ArrayList<>();

    public NewsAdapter(@NonNull Context context, ArrayList<NewsListItem> list) {
        super(context, 0 , list);
        mContext = context;
        newsList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.content_list_item,parent,false);

        NewsListItem currentItem = newsList.get(position);

        TextView name = (TextView) listItem.findViewById(R.id.textView_name);
        name.setText(currentItem.getTitle());

        TextView release = (TextView) listItem.findViewById(R.id.News_date);
        release.setText(currentItem.getDate());

//        TextView description = (TextView) listItem.findViewById(R.id.News_description);
//        description.setText((currentItem.getDecription()));

        return listItem;
    }
}