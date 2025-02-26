<template>
  <div class="tableWrap">
    <Table :loading="loading" :columns="columns" :data="tableDatas" border>
      <template slot-scope="{ row }" slot="operation">
        <div style="margin-left: 5px" @click="showVersionInfo(row)">
          <a href="javascript:void(0)">
            {{ $t('message.streamis.jobHistoryColumns.showVersionInfo') }}
          </a>
        </div>
        <div
          style="margin-left: 5px"
          @click="showDetail(row)"
          v-show="row.status === 'running'"
        >
          <a href="javascript:void(0)">
            {{ $t('message.streamis.jobHistoryColumns.detail') }}
          </a>
        </div>
        <div
          style="margin-left: 5px"
          @click="showLogs(row)"
          v-show="row.status !== 'running'"
        >
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
  </div>
</template>
<script>
import api from '@/common/service/api'
import moment from 'moment'
export default {
  data() {
    return {
      columns: [
        {
          title: 'id',
          key: 'id'
        },
        {
          title: this.$t('message.streamis.versionDetail.createTime'),
          key: 'createTime'
        },
        {
          title: this.$t('message.streamis.jobConfig.formItems.alertLevel'),
          key: 'alertLevel'
        },
        // {
        //   title: this.$t('message.streamis.jobConfig.formItems.alertUser'),
        //   key: 'alertUser'
        // },
        {
          title: this.$t('message.streamis.jobAlert.alertMsg'),
          key: 'alertMsg'
        },
        {
          title: this.$t('message.streamis.jobAlert.errorMsg'),
          key: 'errorMsg'
        }
      ],
      tableDatas: [
        // {
        //   id: 1,
        //   jobId: 55,
        //   alertLevel: 'critical',
        //   alertUser: 'johnnwang',
        //   alertMsg: '请求LinkisManager失败，Linkis集群出现异常，请关注！',
        //   jobVersionId: 96,
        //   taskId: 214,
        //   createTime: 1636646400000,
        //   status: 0,
        //   errorMsg: null
        // }
      ],
      page: {
        current: 1,
        size: 10,
        sizeOpts: [10, 20, 30, 50],
        total: 0,
      },
      loading: false,
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
        .fetch('streamis/streamJobManager/job/alert' + queries, 'get')
        .then(res => {
          if (res && res.list) {
            const results = res.list
            results.forEach(item => {
              if (item.createTime) {
                const newDate = moment(new Date(item.createTime)).format(
                  'YYYY-MM-DD HH:mm:ss'
                )
                item.createTime = newDate
              }
            })
            this.tableDatas = results
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
  }
}
</script>
<style lang="scss" scoped>
.page {
  margin-top: 20px;
}
</style>
