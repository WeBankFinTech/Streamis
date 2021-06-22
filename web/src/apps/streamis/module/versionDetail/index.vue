<template>
  <div>
    <Modal
      :title="$t('message.streamis.versionDetail.modalTitle')"
      v-model="visible"
      footer-hide
      width="1200"
      @on-cancel="cancel"
    >
      <Table :columns="columns" :data="datas" border/>
    </Modal>
  </div>
</template>
<script>
import table from "../../../../components/table/table.vue";
export default {
  components: { table },
  props: {
    visible: Boolean,
    datas: Array
  },
  data() {
    return {
      columns: [
        {
          title: this.$t("message.streamis.versionDetail.jobId"),
          key: "id"
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
          key: "releaseTime"
        },
        {
          title: this.$t("message.streamis.versionDetail.creator"),
          key: "createBy"
        },

        {
          title: this.$t("message.streamis.versionDetail.projectID"),
          key: "projectId"
        },
        {
          title: this.$t("message.streamis.versionDetail.dssLabels"),
          key: "dssEnv"
        },
        {
          title: this.$t("message.streamis.versionDetail.resourceId"),
          key: "bmlId"
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
      this.$emit('modalCancel');
    }
  }
};
</script>
<style lang="scss" scoped></style>
