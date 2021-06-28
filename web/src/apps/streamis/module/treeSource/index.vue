<template>
  <!--数据源内容(动态面板): Source表左侧数据源Sink表左侧 -->
  <div class="left-container">
    <!--搜索 -->
    <div class="search">
      <!--搜索---文本框 -->
      <div class="search-text">
        <Input v-model="search" :border="false" placeholder="搜索" />
      </div>
      <!--搜索---图标 -->
      <div class="search-icon">
        <Icon type="md-sync" />
      </div>
    </div>
    <!--数据源下拉选择框 -->
    <div class="select">
      <div class="select-type">
        <!-- <Icon custom="iconfont icon-apachekafka" size="30" /> -->
        <img class="dataType-img"  :src="dataTypeIcon"/>
        <!-- <img class="dataType-img"  :src="require('../../assets/images/u2450.png')"/> -->
        <!-- <img class="dataType-img" src="../../assets/images/u2450.png"/> -->
        <Select v-model="dataSourceType" style="width: 70px">
          <Option v-for="item in typeList" :value="item.name" :key="item.id">{{ item.name }}</Option>
        </Select>
      </div>
      <!--集群下拉选择框 -->
      <div class="select-colony">
        <Icon custom="iconfont icon-jiqun" size="30" style="color: #3300ff; margin-left:6px"/>
        <Select v-model="colonyType" style="width: 70px; margin-left: 5px">
          <Option v-for="item in colonyList" :value="item.dataSourceName" :key="item.id">{{ item.dataSourceName }}</Option>
        </Select>
      </div>
    </div>
    <!--数据源内容(库表模式) -->
    <div class="libtable">
      <Menu width="auto">
        <Submenu v-for="(item, index) in firstList" :key="index"  :name="item" >
          <template slot="title">
            <div @click="getSecondMenu(item)">
              <Icon custom="iconfont icon-shuju1" size="16" style="margin-right: 8px"/>
              <span>{{ item }}</span>
            </div>
          </template>
          <template v-for="item1 in secondList">
            <Submenu :name="item1.tableName" :key="item1.streamisTableMetaId">
              <template slot="title">
                <div @click="getThreeList(item1.tableName)">
                  <Icon custom="iconfont icon-table" size="14" style="margin-right: 8px"/>
                  <span>{{ item1.tableName }}</span>
                  <template v-if="item1.isStreamisDataSource">
                    <Icon type="md-star" class="streamisIcon" />
                  </template>
                </div>
              </template>
              <MenuItem 
                v-for="(item2) in threeList"	
                :name="item2.name" 								            
                to="跳转路由"
                :key="item2.index">
                {{ item2.name }}
              </MenuItem>
            </Submenu>
          </template>
        </Submenu>
      </Menu>
    </div>
  </div>
</template>

<script>
import api from "@/common/service/api";
export default {
  props: ["saveNotice"],
  data () {
    return {
      nodeId: '',
      dataTypeIcon: '',
      dataSourceTypeId: '',
      search: '',
      firstName: '',
      colonyId: '',
      //默认选中kafka 随着工作流传来的页面而改变
      dataSourceType: 'kafka',
      colonyType: '',
      nodeNameValue: '',
      extraUis: [{
        id: 1,
        datasource_type: "kafka",
        key: "kafka.group.id",
        description: "消费者组，默认为空。",
        lable_name: "消费组名",
        lable_name_en: "consumer group",
        ui_type: "input",
        condition: "[a-zA-Z0-9_]+"
      }],
      typeList: [
        {
          id: "1",
          name: "mysql",
          description: "mysql数据库",
          option: "mysql数据库",
          classifier: "关系型数据库",
          icon: "https://img.alicdn.com/imgextra/i4/O1CN01uLYwgg1zS93Aq9W8C_!!6000000006712-2-tps-280-176.png"
        },
        { 
          id: "4",
          name: "kafka",
          description: "kafka",
          option: "kafka",
          classifier: "消息队列",
          icon: "https://img.alicdn.com/imgextra/i4/O1CN01uLYwgg1zS93Aq9W8C_!!6000000006712-2-tps-280-176.png"
        }
      ],
      colonyList: [
        {
          // value: 'colony1',
          // label: '集群1'
          id: 2,
          dataSourceName: "kafka1",
          dataSourceDesc: "测试",
          dataSourceTypeId: 4,
          createSystem: "Linkis",
          createTime: 1620788919000,
          createUser: "hadoop",
          versionId: 1,
          expire: false,
          dataSourceType: {
            id: "4",
            name: "kafka"
          }
        },
        {
          // value: 'colony2',
          // label: '集群2'
          id: 3,
          dataSourceName: "kafka2",
          dataSourceDesc: "测试",
          dataSourceTypeId: 4,
          createSystem: "Linkis",
          createTime: 1620788919000,
          createUser: "hadoop",
          versionId: 1,
          expire: false,
          dataSourceType: {
            id: "3",
            name: "Mysql"
          }
        }
      ],
      isCollapsed: false,
      //一级菜单
      firstList: [
        "information_schema",
        "drelephant",
        "dss_ah3_prod",
        "dss_dev",
        "dss_prod",
        "dss_prod_test",
        "dss_test",
        "hive","luban",
        "metastore","mysql",
        "op_user","opuser_old",
        "performance_schema",
        "skywalking","sys"
      ],
      secondList: '',
      //三级菜单
      threeList: '',
      data1: [
        {
          title: 'parent 1',
          expand: true,
          children: [
            {
              title: 'parent 1-1',
              expand: true,
            },
            {
              title: 'parent 1-2',
              expand: true,
            }
          ]
        }, {}, {}
      ]
    }
  },
  mounted () {
    //获取数据源类型
    this.getDataSourceType()
    //获取集群信息
    this.getColonyType()
    //获取一级菜单(库名)
    this.getFirstMenu()
  },
  created(){
  },
  methods: {
    getDataSourceType(){
      // 获取数据类型没有带参数 但是得写死kafka先来固定调试
      // 获取从工作流传入过来的类型：type: links.fink.kafka id的值：有值/无值(需要我们保存id回溯到工作流页面)
      // this.dataSourceType = '工作流传过来的类型值'

      //向后台发送请求 获取数据源类型无带参
      api.fetch("streamis/dataSourceType", "post").then(res => {
        console.log(res,"后台返回的数据源类型信息")
        //this.typeList = res.dataSourceTypes 
      })
      // 根据数据源类型的名字找到对应的id
      // 需要循环数据源类型数组 是哪一个类型就显示哪一张图片
      this.typeList.forEach(item => {
        if(item.name === this.dataSourceType){
          this.dataSourceTypeId = item.id
          this.dataTypeIcon = item.icon
        }
      });
      console.log(this.dataSourceTypeId, this.dataTypeIcon, "数据源类型和图片的地址值")
      // this.dataTypeIcon = '../../assets/images/u2450.png'
      this.dataTypeIcon = 'img/u2450.d4f726d6.png'
      // this.dataTypeIcon = '../../assets/images/u2451.png' mysql的图片
      // 定义一个变量存储数据源类型id
      // this.dataSourceTypeId = ''
    },
    getColonyType(){
      const params = {
        //数据源类型的id 
        dataSourceTypeId: this.dataSourceTypeId,
        system: "streamis",
        //传空值，不传会报错
        name: ''
      }
      api.fetch("streamis/dataSourceCluster?", params, "post").then(res => {
        console.log(res,"后台返回的数据源集群信息")
        //this.colonyList = res.dataSourceCluster 
      })
      //通过数据源类型的id去查找对应的集群
      this.colonyList.forEach(item => {
        if(item.dataSourceType.id === this.dataSourceTypeId){
          this.colonyType = item.dataSourceName
          // 存储集群的id
          this.colonyId = item.id
        }
      });
      // 把集群id传递给父组件
      this.$emit('colonyIdFun', this.colonyId)
    },
    getFirstMenu(){
      const params = {
        //集群的id 默认进来选中哪个集群就传哪个集群
        dataSourceId: this.colonyId,
        system: "streamis"
      }
      api.fetch("streamis/dataBases?", params, "post").then(res => {
        console.log(res,"后台返回的获取一级菜单(库名)")
        //this.firstList = res.dataBases
      })
    },
    getSecondMenu(query){
      // if(this.secondList.length){return}
      this.firstName = query   
      this.secondList=[
        {
          "tableName": "test_table1",
          "isStreamisDataSource": true,
          "streamisTableMetaId": 1
        },
        {
          "tableName": "test_table2",
          "isStreamisDataSource": false,
          "streamisTableMetaId": 3
        },
        {
          "tableName": "test_table3",
          "isStreamisDataSource": true,
          "streamisTableMetaId": 2
        },
        {
          "tableName": "test_table4",
          "isStreamisDataSource": true,
          "streamisTableMetaId": 4
        },
        {
          "tableName": "test_table5",
          "isStreamisDataSource": true,
          "streamisTableMetaId": ''
        }
      ]
      //发送请求 获取二级菜单
      const params = {
        //集群的id 默认进来选中哪个集群就传哪个集群
        dataSourceId: this.colonyId,
        system: "streamis",
        dataBase: query
      }
      api.fetch("streamis/tables?", params, "post").then(res => {
        console.log(res,"后台返回的获取二级菜单(表名)")
        //this.secondList = res.tables
      })
    },
    // 三级菜单
    getThreeList(query){
      //把类型、集群、表名传递给父组件 传一个对象过去 然后取值
      let dataBase = {
        dataSourceType: this.dataSourceType,
        colonyType: this.colonyType,
        tableName: `${this.firstName}.${query}`,
        // 额外展示的一些信息
        extraUis: this.extraUis
      }
      // 数据源：传递需要额外展示的值
      this.$emit('dataBaseFun', dataBase)
      //传id
      // 根据名字查找到id
      this.secondList.forEach(item => {
        if(item.tableName === query){
          // 将要传递的id存储起来
          this.nodeId = item.streamisTableMetaId
        }
      })
      //现在传递的是二级菜单的名字
      this.nodeNameValue = query
      this.$emit('goTableNameFun', this.nodeNameValue)
      // 现在传递的是二级菜单的id
      this.$emit('goTableFun', this.nodeId)
      this.threeList = [
        {index: 1, name: "id", type: "BIGINT", primaryKey: true},
        {index: 2, name: "name", type: "VARCHAR", primaryKey: false},
        {index: 3, name: "project_id", type: "BIGINT", primaryKey: false},
        {index: 4, name: "job_type", type: "VARCHAR", primaryKey: false},
        {index: 5, name: "job_status", type: "VARCHAR", primaryKey: false},
        {index: 6, name: "cron_expression", type: "VARCHAR", primaryKey: false},
        {index: 7, name: "start_date", type: "DATETIME", primaryKey: false},
        {index: 8, name: "end_date", type: "DATETIME", primaryKey: false},
        {index: 9, name: "config", type: "VARCHAR", primaryKey: false},
        {index: 10, name: "description", type: "VARCHAR", primaryKey: false},
        {index: 11, name: "exec_log", type: "VARCHAR", primaryKey: false},
        {index: 12, name: "create_by", type: "BIGINT", primaryKey: false},
        {index: 13, name: "create_time", type: "TIMESTAMP", primaryKey: false},
        {index: 14, name: "update_time", type: "TIMESTAMP", primaryKey: false}
      ]
      const params = {
        dataSourceId: "",
        system: "streamis",
        dataBase: "",
        table: "",
        dataSourceType: ""
      }
      api.fetch("streamis/columns?", params, "post").then(res => {
        console.log(res,"后台返回的获取二级菜单(表名)")
        //this.threeList = res.columns
        //还有额外的数据需要存储起来
        //this.extraUis = res.extraUis
      })
    },
  }
}
</script>

<style lang="scss">
.left-container{
  border-width: 0px;
  overflow-y: hidden;
  background-color: rgba(255, 255, 255, 1);
  background-image: none;
  font-family: 'Arial Normal', 'Arial';
  font-weight: 400;
  font-style: normal;
  margin-left: 5px;
  .search{
    display: flex;
    line-height: 40px;
    width: 230px;
    height: 40px;
    font-size: 12px;
    color: #AEAEAE;
    border-bottom: 1px solid #AEAEAE;
    .search-text{
      width: 206px;
      height: 40px;
    }
    .search-icon{
      font-size: 20px;
      color: #6666ff;
      cursor: pointer;
      font-weight: 700;
    }
  }
  .select{
    .ivu-select .ivu-select-dropdown {
      z-index: 999;
    }
    display: flex;
    margin-top: 10px;;
    .select-type{
      background-color: rgba(204, 204, 255, 1);
      border-radius: 7px;
      display: flex;
      margin-right: 8px;
      .select-type{
        width: 10px;
        height: 10px;
      }
      .dataType-img{
        width: 30px;
        height: 32px;
      }
    }
    .select-colony{
      border-radius: 7px;
      background-color: rgba(204, 204, 255, 1);
      display: flex;
      .colony-img{
        width: 28px;
        height: 26px;
        margin-right: 2px;
      }
    }
    .ivu-select-single .ivu-select-selection .ivu-select-placeholder {
      color: #6B6B6B;
    }
    .ivu-select-prefix{
      font-size: 20px;
      font-weight: 400;
    }
    .ivu-select-selection{
      background-color: rgba(204, 204, 255, 1);
      border: 0px;
    }
  }

  .libtable{
    margin-top: 30px;
    width: 228px;
    height: 464px;
    overflow: auto;
    .ivu-icon-ios-arrow-down:before {
      content: "";
    }
    .ivu-menu-vertical .ivu-menu-item, .ivu-menu-vertical .ivu-menu-submenu-title{
      padding: 7px 24px;
    }
    .ivu-menu-light.ivu-menu-vertical .ivu-menu-item-active:not(.ivu-menu-submenu):after {
      content: '';
      background: #fff;
    }
    .streamisIcon{
      color: #3300ff;
      font-size: 16px;
      margin-left: 15px;
    }
  }
  // 菜单的选中样式隐藏
  .ivu-menu-vertical.ivu-menu-light:after{
    display: none;
  }
  .ivu-select-single .ivu-select-selection .ivu-select-selected-value {
    padding-left: 0;
  }
}
</style>