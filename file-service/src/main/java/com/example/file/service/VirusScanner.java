package com.example.file.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Component
public class VirusScanner {
    private final boolean enabled;
    private final String host;
    private final int port;
    private final int timeout;

    public VirusScanner(@Value("${file.virus-scan.enabled:true}") boolean enabled,
                        @Value("${file.virus-scan.host:localhost}") String host,
                        @Value("${file.virus-scan.port:3310}") int port,
                        @Value("${file.virus-scan.timeout-ms:10000}") int timeout) {
        this.enabled = enabled; this.host = host; this.port = port; this.timeout = timeout;
    }

    public void assertClean(byte[] bytes) {
        if (!enabled) return;
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeout);
            socket.setSoTimeout(timeout);
            OutputStream out = socket.getOutputStream();
            out.write("zINSTREAM\0".getBytes(StandardCharsets.US_ASCII));
            for (int offset = 0; offset < bytes.length; offset += 8192) {
                int length = Math.min(8192, bytes.length - offset);
                out.write(ByteBuffer.allocate(4).putInt(length).array());
                out.write(bytes, offset, length);
            }
            out.write(new byte[4]); out.flush();
            String response = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)).readLine();
            if (response == null || !response.contains("OK")) throw new IllegalArgumentException("File failed virus scan");
        } catch (IOException e) {
            throw new IllegalStateException("Virus scanner is unavailable; upload rejected", e);
        }
    }
}
