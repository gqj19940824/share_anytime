package com.unity.me.service.example.api.impl;


import com.unity.me.service.example.api.AuthTokenAPI;
import com.unity.me.service.example.comm.TokenUtil;

public class EasemobAuthToken implements AuthTokenAPI {

	@Override
	public Object getAuthToken(){
		return TokenUtil.getAccessToken();
	}
}
