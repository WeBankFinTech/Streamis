<template>
  <div class="dataSource">
    <div class="container">
      <!--左边树 -->
      <div class="leftContainer">
        <treeSource
          :node="node"
          @goTableFun="getThreeList"
          @dataBaseFun="getDataBase"
          @goTableNameFun="getNodeId"
          @colonyIdFun="getColonyId"/>
      </div>
      <div class="rightContainer">
        <!--切换sql -->
        <div class="designer-toolbar">
          <!-- <div class="button">
            <Icon type="md-settings" />
            <span>配置</span>
          </div>
          <div class="devider" />
          <div class="button">
            <Icon type="md-play" />
            <span>停止</span>
          </div> -->
          <div class="devider" />
          <div class="button" @click="addStreamis">
            <svg
              class="icon"
              viewBox="0 0 1024 1024"
              version="1.1"
              xmlns="http://www.w3.org/2000/svg"
              p-id="4564"
            >
              <path
                d="M176.64 1024c-97.28 0-176.64-80.384-176.64-178.688V178.688c0-98.816 79.36-178.688 176.64-178.688h670.72c97.28 0 176.64 80.384 176.64 178.688V845.312c0 98.816-79.36 178.688-176.64 178.688h-670.72z m0-936.96c-50.688 0-91.648 41.472-91.648 92.16V845.312c0 50.688 40.96 92.16 91.648 92.16h670.72c50.688 0 91.648-41.472 91.648-92.16V178.688c0-50.688-40.96-92.16-91.648-92.16h-3.584v437.248h-663.04v-437.248h-4.096z m581.632 350.208v-350.208h-492.544v350.208h492.544z m-160.768-35.328v-240.128h84.992v240.128h-84.992z"
                p-id="4565"
              ></path>
            </svg>
            <span>保存</span>
          </div>
          <div class="devider" />
          <!-- 必须要选中一个表才可以切换sql -->
          <div class="button" @click="changSql" v-if="changeStatus">
            <Icon type="md-swap" />
            <span>切换SQL</span>
          </div>
          <div class="button" @click="backGraph" v-else>
            <Icon type="md-swap" />
            <span>返回图形界面</span>
          </div>
          <template v-if="!isShowSql&&nodeId">
            <Input class='sqlInput' :rows="5" :autosize="{maxRows:50, minRows: 5}" v-model="sqltext" type="textarea" />
          </template>
        </div>
        <template v-if="!nodeNameValue">
          <dataSourceInit/>
        </template>
        <template v-if="nodeNameValue && isShowSql">
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
                <span>{{ dataBase.dataSourceType }} - {{ dataBase.colonyType }}：</span>
                <span>{{ dataBase.tableName }}</span>
              </div>
              <div class="data-text" v-if="showInput">
                <span style="margin-right: 4px">{{ extraUisLable }}:</span>
                <Input v-model="extraUisName" placeholder="请输入消费组名" style="width: 200px" />
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
            <!-- 把二级菜单的id值传入给表格去发送请求 -->
            <tableFieldsList
              :nodeId="nodeId"
              @funTableColumn="tableColumnObject"
              @tableInfoFun="tableInfoList"
              @extraInfoFun="getStreamisExtraInfo"
              @funChangeFieldList="getChangeFieldList"
              ref="mychildTable"/>
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
            <tableInfo
              :formData.sync="formData"
              @funFlagTableInfo="getFlagTableInfo"/>
          </div>
        </template>
      </div>
    </div>
    <Modal v-model="saveNotice" title="提示" @on-ok="addStreamis(saveNotice)">
      <p>请点击保存按钮，否则会清空您记录哦！！！</p>
    </Modal>
  </div>
</template>
<script>
import api from "@/common/service/api";
import treeSource from '@/apps/streamis/module/treeSource';
import dataSourceInit from '@/apps/streamis/module/dataSourceInit';
import tableFieldsList from '@/apps/streamis/module/tableFieldsList';
import tableInfo from '@/apps/streamis/module/tableInfo';
export default {
  // 接收工作流传来的node 数据源类型
  props: ["node"],
  components: {
    tableFieldsList: tableFieldsList.component,
    tableInfo: tableInfo,
    treeSource: treeSource,
    dataSourceInit: dataSourceInit,
  },
  data() {
    return {
      streamisExtraInfo: '',
      changeExtraUisName: false,
      changeTableInfo: false,
      changeFieldList: false,
      showInput: '',
      colonyId: '',
      returnId: '',
      extraUis: '',
      labelWidth: 80,
      saveNotice: false,
      // 根据二级菜单的名字来判断是否显示右侧动态面板
      nodeNameValue: '',
      // 用户自己填的东西
      extraUisName: '',
      extraUisLable: '',
      // 字段信息的值
      fieldsList: '',
      changeStatus: true,
      isShowSql: true,
      nodeId: '',
      formData: {
        tableName: '',
        alias: '',
        tags: '',
        scope: '',
        layer: '',
        description: '',
        id: ''
      },
      navHeight: 0,
      dataBase: '',
      sqltext: ''
    };
  },
  mounted() {
  },
  watch: {
    // 监听消费组名的变化 如果有变化 要走保存
    extraUisName: {
      handler(newValue,oldValue) {
        if(newValue !== oldValue && oldValue != '' && newValue){
          // 说明已经修改了 如果没有走保存按钮 就要弹出提示框
          this.changeExtraUisName = true
        }
      }
    },
  },
  methods: {
    //保存表信息和字段信息
    addStreamis(){
      if(!this.extraUisName){
        this.$Message.warning('请输入消费组名')
        return
      }
      if(!this.changeFieldList){
        return
      }
      if(!this.formData.tableName || !this.formData.scope || !this.formData.layer){
        this.$Message.warning('表名、作用域、所属分层不能为空')
        return
      }
      if(this.fieldsList){
        this.fieldsList = this.fieldsList.filter(item => item.fieldName)
      }
      this.formData.name  = this.nodeNameValue
      this.formData.tags = this.formData.tags || ''
      let params = {
        authorityId: '',
        streamisTableMeta: this.formData,
        streamisTableFields: this.fieldsList,
        streamisExtraInfo: [{
          key: this.extraUis.key,
          value: this.extraUisName,
          streamisTableMetaId: this.nodeId
        }]
      }
      api.fetch("streamis/save" , params, "post").then(res => {
        if(res){
          // 再去触发一下子组件tableFieldsList的方法
          this.$Message.success('保存成功')
          this.$refs.mychildTable.getFieldsList();
          this.nodeId = res.streamisTableMetaId;
          this.node.jobContent.datasourceId = res.streamisTableMetaId
          this.$emit('save', {}, {...this.node } )
        }else{
          this.$Message.error();('保存失败')
        }
      })
    },
    changSql(){

      this.changeStatus = !this.changeStatus
      this.isShowSql = !this.isShowSql

      //像后端发送请求 翻译成sql
      const params = {
        streamisTableMetaId: this.nodeId,
        dataSourceId: this.colonyId,
        nodeName: this.nodeNameValue,
        streamisExtraInfo: this.streamisExtraInfo
      }
      api.fetch("streamis/transfer" , params, "post").then(res => {
        this.sqltext = res.sqlText
      })
    },
    backGraph(){
      this.changeStatus = !this.changeStatus
      this.isShowSql = !this.isShowSql
    },
    getThreeList(nodeId) {
      this.nodeId = nodeId
      if(this.changeFieldList || this.changeTableInfo || this.changeExtraUisName){
        // 告诉三个其中一个有改变 但是没有走保存按钮 提示框就会出现
        if(this.goSaveButton === false){
          // 弹出提示框
          this.saveNotice = true
          // 把这个变化为空 让它下次不要弹出来
          this.changeTableInfo = false
        }
      }
    },
    tableInfoList(formData) {
      // 子组件tableFieldList传过来的表信息
      this.formData = formData
    },
    tableColumnObject(mapTableList){
      // 传过来的保存的字段信息的值
      this.fieldsList = mapTableList
    },
    getDataBase(dataBase = {}){
      // 数据源：得到一些需要额外展示的值
      // 显示输入框/选择框
      if(Array.isArray(dataBase.extraUis) && dataBase.extraUis.length){
        this.showInput = dataBase.extraUis[0].id
        this.dataBase = dataBase
        this.extraUisLable = dataBase.extraUis[0].lable_name
        this.extraUis = dataBase.extraUis[0]
      }
    },
    getStreamisExtraInfo(streamisExtraInfo){
      this.streamisExtraInfo = streamisExtraInfo
      if(streamisExtraInfo.length !== 0){
        this.extraUisName = streamisExtraInfo[0].value
      }else{
        this.extraUisName = ''
      }
    },
    // 获取点击的二级菜单的名字
    getNodeId(nodeNameValue){
      this.nodeNameValue = nodeNameValue
    },
    // 集群的id
    getColonyId(colonyId){
      this.colonyId = colonyId
    },
    getChangeFieldList(val){
      if(val === true){
        this.changeFieldList = val
      }
    },
    getFlagTableInfo(query){
      this.changeTableInfo = query
    }
  },
};
</script>
<style lang="scss" scoped>
.dataSource{
  .container{
      display: flex;
      width: 100%;
      height: 100%;
    .leftContainer{
      flex-shrink: 1;
      height: 100%;
    }
    .rightContainer{
      flex-shrink: 1;
      width: 100%;
      height: 100%;
      .designer-toolbar {
        height: 40px;
        padding-left: 10px;
        background: #f7f7f7;
        color: #000;
        border: 1px solid #d7dde4;
        .button {
            float: left;
            margin: 6px 5px 6px 0;
            padding: 0;
            height: 18px;
            text-align: left;
            border: 1px solid transparent;
            border-radius: 2px;
            cursor: pointer;
        svg {
            margin-right: 5px;
            color: #666;
          }
        span {
              vertical-align: middle;
              color: #666;
          }
        .icon {
              display: inline-block;
              vertical-align: text-top;
              width: 18px;
              height: 18px;
              color: #333;
              text-align: center;
          }
        }
        .devider {
            float: left;
            border-left: 1px solid #e3e8ee;
            height: 18px;
            margin: 8px 5px;
          }
        }
      .sqlInput{
        width: 100%;
        height: 329px;
        font-family: 'Arial Normal', 'Arial';
        font-weight: 400;
        font-style: normal;
        font-size: 13px;
        text-decoration: none;
        color: #000000;
        text-align: left;
        margin-left: -11px;
        margin-top: 5px;
        box-sizing: border-box;
      }
      .right-container{
        border-width: 0px;
        border-left: 1px solid #AEAEAE;
        height: 100%;
        .panel-table-data{
          display: flex;
          .panel-pg{

            width: calc(100vw - 230px);
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
            width: auto;
            height: 41px;
            background: inherit;
            background-color: rgba(204, 204, 255, 1);
            border: none;
            border-radius: 5px;
            box-shadow: none;
            padding-left: 4px;
            padding-right: 4px;
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
    }
  }
}
</style>
