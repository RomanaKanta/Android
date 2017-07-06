/* Copyright (c) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aircast.photobag.inappbilling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a block of information about in-app items.
 * An Inventory is returned by such methods as {@link PBIabHelper#queryInventory}.
 */
public class PBInventory {
    Map<String,PBSkuDetails> mSkuMap = new HashMap<String,PBSkuDetails>();
    Map<String,PBPurchase> mPurchaseMap = new HashMap<String,PBPurchase>();

    PBInventory() { }

    /** Returns the listing details for an in-app product. */
    public PBSkuDetails getSkuDetails(String sku) {
        return mSkuMap.get(sku);
    }

    /** Returns purchase information for a given product, or null if there is no purchase. */
    public PBPurchase getPurchase(String sku) {
        return mPurchaseMap.get(sku);
    }

    /** Returns whether or not there exists a purchase of the given product. */
    public boolean hasPurchase(String sku) {
        return mPurchaseMap.containsKey(sku);
    }

    /** Return whether or not details about the given product are available. */
    public boolean hasDetails(String sku) {
        return mSkuMap.containsKey(sku);
    }
    
    private final String[] ids = new String[] {
    		"honey",  
    		"honey1", // Unused Item
    		"honey3", // Unused Item
    		"honey6", // Unused Item
    		"maple1", 
			"maple5", 
			"maple8",  // Newly added for 800 Yen item
			"maple10",
			"maple15",
			"maple30",
			"maple50",
			"maple80",
			"maple100"/*,
			"testmanaged5",
			"testunmanaged5"*/
			
    		//"android.test.purchased",// TEST CODE
		};
    
    // TODO add this when goods changed
    public PBPurchase getAvailadPurchase() {    	
    	for (String id : ids) {
    		if (mPurchaseMap.containsKey(id) && mSkuMap.containsKey(id)) {
    			return mPurchaseMap.get(id);
    		}
    	}
    	return null;
    }

    /**
     * Erase a purchase (locally) from the inventory, given its product ID. This just
     * modifies the Inventory object locally and has no effect on the server! This is
     * useful when you have an existing Inventory object which you know to be up to date,
     * and you have just consumed an item successfully, which means that erasing its
     * purchase data from the Inventory you already have is quicker than querying for
     * a new Inventory.
     */
    public void erasePurchase(String sku) {
        if (mPurchaseMap.containsKey(sku)) mPurchaseMap.remove(sku);
    }

    /** Returns a list of all owned product IDs. */
    List<String> getAllOwnedSkus() {
        return new ArrayList<String>(mPurchaseMap.keySet());
    }

    /** Returns a list of all purchases. */
    List<PBPurchase> getAllPurchases() {
        return new ArrayList<PBPurchase>(mPurchaseMap.values());
    }

    void addSkuDetails(PBSkuDetails d) {
        mSkuMap.put(d.getSku(), d);
    }

    void addPurchase(PBPurchase p) {
        mPurchaseMap.put(p.getSku(), p);
    }
}
