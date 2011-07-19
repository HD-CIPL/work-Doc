package com.andrew.demo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

public class IphoneExpandableListActivity extends Activity implements OnScrollListener {
    /** Called when the activity is first created. */
	
	private static final String TAG = "iphone";
	private static final String PRE = "IphoneExpandableListActivity--";
	
	private ExpandableListView listView;
	private MyExpandableListAdapter mAdapter;
	
	private LinearLayout indicatorGroup;
	private int indicatorGroupId;
	private int indicatorGroupHeight;
	
	private LayoutInflater mInflater;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listView = (ExpandableListView)findViewById(R.id.expandableListView);
        indicatorGroup = (LinearLayout)findViewById(R.id.topGroup);
        
        mAdapter = new MyExpandableListAdapter();
        listView.setAdapter(mAdapter);
        listView.setOnScrollListener(this);
        
        mInflater.inflate(R.layout.list_item, indicatorGroup, true);
    }
    
    /**
     * A simple adapter which maintains an ArrayList of photo resource Ids. 
     * Each photo is displayed as an image. This adapter supports clearing the
     * list of photos and adding a new photo.
     *
     */
    public class MyExpandableListAdapter extends BaseExpandableListAdapter {
        // Sample data set.  children[i] contains the children (String[]) for groups[i].
        private String[] groups = { "People Names", "Dog Names", "Cat Names", "Fish Names" };
        private String[][] children = {
                { "Arnold", "Barry", "Chuck", "David" },
                { "Ace", "Bandit", "Cha-Cha", "Deuce" },
                { "Fluffy", "Snuggles" },
                { "Goldy", "Bubbles" }
        };
        
        private int mHideGroupPos = -1;
        
        public Object getChild(int groupPosition, int childPosition) {
            return children[groupPosition][childPosition];
        }

        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        public int getChildrenCount(int groupPosition) {
            return children[groupPosition].length;
        }

        public TextView getGenericView() {
            // Layout parameters for the ExpandableListView
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 64);

            TextView textView = new TextView(IphoneExpandableListActivity.this);
            textView.setLayoutParams(lp);
            // Center the text vertically
            textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            // Set the text starting position
            textView.setPadding(36, 0, 0, 0);
            return textView;
        }
        
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                View convertView, ViewGroup parent) {
            TextView textView = getGenericView();
            textView.setText(getChild(groupPosition, childPosition).toString());
            return textView;
        }

        public Object getGroup(int groupPosition) {
            return groups[groupPosition];
        }

        public int getGroupCount() {
            return groups.length;
        }

        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                ViewGroup parent) {
        	View v;
        	if(convertView == null){
        		v = mInflater.inflate(R.layout.list_item, null);
        	}else{
        		v = convertView;
        	}
            TextView textView = (TextView)v.findViewById(R.id.textView);
           
            textView.setText(getGroup(groupPosition).toString());
            if(mHideGroupPos == groupPosition){
            	v.setVisibility(View.INVISIBLE);
            }else{
            	v.setVisibility(View.VISIBLE);
            }
            return v;
        }

        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        public boolean hasStableIds() {
            return true;
        }
        public void hideGroup(int groupPos){
        	mHideGroupPos = groupPos;
        }

    }

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		
		ExpandableListView listView = (ExpandableListView)view;
		int npos = view.pointToPosition(1,0);
		if(npos != AdapterView.INVALID_POSITION){
			long pos = listView.getExpandableListPosition(npos);
			int childPos = ExpandableListView.getPackedPositionChild(pos);
			int groupPos = ExpandableListView.getPackedPositionGroup(pos);
			if(childPos == AdapterView.INVALID_POSITION){
				View groupView = listView.getChildAt(npos - listView.getFirstVisiblePosition());
				indicatorGroupHeight = groupView.getHeight();
				if(indicatorGroupHeight == 0){
					return;
				}
			}
			if(groupPos != indicatorGroupId){				
				mAdapter.getGroupView(groupPos, 
						listView.isGroupExpanded(groupPos), indicatorGroup.getChildAt(0), null);				
				indicatorGroupId = groupPos;				
				mAdapter.hideGroup(groupPos);				
				mAdapter.notifyDataSetChanged();
				Log.e(TAG,PRE + "move to new group" + groupPos);
			}
		}
		int imageHeight = indicatorGroupHeight;
		int nEndPos = listView.pointToPosition(1,indicatorGroupHeight-1);
		if(nEndPos != AdapterView.INVALID_POSITION){
			long pos = listView.getExpandableListPosition(nEndPos);
			int groupPos = ExpandableListView.getPackedPositionGroup(pos);
			if(groupPos != indicatorGroupId){
				//group
				View viewNext = listView.getChildAt(nEndPos-listView.getFirstVisiblePosition());
				imageHeight = viewNext.getTop();
				Log.e(TAG,PRE + "START UP MOVE:" + imageHeight);
			}
		}
		// show group
		MarginLayoutParams layoutParams = (MarginLayoutParams)indicatorGroup.getLayoutParams();
		
		layoutParams.topMargin = imageHeight-indicatorGroupHeight;
		indicatorGroup.setLayoutParams(layoutParams);
		int firstVisible = listView.getFirstVisiblePosition();
		if(firstVisible != AdapterView.INVALID_POSITION){			
			int firstGroup = ExpandableListView.getPackedPositionGroup(
					listView.getExpandableListPosition(firstVisible));
			if(firstGroup == indicatorGroupId &&
					listView.isGroupExpanded(indicatorGroupId) == false){
				indicatorGroup.setVisibility(View.INVISIBLE);
				Log.e(TAG,PRE + "indicator group hide");
			}else{
				indicatorGroup.setVisibility(View.VISIBLE);
				Log.e(TAG,PRE + "indicator group show");
			}
		}
		
		
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {		
		
	}
}