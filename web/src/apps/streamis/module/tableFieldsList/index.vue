<template>
  <div class="table-list">
    <Table :columns="columns" :data="tableDatas" :loading="loading">
      <template slot-scope="{ row, index }" slot="fieldName">
        <div class="fieldName" v-if="index === 0" @click="addColumn(row, index, tableDatas)">
          <Icon type="md-add" class="addField"/>
          <span>新增字段</span>
        </div>
        <div class="fieldName" v-else>
          <Icon type="md-more" class="more"/>
          <div v-if="!row.edit" style="margin-left: 5px">
            {{ row.fieldName }}
          </div>
          <Input v-else v-model="tableColumn.fieldName" placeholder="字段名称" style="width: 100px" />
        </div>
      </template>
      <!-- <template slot-scope="{ row}" slot="fieldIsPrimary">
        <div v-if="!row.edit" style="margin-left: 5px">
          {{ row.fieldIsPrimary }}
        </div> -->
      <!--<Input v-else v-model="tableColumn.fieldIsPrimary" placeholder="是否主键" style="width: 100px" /> -->
      <!--<RadioGroup v-else v-model="tableColumn.fieldIsPrimary">
          <Radio label="是" disabled></Radio>
          <Radio label="否"></Radio>
        </RadioGroup> -->
      <!-- </template> -->
      <template slot-scope="{ row }" slot="fieldIsPartition">
        <div v-if="!row.edit" style="margin-left: 5px">
          {{ row.fieldIsPartition }}
        </div>
        <Input v-else v-model="tableColumn.fieldIsPartition" placeholder="分区字段" style="width: 100px" />
      </template>
      <template slot-scope="{ row }" slot="fieldType">
        <div v-if="!row.edit" style="margin-left: 5px">
          {{ row.fieldType }}
        </div>
        <Input v-else v-model="tableColumn.fieldType" placeholder="字段类型" style="width: 100px" />
      </template>
      <template slot-scope="{ row }" slot="verifyRule">
        <div v-if="!row.edit" style="margin-left: 5px">
          {{ row.verifyRule }}
        </div>
        <Input v-else v-model="tableColumn.verifyRule" placeholder="检测规则" style="width: 100px" />
      </template>
      <template slot-scope="{ row }" slot="fieldAlias">
        <div v-if="!row.edit" style="margin-left: 5px">
          {{ row.fieldAlias }}
        </div>
        <Input v-else v-model="tableColumn.fieldAlias" placeholder="字段别名" style="width: 100px" />
      </template>
      <template slot-scope="{ row }" slot="fieldDescription">
        <div v-if="!row.edit" style="margin-left: 5px">
          {{ row.fieldDescription }}
        </div>
        <Input v-else v-model="tableColumn.fieldDescription" placeholder="字段说明" style="width: 100px" />
      </template>
      <template slot-scope="{ row, index }" slot="operation">
        <div v-if="!row.edit && index !== 0">
          <!-- <Button
            v-bind:disabled="tableInfoFlag"
            type="primary"
            size="small"
            style="margin-right: 5px"
            @click="editColumn(row, index, tableDatas)">
            修改
          </Button>
          <Button 
            v-bind:disabled="tableInfoFlag"
            type="error" size="small" @click="deleteColumn(row, index)">
            删除
         </Button> -->
          <Button
            type="primary"
            size="small"
            style="margin-right: 5px"
            @click="editColumn(row, index, tableDatas)">
            修改
          </Button>
          <Button type="error" size="small" @click="deleteColumn(row, index)">
            删除
          </Button>
        </div>
        <!--第一列隐藏确定和取消 -->
        <div v-else-if="row.edit && index !== 0">
          <Button
            type="primary"
            size="small"
            style="margin-right: 5px"
            @click="submit(row, index)">
            确定
          </Button>
          <Button type="error" size="small" @click="cancelColumn(row, index)">
            取消
          </Button>
        </div>
      </template>
    </Table>
  </div>
</template>
<script>
import api from "@/common/service/api";
/**
 * 
 */
function renderSpecialHeader(h, params) {
  return h("div", [
    h("strong", params.column.title),
    h(
      "span",
      {
        style: {
          color: "red"
        }
      },
      "*"
    )
  ]);
}

export default {
  props: ["fieldsListInfo"],
  data() {
    return {
      tableInfoFlag: '',
      //保留编辑前的初始值
      saveDatas: '',
      tableColumn: {},
      query: {
        jobName: "",
        jobStatus: "all",
        submitter: "all"
      },
      jobStatus: ["all", "running", "failture", "uploaded"],
      submitterOptions: ["all"],
      tableDatas: [
        {},
        {
          "streamisTableMetaId": 13,
          "fieldName": "id",
          "fieldType": "string",
          "fieldIsPrimary": true,
          "fieldIsPartition": 1,
          "verifyRule": "ls",
          "fieldAlias": "fieldsa",
          "fieldDescription": "this is a files"
        },
        {
          "streamisTableMetaId": 13,
          "fieldName": "age",
          "fieldType": "int",
          "fieldIsPrimary": false,
          "fieldIsPartition": 0,
          "verifyRule": "ls",
          "fieldAlias": "fieldsa",
          "fieldDescription": "this is a files for update"
        },
        {
          "streamisTableMetaId": 13,
          "fieldName": "age",
          "fieldType": "int",
          "fieldIsPrimary": false,
          "fieldIsPartition": 0,
          "verifyRule": "ls",
          "fieldAlias": "fieldsa",
          "fieldDescription": "this is a files"
        }
      ],
      columns: [
        {
          title: "字段名称",
          key: "fieldName",
          renderHeader: renderSpecialHeader,
          slot: "fieldName"
        },
        {
          title: "是否主键",
          key: "fieldIsPrimary",
          renderHeader: renderSpecialHeader,
          // slot: "fieldIsPrimary",
          render: (h, params) => {
            //this.tableDatas.forEach((e)=>{
            // const bol = Boolean(e.fieldIsPrimary);
            if(params.row.fieldIsPrimary) {
              return h('Radio', { props: {value: params.row.fieldIsPrimary },}, "是")
            } else if(params.index==0){
              return h('span', { props: {value: params.row.fieldIsPrimary },},)
            } else{
              return h('span', { props: {value: params.row.fieldIsPrimary },}, "否")
            }
            //})
          }
        },
        {
          title: "分区字段",
          key: "fieldIsPartition",
          renderHeader: renderSpecialHeader,
          slot: "fieldIsPartition"
        },
        {
          title: "字段类型",
          key: "fieldType",
          renderHeader: renderSpecialHeader,
          slot: "fieldType"
        },
        {
          title: "检测规则",
          key: "verifyRule",
          slot: "verifyRule"
        },
        {
          title: "字段别名",
          key: "fieldAlias",
          slot: "fieldAlias"
        },
        {
          title: "字段说明",
          key: "fieldDescription",
          slot: "fieldDescription"
        },
        {
          title: this.$t("message.streamis.jobListTableColumns.operation"),
          key: "operation",
          slot: "operation"
        }
      ],
      jobMoudleRouter: [
        "paramsConfiguration",
        "alertConfiguration",
        "runningHistory",
        "runningLogs"
      ],
      pageData: {
        total: 0,
        current: 1,
        pageSize: 20
      },
      loading: false,
    };
  },
  mounted() {
    this.getFieldsList();
    console.log(this.fieldsListInfo,"父组件传过来的值")///a1
    this.tableInfoFlag = Boolean(this.fieldsListInfo);
  },
  methods: {
    // 删除字段
    deleteColumn(row, index){
      //发送请求
      //const params = {
      //id: row.id
      //}
      //api
      //.fetch("streamis/deleteFields/{id}", "POST")
      //.then(() => {
      //let index = this.tableDatas.findIndex(subitem => subitem.id === subitem.row.id )
      //this.tableDatas.splice(index, 1)
      //this.$Message.success('删除成功')
      //})
      //.catch(() => {
      //this.$Message.success('删除失败')
      //});
      this.tableDatas.splice(index, 1)
    },
    submit(row, index){
      //发送添加的请求接口
      // api
      //.fetch("streamis/addFields",this.tableColumn, "POST")
      //.then(res => {
      //this.$Message.success('添加成功')
      //})
      //.catch(e => console.log(e));
      this.tableDatas.splice(index, 1,{
        fieldName: this.tableColumn.fieldName,
        fieldType: this.tableColumn.fieldType,
        fieldIsPrimary: this.tableColumn.fieldIsPrimary,
        fieldIsPartition: this.tableColumn.fieldIsPartition,
        verifyRule: this.tableColumn.verifyRule,
        fieldAlias: this.tableColumn.fieldAlias,
        fieldDescription: this.tableColumn.fieldDescription,
      })
    },
    //取消新增字段
    cancelColumn(row, index){
      // 如果是添加的取消按钮 我们就直接取消一行
      if(!this.saveDatas){
        this.tableDatas.splice(index, 1)
      } else {
        // 点击取消编辑框里面的数据恢复到原始值
        console.log(this.saveDatas,"6666")
        this.tableDatas.splice(index, 1, {
          fieldName: this.saveDatas[0],
          fieldType: this.saveDatas[1],
          fieldIsPrimary: this.saveDatas[2],
          fieldIsPartition: this.saveDatas[3],
          verifyRule: this.saveDatas[4],
          fieldAlias: this.saveDatas[5],
          fieldDescription: this.saveDatas[6],
          edit: false,
        })
      }
    },
    //新增字段
    addColumn(row, index){
      this.tableColumn.fieldName = ''
      this.tableColumn.fieldIsPrimary = ''
      this.tableColumn.fieldIsPartition = ''
      this.tableColumn.fieldType = ''
      this.tableColumn.verifyRule = ''
      this.tableColumn.fieldAlias = ''
      this.tableColumn.fieldDescription = ''
      this.tableDatas.splice(index + 1, 0, { edit: true })
      this.saveDatas = ''
    },
    getFieldsList() {
      // 有星星的时候删除加的第一行空对象
      // this.tableDatas.splice(0,1)
      
      // this.tableDatas.forEach((e)=>{
      // const bol = Boolean(e.fieldIsPrimary);
      // })
      api
        .fetch("streamis/streamisTableMetaInfo/13", "get")
        .then(res => {
          console.log(res,"后端返回的数据");
          if (res) {
            // const datas = res.tasks || [];
            // datas.unshift({});
            //this.tableDatas = datas;
            this.pageData.total = parseInt(res.totalPage);
          }
        })
        .catch(e => console.log(e));
    },
    handleQuery() {},
    // 修改字段
    editColumn(row, index) {
      this.tableDatas = this.tableDatas.filter((item) => !item.edit)
      this.tableDatas.splice(index, 1, { edit: true })
      this.tableColumn.fieldName = row.fieldName
      this.tableColumn.fieldIsPrimary = row.fieldIsPrimary
      this.tableColumn.fieldIsPartition = row.fieldIsPartition
      this.tableColumn.fieldType = row.fieldType
      this.tableColumn.verifyRule = row.verifyRule
      this.tableColumn.fieldAlias = row.fieldAlias
      this.tableColumn.fieldDescription = row.fieldDescription
      this.saveDatas = [row.fieldName, row.fieldIsPrimary,row.fieldIsPartition,row.fieldType,row.verifyRule,row.fieldAlias,row.fieldDescription]
    },
  }
};
</script>
<style lang="scss" scoped>
.table-list{
padding: 10px 30px;
.select {
  width: 200px;
}
.fieldName {
  display: flex;
  cursor: pointer;
}
.more {
  font-size: 20px;
}
.addField {
  font-size: 24px;
  font-weight: 700;
  margin-right: 5px;
}
.page {
  margin-top: 20px;
}
}
</style>
