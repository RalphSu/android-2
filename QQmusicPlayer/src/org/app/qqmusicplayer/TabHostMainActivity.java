package org.app.qqmusicplayer;

import java.util.ArrayList;
import java.util.List;

import android.app.LocalActivityManager;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
/**此类分两种功能，一种的按底部的按钮切换功能，另一种就是用ViewPager滑屏*/
public class TabHostMainActivity extends TabActivity implements
		OnCheckedChangeListener {
    TabHost tabhost;

	private RadioGroup maintab;

	// 页卡内容
    private ViewPager mPager;
    // Tab页面列表
    private List<View> listViews;
    // 当前页卡编号
    private LocalActivityManager manager = null;
     
    private MyPagerAdapter mpAdapter = null;
      int index;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabhost_list);
		maintab = (RadioGroup) findViewById(R.id.tab_group);
		maintab.setOnCheckedChangeListener(this);// 设置RadioGroup监听器
		tabhost = getTabHost();
		mPager = (ViewPager) findViewById(R.id.center_body_view_pagers);
        manager = new LocalActivityManager(this, true);
        manager.dispatchCreate(savedInstanceState);
        InitViewPager();
		}
	
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.local:
			index=0;
			//tabhost.setCurrentTabByTag("localmusic");
			listViews.set(0, getView("A", new Intent(TabHostMainActivity.this, MusicListActivity.class)));
			mpAdapter.notifyDataSetChanged();
			mPager.setCurrentItem(0);
			break;
		
		case R.id.online:
			index=1;
			//tabhost.setCurrentTabByTag("netmusic");
			listViews.set(1, getView("B", new Intent(TabHostMainActivity.this, MusicOnlineActivity.class)));
			mpAdapter.notifyDataSetChanged();
			mPager.setCurrentItem(1);
			break;
		}

	}
	/***初始化滑动**/
	private void InitViewPager() {
        Intent intent = null;
        listViews = new ArrayList<View>();
        mpAdapter = new MyPagerAdapter(listViews);
        intent = new Intent(TabHostMainActivity.this, MusicListActivity.class);
        listViews.add(getView("A", intent));
       
        intent = new Intent(TabHostMainActivity.this, MusicOnlineActivity.class);
        listViews.add(getView("B", intent));
        mPager.setOffscreenPageLimit(0);
        mPager.setAdapter(mpAdapter);
        mPager.setCurrentItem(0);
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());
}
	public class MyPagerAdapter extends PagerAdapter{
		public List<View> mListViews;
		 
        public MyPagerAdapter(List<View> mListViews) {
                this.mListViews = mListViews;
        }
		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView(mListViews.get(position));
		}
		@Override
		public void finishUpdate(ViewGroup container) {
		
		}
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			 ((ViewPager) container).addView(mListViews.get(position), 0);
             return mListViews.get(position);
		}
		@Override
		public Parcelable saveState() {
			return null;
		}
		@Override
		public void startUpdate(ViewGroup container) {
		
		}
		
	}
	 public class MyOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
			
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
		}

		@Override
		public void onPageSelected(int pos) {
			  manager.dispatchResume();
			  switch (pos) {
			case 0:
				 index = 0;
				 maintab.check(R.id.local);
			    listViews.set(0, getView("A", new Intent(TabHostMainActivity.this, MusicListActivity.class)));
			    mpAdapter.notifyDataSetChanged();	
				break;

			case 1:
				index=1;
				maintab.check(R.id.online);
				listViews.set(1, getView("B", new Intent(TabHostMainActivity.this, MusicOnlineActivity.class)));
				mpAdapter.notifyDataSetChanged();
				mPager.setCurrentItem(1);
				break;
			}
			
		}
		 
	 }
	 private View getView(String id, Intent intent) {
         return manager.startActivity(id, intent).getDecorView();
 }
}
