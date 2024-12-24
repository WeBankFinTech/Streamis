<template>
  <div class="coreWrap">
    <titleCard :title="$t('message.streamis.moduleName.coreIndex')">
      <div class="cardWrap">
        <Card
          v-for="(item, index) in indexItems"
          :key="index"
          style="margin-left: 50px; margin-top: 10px;"
        >
          <div class="cardInner">
            <Icon
              v-show="item.name !== 'running'"
              :type="item.icon"
              size="26"
              :color="item.color"
            />
            <div class="img" v-show="item.name === 'running'">
              <img
                src="../../assets/images/u3951.png"
                :alt="$t('message.streamis.jobStatus.running')"
              />
            </div>

            <p :style="{ color: item.color, 'font-size': '18px' }">
              {{ item.num || 0 }}
            </p>
            <p>{{ $t(`message.streamis.jobStatus.${item.name}`) }}</p>
          </div>
        </Card>
      </div>
    </titleCard>
    <div class="projectFile" @click="gotoProjectFiles()">
      <Icon type="md-folder-open" size="18" />
      <p>{{ $t('message.streamis.routerName.projectResourceFiles') }}</p>
    </div>
    <div class="auditLogs" @click="gotoAuditLogs()">
      <Icon type="md-paper" size="18" />
      <p>查看操作日志</p>
    </div>
  </div>
</template>
<script>
import api from '@/common/service/api'
import titleCard from '@/apps/streamis/components/titleCard'
import { jobStatuses } from '@/apps/streamis/common/common'
export default {
  components: { titleCard },
  props: ['hasHook'],
  data() {
    return {
      indexItems: [...jobStatuses]
    }
  },
  mounted() {
    this.getIndexData()
  },
  methods: {
    getIndexData() {
      api
        .fetch(
          `streamis/streamJobManager/project/core/target?projectName=${this.$route.query.projectName || null}`,
          'get'
        )
        .then(res => {
          if (res && res.taskCore) {
            const newDatas = []
            this.indexItems.forEach(item => {
              const newItem = { ...item }
              newItem.num = res.taskCore[`${newItem.name}Num`] || 0
              newDatas.push(newItem)
            })
            this.indexItems = newDatas
            const hasHook = Array.isArray(res.taskCore.jobShutdownHooks) && res.taskCore.jobShutdownHooks.length > 0
            this.$emit('input', hasHook)
          }
        })
        .catch(e => console.log(e))
    },
    gotoProjectFiles() {
      this.$router.push({
        name: 'ProjectResourceFiles',
        params: {
          projectName: this.$route.query.projectName
        }
      })
    },
    gotoAuditLogs() {
      this.$router.push({
        name: 'AuditLogs',
        params: {
          projectName: this.$route.query.projectName
        }
      })
    }
  }
}
</script>
<style lang="scss" scoped>
.coreWrap {
  position: relative;
}
.projectFile {
  position: absolute;
  top: 16px;
  right: 25px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  font-size: 12px;
  color: #2d8cf0;
  cursor: pointer;
}
.auditLogs {
  position: absolute;
  top: 40px;
  right: 25px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  font-size: 12px;
  color: #2d8cf0;
  cursor: pointer;
}
.cardWrap {
  display: flex;
  flex-wrap: wrap;
}
.cardInner {
  display: flex;
  min-width: 86px;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  & p {
    text-align: center;
  }
}
.img {
  width: 20px;
  & img {
    width: 100%;
  }
}
</style>
