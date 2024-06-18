<template>
  <div class="page">
    <div class="navWrap">
      <div @click="jumpToCenter()" class="center">
        <Icon type="ios-home" size="20" />
        <span>{{ $t('message.streamis.routerName.realTimeJobCenter') }}</span>
      </div>
      <div class="slash">/</div>
      <div class="name">
        操作日志
      </div>
    </div>
    <titleCard style="position: relative;" title="操作日志列表">
      <Spin fix v-if="loading" />
      <iframe ref="myIframe" class="iframe" :src="'/streamis-next/#/auditLogs' + '?projectName=' + projectName"></iframe>
    </titleCard>
  </div>
</template>
<script>
import titleCard from '@/apps/streamis/components/titleCard'
export default {
  components: { titleCard },
  data() {
    return {
      projectName: this.$route.params.projectName,
      loading: true
    }
  },
  methods: {
    jumpToCenter() {
      this.$router.push({
        name: 'RealTimeJobCenter',
        query: {
          projectName: this.projectName
        }
      })
    },
  },
  mounted() {
    const iframe = this.$refs.myIframe;
    iframe.onload = () => {
      this.loading = false
    }
  }
}
</script>
<style lang='scss' scoped>
.page {
  padding: 10px 30px 0;
  height: 100%;
}

.navWrap {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  font-size: 14px;
  height: 60px;

  .center {
    cursor: pointer;
  }

  .slash {
    padding: 0 10px;
  }

  .name {
    font-weight: 700;
  }
}

.iframe {
  width: 100%;
  border: 0;
  height: calc(100vh - 200px);
}
</style>