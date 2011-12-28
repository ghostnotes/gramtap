package jp.ghostnotes.gramnotes.android.api;

import javax.net.ssl.SSLContext;


/*
public class HttpClientTest2 {

    private static void initHttpClient() {
        // 証明書チェックを行わない。
        Protocol.registerProtocol("https", new Protocol("https",
                new MySSLSocketFactory(), 443));
    }

    public static void main(String[] args) throws Exception {

        // HttpClient の初期設定。
        initHttpClient();

        String href = "https://www.example.com/";

        HttpClient httpClient = new HttpClient();

        GetMethod method = new GetMethod(href);

        int retCode = httpClient.executeMethod(method);
        System.out.println(retCode);

        byte[] buf = method.getResponseBody();
        System.out.write(buf);
    }
}
 * 
 */


public class MySSLSocketFactory implements SecureProtocolSocketFactory {
    private SSLContext sslcontext = null;

    private static SSLContext createEasySSLContext() {
        try {
            SSLContext context = SSLContext.getInstance("SSL");
            context.init(null, new TrustManager[] { new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] arg0,
                        String arg1) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] arg0,
                        String arg1) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            } }, null);
            return context;
        } catch (Exception e) {
            throw new HttpClientError(e.toString());
        }
    }

    private SSLContext getSSLContext() {
        if (this.sslcontext == null) {
            this.sslcontext = createEasySSLContext();
        }
        return this.sslcontext;
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port,
            boolean autoClose) throws IOException, UnknownHostException {
        return getSSLContext().getSocketFactory().createSocket(socket, host,
                port, autoClose);
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException,
            UnknownHostException {
        return getSSLContext().getSocketFactory().createSocket(host, port);
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress clientHost,
            int clientPort) throws IOException, UnknownHostException {
        return getSSLContext().getSocketFactory().createSocket(host, port,
                clientHost, clientPort);
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localAddress,
            int localPort, HttpConnectionParams params) throws IOException,
            UnknownHostException, ConnectTimeoutException {
        if (params == null) {
            throw new IllegalArgumentException("Parameters may not be null");
        }
        int timeout = params.getConnectionTimeout();
        SocketFactory socketfactory = getSSLContext().getSocketFactory();
        if (timeout == 0) {
            return socketfactory.createSocket(host, port, localAddress,
                    localPort);
        } else {
            Socket socket = socketfactory.createSocket();
            SocketAddress localaddr = new InetSocketAddress(localAddress,
                    localPort);
            SocketAddress remoteaddr = new InetSocketAddress(host, port);
            socket.bind(localaddr);
            socket.connect(remoteaddr, timeout);
            return socket;
        }
    }

}
