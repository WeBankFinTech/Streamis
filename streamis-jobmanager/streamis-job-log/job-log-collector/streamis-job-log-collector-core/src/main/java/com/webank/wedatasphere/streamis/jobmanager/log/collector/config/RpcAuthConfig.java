package com.webank.wedatasphere.streamis.jobmanager.log.collector.config;


/**
 * Authentication config
 */
public class RpcAuthConfig {
    /**
     * Key of token-code
     */
    private String tokenCodeKey = "Token-Code";

    /**
     * Key of token-user
     */
    private String tokenUserKey = "Token-User";

    /**
     * Token user
     */
    private String tokenUser = System.getProperty("user.name");

    /**
     * Token code
     */
    private String tokenCode = "STREAM-LOG";

    public RpcAuthConfig(){

    }

    public RpcAuthConfig(String tokenCodeKey, String tokenCode, String tokenUserKey, String tokenUser){
        if (null != tokenCodeKey) {
            this.tokenCodeKey = tokenCodeKey;
        }
        if (null != tokenCode){
            this.tokenCode = tokenCode;
        }
        if (null != tokenUserKey){
            this.tokenUserKey = tokenUserKey;
        }
        if (null != tokenUser){
            this.tokenUser = tokenUser;
        }
    }

    public String getTokenCodeKey() {
        return tokenCodeKey;
    }

    public void setTokenCodeKey(String tokenCodeKey) {
        this.tokenCodeKey = tokenCodeKey;
    }

    public String getTokenUserKey() {
        return tokenUserKey;
    }

    public void setTokenUserKey(String tokenUserKey) {
        this.tokenUserKey = tokenUserKey;
    }

    public String getTokenUser() {
        return tokenUser;
    }

    public void setTokenUser(String tokenUser) {
        this.tokenUser = tokenUser;
    }

    public String getTokenCode() {
        return tokenCode;
    }

    public void setTokenCode(String tokenCode) {
        this.tokenCode = tokenCode;
    }

    @Override
    public String toString() {
        return "RpcAuthConfig{" +
                ", tokenUserKey='" + tokenUserKey + '\'' +
                ", tokenUser='" + tokenUser + '\'' +
                '}';
    }
}
