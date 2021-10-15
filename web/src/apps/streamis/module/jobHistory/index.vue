<template>
  <div class="tableWrap">
    <Table :columns="columns" :data="tableDatas" border>
      <template slot-scope="{ row }" slot="operation">
        <div style="margin-left: 5px" @click="showVersionInfo(row)">
          <a href="javascript:void(0)">
            {{ $t("message.streamis.jobHistoryColumns.showVersionInfo") }}
          </a>
        </div>
        <div style="margin-left: 5px" @click="showDetail(row)" v-show="row.status === 'running'">
          <a href="javascript:void(0)">
            {{ $t("message.streamis.jobHistoryColumns.detail") }}
          </a>
        </div>
        <div style="margin-left: 5px" @click="showLogs(row)" v-show="row.status !== 'running'">
          <a href="javascript:void(0)">
            {{ $t("message.streamis.jobHistoryColumns.logs") }}
          </a>
        </div>
      </template>
    </Table>
  </div>
</template>
<script>
import api from "@/common/service/api";
export default {
  data() {
    return {
      columns: [
        {
          title: this.$t("message.streamis.jobHistoryColumns.taskId"),
          key: "taskId"
        },
        {
          title: this.$t("message.streamis.jobHistoryColumns.jobName"),
          key: "jobName"
        },
        {
          title: this.$t("message.streamis.jobHistoryColumns.creator"),
          key: "creator"
        },
        {
          title: this.$t("message.streamis.jobHistoryColumns.version"),
          key: "version"
        },
        {
          title: this.$t("message.streamis.jobHistoryColumns.status"),
          key: "status"
        },
        {
          title: this.$t("message.streamis.jobHistoryColumns.startTime"),
          key: "startTime"
        },
        {
          title: this.$t("message.streamis.jobHistoryColumns.endTime"),
          key: "endTime"
        },
        {
          title: this.$t("message.streamis.jobHistoryColumns.runTime"),
          key: "runTime"
        },
        {
          title: this.$t("message.streamis.jobHistoryColumns.stopCause"),
          key: "stopCause"
        },
        {
          title: this.$t("message.streamis.jobHistoryColumns.operation"),
          key: "operation",
          slot: "operation"
        }
      ],
      tableDatas: []
    };
  },
  mounted() {
    this.getDatas();
    console.log(this.$route.params)
  },
  methods: {
    getDatas() {
      const {id, version} = this.$route.params || {};
      const queries = `?jobId=${id}&version=${version}`
      api
        .fetch(
          "streamis/streamJobManager/job/execute/history" + queries,
          "get"
        )
        .then(res => {
          if(res && res.details){
            this.tableDatas = res.details;
          }
        })
        .catch(e => console.log(e));
    },
    showVersionInfo(row) {
      console.log(row);
    },
    showDetail(row) {
      console.log(row);
    },
    showLogs(row) {
      console.log(row);
    }
  }
};
</script>
<style lang="scss" scoped>
</style>
