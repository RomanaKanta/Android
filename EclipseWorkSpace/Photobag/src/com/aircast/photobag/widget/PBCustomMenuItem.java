package com.aircast.photobag.widget;

/**
 * class bottom menu Item like as iPhone
 * @author lent5
 *
 */
public class PBCustomMenuItem {
    /** item id */
    private int itemId;
    /** item title */
    private CharSequence itemTitleChars;
    /** item  title resouce id */
    private int itemTitleResId;
    /** item type */
    private int menuItemType;

    public static final int MENUITEM_NORMAL = 0;
    public static final int MENUITEM_CANCEL = 1;
    public static final int MENUITEM_DELETE = 2;

    /**
     * PBCustomMenuItem construtor
     * @param itemId id 
     * @param menuItemType type
     * @param itemTitleChars title
     */
    public PBCustomMenuItem(int itemId, int menuItemType,
            CharSequence itemTitleChars) {
        this.itemId = itemId;
        this.itemTitleChars = itemTitleChars;
        this.menuItemType = menuItemType;
    }

    /**
     * PBCustomMenuItem construtor
     * @param itemId
     * @param menuItemType
     * @param itemTitleChars
     */
    public PBCustomMenuItem(int itemId, int menuItemType, int itemTitleChars) {
        this.itemId = itemId;
        this.itemTitleResId = itemTitleChars;
        this.menuItemType = menuItemType;
    }

    /**
     * get item id
     * @return id
     */
    public int getItemId() {
        return this.itemId;
    }

    /**
     * get title menu item
     * @return title
     */
    public CharSequence getMenuItemTitle() {
        return this.itemTitleChars;
    }

    /**
     * get menu item type
     * @return type
     */
    public int getMenuItemType() {
        return this.menuItemType;
    }

    /**
     * get menu item resource id bg
     * @return resource id
     */
    public int getMenuItemTitleResId() {
        return this.itemTitleResId;
    }

}
