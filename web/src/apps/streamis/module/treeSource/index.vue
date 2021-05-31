<template>
  <!--数据源内容(动态面板): Source表左侧数据源Sink表左侧 -->
  <div class="left-container">
    <!--搜索 -->
    <div class="search">
      <!--搜索---文本框 -->
      <div class="search-text">
        <Input v-model="value" :border="false" placeholder="搜索" />
      </div>
      <!--搜索---图标 -->
      <div class="search-icon">
        <Icon type="md-sync" />
      </div>
    </div>
    <!--数据源下拉选择框 -->
    <div class="select">
      <div class="select-type">
        <Icon custom="iconfont icon-apachekafka" size="30" />
        <Select v-model="selectData" style="width: 70px">
          <Option
            v-for="item in cityList4"
            :value="item.value"
            :key="item.value"
          >Kafka</Option>
          <Option v-for="item in cityList4" :key="item.value">Kafka</Option>
        </Select>
      </div>
      <!--集群下拉选择框 -->
      <div class="select-colony">
        <Icon custom="iconfont icon-jiqun" size="30" style="color: #3300ff; margin-left:6px"/>
        <Select v-model="model15" style="width: 70px; margin-left: 5px">
          <Option v-for="item in cityList" :value="item.value" :key="item.value">集群1</Option>
          <Option v-for="item in cityList" :key="item.value">集群1</Option>
          <Option v-for="item in cityList" :key="item.value">集群1</Option>
        </Select>
      </div>
    </div>
    <!--数据源内容(库表模式) -->
    <div class="libtable">
      <Menu width="228">
        <div v-for="item in list" :key="item.nodeName">
          <Submenu :name="item.nodeName" v-if="item.tablist.length !== 0">
            <template slot="title">
              <Icon custom="iconfont icon-shuju1" size="16" />
              <span>{{ item.nodeName }}</span>
              <template v-if="streamisFlag">
                <!--有星星标识的没有添加字段的功能 -->
                <Icon type="md-star" class="streamisIcon" />
              </template>
            </template>
            <MenuItem
              :name="item1.tableName"
              v-for="item1 in item.tablist"
              :key="item1.tableName"
            >
              <Icon custom="iconfont icon-table" size="14" />
              <span>{{ item1.tableName }}</span>
            </MenuItem>
          </Submenu>
          <!--如果一级菜单没有二级子菜单 -->
          <MenuItem v-else :name="item.nodeName">
            <Icon custom="iconfont icon-shuju" size="16" />
            <span>{{ item.nodeName }}</span>
          </MenuItem>
        </div>
      </Menu>
    </div>
  </div>
</template>

<script>
export default {
  data () {
    return {
      selectData: 'Kafka',
      streamisFlag: "true",
      nodeNameValue: '',
      value: '',
      cityList: [],
      cityList4: [],
      model18: '',
      model15: '',
      isCollapsed: false,
      list: [
        // {
        //   nodeName: "db_test_mask1",
        //   tablist: [
        //     { "tableName": "a1", "a2": "a2" },
        //     { "tableName": "a2", "a2": "a2" }
        //   ]
        // },
        // {
        //   nodeName: "db_test_mask2",
        //   tablist: []
        // }
        {
          nodeName: "topic1",//表
          tablist: []////字段
        },
        {
          nodeName: "topic_test1",
          tablist: [
            { "tableName": "uid", "a2": "a2" },
            { "tableName": "name", "a2": "a2" }
          ]
        },
        {
          nodeName: "topic_test2",
          tablist: []
        },
        {
          nodeName: "topic_test3",
          tablist: []
        }
      ],
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
    //this.getDatasets()
    let that = this
    // let submenuValue = document.querySelectorAll(".ivu-menu-item")  
    let submenuValue = document.querySelectorAll(".ivu-menu-submenu-title")
    for (let i = 0; i < submenuValue.length; i++) {
      submenuValue[i].addEventListener("click", function () {
        that.goTablePanel(this.innerText)
      })
    }
  },
  methods: {
    goTablePanel (query) {
      this.nodeNameValue = query
      this.$emit('goTableFun', this.nodeNameValue)
    },
  },
  computed: {
    menuitemClasses: function () {
      return [
        'menu-item',
        this.isCollapsed ? 'collapsed-menu' : ''
      ]
    }
  }
}
</script>

<style lang="scss">
.left-container{
  position: relative;
  width: 230px;
  height: 572px;
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
    display: flex;
    margin-top: 10px;;
    .select-type{
      background-color: rgba(204, 204, 255, 1);
      border-radius: 7px;
      display: flex;
      margin-right: 8px;
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
    height: 471px;
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
  
}


</style>