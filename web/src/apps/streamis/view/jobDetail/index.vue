<template>
  <div class="container">
    <Tabs type="card" v-model="choosedModule">
      <TabPane
        name="jobSummary"
        :label="$t('message.streamis.moduleName.jobSummary')"
      >
        <jobSummary/>
      </TabPane>
      <TabPane
        name="jobHistory"
        :label="$t('message.streamis.moduleName.jobHistory')"
      >
        <jobHistory/>
      </TabPane>
      <TabPane
        name="jobConfig"
        :label="$t('message.streamis.moduleName.jobConfig')"
      >
        <jobConfig/>
      </TabPane>
      <TabPane
        name="jobDetail"
        :label="$t('message.streamis.moduleName.jobDetail')"
      >
        <jobDetail/>
      </TabPane>
    </Tabs>
  </div>
</template>
<script>
import jobSummary from "@/apps/streamis/module/jobSummary";
import jobHistory from "@/apps/streamis/module/jobHistory";
import jobConfig from "@/apps/streamis/module/jobConfig";
import jobDetail from "@/apps/streamis/module/jobDetail";
export default {
  components: {
    jobSummary: jobSummary.component,
    jobHistory: jobHistory.component,
    jobDetail: jobDetail.component,
    jobConfig: jobConfig.component,
  },
  data() {
    return {
      choosedModule: this.$route.params.module || "jobSummary",
      tabs: ["jobSummary", "jobHistory", "jobConfig", "jobDetail"]
    };
  },
  mounted() {
    // this.init();
    console.log(this.$route.params);
  },
  methods: {
    resize(height) {
      this.navHeight = height;
    }
  }
};
</script>
<style lang="scss" scoped>
.container {
  padding: 10px 30px 0;
}
.divider {
  height: 30px;
}
</style>
