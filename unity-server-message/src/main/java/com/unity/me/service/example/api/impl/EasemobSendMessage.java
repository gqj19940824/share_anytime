package com.unity.me.service.example.api.impl;

import com.unity.me.service.example.api.SendMessageAPI;
import com.unity.me.service.example.comm.EasemobAPI;
import com.unity.me.service.example.comm.OrgInfo;
import com.unity.me.service.example.comm.ResponseHandler;
import com.unity.me.service.example.comm.TokenUtil;
import io.swagger.client.ApiException;
import io.swagger.client.api.MessagesApi;
import io.swagger.client.model.Msg;

public class EasemobSendMessage implements SendMessageAPI {
    private ResponseHandler responseHandler = new ResponseHandler();
    private MessagesApi api = new MessagesApi();
    @Override
    public Object sendMessage(final Object payload) {
        return responseHandler.handle(new EasemobAPI() {
            @Override
            public Object invokeEasemobAPI() throws ApiException {
                return api.orgNameAppNameMessagesPost(OrgInfo.ORG_NAME,OrgInfo.APP_NAME,TokenUtil.getAccessToken(), (Msg) payload);
            }
        });
    }
}
