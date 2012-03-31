/*
 * Copyright (C) 2010 The MobileSecurePay Project
 * All right reserved.
 * author: shiqun.shi@alipay.com
 */

package com.tianxia.lib.baseworld.alipay;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tianxia.lib.baseworld.R;

public class ProductListAdapter extends BaseAdapter
{

	private ArrayList<Products.ProductDetail> m_productList = null;
	private Context context;

	private class ProductItemView
	{
		TextView subject;
		TextView body;
		TextView price;
	}

	public ProductListAdapter(Context c, ArrayList<Products.ProductDetail> list) 
	{
		m_productList	= list;
		context 		= c;
	}

	public int getCount()
	{
		return m_productList.size();
	}

	public Object getItem(int arg0)
	{
		return null;
	}

	public long getItemId(int arg0)
	{
		return arg0;
	}

	public View getView(int arg0, View arg1, ViewGroup arg2)
	{
		final ProductItemView itemView;
		if (arg1 == null)
		{
			itemView 			= new ProductItemView();
			arg1 				= LayoutInflater.from(context).inflate(R.layout.product_item, null);
			itemView.subject 	= (TextView) arg1.findViewById(R.id.subject);
			itemView.body 		= (TextView) arg1.findViewById(R.id.body);
			itemView.price 		= (TextView) arg1.findViewById(R.id.price);
			
			arg1.setTag(itemView);
		}
		else
		{
			itemView = (ProductItemView) arg1.getTag();
		}

		itemView.subject.setText(this.m_productList.get(arg0).subject);
		itemView.body.setText(this.m_productList.get(arg0).body);
		itemView.price.setText(this.m_productList.get(arg0).price);
		
		return arg1;
	}
}
