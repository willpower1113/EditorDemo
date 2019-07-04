package com.willpower.editor.adapter;

import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.willpower.editor.R;
import com.willpower.editor.entity.Page;

public class PageAdapter extends BaseQuickAdapter<Page, BaseViewHolder> {

    int currentPosition = 0;/*当前选中的Page*/

    public PageAdapter() {
        super(R.layout.item_page);
    }

    @Override
    protected void convert(BaseViewHolder helper, Page item) {
        int count = mData.indexOf(item);
        Glide.with(mContext).load(item.getThumbnail()).error(R.drawable.empty).into((ImageView) helper.getView(R.id.imgBit));
        if (TextUtils.isEmpty(item.getPageName())) {
            item.setPageName("Page " + count);
        }
        helper.setText(R.id.tvPageName, item.getPageName());
        helper.addOnClickListener(R.id.imgBit)
                .addOnClickListener(R.id.tvPageName);
        if (currentPosition == count) {
            helper.getView(R.id.imgBit).setBackgroundColor(Color.parseColor("#FF4081"));
        } else {
            helper.getView(R.id.imgBit).setBackgroundColor(Color.parseColor("#999999"));
        }
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
        notifyDataSetChanged();
    }

    /**
     * 获取当前Data
     */
    public Page getCurrentData(){
        if (mData == null || currentPosition >= mData.size())return new Page();
        Log.e("MainActivity", "取到Page");
        return mData.get(currentPosition);
    }

    public Page getData(int position) {
        return mData.get(position);
    }
}
