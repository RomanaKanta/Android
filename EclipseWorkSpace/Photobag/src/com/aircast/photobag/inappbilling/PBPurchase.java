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

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents an in-app billing purchase.
 */
public class PBPurchase {
    String mOrderId;
    String mPackageName;
    String mSku;
    long mPurchaseTime;
    int mPurchaseState;
    String mDeveloperPayload;
    String mToken;
    String mOriginalJson;
    String mSignature;
    
    //TODO fix here when goods list changed
    String[] ids = new String[] {
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

    public PBPurchase(String jsonPurchaseInfo, String signature) throws JSONException {
        mOriginalJson = jsonPurchaseInfo;
        JSONObject o = new JSONObject(mOriginalJson);
        mOrderId = o.optString("orderId");
        mPackageName = o.optString("packageName");
        mSku = o.optString("productId");
        mPurchaseTime = o.optLong("purchaseTime");
        mPurchaseState = o.optInt("purchaseState");
        mDeveloperPayload = o.optString("developerPayload");
        mToken = o.optString("token", o.optString("purchaseToken"));
        mSignature = signature;
    }

    public String getOrderId() { return mOrderId; }
    public String getPackageName() { return mPackageName; }
    public String getSku() { return mSku; }
    public long getPurchaseTime() { return mPurchaseTime; }
    public int getPurchaseState() { return mPurchaseState; }
    public String getDeveloperPayload() { return mDeveloperPayload; }
    public String getToken() { return mToken; }
    public String getOriginalJson() { return mOriginalJson; }
    public String getSignature() { return mSignature; }
    
    public boolean isAvailable() {
    	for (String id : ids) {
    		if (id.equals(mSku)) return true;
    	}
    	return false;
    }

    @Override
    public String toString() { return "PurchaseInfo:" + mOriginalJson; }
}
