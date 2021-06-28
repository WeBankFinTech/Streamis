<template>
  <div class="tableInfo">
    <Form ref="formValidate" :model="tableInfo">
      <Row>
        <Col span="12">
          <FormItem>
            <div slot="label" style="margin-left: 34px;"><span style="color: red">*</span>表名</div>
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
          <FormItem>
            <div slot="label" style="margin-left: 34px;"><span style="color: red">*</span>作用域</div>
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
          <FormItem>
            <div slot="label" style="margin-left: 34px;"><span style="color: red">*</span>所属分层</div>
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
    }
  },
  // props: ['formData'],
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
        id: ''
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
      flagChange: {}
    }
  },
  watch: {
    formData: {
      handler() {
        this.getTableInfo()
      },
    },
    'tableInfo.id'(newVal,oldVal){
      if(newVal != oldVal && oldVal != ""){
        this.$emit('funFlagTableInfo',false)
      }
    },
    'tableInfo.tableName'(newVal,oldVal){
      oldVal = this.flagChange.tableName
      console.log(oldVal,newVal,"旧值和新值")
      if(newVal != oldVal && oldVal != undefined){
        console.log("发送弹框信息")
        this.$emit('funFlagTableInfo',true)
      }
    },
    'tableInfo.alias'(newVal,oldVal){
      oldVal = this.flagChange.alias
      if(newVal != oldVal && oldVal != undefined){
        this.$emit('funFlagTableInfo',true)
      }
    },
    'tableInfo.tags'(newVal,oldVal){
      oldVal = this.flagChange.tags
      if(newVal != oldVal && oldVal != undefined){
        this.$emit('funFlagTableInfo',true)
      }
    },
    'tableInfo.layer'(newVal,oldVal){
      oldVal = this.flagChange.layer
      if(newVal != oldVal && oldVal != undefined){
        this.$emit('funFlagTableInfo',true)
      }
    },
    'tableInfo.scope'(newVal,oldVal){
      oldVal = this.flagChange.scope
      if(newVal != oldVal && oldVal != undefined){
        this.$emit('funFlagTableInfo',true)
      }
    },
    'tableInfo.description'(newVal,oldVal){
      oldVal = this.flagChange.description
      if(newVal != oldVal && oldVal != undefined){
        this.$emit('funFlagTableInfo',true)
      }
    }
  },
  mounted () {
    this.getTableInfo()
  },
  methods: {
    getTableInfo(){
      this.flagChange = JSON.parse(JSON.stringify(this.formData))
      this.tableInfo = this.formData
    }
  }
}
</script>

<style lang="scss">
.tableInfo{
  padding: 20px 60px;
}
</style>