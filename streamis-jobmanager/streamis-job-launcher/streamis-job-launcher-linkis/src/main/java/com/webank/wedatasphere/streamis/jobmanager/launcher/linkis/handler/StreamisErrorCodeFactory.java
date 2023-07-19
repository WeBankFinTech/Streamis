package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.handler;



public class StreamisErrorCodeFactory {

  private static final StreamisErrorCodeHandler DEFAULT_ERRORCODE_HANDLER = StreamisErrorCodeHandler.getInstance();

  @Deprecated
  public static StreamisErrorCodeHandler getDefaultErrorcodeHandler() {
    return DEFAULT_ERRORCODE_HANDLER;
  }
}
