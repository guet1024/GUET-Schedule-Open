package com.telephone.coursetable.Https;

import android.net.SSLCertificateSocketFactory;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * 参考：最佳实践-HTTPS(含SNI)-Android业务场景"IP直连"方案说明（来自阿里云开发指南）
 * Reference: https://help.aliyun.com/knowledge_detail/151175.html
 */
class TelTlsSniSocketFactory extends SSLSocketFactory {
    private final static String TAG = TelTlsSniSocketFactory.class.getSimpleName();
    private HostnameVerifier hostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier();
    private HttpsURLConnection conn;

    public TelTlsSniSocketFactory(HttpsURLConnection conn) {
        this.conn = conn;
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return new String[0];
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return new String[0];
    }

    /**
     * this method is used to update the SNI to the customized host name
     */
    @Override
    public Socket createSocket(Socket origin_socket, String origin_host, int origin_port, boolean autoClose) throws IOException {
        String customized_host = this.conn.getRequestProperty("Host");
        if (customized_host == null)
            customized_host = origin_host;
        com.telephone.coursetable.LogMe.LogMe.i(TAG, "customized host name: " + customized_host);
        InetAddress origin_address = origin_socket.getInetAddress();
        if (autoClose) {
            // we don't need the origin_socket
            origin_socket.close();
        }
        // create and connect SSL socket, but don't do hostname/certificate verification yet
        SSLCertificateSocketFactory sslSocketFactory = (SSLCertificateSocketFactory) SSLCertificateSocketFactory.getDefault(0);
        SSLSocket ssl = (SSLSocket) sslSocketFactory.createSocket(origin_address, origin_port);
        // enable TLSv1.1/1.2 if available
        ssl.setEnabledProtocols(ssl.getSupportedProtocols());
        // set up SNI before the handshake
        com.telephone.coursetable.LogMe.LogMe.i(TAG, "Setting SNI hostname: " + customized_host);
        sslSocketFactory.setHostname(ssl, customized_host);
        // verify hostname and certificate
        SSLSession session = ssl.getSession();
        // verification uses the customized host name, not the origin one
        if (!hostnameVerifier.verify(customized_host, session))
            throw new SSLPeerUnverifiedException("Cannot verify hostname: " + customized_host);
        com.telephone.coursetable.LogMe.LogMe.i(TAG, "Established " + session.getProtocol() + " connection with " + ssl.getInetAddress() + " using mask of " + session.getPeerHost() +
                " using " + session.getCipherSuite());
        return ssl;
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        return null;
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
        return null;
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        return null;
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        return null;
    }
}
