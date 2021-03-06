package com.baidu.api.client.examples.account;

import com.baidu.api.client.core.*;
import com.baidu.api.sem.common.v2.ResHeader;
import com.baidu.api.sem.nms.v2.AccountService;
import com.baidu.api.sem.nms.v2.GetAccountInfoRequest;
import com.baidu.api.sem.nms.v2.GetAccountInfoResponse;

import java.rmi.RemoteException;

/**
 * ClassName: AccountServiceExample  <br>
 * Function: TODO ADD FUNCTION
 *
 * @author Baidu API Team
 * @date Feb 9, 2012
 */
public class GetAccountInfoExample {

    private AccountService service;

    public GetAccountInfoExample() {
        // Get service factory. Your authentication information will be popped up automatically from
        // baidu-api.properties
        VersionService factory = ServiceFactory.getInstance();
        // Get service stub by given the Service interface.
        // Please see the bean-api.tar.gz to get more details about all the service interfaces.
        this.service = factory.getService(AccountService.class);
    }

    public GetAccountInfoResponse getAccountInfo() {
        // Prepare your parameters.
        GetAccountInfoRequest parameters = new GetAccountInfoRequest();
        // Invoke the method.
        GetAccountInfoResponse ret = service.getAccountInfo(parameters);
        // Deal with the response header, the second parameter controls whether to print the response header to console
        // or not.
        ResHeader rheader = ResHeaderUtil.getResHeader(service, true);
        // If status equals zero, there is no error. Otherwise, you need to check the errors in the response header.
        if (rheader.getStatus() == 0) {
            System.out.println("getAccountInfo.result\n" + ObjToStringUtil.objToString(ret.getAccountInfo()));
            return ret;
        } else {
            throw new ClientBusinessException(rheader, ret);
        }
    }

    /**
     * @param args
     * @throws Throwable
     * @throws RemoteException
     */
    public static void main(String[] args) throws Throwable {
        GetAccountInfoExample example = new GetAccountInfoExample();
        example.getAccountInfo();
    }

}
