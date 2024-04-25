<template>
  <div class="container">
    <coreIndex v-model="hasHook" ref="coreIndex"/>
    <div class="divider"/>
    <jobList :hasHook="hasHook" @refreshCoreIndex="refreshCoreIndex"/>
  </div>
</template>
<script>
import coreIndex from '@/apps/streamis/module/coreIndex';
import jobList from '@/apps/streamis/module/jobList';
import watermark from "@/common/util/waterMark.js";
import storage from "@/common/helper/storage";
import { isEmpty } from "lodash";
import api from '@/common/service/api'
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
      hasHook: false,
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
        const rst = await api.fetch('streamis/streamJobManager/highAvailable/username', 'get')
        if (!isEmpty(rst)) {
          storage.set("username", rst, "local");
          this.user= rst.userName
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
        frontTxtAlpha: 0.2,
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
