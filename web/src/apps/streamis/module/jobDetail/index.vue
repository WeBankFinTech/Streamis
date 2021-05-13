<template>
  <div>
    <jarDetail v-if="detailName==='jarDetail'" :jarData="data" key="jar"/>
    <workflow v-if="detailName==='workflow'" key="workflow"/>
  </div>
</template>
<script>
import api from "@/common/service/api";
import jarDetail from "@/apps/streamis/module/jarDetail";
import workflow from "@/apps/streamis/module/workflow";
  
export default {
  components: {
    jarDetail: jarDetail.component,
    workflow: workflow.component
  },
  data() {
    return {
      detailName: 'workflow',
      data: {}
    };
  },
  mounted() {
    console.log(this.$route.params);
    this.getDetail();
  },
  methods: {
    getDetail() {
      api
        .fetch(
          "streamis/streamJobManager/job/upload/details?jobId=" +
            this.$route.params.id,
          "get"
        )
        .then(res => {
          console.log(res);
          if (res && res.details) {
            this.detailName = "jarDetail";
            this.data = res.details;

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
.itemWrap {
  padding: 10px;
  & > p {
    font-weight: 700;
    font-size: 16px;
  }
  & > div {
    margin-left: 20px;
    margin-top: 10px;
  }
}
.programArguement{
  background: rgba(94, 94, 94, 1);
  color: #fff;
  padding: 10px 20px;
  min-height: 64px;;
}
</style>
