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
              :label="$t('message.streamis.formItems.submitter')"
              :label-width="120"
            >
              <Select v-model="query.submitter" class="select">
                <Option
                  v-for="(item, index) in submitterOptions"
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
                style="margin-left:80px"
              >
                {{ $t("message.streamis.formItems.queryBtn") }}
              </Button>
            </FormItem>
          </Form>
          <Table :columns="columns" :data="tableDatas">
            <template slot-scope="{ row, index }" slot="jobName">
              <div class="jobName" v-show="index === 0">
                <Upload action="uuu" format="['jar']">
                  <Icon type="md-add" class="upload" />
                  <span>{{
                    $t("message.streamis.jobListTableColumns.upload")
                  }}</span>
                </Upload>
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
            <template slot-scope="{ row, index }" slot="operation">
              <div v-show="index !== 0">
                <Button
                  :type="row.taskStatus !== 'running' ? 'success' : 'error'"
                  size="small"
                  style="margin-right: 5px"
                  @click="handleAction(row)"
                >
                  {{
                    row.taskStatus !== "running"
                      ? $t("message.streamis.formItems.startBtn")
                      : $t("message.streamis.formItems.stopBtn")
                  }}
                </Button>
                <Button type="primary" size="small" @click="handleConfig(row)">
                  {{ $t("message.streamis.formItems.configBtn") }}
                </Button>
                <Button
                  type="success"
                  size="small"
                  style="margin-right: 5px"
                  v-show="row.taskStatus !== 'running'"
                  @click="handleAction(row)"
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
          />
        </div>
      </div>
    </titleCard>
  </div>
</template>
<script>
import api from "@/common/service/api";
import titleCard from "@/apps/streamis/components/titleCard";
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
  components: { titleCard },
  data() {
    return {
      query: {
        jobName: "",
        jobStatus: "all",
        submitter: "all"
      },
      jobStatus: ["all", "running", "failture", "uploaded"],
      submitterOptions: ["all"],

      tableDatas: [
        {
          taskId: "elit",
          jobName: "ex elit",
          taskStatus: -70675600.96517575,
          lastReleaseTime: "tempor nostrud",
          label: "reprehenderit",
          version: "Ut aliqua elit ad",
          lastRelease: "magna quis elit in ad",
          description: "amet labore do"
        }
      ],
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
            return h("div", [
              h(
                "strong",
                {
                  style: {
                    color:
                      params.row.taskStatus === "running"
                        ? "#009900"
                        : "#FF0000"
                  }
                },
                params.row.taskStatus
              )
            ]);
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
          render: (h, params) => {
            return h(
              "span",
              {
                style: {
                  background: params.index === 0 ? "fff" : "#008000",
                  color: "#fff",
                  display: "inline-block",
                  padding: "2px"
                }
              },
              params.row.version
            );
          }
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
        total: 200,
        current: 1,
        pageSize: 20
      }
    };
  },
  mounted() {
    this.getJobList();
  },
  methods: {
    getJobList() {
      const datas = new Array(10).fill(
        {
          taskId: "ipsum laboris ut proiden",
          jobName: "in",
          taskStatus: -31359204.375604272,
          lastReleaseTime: "ea",
          label: "non",
          version: "et occaecat velit",
          lastRelease: "dolor co",
          description: "exercitation nulla et"
        },
        0,
        30
      );
      datas.unshift({});
      this.tableDatas = datas;

      api
        .fetch("api/rest_j/v1/streamis/streamJobManager/job/list", "get")
        .then(res => {
          console.log(res);
        })
        .catch(e => console.log(e));
    },
    handleNameQuery() {
      console.log(this.query.jobName);
    },
    handleQuery() {},
    handleAction(data) {
      console.log(data);
    },
    handleConfig(data) {
      console.log(data);
    },
    handleRouter(rowData, moduleName) {
      console.log(rowData);
      console.log(moduleName);
      const moduleMap = {
        paramsConfiguration: "jobConfigure",
        alertConfiguration: "jobConfigure",
        runningHistory: "jobHistory"
      };
      this.$router.push({
        name: "JobDetail",
        params: {
          id: rowData.taskId.replace(/\s/g, ""),
          module: moduleName
            ? moduleMap[moduleName] || moduleName
            : "jobSummary",
          name: rowData.jobName
        }
      });
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
</style>
