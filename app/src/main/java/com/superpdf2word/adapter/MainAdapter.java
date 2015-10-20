package com.superpdf2word.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.superpdf2word.MainActivity;
import com.superpdf2word.PWApplication;
import com.superpdf2word.R;
import com.superpdf2word.db.beans.PWFile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

public class MainAdapter extends BaseAdapter
{
	private LayoutInflater layoutInflater;
	private MainActivity context;
	
	private List<PWFile> data = new ArrayList<PWFile>();
	
	public MainAdapter(MainActivity context,List<PWFile> d) {
		this.context = context;
		this.layoutInflater = LayoutInflater.from(context);
		this.data = d;
	}
	
	public void addOneTask(PWFile object) {
		List<PWFile> newdata = new ArrayList<PWFile>();
		newdata.add(object);
		for (int i=0;i<data.size();i++) {
			newdata.add(data.get(i));
		}
		this.data = null;
		this.data = newdata;
		this.notifyDataSetChanged();
	}
	
	public void updataTaskStat(PWFile object) {
		for (int i=0;i<data.size();i++) {
			PWFile pwfile = data.get(i);
			if (object.equals(pwfile)){
				data.set(i, object);
				break;
			}
		}
		this.notifyDataSetChanged();
	}
	

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null ) {
			holder = new ViewHolder();
			convertView = this.layoutInflater.inflate(R.layout.listitem, null);
			holder.image = (ImageView)convertView.findViewById(R.id.listitem_icon);
			holder.titleview = (TextView)convertView.findViewById(R.id.listitem_filetitle);
			holder.statview = (TextView)convertView.findViewById(R.id.listitem_stat);
			holder.pathview = (TextView)convertView.findViewById(R.id.listitem_wordpath);	
			holder.itemView = convertView.findViewById(R.id.listitem_layoutview);
			
			convertView.setTag(holder);
		} else
			holder = (ViewHolder)convertView.getTag();
		
		int stat = this.data.get(position).getStat();
		holder.titleview.setText(this.data.get(position).getFilename());
		holder.statview.setText(context.getString(R.string.stat, setStatstr(stat)));
		holder.statview.setTextColor(setStatColor(stat));
		if (stat == 4) {
			holder.image.setImageResource(R.drawable.icon_word);
			holder.pathview.setVisibility(View.VISIBLE);
			holder.pathview.setText(context.getString(R.string.wordpath,this.data.get(position).getWordpath()));
			holder.itemView.setOnClickListener(new OnItemClickListener(this.data.get(position).getWordpath(),true));
		}
		else{
			holder.image.setImageResource(R.drawable.icon_pdf);
			holder.pathview.setVisibility(View.GONE);
			holder.itemView.setOnClickListener(new OnItemClickListener(this.data.get(position).getSourcepath(),false));
		}
		
		return convertView;		
	}
	
	public String setStatstr(int stat) {
		String str = null;
		switch(stat) {
		case 1:
			str="正在转换";
			break;
		case 2:
			str="转换结束，准备下载";
			break;
		case 3:
			str="转换成功，正在下载";
			break;
		case 4:
			str="转换成功，下载完成";
			break;
		case 5:
			str="下载失败";
			break;
		case 6:
			str="转换失败";
			break;
		}
		return str;
	}
	
	public int setStatColor(int stat) {
		int color = 0;
		switch(stat) {
		case 1:
			color = context.getResources().getColor(R.color.grey_light);
			break;
		case 2:
			color = context.getResources().getColor(R.color.grey_light);
			break;
		case 3:
			color = context.getResources().getColor(R.color.SpringGreen);
			break;
		case 4:
			color = context.getResources().getColor(R.color.ThinkBlue);
			break;
		case 5:
			color = context.getResources().getColor(R.color.OrangeRed);
			break;
		case 6:
			color = context.getResources().getColor(R.color.OrangeRed);
			break;
		}
		return color;
	}
	
	public void openWord(String wordpath) {
		Intent intent = new Intent("android.intent.action.VIEW");     
        intent.addCategory("android.intent.category.DEFAULT");     
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);     
        Uri uri = Uri.fromFile(new File(wordpath));     
        intent.setDataAndType(uri, "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        context.startActivity(intent);
	}
	
	public static class ViewHolder
	{
		public ImageView image;
		public TextView titleview;
		public TextView statview;
		public TextView pathview;
		public View itemView;
	}
	
	public class OnItemClickListener implements OnClickListener
	{
		String wordpath;
		boolean isword;
		Dialog alterDailog;
		public OnItemClickListener(String path,boolean isword) {
			this.wordpath = path;
			this.isword = isword;
		}
		
		@Override
		public void onClick(View v) {
			if (isword) {
				if (!PWApplication.getIsalterword()) {
					openWord(wordpath);
					return;
				}
				
				alterDailog = null;
		    	Builder builder = new AlertDialog.Builder(context,AlertDialog.THEME_HOLO_LIGHT);	
				View view = LayoutInflater.from(context).inflate(R.layout.dialog_openword, null);
				CheckBox checkbox = (CheckBox) view.findViewById(R.id.dialog_word_checkbox);
				View button = view.findViewById(R.id.dialog_word_button);
				builder.setView(view);
				button.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						alterDailog.dismiss();
						alterDailog = null;
						openWord(wordpath);
					}
					
				});
				checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener(){
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						PWApplication.setIsalterword(!isChecked);
					}
					
				});
				alterDailog = builder.create();
				alterDailog.setCanceledOnTouchOutside(true);
				alterDailog.show();

			} else {
				Intent intent = new Intent("android.intent.action.VIEW");     
		        intent.addCategory("android.intent.category.DEFAULT");     
		        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);     
		        Uri uri = Uri.fromFile(new File(wordpath));     
		        intent.setDataAndType(uri, "application/pdf");
		        context.startActivity(intent);
			}
		}
		
	}
}