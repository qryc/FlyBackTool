package backtool.domain;


import backtool.service.compare.DigestCalculate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qryc on 2020/3/7.
 */
public class LocalConfig {
    private boolean betaOpen = false;
    private DownConfig downConfig ;


    public static record DownConfig(String remoteUrl,String localDir) {
    }

    public boolean isBetaOpen() {
        return betaOpen;
    }

    public LocalConfig setBetaOpen(boolean betaOpen) {
        this.betaOpen = betaOpen;
        return this;
    }

    public DownConfig getDownConfig() {
        return downConfig;
    }

    public LocalConfig setDownConfig(DownConfig downConfig) {
        this.downConfig = downConfig;
        return this;
    }


}
