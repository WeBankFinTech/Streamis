<template>
  <div class="container">
    <coreIndex ref="coreIndex"/>
    <div class="divider"/>
    <jobList @refreshCoreIndex="refreshCoreIndex"/>
  </div>
</template>
<script>
import coreIndex from '@/apps/streamis/module/coreIndex';
import jobList from '@/apps/streamis/module/jobList';
import watermark from "@/common/util/waterMark.js";
import { GetBaseInfo } from '@/common/service/apiCommonMethod.js';
import storage from "@/common/helper/storage";
import { isEmpty } from "lodash";
export default {
  components: {
    coreIndex: coreIndex.component,
    jobList: jobList.component
  },
  data() {
    return {
      navHeight: 0,
      projectName: this.$route.query.projectName || (new URLSearchParams(window.location.search)).get('projectName'),
      user: '',
      region: '',
    };
  },
  mounted() {
    if(!this.$route.query.projectName){
      this.$router.push(`/realTimeJobCenter?projectName=${this.projectName}`)
    }
    this.init()
  },
  methods: {
    resize(height) {
      this.navHeight = height;
    },
    refreshCoreIndex(){
      this.$refs.coreIndex.getIndexData();
    },
    async init(){
      try {
        const rst = await GetBaseInfo()
        if (!isEmpty(rst)) {
          storage.set("baseInfo", rst, "local");
          this.user= rst.username
        }
      } catch (error) {
        console.log(error)
      }
      this.region = window.watermarkRegion
      watermark.init({
        maskTxt: `${this.user} ${this.region}`, 
        addTime: true, 
        setIntervalTime: "60000", 
        fontSize: "12px",
        frontXSpace: 150, // 水印横向间隙
        frontYSpace: 100, // 水印纵向间隙
      });
    }
  },
};
</script>
<style lang="scss" scoped>
.container{
  padding: 10px 30px 0;
}
.divider{
  height: 30px;
}
</style>
