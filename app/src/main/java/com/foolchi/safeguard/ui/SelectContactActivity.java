package com.foolchi.safeguard.ui;

import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.foolchi.safeguard.R;
import com.foolchi.safeguard.domain.ContactInfo;
import com.foolchi.safeguard.engine.ContactInfoService;
/**
 * Created by foolchi on 6/21/14.
 */
public class SelectContactActivity extends Activity {
    private ListView listView;
    private List<ContactInfo> infos;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_contact);
        infos = new ContactInfoService(this).getContactInfos();

        listView = (ListView)findViewById(R.id.lv_select_contact);
        listView.setAdapter(new SelectContactAdapter());
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String number = infos.get(i).getPhone();
                Intent intent = new Intent();
                intent.putExtra("number", number);
                setResult(1, intent);
                finish();
            }
        });

    }

    private class SelectContactAdapter extends BaseAdapter{
        @Override
        public int getCount(){
            return infos.size();
        }

        @Override
        public Object getItem(int i) {
            return infos.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ContactInfo info = infos.get(i);
            View view1;
            ContactViews views;
            if (view == null){
                views = new ContactViews();
                view1 = View.inflate(SelectContactActivity.this, R.layout.contact_item, null);
                views.tv_name = (TextView)view1.findViewById(R.id.tv_contact_name);
                views.tv_number = (TextView)view1.findViewById(R.id.tv_contact_number);
                views.tv_name.setText("Contact: " + info.getName());
                views.tv_number.setText("Phone: " + info.getPhone());
                view1.setTag(views);
            }
            else {
                view1 = view;
            }
            return view1;
        }
    }

    private class ContactViews{
        TextView tv_name, tv_number;
    }
}
