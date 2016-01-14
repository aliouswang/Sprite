package com.aliouswang.sprite.http.library.ssl;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

/**
 * Created by aliouswang on 16/1/14.
 */
public class DefaultSSLProtocolSocketFactory implements ProtocolSocketFactory{

    private SSLContext sslContext = null;

    private SSLContext createSSLContext() {
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[] {new TrustAnyTrustManager()},
                    new SecureRandom());
        }catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
        }catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return sslContext;
    }

    public SSLContext getSSLContext() {
        if (this.sslContext == null) {
            this.sslContext = createSSLContext();
        }
        return this.sslContext;
    }

    @Override
    public Socket createSocket(String s, int i, InetAddress inetAddress, int i1) throws IOException, UnknownHostException {
        return getSSLContext().getSocketFactory().createSocket(s,i, inetAddress, i1);
    }

    @Override
    public Socket createSocket(String s, int i, InetAddress inetAddress, int i1, HttpConnectionParams httpConnectionParams) throws IOException, UnknownHostException, ConnectTimeoutException {
        return getSSLContext().getSocketFactory().createSocket(s,i, inetAddress, i1);
    }

    @Override
    public Socket createSocket(String s, int i) throws IOException, UnknownHostException {
        return getSSLContext().getSocketFactory().createSocket(s, i);
    }


}
