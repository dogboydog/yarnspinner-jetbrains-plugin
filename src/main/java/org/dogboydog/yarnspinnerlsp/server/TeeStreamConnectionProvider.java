package org.dogboydog.yarnspinnerlsp.server;

import com.intellij.openapi.diagnostic.Logger;
import org.apache.commons.io.input.TeeInputStream;
import org.bouncycastle.util.io.TeeOutputStream;
import org.wso2.lsp4intellij.client.connection.StreamConnectionProvider;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

import static org.dogboydog.yarnspinnerlsp.YarnSpinnerLSPConstants.LOG_PREFIX;

/**
 * Stream connection provider that optionally logs input and output
 * to output streams.
 */
public class TeeStreamConnectionProvider implements StreamConnectionProvider {

    private static final Logger log = Logger.getInstance(TeeStreamConnectionProvider.class);
    private final StreamConnectionProvider realConnectionProvider;
    private final OutputStream stdinDebugLogStream;
    private final OutputStream stdoutDebugLogStream;
    private InputStream inputStream;
    private OutputStream outputStream;

    /**
     * @param realConnectionProvider - Original StreamConnectionProvider from the RawCommandServerDefinition
     * @param stdinDebugLogStream            - The output stream to echo stdin to
     * @param stdoutDebugLogStream           - The output stream to echo stdout to
     */
    public TeeStreamConnectionProvider(StreamConnectionProvider realConnectionProvider,
                                       OutputStream stdinDebugLogStream,
                                       OutputStream stdoutDebugLogStream) {
        this.realConnectionProvider = realConnectionProvider;
        this.stdinDebugLogStream = stdinDebugLogStream;
        this.stdoutDebugLogStream = stdoutDebugLogStream;
        try {
            var startupString = String.format("%s%s: created TeeStreamConnectionProvider%n",
                    LOG_PREFIX, Instant.now().toString()).getBytes(StandardCharsets.UTF_8);
            this.stdoutDebugLogStream.write(startupString);
            this.stdinDebugLogStream.write(startupString);
        } catch (IOException e) {
            log.error("Couldn't log initial startup message. ", e);
        }
    }

    @Override
    public void start() throws IOException {
        realConnectionProvider.start();
        inputStream = new TeeInputStream(realConnectionProvider.getInputStream(), stdoutDebugLogStream);
        outputStream = new TeeOutputStream(realConnectionProvider.getOutputStream(), stdinDebugLogStream);
    }

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public OutputStream getOutputStream() {
        return outputStream;
    }

    @Override
    public void stop() {
        realConnectionProvider.stop();
        inputStream = null;
        outputStream = null;
    }
}
