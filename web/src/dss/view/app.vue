<template>
  <div class="layout">
    <router-view/>
  </div>
</template>
<script>
import watermark from "@/common/util/waterMark.js";
export default {
  name: 'App',
  data(){
    return{
      user: '',
      region: '',
    }
  },
  mounted(){
    const baseInfo = JSON.parse(localStorage.getItem('baseInfo'))
    this.user= baseInfo.username
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
};
</script>
<style src="@/dss/assets/styles/app.scss" lang="scss"></style>