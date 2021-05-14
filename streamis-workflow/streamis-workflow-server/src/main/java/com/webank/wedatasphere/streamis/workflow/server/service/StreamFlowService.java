package com.webank.wedatasphere.streamis.workflow.server.service;

import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import com.webank.wedatasphere.streamis.workflow.common.protocol.StreamFlowCopyRequest;
import com.webank.wedatasphere.streamis.workflow.common.protocol.StreamFlowCreateRequest;
import com.webank.wedatasphere.streamis.workflow.common.protocol.StreamFlowExportRequest;
import com.webank.wedatasphere.streamis.workflow.common.protocol.StreamFlowImportRequest;
import com.webank.wedatasphere.streamis.workflow.server.entity.ExportResponse;
import com.webank.wedatasphere.streamis.workflow.server.exception.StreamisFlowErrorException;

import java.util.List;

/**
 * created by yangzhiyue on 2021/4/19
 * Description:
 */
public interface StreamFlowService {

    DSSFlow createStreamFlow(StreamFlowCreateRequest streamFlowCreateRequest) throws StreamisFlowErrorException;

    ExportResponse exportStreamFlow(StreamFlowExportRequest streamFlowExportRequest) throws StreamisFlowErrorException;

    List<DSSFlow> importStreamFlow(StreamFlowImportRequest streamFlowImportRequest) throws StreamisFlowErrorException;

    void updateStreamFlow() throws StreamisFlowErrorException ;

    void publishStreamFlow() throws StreamisFlowErrorException;

    DSSFlow copyStreamFlow(StreamFlowCopyRequest streamFlowCopyRequest) throws StreamisFlowErrorException;

    void deleteStreamFlow() throws StreamisFlowErrorException;

    DSSFlow getStreamFlow(Long flowId) throws StreamisFlowErrorException;
}
