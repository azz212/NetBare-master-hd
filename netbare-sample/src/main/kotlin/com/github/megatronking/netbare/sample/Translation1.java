package com.github.megatronking.netbare.sample;

import java.util.List;

/**
 * Created by Carson_Ho on 17/3/21.
 */
public class Translation1 {

    private String type;
    private int errcode;
    private String edata;

    private List<List<TranslateResultBean>> translateResult;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getErrorCode() {
        return errcode;
    }

    public void setErrorCode(int errorCode) {
        this.errcode = errcode;
    }

    public String getEdata() {
        return edata;
    }

    public void setEdata(String edata) {
        this.edata = edata;
    }

    public List<List<TranslateResultBean>> getTranslateResult() {
        return translateResult;
    }

    public void setTranslateResult(List<List<TranslateResultBean>> translateResult) {
        this.translateResult = translateResult;
    }

    public static class TranslateResultBean {
        /**
         * src : merry me
         * tgt : 我快乐
         */

        public String src;
        public String tgt;

        public String getSrc() {
            return src;
        }

        public void setSrc(String src) {
            this.src = src;
        }

        public String getTgt() {
            return tgt;
        }

        public void setTgt(String tgt) {
            this.tgt = tgt;
        }
    }

}
