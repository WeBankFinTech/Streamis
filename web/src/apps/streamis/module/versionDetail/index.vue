<template>
  <div>
    <Modal
      :title="$t('message.streamis.versionDetail.modalTitle')"
      v-model="visible"
      footer-hide
      width="1200"
    >
      <Table :columns="columns" :data="tableDatas" border/>
    </Modal>
  </div>
</template>
<script>
import table from "../../../../components/table/table.vue";
export default {
  components: { table },
  props: {
    visible: Boolean,
    tableDatas: Array
  },
  data() {
    return {
      columns: [
        {
          title: this.$t("message.streamis.versionDetail.jobId"),
          key: "jobId"
        },
        {
          title: this.$t("message.streamis.versionDetail.version"),
          key: "version"
        },
        {
          title: this.$t("message.streamis.versionDetail.description"),
          key: "description"
        },
        {
          title: this.$t("message.streamis.versionDetail.createTime"),
          key: "createTime"
        },
        {
          title: this.$t("message.streamis.versionDetail.creator"),
          key: "creator"
        },

        {
          title: this.$t("message.streamis.versionDetail.projectID"),
          key: "projectID"
        },
        {
          title: this.$t("message.streamis.versionDetail.dssLabels"),
          key: "dssLabels"
        },
        {
          title: this.$t("message.streamis.versionDetail.resourceId"),
          key: "resourceId"
        },
        {
          title: this.$t("message.streamis.versionDetail.bmlVersion"),
          key: "bmlVersion"
        }
      ],
    };
  },
  methods: {
    showVersionInfo(row) {
      console.log(row);
    },
    showDetail(row) {
      console.log(row);
    },
    showLogs(row) {
      console.log(row);
    },
    ok() {
      this.$Message.info("Clicked ok");
    },
    cancel() {
      this.$Message.info("Clicked cancel");
    }
  }
};
</script>
<style lang="scss" scoped></style>
