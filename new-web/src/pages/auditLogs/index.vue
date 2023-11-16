<template>
    <BTablePage
        :isLoading="isLoading"
        :actionType="actionType"
    >
        <template v-slot:search>
            <BSearch
                v-model:form="searchForm"
                :isAdvance="false"
                @search="handleSearch"
                @reset="handleReset"
            >
                <template v-slot:form>
                    <div>
                        <span class="condition-label">接口名</span>
                        <FInput
                            v-model="searchForm.apiName"
                            placeholder="接口名"
                        >
                        </FInput>
                    </div>
                    <div>
                        <span class="condition-label">实名用户</span>
                        <FInput
                            v-model="searchForm.user"
                            placeholder="实名用户"
                        >
                        </FInput>
                    </div>
                    <div>
                        <span class="condition-label">代理用户</span>
                        <FInput
                            v-model="searchForm.proxyUser"
                            placeholder="代理用户"
                        >
                        </FInput>
                    </div>
                    <div>
                        <span class="condition-label">时间范围</span>
                        <FDatePicker
                            v-model="searchForm.timeRange"
                            class="date-input"
                            type="datetimerange"
                            style="width: 340px;"
                        >
                        </FDatePicker>
                    </div>
                </template>
            </BSearch>
        </template>
        <template v-slot:table>
            <f-table :data="tableData">
                <f-table-column
                    prop="user"
                    label="用户"
                    :width="88"
                    :formatter="formatterEmptyValue"
                    ellipsis
                />
                <f-table-column
                    prop="proxyUser"
                    label="代理用户"
                    :width="88"
                    :formatter="formatterEmptyValue"
                    ellipsis
                />
                <f-table-column
                    prop="apiName"
                    label="接口名"
                    :width="88"
                    :formatter="formatterEmptyValue"
                    ellipsis
                />
                <f-table-column
                    prop="apiDesc"
                    label="接口说明"
                    :width="160"
                    :formatter="formatterEmptyValue"
                    ellipsis
                />
                <f-table-column
                    prop="apiType"
                    label="操作类型"
                    :width="88"
                    :formatter="formatterEmptyValue"
                    ellipsis
                />
                <f-table-column
                    prop="operateTime"
                    label="操作时间"
                    :width="160"
                    :formatter="formatterEmptyValue"
                    ellipsis
                />
                <f-table-column
                    v-slot="{ row }"
                    prop="inputParameters"
                    label="入参"
                    :width="160"
                    ellipsis
                >
                    <SpecialEllipsis
                        v-if="row.inputParameters"
                        :content="row.inputParameters"
                        @showDetail="handleShowParamsDetail($event, row.inputParameters)"
                    />
                    <span v-else>--</span>
                </f-table-column>
                <f-table-column
                    v-slot="{ row }"
                    prop="outputParameters"
                    label="出参"
                    :width="160"
                    ellipsis
                >
                    <SpecialEllipsis
                        v-if="row.outputParameters"
                        :content="row.outputParameters"
                        @showDetail="handleShowParamsDetail($event, row.outputParameters)"
                    />
                    <span v-else>--</span>
                </f-table-column>
            </f-table>
        </template>
        <template v-slot:pagination>
            <div class="pagination-wrapper">
                <FPagination
                    v-model:currentPage="pagination.current"
                    v-model:pageSize="pagination.size"
                    show-size-changer
                    show-total
                    show-quick-jumper
                    :total-count="pagination.total"
                    @change="fetchTableData"
                    @pageSizeChange="fetchTableData"
                />
            </div>
        </template>
    </BTablePage>
    <FModal
        v-model:show="showParamsDetailModal"
        :maskClosable="false"
        title="参数详情"
        :width="600"
        :footer="false"
    >
        <FScrollbar height="50vh">
            <pre v-if="isJson">
                {{paramsModalContent}}
            </pre>
            <div v-else>
                {{paramsModalContent}}
            </div>
        </FScrollbar>
    </FModal>
</template>
<script setup>
import { BTablePage, BSearch, formatterEmptyValue } from '@fesjs/traction-widget';
import {
    FInput, FButton, FTable, FPagination, FSelect, FScrollbar,
} from '@fesjs/fes-design';
import {
    onMounted, ref, reactive, computed,
} from 'vue';
import { PlusOutlined, MoreCircleOutlined } from '@fesjs/fes-design/icon';
import SpecialEllipsis from '@/components/SpecialEllipsis.vue';
import dayjs from 'dayjs';
import { useRoute } from '@fesjs/fes';
import { fetchAuditLog } from './api';

const route = useRoute();
const projectName = computed(() => route.query.projectName);
const pagination = reactive({
    current: 1,
    size: 10,
    total: 0,
});
const searchForm = ref({
    timeRange: [],
});
const isLoading = ref(false);
const actionType = ref('loading');

const tableLists = ref([]);
const tableData = ref([]);

// 获取表格数据
const fetchTableData = async () => {
    try {
        const param = {
            ...searchForm.value,
            pageNow: pagination.current,
            pageSize: pagination.size,
            startDate: searchForm.value.timeRange.length > 1 ? dayjs(searchForm.value.timeRange[0]).format('YYYY-MM-DD HH:mm:ss') : '',
            endDate: searchForm.value.timeRange.length > 1 ? dayjs(searchForm.value.timeRange[1]).format('YYYY-MM-DD HH:mm:ss') : '',
            projectName: projectName.value,
        };
        isLoading.value = true;
        actionType.value = 'loading';
        const res = await fetchAuditLog(param);
        console.log('fetchTableData', res);
        isLoading.value = false;
        tableData.value = res.auditLogs.map(item => ({
            ...item,
            operateTime: item.operateTime ? dayjs(item.operateTime).format('YYYY-MM-DD HH:mm:ss') : '',
        }));
        pagination.total = res.totalPage;
        if (pagination.total === 0) {
            actionType.value = 'emptyQueryResult';
        }
    } catch (error) {
        isLoading.value = false;
        console.log('fetchTableData error', error);
    }
};

// 查询操作
const handleSearch = () => {
    fetchTableData();
};

// 重置操作
const handleReset = () => {
    pagination.current = 1;
    fetchTableData();
};

const isValidJSON = (str) => {
    try {
        JSON.parse(str);
        return true;
    } catch (error) {
        console.log(error);
        return false;
    }
};

const showParamsDetailModal = ref(false);
const paramsModalContent = ref('');
const isJson = ref(false);
const handleShowParamsDetail = (isOverflow, content) => {
    console.log(isOverflow, content);
    if (isOverflow) {
        paramsModalContent.value = content;
        isJson.value = isValidJSON(content);
        console.log(isJson.value);
        showParamsDetailModal.value = true;
    }
};

onMounted(() => {
    fetchTableData();
});
</script>
<style lang="less" scoped >
.pagination-wrapper {
    display: flex;
    justify-content: flex-start;
}

.ellipsis {
    overflow: hidden;
    text-overflow: ellipsis;
}
</style>
