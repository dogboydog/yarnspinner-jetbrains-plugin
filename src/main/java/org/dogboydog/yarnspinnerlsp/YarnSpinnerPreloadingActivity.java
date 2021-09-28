package org.dogboydog.yarnspinnerlsp;

import com.intellij.openapi.application.PreloadingActivity;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import org.dogboydog.yarnspinnerlsp.server.YarnSpinnerServerDefinition;
import org.wso2.lsp4intellij.IntellijLanguageClient;
import org.wso2.lsp4intellij.requests.Timeouts;

public class YarnSpinnerPreloadingActivity extends PreloadingActivity {
    private static final Logger log = Logger.getInstance(YarnSpinnerPreloadingActivity.class);

    private static final int initTimeout = 1500000;

    public void preload(ProgressIndicator indicator) {
        var server = new YarnSpinnerServerDefinition();
        IntellijLanguageClient.addServerDefinition(server);
        IntellijLanguageClient.setTimeout(Timeouts.INIT, initTimeout);
    }


}
