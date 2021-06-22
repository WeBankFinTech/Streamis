<template>
  <div class="tableInfo">
    <Form>
      <Row>
        <Col span="12">
          <FormItem label="表名:" :label-width="labelWidth">
            <Input v-model="tableInfo.tableName" style="width: 300px"/>
          </FormItem>
        </Col>
        <Col span="12">
          <FormItem label="别名:" :label-width="labelWidth">
            <Input type="text" v-model="tableInfo.alias" style="width: 300px"></Input>
          </FormItem>
        </Col>
      </Row>
      <Row>
        <Col span="12">
          <FormItem label="标签:" :label-width="labelWidth">
            <Input type="text" v-model="tableInfo.tags" style="width: 300px" placeholder="多个标签请用逗号隔开"></Input>
          </FormItem>
        </Col>
        <Col span="12">
          <FormItem label="作用域:" :label-width="labelWidth">
            <Select v-model="tableInfo.scope" style="width:300px">
              <Option v-for="item in scopeList" :value="item.value" :key="item.value">{{ item.label }}</Option>
            </Select>
          </FormItem>
          <!-- <FormItem label="可用用户:" :label-width="labelWidth">
          <Select v-model="model1" style="width:300px">
            <Option v-for="item in cityList" :value="item.value" :key="item.value">{{ item.label }}</Option>
          </Select>
        </FormItem> -->
        </Col>
      </Row>
      <Row>
        <Col span="12">
          <FormItem label="所属分层:" :label-width="labelWidth">
            <Select v-model="tableInfo.layer" style="width:300px">
              <Option v-for="item in layerList" :value="item.value" :key="item.value">{{ item.label }}</Option>
            </Select>
          </FormItem>
        </Col>
        <Col span="12">
          <FormItem label="描述:" :label-width="labelWidth">
            <Input v-model="tableInfo.description" type="textarea" :autosize="{minRows: 2,maxRows: 5}" style="width: 300px"></Input>
          </FormItem>
        </Col>
      </Row>
      <!-- <Row>
        <Col span="18">
        </Col>
        <Col span="6">
           <FormItem>
            <Button type="primary" @click="">确定</Button>
            <Button @click="" style="margin-left: 8px">取消</Button>
        </FormItem>
        </Col>
      </Row> -->
    </Form>
  </div>
</template>

<script>
export default {
  props: {
    formData: {
      type: Object,
      default () {
        return {}
      }
    }
  },
  data(){
    return{
      labelWidth: 80,
      tableInfo: {
        tableName: '',
        alias: '',
        tags: '',
        scope: '',
        layer: '',
        description: '',
      },
      scopeList: [
        {
          value: '任务级',
          label: '任务级'
        },
        {
          value: '工程级',
          label: '工程级'
        }
      ],
      layerList: [
        {
          value: 'ODS',
          label: 'ODS'
        },
        {
          value: '维表',
          label: '维表'
        },
        {
          value: 'DWD',
          label: 'DWD'
        },
        {
          value: 'DWS',
          label: 'DWS'
        }
      ],
    }
  },
  watch: {
    formData(){
      this.tableInfo.tableName = this.formData.tableName
      this.tableInfo.alias = this.formData.alias
      this.tableInfo.tags = this.formData.tags
      this.tableInfo.layer = this.formData.layer
      this.tableInfo.description = this.formData.description
      if(this.formData.scope === "task"){
        this.tableInfo.scope = '任务级'
      }else if(this.formData.scope === "project"){
        this.tableInfo.scope = '工程级'
      }
    },
  },
  mounted() {
  },
}
</script>

<style lang="scss">
.tableInfo{
  padding: 20px 60px;
}
</style>