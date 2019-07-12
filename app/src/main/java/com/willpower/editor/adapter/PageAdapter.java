package com.willpower.editor.adapter;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.willpower.editor.R;
import com.willpower.editor.entity.Page;

import java.util.List;

public class PageAdapter extends BaseQuickAdapter<Page, BaseViewHolder> {

    int currentPosition = 0;/*当前选中的Page*/

    public PageAdapter() {
        super(R.layout.item_page);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void convert(BaseViewHolder helper, Page item) {
        int count = mData.indexOf(item);
        Glide.with(mContext).load(item.getThumbnail())
                .into((ImageView) helper.getView(R.id.imgBit));
        if (TextUtils.isEmpty(item.getPageName())) {
            item.setPageName("Page " + count);
        }
        helper.setText(R.id.tvPageName, item.getPageName());
        helper.addOnClickListener(R.id.imgBit)
                .addOnClickListener(R.id.tvPageName);
        if (currentPosition == count) {
            helper.getView(R.id.imgBit).setBackground(mContext.getResources().getDrawable(R.drawable.shape_page_selected));
        } else {
            helper.getView(R.id.imgBit).setBackground(mContext.getResources().getDrawable(R.drawable.shape_page_normal));
        }
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
        notifyDataSetChanged();
    }

    /**
     * 获取当前Data
     */
    public Page getCurrentData() {
        if (mData == null || currentPosition >= mData.size()) return new Page();
        return mData.get(currentPosition);
    }

    public int getCurrent() {
        return currentPosition;
    }

    public Page getData(int position) {
        return mData.get(position);
    }
}
