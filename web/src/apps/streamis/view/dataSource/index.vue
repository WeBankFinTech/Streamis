<template>
  <div class="dataSource">
    <div class="header">
      <Button type="primary" @click="goToRealTime">实时数据源</Button>
    </div> 
    <div class="container"> 
      <!--子组件A -->
      <treeSource @goTableFun="goTablePanel" />
      <div class="rightContainer"> 
        <datasourceToolbar/>
        <template v-if="!nodeNameValue">
          <dataSourceInit/>
        </template>
        <template v-else>
          <!--子组件B -->
          <sourceTablePanel :fieldsListInfo="nodeNameValue"/>
        </template>
      </div>
    </div>
  </div>
</template>
<script>
import treeSource from '@/apps/streamis/module/treeSource';
import dataSourceInit from '@/apps/streamis/module/dataSourceInit';
import datasourceToolbar from '@/apps/streamis/module/datasourceToolbar';
import sourceTablePanel from '@/apps/streamis/module/sourceTablePanel';
export default {
  components: {
    sourceTablePanel: sourceTablePanel,
    treeSource: treeSource,
    dataSourceInit: dataSourceInit,
    datasourceToolbar: datasourceToolbar,
  },
  data() {
    return {
      nodeNameValue: '',
      navHeight: 0,
    };
  },
  mounted() {
  },
  methods: {
    // 父组件
    goTablePanel(nodeNameValue) {
      this.nodeNameValue = nodeNameValue
      console.log(this.nodeNameValue,'子组件传过来的值')
    },
    goToRealTime(){
    // this.$router.push({path:'/realTimeDataSource'})
    },
    resize(height) {
      this.navHeight = height;
    },
  },
};
</script>
<style lang="scss" scoped>
.dataSource{
   padding: 10px 30px 0px;
.container{
  display: flex;
  .rightContainer{
    width: 1167px;
    display: flex;
    flex-direction: column;
  }
}
 .header{
    width: 2000px;
    height: 115px;
    background-color: rgba(242, 242, 242, 1);
  }
}
</style>
