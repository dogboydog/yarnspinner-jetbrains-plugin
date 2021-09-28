package org.dogboydog.yarnspinnerlsp.server;

import com.intellij.openapi.diagnostic.Logger;
import org.dogboydog.yarnspinnerlsp.settings.AppSettingsState;
import org.wso2.lsp4intellij.client.connection.ProcessStreamConnectionProvider;
import org.wso2.lsp4intellij.client.connection.StreamConnectionProvider;
import org.wso2.lsp4intellij.client.languageserver.serverdefinition.LanguageServerDefinition;
import org.wso2.lsp4intellij.client.languageserver.serverdefinition.RawCommandServerDefinition;

import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

import static org.dogboydog.yarnspinnerlsp.YarnSpinnerLSPConstants.LOG_PREFIX;

public class YarnSpinnerServerDefinition extends LanguageServerDefinition {

    private static final String lspDllRelativePath = "Server/YarnLanguageServer.dll";
    private static final Logger log = Logger.getInstance(YarnSpinnerServerDefinition.class);
    protected String[] command;
    private File debugStdoutFile = new File("./yarnspinner-lsp.stdout.log");
    private File debugStdinFile = new File("./yarnspinner-lsp.stdin.log");
    private RawCommandServerDefinition rawCommandServerDefinition = null;
    private OutputStream debugStdinStream;
    private OutputStream debugStdoutStream;

    /**
     * Helper class used when debug logging is turned off to not write to any log files
     */
    private class DummyOutputStream extends OutputStream {
        @Override
        public void write(int b) throws IOException {
        }
    }

    /**
     * Creates new instance with the given languag id which is different from the file extension.
     *
     * @param ext         The extension
     * @param languageIds The language server ids mapping to extension(s).
     * @param command     The command to run
     */
    @SuppressWarnings("WeakerAccess")
    public YarnSpinnerServerDefinition(String ext, Map<String, String> languageIds, String[] command) {
        this.ext = "yarn";
        this.languageIds = Map.of();
        String dllPath = getResourcePath(lspDllRelativePath);
        if (dllPath == null) {
            log.error(LOG_PREFIX + "Could not find " + lspDllRelativePath + " which should have been bundled in the plugin.");
            return;
        }

        this.command = new String[]{"dotnet", dllPath};
        rawCommandServerDefinition = new RawCommandServerDefinition(this.ext, this.command);

        var settings = AppSettingsState.getInstance();
        if (settings.debugLogging) {
            try {
                debugStdinStream = new FileOutputStream(debugStdinFile);
                debugStdoutStream = new FileOutputStream(debugStdoutFile);
            } catch (IOException e) {
                log.error(LOG_PREFIX + "Unable to open debug logging files.", e);
            }
        } else {
            debugStdinStream = new DummyOutputStream();
            debugStdoutStream = new DummyOutputStream();
        }
    }

    public YarnSpinnerServerDefinition() {
        this(null, null, null); // use all defaults
    }

    public String toString() {
        return "YarnSpinnerServerDefinition : " + String.join(" ", command);
    }

    @Override
    public StreamConnectionProvider createConnectionProvider(String workingDir) {
        var realStreamProvider = rawCommandServerDefinition.createConnectionProvider(workingDir);
        return new TeeStreamConnectionProvider(realStreamProvider, debugStdinStream, debugStdoutStream);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof YarnSpinnerServerDefinition) {
            YarnSpinnerServerDefinition commandsDef = (YarnSpinnerServerDefinition) obj;
            return ext.equals(commandsDef.ext) && Arrays.equals(command, commandsDef.command);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return ext.hashCode() + 3 * Arrays.hashCode(command);
    }

    private String getResourcePath(String relativePath) {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            Enumeration<URL> allResources = classLoader.getResources("");
            while (allResources.hasMoreElements()) {
                URL url = allResources.nextElement();
                log.debug(LOG_PREFIX + "resource found: " + url.toString());
            }
            URL resourceUrl = classLoader.getResource(relativePath);
            if (resourceUrl == null) {
                return null;
            }
            log.info(LOG_PREFIX + "Found requested resource: " + resourceUrl.getFile());
            return resourceUrl.getFile();
        } catch (IOException e) {
            log.warn(LOG_PREFIX + "Failed to read resources: " + e.getMessage() + "\n" + e.getStackTrace());
            return null;
        }
    }
}