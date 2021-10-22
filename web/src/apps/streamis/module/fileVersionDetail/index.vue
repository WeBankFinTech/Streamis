<template>
  <div>
    <Modal
      :title="$t('message.streamis.versionDetail.modalTitle')"
      v-model="visible"
      footer-hide
      width="1200"
      @on-cancel="cancel"
    >
      <Table :columns="columns" :data="datas" border>
        <template slot-scope="{ row }" slot="operation">
          <div>
            <a
              :href="
                `/api/rest_j/v1/streamis/streamProjectManager/project/files/download?id=${row.id}`
              "
              download
            >
              <Button
                type="primary"
                style="width:55px;height:22px;background:rgba(22, 155, 213, 1);margin-right: 5px"
              >
                {{ $t('message.streamis.projectFile.download') }}
              </Button>
            </a>
          </div>
        </template>
      </Table>
    </Modal>
  </div>
</template>
<script>
import table from '../../../../components/table/table.vue'
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
          title: this.$t('message.streamis.jobListTableColumns.version'),
          key: 'version'
        },
        {
          title: this.$t('message.streamis.projectFile.createBy'),
          key: 'createBy'
        },
        {
          title: this.$t('message.streamis.projectFile.versionDescription'),
          key: 'comment'
        },
        {
          title: this.$t('message.streamis.projectFile.createTime'),
          key: 'createTime'
        },
        {
          title: this.$t('message.streamis.jobListTableColumns.operation'),
          key: 'operation',
          slot: 'operation'
        }
      ]
    }
  },
  methods: {
    showVersionInfo(row) {
      console.log(row)
    },
    showDetail(row) {
      console.log(row)
    },
    showLogs(row) {
      console.log(row)
    },
    ok() {
      this.$Message.info('Clicked ok')
    },
    cancel() {
      this.$emit('modalCancel')
    }
  }
}
</script>
<style lang="scss" scoped></style>
