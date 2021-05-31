<template>
  <div class="right-container">
    <div class="panel-table-data">
      <div class="panel-color">
        <img class="icon" src="../../assets/images/u2618.png" />
        <div class="text">
          <span>数据源</span>
        </div>
        <img src="../../assets/images/u2616.png" />
      </div>
      <div class="panel-pg"></div>
    </div>
    <div class="datasource">
      <div class="data-title">
        <span style="color: #000000">数据源</span>
        <span style="color: #ff0000">*</span>
      </div>
      <div class="data-text">
        <span>Kafka - 集群1：</span>
        <span>topic_test1</span>
      </div>
    </div>
    <div class="panel-table-data">
      <div class="panel-color">
        <img class="icon" src="../../assets/images/u2618.png" />
        <div class="text">
          <span>表字段</span>
        </div>
        <img src="../../assets/images/u2616.png" />
      </div>
      <div class="panel-pg"></div>
    </div>
    <tableFieldsList :fieldsListInfo="nodeNameValue"/>
    <div class="panel-table-data">
      <div class="panel-color">
        <img class="icon" src="../../assets/images/u2618.png" />
        <div class="text">
          <span>表信息</span>
        </div>
        <img src="../../assets/images/u2616.png" />
      </div>
      <div class="panel-pg"></div>
    </div>
    <tableInfo />
  </div>
</template>

<script>
import tableFieldsList from '@/apps/streamis/module/tableFieldsList';
import tableInfo from '@/apps/streamis/module/tableInfo';
export default {
  props: ["fieldsListInfo"],
  data(){
    return {
      nodeNameValue: this.fieldsListInfo
    }
  },
  watch: {
    fieldsListInfo(){
      this.nodeNameValue = this.fieldsListInfo; 
    }
  },
  mounted(){
    // this.nodeNameValue = this.fieldsListInfo; 
  },
  components: {
    tableFieldsList: tableFieldsList.component,
    tableInfo: tableInfo
  },
}
</script>

<style lang="scss">
.right-container{
  width: 1167px;
  height: 572px;
  border-width: 0px;
  border-left: 1px solid #AEAEAE;
  .panel-table-data{
    display: flex;
    .panel-pg{
      width: 1068px;
      height: 33px;
      background-color: rgba(107, 107, 107, 1);
      margin-left: -16px;
    }
    .panel-color{
      position: relative;
      width: 123px;
      height: 33px;
      z-index: 1;
      .icon{
        position: absolute;
        top: 4px;
        left: 18px;
        width: 25px;
        height: 25px;
      }
      .text{
        position: absolute;
        left: 49px;
        top: 5px;
        margin-left: 7px;
         span{
           font-weight: 700;
           font-style: normal;
           font-size: 16px;
           color: #FFFFFF;
         }
      }
    }
  }
  .datasource{
    display: flex;
    padding: 18px 0px 25px 48px;
    .data-title{
      width: 48px;
      white-space: nowrap;
      line-height: 38px;
    }
      .data-text{
        width: 206px;
        height: 41px;
        background: inherit;
        background-color: rgba(204, 204, 255, 1);
        border: none;
        border-radius: 5px;
        box-shadow: none;
        margin-left: 84px;
        font-size: 13px;
        color: #0033CC;
        line-height: 41px;
        text-align: center;
          span:nth-child(2){
            margin-left: 9px;
          }
      }
  }
}
</style>