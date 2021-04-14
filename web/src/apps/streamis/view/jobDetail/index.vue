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
        name="jobConfigure"
        :label="$t('message.streamis.moduleName.jobConfigure')"
      >
        <coreIndex/>
      </TabPane>
      <TabPane
        name="jobDetail"
        :label="$t('message.streamis.moduleName.jobDetail')"
      >
        <jobList/>
      </TabPane>
    </Tabs>
  </div>
</template>
<script>
import jobSummary from "@/apps/streamis/module/jobSummary";
import jobHistory from "@/apps/streamis/module/jobHistory";
export default {
  components: {
    jobSummary: jobSummary.component,
    jobHistory: jobHistory.component
  },
  data() {
    return {
      choosedModule: this.$route.params.module || "jobSummary",
      tabs: ["jobSummary", "jobHistory", "jobConfigure", "jobDetail"]
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
