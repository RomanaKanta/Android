package com.aircast.photobag.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.aircast.photobag.adapter.PBHistoryManager;
import com.aircast.photobag.api.PBAPIContant;
import com.aircast.photobag.api.PBAPIHelper;
import com.aircast.photobag.api.ResponseHandle;
import com.aircast.photobag.api.ResponseHandle.Response;
import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.database.PBDatabaseDefinition;
import com.aircast.photobag.database.PBDatabaseManager;
import com.aircast.photobag.model.PBHistoryEntryModel;
import com.aircast.photobag.model.PBHistoryPhotoModel;
import com.aircast.photobag.utils.PBBitmapUtils;
import com.aircast.photobag.utils.PBGeneralUtils;
import com.aircast.photobag.utils.PBPreferenceUtils;



public class UpdateHistoryTask extends AsyncTask<Void, Void, Void>  {

	private PBHistoryManager mHistory;
//	private Handler mHandlerUpdateData;
	private Context cxt;
	private PBDatabaseManager mDatabaseManager;
	private String mToken;
	
	// private ProgressDialog mProgressDialog;

	private String mDeleteName;
	
	
	private Response response ;

	public UpdateHistoryTask() {
		mHistory = PBHistoryManager.getInstance();
		//mHandlerUpdateData = handler;
		cxt = PBApplication.getBaseApplicationContext();

		mDatabaseManager = PBDatabaseManager.getInstance(cxt);
		mToken = PBPreferenceUtils.getStringPref(cxt, PBConstant.PREF_NAME,
				PBConstant.PREF_NAME_TOKEN, "");
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		//System.out.println("Atik call PBTaskGetHistoryInfo ");
		mDeleteName = "";
	}

	@Override
	protected Void doInBackground(Void... arg0) {

		String photoUrl = null;
		int saveMark = 1;
		String thumb = null;
		int addibility = 0;
		long updatedAtForJson = 0;
		int numberDownlaod = 1;
		int localSave = 0;
		boolean mIsCanSaveDataToDB = true;
		String historyId = null;
		long createDate = 0;
		long chargeDate = 0;
		String mPhotoListPassword = "";
		String adLink = "";
		String mDefaultPhotoListThumbUrl = null;
		int numOfPhoto = 0;
		int isPublic = 0;
		int accepted = 0;
		

		String token = PBPreferenceUtils.getStringPref(cxt,
				PBConstant.PREF_NAME, PBConstant.PREF_NAME_TOKEN, "");


		response = PBAPIHelper.getHistory(token);
		if (response.errorCode != ResponseHandle.CODE_200_OK) {
			return null;
		}

		try {

			JSONObject result = new JSONObject(response.decription);

			if (result != null) {
				if (cxt == null) {
					return null;
				}

				if (result.has("upload")) {
					JSONArray upload = result.getJSONArray("upload");
					PBHistoryEntryModel entryForInsertIntoSentItem = null;
					if (upload != null) {
						boolean existAikotobaInHistorySent = false;
						//boolean isPasswordExistsInHistorySentItems = false;
						for (int i = 0; i < upload.length(); i++) {
							PBHistoryEntryModel model = new PBHistoryEntryModel();

							JSONObject objInfo = upload.getJSONObject(i);
							try {
								existAikotobaInHistorySent = mDatabaseManager
										.isPasswordExistsInHistorySent(objInfo
												.getString("id"));
//								isPasswordExistsInHistorySentItems = mDatabaseManager
//										.isPasswordExistsInHistorySentItems(objInfo
//												.getString("password"));
								
								Log.d("history update ",objInfo
										.getString("id")+ "  "+existAikotobaInHistorySent);

								if (existAikotobaInHistorySent
										) {
									model.setEntryCollectionId(objInfo
											.getString("id"));
									model.setEntryPassword(objInfo
											.getString("password"));
									model.setEntryCreateDate(objInfo
											.getLong("created_at"));
									model.setEntryNumofDownload(Integer.parseInt(objInfo
											.getString("downloaded_users_count")));
									model.setEntryNumOfPhoto(Integer
											.parseInt(objInfo
													.getString("photos_count")));
									model.setEntryChargeDate(objInfo
											.getLong("charges_at"));
									model.setEntryAddibility(Integer
											.parseInt(objInfo
													.getString("can_add")));
									model.setEntryMapleUsed(Integer.parseInt(objInfo
											.getString("used_maple_count")));
									model.setEntryHoneyUsed(Integer.parseInt(objInfo
											.getString("used_honey_count")));
									model.setEntrySaveMark(Integer
											.parseInt(objInfo
													.getString("can_save")));
									model.setEntrySaveDays(Integer.parseInt(objInfo
											.getString("client_keep_days")));
									model.setIsPublic(Integer.parseInt(objInfo
											.getString("is_public")));
									model.setAccepted(Integer.parseInt(objInfo
											.getString("accepted")));
									model.setmMessageCount(Integer.parseInt(objInfo
											.getString("message_count")));
									
									
								} else {

									if (objInfo != null && objInfo.has("id")) {
										historyId = objInfo.getString("id");
									}

									if (objInfo.has("can_save")) {
										saveMark = objInfo.getInt("can_save");
									}

									if (objInfo.has("created_at")) {
										createDate = objInfo
												.getLong("created_at");
									}
									if (objInfo.has("charges_at")) {
										chargeDate = objInfo
												.getLong("charges_at");
									}
									if (objInfo.has("downloaded_users_count")) {
										numberDownlaod = objInfo
												.getInt("downloaded_users_count");
									}
									if (objInfo.has("can_add")) {
										addibility = objInfo.getInt("can_add");
									}
									if (objInfo.has("updated_at")) {
										updatedAtForJson = objInfo
												.getLong("updated_at");
									}
									if (objInfo.has("can_save")) {
										saveMark = objInfo.getInt("can_save");
									}
									if (objInfo.has("client_keep_days")) {
										localSave = objInfo
												.getInt("client_keep_days");
									}
									if (objInfo.has("last_photo_link")) {
										adLink = objInfo
												.getString("last_photo_link");
									}

									if (objInfo.has("password")) {
										mPhotoListPassword = objInfo
												.getString("password");
									}

									if (objInfo.has("is_public")) { // added
																	// by
																	// Atik
																	// for
																	// koukaibukuro
										isPublic = objInfo.getInt("is_public");
									}

									if (objInfo.has("accepted")) { // added
																	// by
																	// Atik
																	// for
																	// koukaibukuro
										isPublic = objInfo.getInt("accepted");
									}

									Options mOptions = new Options();

									Response res = PBAPIHelper
											.doDownloadUploadedPhotoListJsonOfPassword(
													token,
													objInfo.getString("password"),
													false);

									JSONObject jsonRoot;
									boolean saveImgThumbResult = false;
									boolean saveRealImgResult = false;
									FileInputStream fis = null;

									try {
										jsonRoot = new JSONObject(
												res.decription);
										JSONArray jsonPhotosArray;
										jsonPhotosArray = jsonRoot
												.getJSONArray("photos");
										mDefaultPhotoListThumbUrl = jsonPhotosArray
												.getJSONObject(0).getString(
														"thumb");

										numOfPhoto = jsonPhotosArray.length();

										for (int j = 0; j < jsonPhotosArray
												.length(); j++) {

											JSONObject jsonObject = jsonPhotosArray
													.getJSONObject(j);
											String imageURL = jsonObject
													.optString("url")
													.toString();
											String thumbURL = jsonObject
													.optString("thumb")
													.toString();

											int mediaType = 0;
											if (!TextUtils.isEmpty(imageURL)
													&& imageURL
															.contains("video")) {
												mediaType = PBDatabaseDefinition.MEDIA_VIDEO;
											} else { // default in this case
														// is a photo.
												mediaType = PBDatabaseDefinition.MEDIA_PHOTO;
											}

											mDefaultPhotoListThumbUrl = saveMark != 0 ? mDefaultPhotoListThumbUrl
													: mDefaultPhotoListThumbUrl
															+ "?can_save=0";

											if (jsonObject != null
													&& jsonObject.has("thumb")) {
												thumb = jsonObject
														.getString("thumb");
												try {
													saveImgThumbResult = PBAPIHelper
															.savePhoto(
																	token,
																	thumb,
																	objInfo.getString("password"),
																	(saveMark != 0),
																	null);
												} catch (Exception e) {
													Log.w("mapp",
															"[download] getting thumb from server error: "
																	+ e.getMessage());
												}
												Log.i("mapp",
														"save image thumb OK --> "
																+ saveImgThumbResult);
											}

											// Atik coding start from here
											// 2014-08-06
											// parse "url" for real image
											if (jsonObject.has("url")) {
												photoUrl = jsonObject
														.getString("url");
												// 20120521 add support
												// download format with
												// video file
												// <S>
												if (photoUrl.contains("video")) {
													photoUrl = photoUrl
															+ PBConstant.VIDEO_FORMAT_3GP;
												}
												try {
													saveRealImgResult = PBAPIHelper
															.savePhoto(
																	token,
																	photoUrl,
																	objInfo.getString("password"),
																	(jsonRoot
																			.getInt("can_save") != 0),
																	null);

												} catch (IOException e) {
													// Atik TODO check
													// SDCARD full or not
												}

											}

											if (!TextUtils.isEmpty(photoUrl)
													&& photoUrl
															.contains("video")) {
												mediaType = PBDatabaseDefinition.MEDIA_VIDEO;
											} else { // default in this case
														// is a photo.
												mediaType = PBDatabaseDefinition.MEDIA_PHOTO;
											}

											// 20120809 added by NhatVT,
											// support saving photo or video
											// <E>

											// 20120215 add by NhatVT <S>
											// if we cannot get thumb from
											// server -> crop real image to
											// 150x150
											// 20120419 moved realImgPath
											// from if clause by TinhNH1 <S>
											String realImgPath = PBGeneralUtils
													.getPathFromCacheFolder(saveMark != 0 ? photoUrl
															: photoUrl
																	+ "?can_save=0");

											// 20120419 by TinhNH1 <E>
											if (!saveImgThumbResult
													&& mediaType == PBDatabaseDefinition.MEDIA_PHOTO) {
												Log.i("mapp",
														">>> process crop real image to create thumb!");
												String thumbImgPath = PBGeneralUtils
														.getPathFromCacheFolder(thumb);
												if (PBBitmapUtils
														.isPhotoValid(realImgPath)) {
													mOptions.inSampleSize = PBBitmapUtils
															.sampleSizeNeeded(
																	realImgPath,
																	PBConstant.MAXIMUM_IMAGE_SIZE_UPLOAD);
													Bitmap bmp = PBBitmapUtils
															.centerCropImage(
																	BitmapFactory
																			.decodeFile(
																					realImgPath,
																					mOptions),
																	PBConstant.PHOTO_THUMB_WIDTH,
																	PBConstant.PHOTO_THUMB_HEIGHT);
													try {
														FileOutputStream fos = null;
														if (android.os.Environment
																.getExternalStorageState()
																.equals(android.os.Environment.MEDIA_MOUNTED)) {
															fos = new FileOutputStream(
																	new File(
																			thumbImgPath));
														} else {

														}
														bmp.compress(
																PBConstant.COMPRESS_FORMAT,
																PBConstant.DECODE_COMPRESS_PRECENT,
																fos);
														// release resources
														// after saving
														// photo DONE!
														if (fos != null) {
															fos.flush();
															fos.close();
															fos = null;
														}
														if (bmp != null) {
															bmp.recycle();
															bmp = null;
														}
													} catch (Exception e) {
														e.printStackTrace();
													}
												} else {
													Log.w("Atik call",
															"cannot create thumb from photo, photo is invalid!");
												}
												// 20120215 add by NhatVT
												// <S>
											}

											if (mIsCanSaveDataToDB) {
												long duration = 0;
												if (mediaType == PBDatabaseDefinition.MEDIA_VIDEO) {
													try {
														File media = new File(
																realImgPath);
														fis = new FileInputStream(
																media);
														MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
														mediaMetadataRetriever
																.setDataSource(fis
																		.getFD());

														duration = Long
																.parseLong(mediaMetadataRetriever
																		.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
														Bitmap thumb_video;
														thumb_video = ThumbnailUtils
																.createVideoThumbnail(
																		realImgPath,
																		MediaStore.Video.Thumbnails.MINI_KIND);

														if (thumb_video != null) {
															Log.i("mapp",
																	"> downloader > process saving video frame thumb! "
																			+ thumb_video
																					.getWidth()
																			+ "x"
																			+ thumb_video
																					.getHeight());
															boolean savingResult = PBBitmapUtils
																	.saveBitmapToSdcard(
																			thumb_video,
																			photoUrl,
																			true,
																			true,
																			true);
															Log.i("mapp",
																	"> downloader > process saving video frame thumb! "
																			+ savingResult);
														} else {
															Log.e("mapp",
																	"> downloader > CAN NOT extract video thumbnail");
														}
													} catch (Exception e) {
														Log.e("Atik call",
																"Cannot get video information, cause: "
																		+ e.toString());
													}
													// 20120427 only get
													// video frame when this
													// file is a
													// video file <E>
												}
												// 20120416 <E>
												photoUrl = saveMark != 0 ? photoUrl
														: photoUrl
																+ "?can_save=0";
												thumb = saveMark != 0 ? thumb
														: thumb + "?can_save=0";

												if (!existAikotobaInHistorySent
														) {
													mDatabaseManager
															.setPhoto(
																	new PBHistoryPhotoModel(
																			photoUrl,
																			thumb,
																			historyId,
																			mediaType,
																			mediaType == PBDatabaseDefinition.MEDIA_VIDEO ? duration
																					: 0),
																	PBDatabaseDefinition.HISTORY_SENT);
												}

											}

										}
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}

							} catch (Exception ex) {
								ex.printStackTrace();
							}
							

							// Atik check whether password exists in history
							// inbox or not

							existAikotobaInHistorySent = mDatabaseManager
									.isPasswordExistsInHistorySent(objInfo
											.getString("id"));
							
							Log.d("history update 11",objInfo
									.getString("id")+ "  "+existAikotobaInHistorySent);
//							isPasswordExistsInHistorySentItems = mDatabaseManager
//									.isPasswordExistsInHistorySentItems(objInfo
//											.getString("password"));

							if (!existAikotobaInHistorySent
									/*|| !isPasswordExistsInHistorySentItems*/) {

								entryForInsertIntoSentItem = new PBHistoryEntryModel(
										System.currentTimeMillis(), historyId,
										mPhotoListPassword, createDate,
										chargeDate, numOfPhoto, numberDownlaod,
										mDefaultPhotoListThumbUrl, addibility,
										updatedAtForJson, saveMark, localSave,
										isPublic, accepted); // added by Atik
																// for
																// koukaibukuro
								// Update information of upload into
								// database
								
								int dublicateCounter = 0;
								
								Cursor cursor = mDatabaseManager.getHistoriesCursor(PBDatabaseDefinition.HISTORY_INBOX);
								//System.out.println("Atik number of Inbox cursor is:"+cursor.getCount());
								String currentCollectionId = entryForInsertIntoSentItem.getCollectionId();
								if (cursor.moveToFirst()){
									   while(!cursor.isAfterLast()){
										  String collection_Id = cursor.getString(cursor.getColumnIndex(PBDatabaseDefinition.HistoryData.C_COLECTION_ID));
										  String password = cursor.getString(cursor.getColumnIndex(PBDatabaseDefinition.HistoryData.C_PASSWORD));
										 // System.out.println("collection_Id:"+collection_Id);
										  if(!collection_Id.equalsIgnoreCase(currentCollectionId) && password.equalsIgnoreCase(entryForInsertIntoSentItem.getEntryPassword())) {
									    	 // collection_Id = cursor.getString(cursor.getColumnIndex(PBDatabaseDefinition.HistoryData.C_COLECTION_ID));
									    	  dublicateCounter++;
									      }
									      
									      cursor.moveToNext();
									   }
									}
								
								if(dublicateCounter > 0){
									
									String tmpPassword = entryForInsertIntoSentItem.getEntryPassword();
									entryForInsertIntoSentItem.setEntryPassword(tmpPassword+" ("+dublicateCounter+")");
								}
								if(PBAPIContant.DEBUG){
									
									System.out.println(dublicateCounter);
									System.out.println(entryForInsertIntoSentItem.getEntryPassword());
								}

								mDatabaseManager.insertHistory(
										entryForInsertIntoSentItem,
										PBDatabaseDefinition.HISTORY_SENT);
								entryForInsertIntoSentItem
										.setEntryAdLink(adLink);

							}

							// @lent5 check key upload photos fail with
							// number of photos = 0
							// Log.w("testing", "" +
							// Integer.parseInt(objInfo.getString("photos_count")));
							if (model.getEntryNumOfPhoto() != 0) {
								existAikotobaInHistorySent = mDatabaseManager
										.isPasswordExistsInHistorySent(objInfo
												.getString("id"));
//								isPasswordExistsInHistorySentItems = mDatabaseManager
//										.isPasswordExistsInHistorySentItems(objInfo
//												.getString("password"));
								if (existAikotobaInHistorySent
										/*|| isPasswordExistsInHistorySentItems*/) {
									// Update information of upload into
									// database
									
//									String password = model.getEntryPassword();
//									  if(mDatabaseManager.isPasswordExistsInHistoryInbox(model.getCollectionId())){
//										  
//										  password = mDatabaseManager.getPassord(model.getCollectionId(), PBDatabaseDefinition.HISTORY_INBOX);
//									  }
//									  
//									  if(mDatabaseManager.isPasswordExistsInHistorySent(model.getCollectionId())){
//										  
//										  password = mDatabaseManager.getPassord(model.getCollectionId(), PBDatabaseDefinition.HISTORY_SENT);
//									  }
//									  model.setEntryPassword(password);
//									
									String secretCode = mDatabaseManager.getPassswordSecretDigit(model.getCollectionId());
									model.setFourDigit(secretCode);
									Log.d("upload history ", ""+objInfo
											.getString("id4"));
									if(objInfo.has("id4")){
										model.setFourDigit(objInfo
												.getString("id4"));
									}
									mDatabaseManager.setHistory(model,
											PBDatabaseDefinition.HISTORY_SENT);
								} else {
									
								}
							}
						}

						if (upload.length() > 0) {
							ArrayList<PBHistoryEntryModel> uploadLocal = mDatabaseManager
									.getHistories(PBDatabaseDefinition.HISTORY_SENT);
							System.out
									.println("Atik call total histor sent item size"
											+ uploadLocal.size());
							mHistory.setUploadList(uploadLocal);
						}
					}
				}

				if (result.has("download")) {
					JSONArray download = result.getJSONArray("download");
					if (download != null) {

						List<PBHistoryEntryModel> historyArray = mDatabaseManager
								.getHistories(PBDatabaseDefinition.HISTORY_INBOX);
						Map<String, PBHistoryEntryModel> histories = new HashMap<String, PBHistoryEntryModel>();
						for (PBHistoryEntryModel history : historyArray) {
							String collectionId = history.getCollectionId();
							if (histories.containsKey(collectionId)) {
								if (history.getEntryUpdatedAt() <= histories
										.get(collectionId).getEntryUpdatedAt()) {
									continue;
								}
							}
							if (isSaveExpired(history)) {
								if (!TextUtils.isEmpty(mDeleteName)) {
									mDeleteName += "、";
								}
								mDeleteName = mDeleteName + "「"
										+ history.getEntryPassword() + "」";
								mDatabaseManager.deleteHistory(
										String.valueOf(history.getEntryId()),
										history.getCollectionId());
								PBAPIHelper.deleteDownloadedCollection(
										history.getCollectionId(), token);
							} else {
								histories.put(collectionId, history);
							}
						}

						for (int i = 0; i < download.length(); i++) {
							PBHistoryEntryModel model = new PBHistoryEntryModel();
							JSONObject objInfo = download.getJSONObject(i);
							try {
								model.setEntryCollectionId(objInfo
										.getString("id"));
								model.setEntryPassword(objInfo
										.getString("password"));
								Log.d("download obj loop ",objInfo
										.getString("id")+ "  "+objInfo
										.getString("password"));
								model.setEntryCreateDate(objInfo
										.getLong("created_at"));
								model.setEntryNumofDownload(Integer.parseInt(objInfo
										.getString("downloaded_users_count")));
								model.setEntryChargeDate(objInfo
										.getLong("charges_at"));
								model.setEntryNumOfPhoto(Integer
										.parseInt(objInfo
												.getString("photos_count")));
								model.setEntryAddibility(Integer
										.parseInt(objInfo.getString("can_add")));
								model.setEntryMapleUsed(Integer
										.parseInt(objInfo
												.getString("used_maple_count")));
								model.setEntryHoneyUsed(Integer
										.parseInt(objInfo
												.getString("used_honey_count")));
								model.setEntrySaveMark(Integer.parseInt(objInfo
										.getString("can_save")));
								model.setEntrySaveDays(Integer.parseInt(objInfo
										.getString("client_keep_days")));
								model.setIsPublic(Integer.parseInt(objInfo
										.getString("is_public")));
								model.setAccepted(Integer.parseInt(objInfo
										.getString("accepted")));
								model.setmMessageCount(Integer.parseInt(objInfo
										.getString("message_count")));
								

								model.setmExpiresAt(objInfo
										.getLong("expires_at"));

							} catch (Exception ex) {
								ex.printStackTrace();
							}

							// set updatability
							model.setEntryIsUpdatable(0);

							if (objInfo.getLong("updated_at") > 0) {
								if (histories.containsKey(model
										.getCollectionId())) {
									String key = model.getCollectionId();
									long updatedAt = histories.get(key)
											.getEntryUpdatedAt();
									if (updatedAt > 0) {
										model.setEntryUpdatedAt(updatedAt);
										if (objInfo.getLong("updated_at") > updatedAt) {
											model.setEntryIsUpdatable(1);
										}
									} else {
										model.setEntryUpdatedAt(objInfo
												.getLong("updated_at"));
									}
									histories.remove(key);
								} else {
									try {
										model.setEntryUpdatedAt(objInfo
												.getLong("updated_at"));
									} catch (Exception ex) {
										ex.printStackTrace();
									}
								}
							}

							if (Long.parseLong(objInfo.getString("expires_at")) < Long
									.parseLong(objInfo.getString("updated_at"))) {

								model.setEntryIsUpdatable(0);
								model.setEntryAddibility(0);
								// expiredPassword.add(model.getEntryPassword());

							}

							// Update information of download into database
							if (isSaveExpired(model) && model.getEntryId() > 0) {
								PBAPIHelper.deleteDownloadedCollection(
										model.getCollectionId(), token);
							} else {
								
								String secretCode = mDatabaseManager.getPassswordSecretDigit(model.getCollectionId());
								model.setFourDigit(secretCode);
								
//								String password = model.getEntryPassword();
//								  if(mDatabaseManager.isPasswordExistsInHistoryInbox(model.getCollectionId())){
//									  
//									  password = mDatabaseManager.getPassord(model.getCollectionId(), PBDatabaseDefinition.HISTORY_INBOX);
//								  }
//								  
//								  if(mDatabaseManager.isPasswordExistsInHistorySent(model.getCollectionId())){
//									  
//									  password = mDatabaseManager.getPassord(model.getCollectionId(), PBDatabaseDefinition.HISTORY_SENT);
//								  }
//								  model.setEntryPassword(password);
								
								mDatabaseManager.setHistory(model,
										PBDatabaseDefinition.HISTORY_INBOX);
							}
						}

						// set entries non-updatable if it is removed from
						// server because of expiration
						for (PBHistoryEntryModel localHistory : histories
								.values()) {

							if (localHistory.getEntryIsUpdatable() == 1
									|| localHistory.getEntryAddibility() == 1) {
								localHistory.setEntryIsUpdatable(0);
								localHistory.setEntryAddibility(0);
								String secretCode = mDatabaseManager.getPassswordSecretDigit(localHistory.getCollectionId());
								localHistory.setFourDigit(secretCode);
//								String password = localHistory.getEntryPassword();
//								  if(mDatabaseManager.isPasswordExistsInHistoryInbox(localHistory.getCollectionId())){
//									  
//									  password = mDatabaseManager.getPassord(localHistory.getCollectionId(), PBDatabaseDefinition.HISTORY_INBOX);
//								  }
//								  
//								  if(mDatabaseManager.isPasswordExistsInHistorySent(localHistory.getCollectionId())){
//									  
//									  password = mDatabaseManager.getPassord(localHistory.getCollectionId(), PBDatabaseDefinition.HISTORY_SENT);
//								  }
//								  localHistory.setEntryPassword(password);
								
								mDatabaseManager.setHistory(localHistory,
										PBDatabaseDefinition.HISTORY_INBOX);
							}
						}

						if (download.length() > 0) {
							ArrayList<PBHistoryEntryModel> downloadLocal = mDatabaseManager
									.getHistories(PBDatabaseDefinition.HISTORY_INBOX);

							if (downloadLocal == null
									|| downloadLocal.size() == 0) {
								return null;
							}
							PBHistoryManager.getInstance().setDownloadList(
									downloadLocal);
						}
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		downloadPhoto();
		return null;
	}

	private boolean isSaveExpired(PBHistoryEntryModel model) {
		final long SAVE_TIME = model.getEntrySaveDays() * 24 * 60 * 60 * 1000;
		if (SAVE_TIME == 0)
			return false;
		try {
			return System.currentTimeMillis() - model.getEntryId() > SAVE_TIME;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);

		Intent intent = new Intent("photobag.history.update");
		intent.putExtra("status", 1);
		cxt.sendBroadcast(intent);
	}

	private void downloadPhoto() {
		System.out.println("Atik call call download photo");

		// Get photo from server in history download
		ArrayList<PBHistoryEntryModel> download = PBHistoryManager
				.getInstance().getDownloadList();
		for (int i = 0; i < download.size(); i++) {
			PBHistoryEntryModel model = download.get(i);
			String path = PBGeneralUtils.getPathFromCacheFolder(model
					.getEntryThump());
			if (!PBGeneralUtils.checkExistFile(path)) {
				try {
					// Get thump from server to save local
					boolean isSaveThumSuccess = PBAPIHelper.savePhoto(mToken,
							model.getEntryThump(), model.getEntryPassword(),
							model.canSave(), null);
					if (!isSaveThumSuccess) {
						// Get photo from server to save local if thump not
						// found
						String urlPhoto = model.getEntryThump().substring(0,
								model.getEntryThump().lastIndexOf("?"));
						boolean isSaveSuccess = PBAPIHelper.savePhoto(mToken,
								urlPhoto, model.getEntryPassword(),
								model.canSave(), null);
						if (isSaveSuccess) {
							createThumpFile(urlPhoto, model.getEntryThump());
						}
					}
					// mIsUpdateThump = true;
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}

		// Get photo from server in history upload
		ArrayList<PBHistoryEntryModel> upload = PBHistoryManager.getInstance()
				.getUploadList();
		for (int i = 0; i < upload.size(); i++) {
			PBHistoryEntryModel model = upload.get(i);

			if (PBPreferenceUtils.getBoolPref(
					PBApplication.getBaseApplicationContext(),
					PBConstant.PREF_NAME,
					PBConstant.PREF_NEED_REFRESH_HISTORY_SCREEN_FOR_UPLOAD,
					false)) {

				System.out.println("Atik call entered when upload done");
				// Atik check all the photos exists in SDCard or not
				ArrayList<PBHistoryPhotoModel> mLocalPhotos = mDatabaseManager
						.getPhotos(model.getCollectionId());
				for (int j = 0; j < mLocalPhotos.size(); j++) {
					PBHistoryPhotoModel modelLocal = mLocalPhotos.get(j);
					String pathForThumb = PBGeneralUtils
							.getPathFromCacheFolder(modelLocal.getThumb());

					if (!PBGeneralUtils.checkExistFile(pathForThumb)) {
						// Atik save the file if not exist for the photo list of
						// collection id
						try {
							// Get thump from server to save local
							boolean isSaveThumSuccess = PBAPIHelper.savePhoto(
									mToken, modelLocal.getThumb(),
									model.getEntryPassword(), model.canSave(),
									null);
							if (!isSaveThumSuccess) {
								// Get photo from server to save local if thump
								// not found
								String urlPhoto = modelLocal.getThumb()
										.substring(
												0,
												modelLocal.getThumb()
														.lastIndexOf("?"));
								boolean isSaveSuccess = PBAPIHelper.savePhoto(
										mToken, urlPhoto,
										model.getEntryPassword(),
										model.canSave(), null);
								if (isSaveSuccess) {
									createThumpFile(urlPhoto,
											model.getEntryThump());
								}
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}

			// Atik check Only thumb file exists or not
			String path = PBGeneralUtils.getPathFromCacheFolder(model
					.getEntryThump());
			System.out
					.println("Atik call entry for all file for video thumb check "
							+ path);
			if (!PBGeneralUtils.checkExistFile(path)) {
				try {
					// Get thump from server to save local
					boolean isSaveThumSuccess = PBAPIHelper.savePhoto(mToken,
							model.getEntryThump(), model.getEntryPassword(),
							model.canSave(), null);
					if (!isSaveThumSuccess) {
						// Get photo from server to save local if thump not
						// found
						String urlPhoto = model.getEntryThump().substring(0,
								model.getEntryThump().lastIndexOf("?"));
						boolean isSaveSuccess = PBAPIHelper.savePhoto(mToken,
								urlPhoto, model.getEntryPassword(),
								model.canSave(), null);
						if (isSaveSuccess) {
							createThumpFile(urlPhoto, model.getEntryThump());
						}
					}
					// mIsUpdateThump = true;
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else {

			}
		}

		// Atik update shared preference value after upload
		if (PBPreferenceUtils.getBoolPref(
				PBApplication.getBaseApplicationContext(),
				PBConstant.PREF_NAME,
				PBConstant.PREF_NEED_REFRESH_HISTORY_SCREEN_FOR_UPLOAD, false)) {
			System.out
					.println("Atik call update shared preference when upload done");

			PBPreferenceUtils.saveBoolPref(cxt, PBConstant.PREF_NAME,
					PBConstant.PREF_NEED_REFRESH_HISTORY_SCREEN_FOR_UPLOAD,
					false);
		}
	}

	private void createThumpFile(String urlPhoto, String urlThump) {
		Log.i("mapp", ">>> process crop real image to create thumb!");
		String realImgPath = PBGeneralUtils.getPathFromCacheFolder(urlPhoto);
		String thumbImgPath = PBGeneralUtils.getPathFromCacheFolder(urlThump);
		Bitmap bmp = null;
		try {
			Options mOptions = new Options();
			mOptions.inSampleSize = PBBitmapUtils.sampleSizeNeeded(realImgPath,
					PBConstant.MAXIMUM_IMAGE_SIZE_UPLOAD);
			bmp = PBBitmapUtils
					.centerCropImage(
							BitmapFactory.decodeFile(realImgPath, mOptions),
							PBConstant.PHOTO_THUMB_WIDTH,
							PBConstant.PHOTO_THUMB_HEIGHT);
		} catch (Exception e) {
			e.printStackTrace();
		} catch (OutOfMemoryError oom) {
			Log.e("PBHistoryMainFragment",
					">>> Create thumb file, OOM when decode image "
							+ realImgPath);
		}
		FileOutputStream fos = null;
		try {
			if (android.os.Environment.getExternalStorageState().equals(
					android.os.Environment.MEDIA_MOUNTED)) {
				fos = new FileOutputStream(new File(thumbImgPath));
			} else {
				// save on internal memory if sdcard is invalid.
				fos = PBApplication.getBaseApplicationContext().openFileOutput(
						String.valueOf(urlThump.hashCode()), 0);
			}
			if (bmp != null) {
				bmp.compress(PBConstant.COMPRESS_FORMAT,
						PBConstant.DECODE_COMPRESS_PRECENT, fos);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fos.flush();
				fos.close();
				fos = null;
				bmp.recycle();
				bmp = null;
			} catch (Exception e) {
			}
		}

	}


	
}
