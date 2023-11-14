import { request as FRequest, request } from '@fesjs/fes';
// 获取审计日志表格数据
export function fetchAuditLog(params = {}) {
    return FRequest('/streamis/streamJobManager/audit/logs', params, {
        method: 'get',
    });
}
