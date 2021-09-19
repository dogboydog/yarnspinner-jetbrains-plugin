package org.dogboydog.yarnspinnerlsp;

import com.intellij.openapi.application.PreloadingActivity;
import com.intellij.openapi.progress.ProgressIndicator;
import org.wso2.lsp4intellij.IntellijLanguageClient;
import org.wso2.lsp4intellij.client.languageserver.serverdefinition.RawCommandServerDefinition;
import com.intellij.openapi.diagnostic.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

public class YarnSpinnerPreloadingActivity extends PreloadingActivity {
    private static final Logger log = Logger.getInstance(YarnSpinnerPreloadingActivity.class);

    private static final String lspDllRelativePath = "Server/YarnLanguageServer.dll";

    public void preload(ProgressIndicator indicator) {

        indicator.setFraction(.1);
        String dllPath = getResourcePath(lspDllRelativePath);

        if (dllPath == null) {
            log.error("Could not find " + lspDllRelativePath + " which should have been bundled in the plugin.");
            return;
        }
        RawCommandServerDefinition server = new RawCommandServerDefinition("yarn",
                new String[]{"\"dotnet\"", dllPath});
                indicator.setFraction(.15);
        IntellijLanguageClient.addServerDefinition(server);

    }

    private String getResourcePath(String relativePath) {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            Enumeration<URL> allResources = classLoader.getResources("");
            while(allResources.hasMoreElements()){
                URL url = allResources.nextElement();
                log.debug("resource found: " + url.toString());
            }
            URL resourceUrl = classLoader.getResource(relativePath);
            if (resourceUrl == null) {
                return null;
            }
            log.info("Found requested resource: " + resourceUrl.getFile());
            return resourceUrl.getFile();
        } catch (IOException e){
            log.warn("Failed to read resources: " + e.getMessage() + "\n" + e.getStackTrace());
            return null;
        }
    }
}
