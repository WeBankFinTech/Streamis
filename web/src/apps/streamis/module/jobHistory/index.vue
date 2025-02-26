<template>
  <div class="tableWrap">
    <Table :loading="loading" :columns="columns" :data="tableDatas" border>
      <template slot-scope="{ row }" slot="jobParams">
        <div style="margin-left: 5px" @click="showMetaJson(row)">
          <a href="javascript:void(0)">
            {{ $t('message.streamis.jobHistoryColumns.metaJson') }}
          </a>
        </div>
        <div style="margin-left: 5px" @click="downloadMetaJson(row)">
          <a href="javascript:void(0)">
            {{ $t('message.streamis.jobHistoryColumns.download') }}
          </a>
        </div>
      </template>
      <template slot-scope="{ row }" slot="operation">
        <div style="margin-left: 5px" @click="showVersionInfo(row)">
          <a href="javascript:void(0)">
            {{ $t('message.streamis.jobHistoryColumns.showVersionInfo') }}
          </a>
        </div>
        <div style="margin-left: 5px" @click="showDetail(row)" v-show="false">
          <a href="javascript:void(0)">
            {{ $t('message.streamis.jobHistoryColumns.detail') }}
          </a>
        </div>
        <div style="margin-left: 5px" @click="showLogs(row)">
          <a href="javascript:void(0)">
            {{ $t('message.streamis.jobHistoryColumns.logs') }}
          </a>
        </div>
      </template>
    </Table>
    <Page
      class="page"
      :total="page.total"
      :page-size-opts="page.sizeOpts"
      :page-size="page.size"
      :current="page.current"
      show-sizer
      show-total
      size="small"
      @on-change="change"
      @on-page-size-change="changeSize"/>
    <versionDetail
      :visible="modalVisible"
      :datas="versionDatas"
      :fromHistory="fromHistory"
      @modalCancel="modalCancel"
    />
    <logDetail
      :visible="logVisible"
      :taskId="taskId"
      @modalCancel="modalCancel"
      ref="logDetail"
    />
    <Modal
      :title="metaJsonJobName + ' ' + $t('message.streamis.jobHistoryColumns.jobParams')"
      v-model="metaJsonVisible"
      footer-hide
      width="1200"
      @on-cancel="modalCancel"
      :class="full ? 'full' : ''"
    >
      <div>
        <div class="meta-wrapper">
          <Input
            ref="metaInputRef"
            v-model="meta"
            type="textarea"
            :autosize="{ minRows: 10, maxRows: 15 }"
            readonly
            :placeholder="$t('message.streamis.jobHistoryColumns.noInfo')"
          />
          <span
            class="full-btn"
            @click="fullToggle"
          >
            {{ full ? '> <' : '< >' }}
          </span>
        </div>
        <div class="btnWrap">
          <Button
            type="primary"
            @click="copy"
          >
            {{ $t('message.streamis.jobHistoryColumns.copy') }}
          </Button>
          <Button
            type="primary"
            @click="modalCancel"
            style="margin-left: 30px;"
          >
            {{ $t('message.streamis.jobHistoryColumns.close') }}
          </Button>
        </div>
      </div>
    </Modal>
  </div>
</template>
<script>
import api from '@/common/service/api'
import versionDetail from '@/apps/streamis/module/versionDetail'
import logDetail from '@/apps/streamis/module/logDetail'
export default {
  components: { versionDetail, logDetail },
  props: {
    projectName: {
      type: String,
      default: '',
      required: true
    }
  },
  data() {
    return {
      columns: [
        {
          title: this.$t('message.streamis.jobHistoryColumns.taskId'),
          key: 'taskId'
        },
        {
          title: this.$t('message.streamis.jobHistoryColumns.jobName'),
          key: 'jobName'
        },
        {
          title: this.$t('message.streamis.jobHistoryColumns.creator'),
          key: 'creator'
        },
        {
          title: this.$t('message.streamis.jobHistoryColumns.version'),
          key: 'version'
        },
        {
          title: this.$t('message.streamis.jobHistoryColumns.status'),
          key: 'status'
        },
        {
          title: this.$t('message.streamis.jobHistoryColumns.startTime'),
          key: 'startTime'
        },
        {
          title: this.$t('message.streamis.jobHistoryColumns.endTime'),
          key: 'endTime'
        },
        {
          title: this.$t('message.streamis.jobHistoryColumns.runTime'),
          key: 'runTime'
        },
        {
          title: this.$t('message.streamis.jobHistoryColumns.stopCause'),
          key: 'stopCause',
          tooltip: true
        },
        {
          title: this.$t('message.streamis.jobHistoryColumns.jobParams'),
          key: 'jobParams',
          slot: 'jobParams'
        },
        {
          title: this.$t('message.streamis.jobHistoryColumns.operation'),
          key: 'operation',
          slot: 'operation'
        }
      ],
      tableDatas: [
        // {
        //   id: 10,
        //   name: 'flinkJarTestc',
        //   workspaceName: null,
        //   projectName: 'flinkJarTest3',
        //   jobType: 'flink.jar',
        //   label: 'e,t,y,h,g',
        //   createBy: 'hdfs',
        //   createTime: 1636338541000,
        //   status: "running",
        //   version: 'v00002',
        //   lastVersionTime: 1636339360000,
        //   description: '这是一个FlinkJar测试Job3'
        // }
      ],
      modalVisible: false,
      logVisible: false,
      taskId: 0,
      versionDatas: [],
      jobId: this.$route.params.id,
      fromHistory: true,
      page: {
        current: 1,
        size: 10,
        sizeOpts: [10, 20, 30, 50],
        total: 0,
      },
      loading: false,
      metaJsonVisible: false,
      metaJsonJobName: '',
      meta: '',
      full: false,
    }
  },
  mounted() {
    this.getDatas()
  },
  methods: {
    getDatas() {
      const { id, version, lastVersion, status } = this.$route.params
      const useVersion = [5, 8, 9].includes(status) ? version : lastVersion
      const queries = `?jobId=${id}&version=${useVersion || version}&pageNow=${this.page.current}&pageSize=${this.page.size}`
      this.loading = true
      api
        .fetch('streamis/streamJobManager/job/execute/history' + queries, 'get')
        .then(res => {
          if (res && res.details) {
            this.tableDatas = res.details
            this.page.total = res.totalPage
          }
          this.loading = false
        })
        .catch(e => {
          console.log(e)
          this.loading = false
        })
    },
    change(page) {
      this.page.current = page;
      this.getDatas();
    },
    changeSize(size) {
      this.page.size = size;
      if(this.page.current === 1){
        this.getDatas();
      } else{
        this.page.current = 1;
      }
    },
    showVersionInfo(data) {
      this.loading = true
      api
        .fetch(
          'streamis/streamJobManager/job/version?jobId=' +
            this.jobId +
            '&version=' +
            data.version,
          'get'
        )
        .then(res => {
          if (res) {
            this.loading = false
            this.modalVisible = true
            this.versionDatas = [res.detail]
          }
        })
        .catch(() => {
          this.loading = false
        })
    },
    showDetail() {
      // console.log(row)
    },
    showLogs(row) {
      this.$refs['logDetail'].getDatas(row.taskId)
      this.logVisible = true;
      this.taskId = +row.taskId;
    },
    modalCancel() {
      this.modalVisible = false
      this.logVisible = false
      this.metaJsonVisible = false
    },
    showMetaJson(row) {
      console.log('show meta.json, taskId:', row.taskId)
      this.metaJsonJobName = row.jobName
      this.meta = row.jobStartConfig ? this.formatJSON(row.jobStartConfig) : ''
      this.metaJsonVisible = true
    },
    downloadMetaJson(row) {
      if (!row.jobStartConfig) {
        this.$Message.error({ content: 'Job参数为空！' })
        return
      }
      const blob = new Blob([row.jobStartConfig], { type: 'application/json' })
      const url = URL.createObjectURL(blob);

      const a = document.createElement('a');
      a.href = url;
      a.download = `${this.projectName}.${row.jobName}-${row.version}-meta.json`;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);

      // 下载完成后移除 <a> 标签
      document.body.removeChild(a);
    },
    formatJSON(text) {
      try {
        const jsonObj = JSON.parse(text)
        const formatJSON = JSON.stringify(jsonObj, null, 2)
        return formatJSON
      } catch (error) {
        return text
      }
    },
    async copy(){
      try {
        console.log('copy', this.meta)
        if (this.meta === '') {
          this.$Message.error({ content: 'Job参数为空！' })
          return
        }
        const textArea = document.createElement("textarea")
        textArea.value = this.meta.replace(/\n/g, '')
        document.body.append(textArea)
        textArea.select()
        document.execCommand('copy')
        document.body.removeChild(textArea)
      } catch (error) {
        console.log(error)
      }
    },
    fullToggle() {
      this.full = !this.full
    },
  }
}
</script>
<style lang="scss" scoped>
.btnWrap {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
}
.meta-wrapper{
  position: relative;
  .full-btn{
    position: absolute;
    cursor: pointer;
    top: 5px;
    right: 8px;
    font-weight: bold;
  }
}
.full {
  /deep/.ivu-modal{
    width: 100vw !important;
    height: 100vh;
    min-height: 430px;
    top:0;
    .ivu-modal-content{
      height: 100%;
    }
  }
  /deep/textarea{
   height: calc(100vh - 200px) !important;
   min-height: 200px;
   max-height: calc(100vh - 200px) !important;
  }
}</style>
