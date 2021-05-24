<template>
  <div class="tableInfo">
    <Form>
      <Row>
        <Col span="12">
          <FormItem label="Topic名:" :label-width="labelWidth">
            <Input v-model="tableInfo.tableName" type="number" style="width: 300px"/>
          </FormItem>
        </Col>
        <Col span="12">
          <FormItem label="别名:" :label-width="labelWidth">
            <Input type="text" v-model="tableInfo.tableName" style="width: 300px"></Input>
          </FormItem>
        </Col>
      </Row>
      <Row>
        <Col span="12">
          <FormItem label="标签:" :label-width="labelWidth">
            <Input type="text" v-model="tableInfo.tableName" style="width: 300px"></Input>
          </FormItem>
        </Col>
        <Col span="12">
          <FormItem label="作用域:" :label-width="labelWidth">
            <Select v-model="model1" style="width:300px">
              <Option v-for="item in cityList" :value="item.value" :key="item.value">{{ item.label }}</Option>
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
            <Select v-model="model1" style="width:300px">
              <Option v-for="item in cityList" :value="item.value" :key="item.value">{{ item.label }}</Option>
            </Select>
          </FormItem>
        </Col>
        <Col span="12">
          <FormItem label="描述:" :label-width="labelWidth">
            <Input v-model="tableInfo.tableName" type="textarea" :autosize="{minRows: 2,maxRows: 5}" style="width: 300px"></Input>
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
  data(){
    return{
      labelWidth: 80,
      model1: '',
      tableInfo: {
        tableName: ''
      },
      cityList: [
        {
          value: '工程级',
          label: '工程级'
        },
        {
          value: '二级',
          label: '二级'
        },
      ],
    }
  }
}
</script>

<style lang="scss">
.tableInfo{
  padding: 20px 60px;
}
</style>