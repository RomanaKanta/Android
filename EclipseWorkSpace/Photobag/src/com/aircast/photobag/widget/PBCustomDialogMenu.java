package com.aircast.photobag.widget;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aircast.photobag.R;

public class PBCustomDialogMenu {

    private Dialog mDlgMenu;
    private Context mContext;
    private LinearLayout mRootView;
    private TextView mMenuTitle;
    private LayoutInflater mInflater;
    private CustomMenuItemClickListener mMenuItemClickListener = null;
    private static final int TEXT_SIZE = 15;

    /**
     * Constructer for custom menu like iPhone style.
     * 
     * @param context
     *            must using <b>this</b> when using custom Dialog in this case.
     */
    public PBCustomDialogMenu(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(this.mContext);

        mDlgMenu = new Dialog(context, R.style.ThemeDialogCustom);
        mDlgMenu.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View mMenuLayout = this.mInflater.inflate(
                R.layout.pb_custom_dialog_menu, null);
        mDlgMenu.setContentView(mMenuLayout);

        mRootView = (LinearLayout) mMenuLayout
                .findViewById(R.id.custom_menu_root);
        mMenuTitle = (TextView) mMenuLayout
                .findViewById(R.id.custom_menu_title);
    }

    public void showMenu() {
        if (mDlgMenu != null && !mDlgMenu.isShowing()) {
            mDlgMenu.show();
        }
    }

    public void hideMenu() {
        if (mDlgMenu != null)
            if (mDlgMenu.isShowing())
                mDlgMenu.dismiss();
    }

    /**
     * Setup menu title.
     * 
     * @param menuTitle
     *            set to <b>null</b> if we don't want display menu title.
     */
    public void setupMenuTitle(CharSequence menuTitle) {
        if (TextUtils.isEmpty(menuTitle)) {
            this.mMenuTitle.setVisibility(View.GONE);
        } else {
            this.mMenuTitle.setVisibility(View.VISIBLE);
            this.mMenuTitle.setText(menuTitle);
        }
    }

    /**
     * Add menu item to CustomMenu.
     * 
     * @param item
     *            item want to add.
     */
    public void addMenuItem(final PBCustomMenuItem item) {
        Button newBtn = new Button(this.mContext);
        newBtn.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
        newBtn.setText(item.getMenuItemTitle());
        newBtn.setTextSize(TEXT_SIZE);
        newBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mMenuItemClickListener.onCustomMenuItemClick(item.getItemId());
            }
        });
        switch (item.getMenuItemType()) {
        case PBCustomMenuItem.MENUITEM_NORMAL:
            newBtn.setBackgroundResource(R.drawable.pb_menu_item_normal_bg);
            break;
        case PBCustomMenuItem.MENUITEM_CANCEL:
            newBtn.setBackgroundResource(R.drawable.pb_menu_item_cancel_bg);
            break;
        case PBCustomMenuItem.MENUITEM_DELETE:
            newBtn.setBackgroundResource(R.drawable.pb_menu_item_del_bg);
            break;
        default:
            break;
        }
        mRootView.addView(newBtn);
        View viewTmp = new View(this.mContext);
        viewTmp.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 20));
        mRootView.addView(viewTmp);
    }

    public void setOnCustomMenuListener(CustomMenuItemClickListener listen) {
        this.mMenuItemClickListener = listen;
    }

    public interface CustomMenuItemClickListener {
        public boolean onCustomMenuItemClick(int item);

    }

}
