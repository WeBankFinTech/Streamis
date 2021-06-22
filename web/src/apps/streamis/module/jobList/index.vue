<template>
  <div>
    <titleCard :title="$t('message.streamis.moduleName.jobList')">
      <div>
        <div>
          <Form ref="queryForm" inline>
            <FormItem>
              <Input
                search
                v-model="query.jobName"
                :placeholder="$t('message.streamis.formItems.jobName')"
                @on-click="handleNameQuery"
                @on-enter="handleNameQuery"
              >
              </Input>
            </FormItem>
            <FormItem
              :label="$t('message.streamis.formItems.jobStatus')"
              :label-width="120"
            >
              <Select v-model="query.jobStatus" class="select">
                <Option
                  v-for="(item, index) in jobStatus"
                  :value="item"
                  :key="index"
                >
                  {{ $t("message.streamis.jobStatus." + item) }}
                </Option>
              </Select>
            </FormItem>
            <FormItem
              :label="$t('message.streamis.formItems.jobCreator')"
              :label-width="120"
            >
              <Select v-model="query.jobCreator" class="select">
                <Option
                  v-for="(item, index) in jobCreatorOptions"
                  :value="item"
                  :key="index"
                >
                  {{
                    item === "all"
                      ? $t("message.streamis.jobStatus." + item)
                      : item
                  }}
                </Option>
              </Select>
            </FormItem>

            <FormItem>
              <Button
                type="primary"
                @click="handleQuery()"
                style="width:80px;height:30px;background:rgba(22, 155, 213, 1);margin-left: 80px;"
              >
                {{ $t("message.streamis.formItems.queryBtn") }}
              </Button>
            </FormItem>
          </Form>
          <Table :columns="columns" :data="tableDatas" :loading="loading">
            <template slot-scope="{ row, index }" slot="jobName">
              <div class="jobName" v-show="index === 0" @click="handleUpload()">
                <Icon type="md-add" class="upload" />
                <span>{{
                  $t("message.streamis.jobListTableColumns.upload")
                }}</span>
              </div>
              <div class="jobName" v-show="index !== 0">
                <Dropdown transfer @on-click="name => handleRouter(row, name)">
                  <Icon type="md-more" class="more" />
                  <DropdownMenu slot="list">
                    <DropdownItem
                      v-for="(item, index) in jobMoudleRouter"
                      :name="item"
                      :key="index"
                    >
                      {{ $t("message.streamis.jobMoudleRouter." + item) }}
                    </DropdownItem>
                  </DropdownMenu>
                </Dropdown>
                <div style="margin-left: 5px" @click="handleRouter(row)">
                  <a href="javascript:void(0)">{{ row.jobName }} </a>
                </div>
              </div>
            </template>
            <template slot-scope="{ row, index }" slot="version">
              <div
                class="version"
                v-show="index !== 0"
                @click="versionDetail(row)"
              >
                {{ row.version }}
              </div>
            </template>
            <template slot-scope="{ row, index }" slot="operation">
              <div v-show="index !== 0">
                <Button
                  type="primary"
                  v-show="row.taskStatus !== 5"
                  style="width:60px;height:22px;background:#008000;margin-right: 5px"
                  @click="handleAction(row)"
                >
                  {{ $t("message.streamis.formItems.startBtn") }}
                </Button>
                <Button
                  type="primary"
                  v-show="row.taskStatus === 5"
                  style="width:60px;height:22px;background:#ff0000;margin-right: 5px"
                  @click="handleAction(row)"
                >
                  {{ $t("message.streamis.formItems.stopBtn") }}
                </Button>
                <Button
                  type="primary"
                  @click="handleRouter(row, 'jobConfig')"
                  style="width:60px;height:22px;background:rgba(22, 155, 213, 1);margin-right: 5px;"
                >
                  {{ $t("message.streamis.formItems.configBtn") }}
                </Button>
                <br />
                <Button
                  type="primary"
                  v-show="row.taskStatus !== 'running'"
                  @click="handleAction(row)"
                  style="width:125px;height:24px;background:#008000;margin-right: 5px;margin-top:2px;"
                >
                  checkpoint
                </Button>
              </div>
            </template>
          </Table>
          <Page
            :total="pageData.total"
            class="page"
            :page-size="pageData.pageSize"
            show-total
            show-elevator
            show-sizer
            @on-change="handlePageChange"
            @on-page-size-change="handlePageSizeChange"
          />
        </div>
      </div>
    </titleCard>
    <versionDetail :visible="modalVisible" :datas="versionDatas" @modalCancel="modalCancel"/>
    <uploadJobJar :visible="uploadVisible" @jarModalCancel="jarModalCancel" @jarUploadSuccess="jarUploadSuccess"/>
  </div>
</template>
<script>
import api from "@/common/service/api";
import titleCard from "@/apps/streamis/components/titleCard";
import versionDetail from "@/apps/streamis/module/versionDetail";
import uploadJobJar from "@/apps/streamis/module/uploadJobJar";
import { jobStatuses } from "@/apps/streamis/common/common";

/**
 * 渲染特殊表头
 */
function renderSpecialHeader(h, params) {
  return h("div", [
    h("strong", params.column.title),
    h(
      "span",
      {
        style: {
          color: "red"
        }
      },
      "*"
    )
  ]);
}

export default {
  components: { titleCard, versionDetail, uploadJobJar },
  data() {
    return {
      query: {
        jobName: "",
        jobStatus: "all",
        jobCreator: "all"
      },
      jobStatus: ["all"].concat(jobStatuses.map(item => item.name)),
      jobCreatorOptions: ["all"],

      tableDatas: [{}],
      columns: [
        {
          title: this.$t("message.streamis.jobListTableColumns.jobName"),
          key: "jobName",
          renderHeader: renderSpecialHeader,
          slot: "jobName"
        },
        {
          title: this.$t("message.streamis.jobListTableColumns.taskStatus"),
          key: "taskStatus",
          renderHeader: renderSpecialHeader,
          render: (h, params) => {
            const hitStatus = jobStatuses.find(
              item => item.code === params.row.taskStatus
            );
            if (hitStatus) {
              return h("div", [
                h(
                  "strong",
                  {
                    style: {
                      color: hitStatus.color
                    }
                  },
                  this.$t("message.streamis.jobStatus." + hitStatus.name)
                )
              ]);
            }
          }
        },
        {
          title: this.$t(
            "message.streamis.jobListTableColumns.lastReleaseTime"
          ),
          key: "lastReleaseTime",
          renderHeader: renderSpecialHeader
        },
        {
          title: this.$t("message.streamis.jobListTableColumns.label"),
          key: "label",
          renderHeader: renderSpecialHeader,
          render: (h, params) => {
            return h(
              "span",
              {
                style: {
                  background:
                    params.index === 0 ? "fff" : "rgba(204, 204, 255, 1)",
                  display: "inline-block",
                  padding: "2px"
                }
              },
              params.row.label
            );
          }
        },
        {
          title: this.$t("message.streamis.jobListTableColumns.version"),
          key: "version",
          slot: "version"
        },
        {
          title: this.$t("message.streamis.jobListTableColumns.lastRelease"),
          key: "lastRelease",
          renderHeader: renderSpecialHeader
        },
        {
          title: this.$t("message.streamis.jobListTableColumns.description"),
          key: "description"
        },
        {
          title: this.$t("message.streamis.jobListTableColumns.operation"),
          key: "operation",
          slot: "operation"
        }
      ],
      jobMoudleRouter: [
        "paramsConfiguration",
        "alertConfiguration",
        "runningHistory",
        "runningLogs"
      ],
      pageData: {
        total: 0,
        current: 1,
        pageSize: 20
      },
      loading: false,
      modalVisible: false,
      versionDatas: [],
      uploadVisible: false,
    };
  },
  mounted() {
    this.getJobList();
  },
  methods: {
    getJobList() {
      if (this.loading) {
        return;
      }
      this.loading = true;
      const { current, pageSize } = this.pageData;
      const params = { projectId: 1, pageNow: current, pageSize };
      const { jobName, jobStatus, jobCreator } = this.query;
      if (jobName) {
        params.jobName = jobName;
      }
      if (jobStatus !== "all") {
        const hitStatus = jobStatuses.find(item => item.name === jobStatus);
        params.jobStatus = hitStatus.code;
      }
      if (jobCreator !== "all") {
        params.jobCreator = jobCreator;
      }

      const queries = Object.entries(params)
        .filter(item => !!item[1])
        .map(item => item.join("="))
        .join("&");
      api
        .fetch("streamis/streamJobManager/job/list?" + queries, "get")
        .then(res => {
          console.log(res);
          if (res) {
            const datas = res.tasks || [];
            datas.unshift({});
            this.tableDatas = datas;
            this.pageData.total = parseInt(res.totalPage);
            this.loading = false;
          }
        })
        .catch(e => {
          console.log(e);
          this.loading = false;
        });
    },
    handleNameQuery() {
      console.log(this.query.jobName);
    },
    handleQuery() {
      this.pageData.current = 1;
      this.getJobList();
    },
    handleUpload(){
      this.uploadVisible = true;
    },
    handleAction(data) {
      console.log(data);
      const { taskStatus, jobId } = data;
      const path =
        taskStatus === 5
          ? "streamis/streamJobManager/job/stop?jobId=" + jobId
          : "streamis/streamJobManager/job/execute";
      const second = taskStatus === 5 ? "get" : { jobId };
      api
        .fetch(path, second)
        .then(res => {
          console.log(res);
          if (res) {
            this.loading = false;
            this.getJobList();
          }
        })
        .catch(e => {
          console.log(e);
          this.loading = false;
        });
    },
    handleConfig(data) {
      console.log(data);
    },
    handleRouter(rowData, moduleName) {
      console.log(rowData);
      console.log(moduleName);
      const moduleMap = {
        paramsConfiguration: "jobConfig",
        alertConfiguration: "jobConfig",
        runningHistory: "jobHistory",
        runningLogs: "jobHistory"
      };
      this.$router.push({
        name: "JobDetail",
        params: {
          id: rowData.jobId,
          module: moduleName
            ? moduleMap[moduleName] || moduleName
            : "jobSummary",
          name: rowData.jobName,
          version: rowData.version,
          taskStatus: rowData.taskStatus
        }
      });
    },
    handlePageChange(page) {
      console.log(page);
      this.pageData.current = page;
      this.getJobList();
    },
    handlePageSizeChange(pageSize) {
      console.log(pageSize);
      this.pageData.pageSize = pageSize;
      this.pageData.current = 1;
      this.getJobList();
    },
    versionDetail(data) {
      console.log(data);
      this.loading = true;
      api
        .fetch(
          "streamis/streamJobManager/job/version?jobId=" +
            data.jobId +
            "&version=" +
            data.version,
          "get"
        )
        .then(res => {
          console.log(res);
          if (res) {
            this.loading = false;
            this.modalVisible = true;
            this.versionDatas = [res.detail];
            // this.versionDatas = [
            //   {
            //     id: 1,
            //     version: "v00001",
            //     description: "test",
            //     releaseTime: "2021-03-01 00:00:00",
            //     createBy: "johnnwang",
            //     projectId: 1,
            //     bmlId: 1111,
            //     bmlVersion: "v000001",
            //     dssEnv: ""
            //   }
            // ];
          }
        })
        .catch(e => {
          console.log(e);
          this.loading = false;
        });
    },
    modalCancel(){
      this.modalVisible = false;
    },
    jarModalCancel(){
      this.uploadVisible = false;
    },
    jarUploadSuccess(){
      this.getJobList();
    }
  }
};
</script>
<style lang="scss" scoped>
.select {
  width: 200px;
}
.jobName {
  display: flex;
  cursor: pointer;
}
.more {
  font-size: 20px;
}
.upload {
  font-size: 24px;
}
.page {
  margin-top: 20px;
}
.version {
  background-color: #008000;
  width: 30px;
  text-align: center;
  color: #ffffff;
  font-size: 16px;
  cursor: pointer;
}
</style>
