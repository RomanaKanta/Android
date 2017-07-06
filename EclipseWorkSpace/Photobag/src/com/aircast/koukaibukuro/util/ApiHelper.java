package com.aircast.koukaibukuro.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.text.TextUtils;


public class ApiHelper {
	 public static final String ERROR_DESC = "Conenction timeout";
	 
	 public static class Response{
	        public int errorCode = 200;
	        public String decription = "";

	        /** Contructor Response */
	        public Response() {
	        }

	        /**
	         * Contructor Response 
	         * @param errorCode
	         * @param decription
	         */
	        public Response(int errorCode, String decription) {
	            this.errorCode = errorCode;
	            this.decription = decription;
	        }
	    }
	
	 public static Response getsearchListByArrival(String uid,String password,String type,String limit) {
//			if (TextUtils.isEmpty(token)) {
//				return new Response(999,
//						"Token is empty! Please try again later!");
//			}

			HttpUtils mHttpUtils = HttpUtils.getInstance();
			String content = ERROR_DESC;
			int statusCode = 900;

			if (mHttpUtils != null) {
				HttpClient httpClient = mHttpUtils.createHttpsClient();

				if (httpClient == null) {
					return new Response(statusCode, content);
				}

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		     	//nameValuePairs.add(new BasicNameValuePair("uid", ""));
				nameValuePairs.add(new BasicNameValuePair("password", password));
				nameValuePairs.add(new BasicNameValuePair("type", type));
				nameValuePairs.add(new BasicNameValuePair("search_key", password));
				nameValuePairs.add(new BasicNameValuePair("limit", limit));
				
				int tryAgain = 3;
				do {
					try {
						HttpResponse httpResponse = mHttpUtils.doPostForChat(httpClient,
								Constant.Get_Password_List_For_Search_Url_With_New_Arrival, nameValuePairs);

						// fix check null
						if (httpResponse != null
								&& httpResponse.getStatusLine() != null) {
							statusCode = httpResponse.getStatusLine()
									.getStatusCode();
						}
						content = parseReponseBody(statusCode,
								httpResponse.getEntity());
						// shut down connection client

					} catch (IOException e) {
						content = "IOException";
						statusCode = 900;
						e.printStackTrace();
					} catch (URISyntaxException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (HttpException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} while (statusCode ==900
						&& (--tryAgain) > 0); // try 3 time if connection fail

				if (httpClient != null) {
					httpClient.getConnectionManager().shutdown();
				}
			}

			return new Response(statusCode, content);
	}
	
	
	public static Response getUpdatePasswordList(String uid,String password,String sortType) {
//		if (TextUtils.isEmpty(token)) {
//			return new Response(999,
//					"Token is empty! Please try again later!");
//		}

		HttpUtils mHttpUtils = HttpUtils.getInstance();
		String content = ERROR_DESC;
		int statusCode = 900;

		if (mHttpUtils != null) {
			HttpClient httpClient = mHttpUtils.createHttpsClient();

			if (httpClient == null) {
				return new Response(statusCode, content);
			}

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	     	//nameValuePairs.add(new BasicNameValuePair("uid", ""));
			nameValuePairs.add(new BasicNameValuePair("password", password));
			nameValuePairs.add(new BasicNameValuePair("type", "top"));
			nameValuePairs.add(new BasicNameValuePair("sortby", sortType));
			int tryAgain = 3;
			do {
				try {
					HttpResponse httpResponse = mHttpUtils.doPostForChat(httpClient,
							Constant.Get_Main_Url_For_Get_List, nameValuePairs);

					// fix check null
					if (httpResponse != null
							&& httpResponse.getStatusLine() != null) {
						statusCode = httpResponse.getStatusLine()
								.getStatusCode();
					}
					content = parseReponseBody(statusCode,
							httpResponse.getEntity());
					// shut down connection client

				} catch (IOException e) {
					content = "IOException";
					statusCode = 900;
					e.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (HttpException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} while (statusCode ==900
					&& (--tryAgain) > 0); // try 3 time if connection fail

			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
			}
		}

		return new Response(statusCode, content);
	}
	
	
	public static Response getUpdatePasswordListForType(String uid,String password,String sortType,String type) {
//		if (TextUtils.isEmpty(token)) {
//			return new Response(999,
//					"Token is empty! Please try again later!");
//		}

		HttpUtils mHttpUtils = HttpUtils.getInstance();
		String content = ERROR_DESC;
		int statusCode = 900;

		if (mHttpUtils != null) {
			HttpClient httpClient = mHttpUtils.createHttpsClient();

			if (httpClient == null) {
				return new Response(statusCode, content);
			}

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	     	nameValuePairs.add(new BasicNameValuePair("uid", ""));
			nameValuePairs.add(new BasicNameValuePair("password", password));
			nameValuePairs.add(new BasicNameValuePair("type", type));
			nameValuePairs.add(new BasicNameValuePair("sortby", sortType));
			int tryAgain = 3;
			do {
				try {
					HttpResponse httpResponse = mHttpUtils.doPostForChat(httpClient,
							Constant.Get_Main_Url_For_Get_List, nameValuePairs);

					// fix check null
					if (httpResponse != null
							&& httpResponse.getStatusLine() != null) {
						statusCode = httpResponse.getStatusLine()
								.getStatusCode();
					}
					content = parseReponseBody(statusCode,
							httpResponse.getEntity());
					// shut down connection client

				} catch (IOException e) {
					content = "IOException";
					statusCode = 900;
					e.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (HttpException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} while (statusCode ==900
					&& (--tryAgain) > 0); // try 3 time if connection fail

			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
			}
		}

		return new Response(statusCode, content);
	}

	
	public static Response addFavourite(String uid,String password) {
//		if (TextUtils.isEmpty(token)) {
//			return new Response(999,
//					"Token is empty! Please try again later!");
//		}

		HttpUtils mHttpUtils = HttpUtils.getInstance();
		String content = ERROR_DESC;
		int statusCode = 900;

		if (mHttpUtils != null) {
			HttpClient httpClient = mHttpUtils.createHttpsClient();

			if (httpClient == null) {
				return new Response(statusCode, content);
			}

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("password", password));
			nameValuePairs.add(new BasicNameValuePair("uid", uid));
			

			int tryAgain = 3;
			do {
				try {
					HttpResponse httpResponse = mHttpUtils.doPostForChat(httpClient,
							Constant.Add_Favourite_Url, nameValuePairs);

					// fix check null
					if (httpResponse != null
							&& httpResponse.getStatusLine() != null) {
						statusCode = httpResponse.getStatusLine()
								.getStatusCode();
					}
					content = parseReponseBody(statusCode,
							httpResponse.getEntity());
					// shut down connection client

				} catch (IOException e) {
					content = "IOException";
					statusCode = 900;
					e.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (HttpException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} while (statusCode ==900
					&& (--tryAgain) > 0); // try 3 time if connection fail

			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
			}
		}

		return new Response(statusCode, content);
	}
	
	public static Response getNickName(String uid) {
//		if (TextUtils.isEmpty(token)) {
//			return new Response(999,
//					"Token is empty! Please try again later!");
//		}

		HttpUtils mHttpUtils = HttpUtils.getInstance();
		String content = ERROR_DESC;
		int statusCode = 900;

		if (mHttpUtils != null) {
			HttpClient httpClient = mHttpUtils.createHttpsClient();

			if (httpClient == null) {
				return new Response(statusCode, content);
			}

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("uid", uid));
			

			int tryAgain = 3;
			do {
				try {
					HttpResponse httpResponse = mHttpUtils.doPostForChat(httpClient,
							Constant.Get_NickName_Url, nameValuePairs);

					// fix check null
					if (httpResponse != null
							&& httpResponse.getStatusLine() != null) {
						statusCode = httpResponse.getStatusLine()
								.getStatusCode();
					}
					content = parseReponseBody(statusCode,
							httpResponse.getEntity());
					// shut down connection client

				} catch (IOException e) {
					content = "IOException";
					statusCode = 900;
					e.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (HttpException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} while (statusCode ==900
					&& (--tryAgain) > 0); // try 3 time if connection fail

			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
			}
		}

		return new Response(statusCode, content);
	}
	
	public static Response getFavouriteList(String uid) {
//		if (TextUtils.isEmpty(token)) {
//			return new Response(999,
//					"Token is empty! Please try again later!");
//		}

		HttpUtils mHttpUtils = HttpUtils.getInstance();
		String content = ERROR_DESC;
		int statusCode = 900;

		if (mHttpUtils != null) {
			HttpClient httpClient = mHttpUtils.createHttpsClient();

			if (httpClient == null) {
				return new Response(statusCode, content);
			}

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("uid", uid));
			

			int tryAgain = 3;
			do {
				try {
					HttpResponse httpResponse = mHttpUtils.doPostForChat(httpClient,
							Constant.Get_Favourite_List_Url, nameValuePairs);

					// fix check null
					if (httpResponse != null
							&& httpResponse.getStatusLine() != null) {
						statusCode = httpResponse.getStatusLine()
								.getStatusCode();
					}
					content = parseReponseBody(statusCode,
							httpResponse.getEntity());
					// shut down connection client

				} catch (IOException e) {
					content = "IOException";
					statusCode = 900;
					e.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (HttpException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} while (statusCode ==900
					&& (--tryAgain) > 0); // try 3 time if connection fail

			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
			}
		}

		return new Response(statusCode, content);
	}

	
	public static Response getUpdateFavouriteList(String uid,String password) {
//		if (TextUtils.isEmpty(token)) {
//			return new Response(999,
//					"Token is empty! Please try again later!");
//		}

		HttpUtils mHttpUtils = HttpUtils.getInstance();
		String content = ERROR_DESC;
		int statusCode = 900;

		if (mHttpUtils != null) {
			HttpClient httpClient = mHttpUtils.createHttpsClient();

			if (httpClient == null) {
				return new Response(statusCode, content);
			}

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("uid", uid));
			nameValuePairs.add(new BasicNameValuePair("type", "top"));
			nameValuePairs.add(new BasicNameValuePair("password", password));
			

			int tryAgain = 3;
			do {
				try {
					HttpResponse httpResponse = mHttpUtils.doPostForChat(httpClient,
							Constant.Get_Favourite_List_Url, nameValuePairs);

					// fix check null
					if (httpResponse != null
							&& httpResponse.getStatusLine() != null) {
						statusCode = httpResponse.getStatusLine()
								.getStatusCode();
					}
					content = parseReponseBody(statusCode,
							httpResponse.getEntity());
					// shut down connection client

				} catch (IOException e) {
					content = "IOException";
					statusCode = 900;
					e.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (HttpException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} while (statusCode ==900
					&& (--tryAgain) > 0); // try 3 time if connection fail

			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
			}
		}

		return new Response(statusCode, content);
	}


	public static Response getPasswordList(String uid,String sortType) {
//		if (TextUtils.isEmpty(token)) {
//			return new Response(999,
//					"Token is empty! Please try again later!");
//		}

		HttpUtils mHttpUtils = HttpUtils.getInstance();
		String content = ERROR_DESC;
		int statusCode = 900;

		if (mHttpUtils != null) {
			HttpClient httpClient = mHttpUtils.createHttpsClient();

			if (httpClient == null) {
				return new Response(statusCode, content);
			}

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("uid", uid));
			nameValuePairs.add(new BasicNameValuePair("sortby", sortType));
			

			int tryAgain = 3;
			do {
				try {
					HttpResponse httpResponse = mHttpUtils.doPostForChat(httpClient,
							Constant.Get_Main_Url_For_Get_List, nameValuePairs);

					// fix check null
					if (httpResponse != null
							&& httpResponse.getStatusLine() != null) {
						statusCode = httpResponse.getStatusLine()
								.getStatusCode();
					}
					content = parseReponseBody(statusCode,
							httpResponse.getEntity());
					// shut down connection client

				} catch (IOException e) {
					content = "IOException";
					statusCode = 900;
					e.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (HttpException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} while (statusCode ==900
					&& (--tryAgain) > 0); // try 3 time if connection fail

			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
			}
		}

		return new Response(statusCode, content);
	}
	
	
	/*
	 * API method for getting all the password 
	 * from server
	 */
	public static Response getAllPasswordList(String uid) {
//		if (TextUtils.isEmpty(token)) {
//			return new Response(999,
//					"Token is empty! Please try again later!");
//		}

		HttpUtils mHttpUtils = HttpUtils.getInstance();
		String content = ERROR_DESC;
		int statusCode = 900;

		if (mHttpUtils != null) {
			HttpClient httpClient = mHttpUtils.createHttpsClient();

			if (httpClient == null) {
				return new Response(statusCode, content);
			}

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("uid", uid));
			nameValuePairs.add(new BasicNameValuePair("limit", "all"));

			int tryAgain = 3;
			do {
				try {
					HttpResponse httpResponse = mHttpUtils.doPostForChat(httpClient,
							Constant.Get_Main_Url_For_Get_List, nameValuePairs);

					// fix check null
					if (httpResponse != null
							&& httpResponse.getStatusLine() != null) {
						statusCode = httpResponse.getStatusLine()
								.getStatusCode();
					}
					content = parseReponseBody(statusCode,
							httpResponse.getEntity());
					// shut down connection client

				} catch (IOException e) {
					content = "IOException";
					statusCode = 900;
					e.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (HttpException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} while (statusCode ==900
					&& (--tryAgain) > 0); // try 3 time if connection fail

			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
			}
		}

		return new Response(statusCode, content);
	}
	
	public static Response nickNameRegistration(String uid,String name) {

		HttpUtils mHttpUtils = HttpUtils.getInstance();
		String content = ERROR_DESC;
		int statusCode = 900;

		if (mHttpUtils != null) {
			HttpClient httpClient = mHttpUtils.createHttpsClient();

			if (httpClient == null) {
				return new Response(statusCode, content);
			}

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("nickname", name));
			nameValuePairs.add(new BasicNameValuePair("uid", uid));
			

			int tryAgain = 3;
			do {
				try {
					HttpResponse httpResponse = mHttpUtils.doPostForChat(httpClient,
							Constant.NickName_Registration_Url, nameValuePairs);

					// fix check null
					if (httpResponse != null
							&& httpResponse.getStatusLine() != null) {
						statusCode = httpResponse.getStatusLine()
								.getStatusCode();
					}
					content = parseReponseBody(statusCode,
							httpResponse.getEntity());
					// shut down connection client

				} catch (IOException e) {
					content = "IOException";
					statusCode = 900;
					e.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (HttpException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} while (statusCode ==900
					&& (--tryAgain) > 0); // try 3 time if connection fail

			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
			}
		}

		return new Response(statusCode, content);
	}
	
	public static Response getThumbImageUrl(String token,String password) {

		HttpUtils mHttpUtils = HttpUtils.getInstance();
		String content = ERROR_DESC;
		int statusCode = 900;

		if (mHttpUtils != null) {
			HttpClient httpClient = mHttpUtils.createHttpsClient();

			if (httpClient == null) {
				return new Response(statusCode, content);
			}

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("token", token));
			nameValuePairs.add(new BasicNameValuePair("password", password));
			

			int tryAgain = 3;
			do {
				try {
					HttpResponse httpResponse = mHttpUtils.doPostForChat(httpClient,
							Constant.NickName_Registration_Url, nameValuePairs);

					// fix check null
					if (httpResponse != null
							&& httpResponse.getStatusLine() != null) {
						statusCode = httpResponse.getStatusLine()
								.getStatusCode();
					}
					content = parseReponseBody(statusCode,
							httpResponse.getEntity());
					// shut down connection client

				} catch (IOException e) {
					content = "IOException";
					statusCode = 900;
					e.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (HttpException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} while (statusCode ==900
					&& (--tryAgain) > 0); // try 3 time if connection fail

			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
			}
		}

		return new Response(statusCode, content);
	}
	
	
	public static Response openIDRegistration(String uid,String share_id,String share_type) {

		HttpUtils mHttpUtils = HttpUtils.getInstance();
		String content = ERROR_DESC;
		int statusCode = 900;

		if (mHttpUtils != null) {
			HttpClient httpClient = mHttpUtils.createHttpsClient();

			if (httpClient == null) {
				return new Response(statusCode, content);
			}

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("uid", uid));
			nameValuePairs.add(new BasicNameValuePair("share_id", share_id));
			nameValuePairs.add(new BasicNameValuePair("share_type", share_type));
			

			int tryAgain = 3;
			do {
				try {
					HttpResponse httpResponse = mHttpUtils.doPostForChat(httpClient,
							Constant.openid_login_url, nameValuePairs);

					// fix check null
					if (httpResponse != null
							&& httpResponse.getStatusLine() != null) {
						statusCode = httpResponse.getStatusLine()
								.getStatusCode();
					}
					content = parseReponseBody(statusCode,
							httpResponse.getEntity());
					// shut down connection client

				} catch (IOException e) {
					content = "IOException";
					statusCode = 900;
					e.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (HttpException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} while (statusCode ==900
					&& (--tryAgain) > 0); // try 3 time if connection fail

			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
			}
		}

		return new Response(statusCode, content);
	}


	
	/**
     * parse body from httpResponse
     * in case 4xx error response, parse the body content to get error desciption
     * @param statusCode error code reponse
     * @param entityReponse the Entity contain the body contain of HttpReponse
     * @return the content result after parsing
     */
    private static String parseReponseBody(int statusCode, HttpEntity entityReponse) throws ParseException, IOException{
        if(entityReponse == null) return "";
        
        String reponseBody = EntityUtils.toString(entityReponse);
        
        if(TextUtils.isEmpty(reponseBody)) return "";
        // parse error description in case error
        if (statusCode != HttpStatus.SC_OK && statusCode != 404) {
            if(reponseBody.contains("error")){
                try {
                    JSONObject error = new JSONObject(reponseBody);
    
                    if(error != null){                          
                        if(error.has("error_description")){
                            reponseBody = error.getString("error_description");
                        }
                    }                       
                }catch (Exception e) {
                    // reponseBody = "JSONObject parsing";
                    e.printStackTrace();
                }
            }
        }

        return reponseBody;
    }
    
    
}
