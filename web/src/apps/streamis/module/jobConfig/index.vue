<template>
  <div>
    <Row :gutter="0">
      <Col span="14">
        <div class="itemWrap">
          <p>{{ $t('message.streamis.jobConfig.resourceConfig') }}</p>
          <div>
            <Form ref="resourceConfig">
              <Row :gutter="60">
                <Col span="12">
                  <FormItem
                    :label="
                      $t(
                        'message.streamis.jobConfig.formItems.taskManagersNum'
                      ) + ':'
                    "
                    :label-width="labelWidth2"
                  >
                    <Input
                      v-model="resourceConfig.taskManagerNum"
                      type="number"
                    />
                  </FormItem>
                </Col>
                <Col span="12">
                  <FormItem label="JobManager CPUs:" :label-width="labelWidth2">
                    <Input
                      v-model="resourceConfig.jobManagerCPUs"
                      type="number"
                    />
                  </FormItem>
                </Col>
              </Row>
              <Row :gutter="60">
                <Col span="12">
                  <FormItem
                    label="JobManager Memory:"
                    :label-width="labelWidth2"
                  >
                    <div class="inputWrap">
                      <Input
                        v-model="resourceConfig.jobManagerMemory"
                        type="number"
                      />
                      <div class="unit">G</div>
                    </div>
                  </FormItem>
                </Col>
                <Col span="12">
                  <FormItem
                    label="TaskManager CPUs:"
                    :label-width="labelWidth2"
                  >
                    <Input
                      v-model="resourceConfig.taskManagerCPUs"
                      type="number"
                    />
                  </FormItem>
                </Col>
              </Row>

              <Row :gutter="60">
                <Col span="12">
                  <FormItem
                    label="TaskManager Memory:"
                    :label-width="labelWidth2"
                  >
                    <div class="inputWrap">
                      <Input
                        v-model="resourceConfig.taskManagerMemory"
                        type="number"
                      />
                      <div class="unit">G</div>
                    </div>
                  </FormItem>
                </Col>
              </Row>
            </Form>
          </div>
        </div>
        <div class="itemWrap">
          <p>{{ $t('message.streamis.jobConfig.productionConfig') }}</p>
          <div>
            <Form ref="productionConfig">
              <Row :gutter="60">
                <Col span="12">
                  <FormItem
                    :label="
                      $t('message.streamis.jobConfig.formItems.checkpointGap') +
                        ':'
                    "
                    :label-width="labelWidth2"
                  >
                    <Input
                      v-model="productionConfig.checkpointInterval"
                      type="number"
                    />
                  </FormItem>
                </Col>
                <Col span="12">
                  <FormItem
                    :label="
                      $t(
                        'message.streamis.jobConfig.formItems.restartStrategy'
                      ) + ':'
                    "
                    :label-width="labelWidth"
                  >
                    <Select
                      v-model="productionConfig.rebootStrategy"
                      class="select"
                    >
                      <Option
                        v-for="(item, index) in rebootStrategyOptions"
                        :value="item.value"
                        :key="index"
                      >
                        {{ item.title }}
                      </Option>
                    </Select>
                  </FormItem>
                </Col>
              </Row>
            </Form>
          </div>
        </div>
        <div class="itemWrap">
          <p>{{ $t('message.streamis.jobConfig.flinkParameters') }}</p>
          <div>
            <Form ref="alertSetForm">
              <Row v-for="(item, index) in flinkParameters" :key="index">
                <Col span="9">
                  <FormItem>
                    <div class="inputWrap">
                      <div class="flinkIndex">{{ index + 1 }}</div>
                      <Input
                        v-model="item[0]"
                        :placeholder="
                          $t(
                            'message.streamis.jobConfig.formItems.placeholders.flinkParameters'
                          )
                        "
                      />
                      <div class="equity">=</div>
                    </div>
                  </FormItem>
                </Col>
                <Col span="5">
                  <FormItem>
                    <Input
                      v-model="item[1]"
                      :placeholder="
                        $t(
                          'message.streamis.jobConfig.formItems.placeholders.variable'
                        )
                      "
                    />
                  </FormItem>
                </Col>
                <Col span="10">
                  <div class="inputWrap">
                    <div
                      class="icon"
                      v-show="flinkParameters.length !== 1"
                      @click="removeFlinkParameter(index)"
                    >
                      <Icon type="md-close" />
                    </div>
                    <div
                      class="icon"
                      v-show="index + 1 === flinkParameters.length"
                      @click="addFlinkParameter()"
                    >
                      <Icon type="md-add" />
                    </div>
                  </div>
                </Col>
              </Row>
            </Form>
          </div>
        </div>
      </Col>
      <Col span="1">
      </Col>
      <Col span="9">
        <div class="itemWrap">
          <p>{{ $t('message.streamis.jobConfig.alertSet') }}</p>
          <div>
            <Form ref="alertSetForm">
              <FormItem
                :label="
                  $t('message.streamis.jobConfig.formItems.alertRule') + ':'
                "
                :label-width="labelWidth"
              >
                <CheckboxGroup v-model="alertSet.alertRule">
                  <Checkbox
                    :label="option.value"
                    v-for="option in alertRuleOptions"
                    :key="option.value"
                  >
                    <span>{{ option.title }}</span>
                  </Checkbox>
                </CheckboxGroup>
                <!-- <RadioGroup v-model="alertSet.alertRule">
                  <Radio
                    :label="option.value"
                    v-for="option in alertRuleOptions"
                    :key="option.value"
                  ></Radio>
                </RadioGroup> -->
              </FormItem>
              <FormItem
                :label="
                  $t('message.streamis.jobConfig.formItems.alertLevel') + ':'
                "
                :label-width="labelWidth"
              >
                <Select v-model="alertSet.alertLevel" class="select">
                  <Option
                    v-for="(item, index) in alertLevelOptions"
                    :value="item.value"
                    :key="index"
                  >
                    {{ item.title }}
                  </Option>
                </Select>
              </FormItem>
              <FormItem
                :label="
                  $t('message.streamis.jobConfig.formItems.alertUser') + ':'
                "
                :label-width="labelWidth"
              >
                <Select v-model="alertSet.alertUser" multiple class="select">
                  <Option
                    v-for="(item, index) in users"
                    :value="item"
                    :key="index"
                  >
                    {{ item }}
                  </Option>
                </Select>
              </FormItem>
              <FormItem
                :label="
                  $t('message.streamis.jobConfig.formItems.alertLevelFailed') +
                    ':'
                "
                :label-width="labelWidth"
              >
                <Select v-model="alertSet.alertFailureLevel" class="select">
                  <Option
                    v-for="(item, index) in alertFailureLevelOptions"
                    :value="item.value"
                    :key="index"
                  >
                    {{ item.title }}
                  </Option>
                </Select>
              </FormItem>
              <FormItem
                :label="
                  $t('message.streamis.jobConfig.formItems.alertUserFailed') +
                    ':'
                "
                :label-width="labelWidth"
              >
                <Select
                  v-model="alertSet.alertFailureUser"
                  multiple
                  class="select"
                >
                  <Option
                    v-for="(item, index) in users"
                    :value="item"
                    :key="index"
                  >
                    {{ item }}
                  </Option>
                </Select>
              </FormItem>
            </Form>
          </div>
        </div>
        <div class="itemWrap">
          <p>{{ $t('message.streamis.jobConfig.authoritySet') }}</p>
          <div>
            <Form ref="authorityForm">
              <FormItem
                :label="
                  $t('message.streamis.jobConfig.formItems.authorityModel') +
                    ':'
                "
                :label-width="labelWidth"
              >
                <Select v-model="authoritySet.authorityAuthor" class="select">
                  <Option
                    v-for="(item, index) in authorityAuthorOptions"
                    :value="item.value"
                    :key="index"
                  >
                    {{ item.title }}
                  </Option>
                </Select>
              </FormItem>
              <FormItem
                :label="
                  $t('message.streamis.jobConfig.formItems.authorityPersons') +
                    ':'
                "
                :label-width="labelWidth"
              >
                <Select
                  v-model="authoritySet.authorityVisible"
                  multiple
                  class="select"
                >
                  <Option
                    v-for="(item, index) in users"
                    :value="item"
                    :key="index"
                  >
                    {{ item }}
                  </Option>
                </Select>
              </FormItem>
            </Form>
          </div>
        </div>
      </Col>
    </Row>
    <div class="saveBtn">
      <Button
        type="primary"
        @click="handleSaveConfig()"
        :loading="saveLoading"
        style="width:100px;height:40px;background:rgba(22, 155, 213, 1);"
      >
        {{
          !hadSaved
            ? $t('message.streamis.formItems.saveBtn')
            : $t('message.streamis.formItems.updateBtn')
        }}
      </Button>
    </div>
  </div>
</template>
<script>
import api from '@/common/service/api'
/**
 * 重置表单数据
 */
function resetFormValue(vueThis, dataName, configs) {
  if (!configs) {
    return
  }
  const newValues = {}
  const keys = Object.keys(vueThis[dataName])
  const options = {}
  const temp1 = []
  configs.forEach(item => {
    const { key, value, valueLists, name } = item
    const temp = (key && key.replace(/\./g, '').toLowerCase()) || ''
    const hit = keys.find(i => temp.endsWith(i.toLowerCase()))
    const isUser = ['告警用户', '失败时告警用户', '可见人员'].includes(name)
    let finalValue = value || value === 0 ? value : ''
    if (isUser && finalValue) {
      finalValue = finalValue.split(',')
    }
    if (name === '告警规则' && finalValue) {
      temp1.push(finalValue)
      finalValue = [...temp1]
    }
    if (valueLists && !isUser) {
      const ar = []
      valueLists.forEach(option => {
        ar.push({
          value: option.value,
          title: option.value
        })
        if (option.selected && name !== '告警规则') {
          finalValue = option.value
        }
      })
      options[hit + 'Options'] = ar
    }
    newValues[hit] = finalValue
  })

  vueThis[dataName] = newValues
  Object.assign(vueThis, options)
}
export default {
  data() {
    return {
      labelWidth: 80,
      labelWidth2: 140,
      resourceConfig: {
        taskManagerNum: '',
        jobManagerCPUs: '',
        jobManagerMemory: '',
        taskManagerCPUs: '',
        taskManagerMemory: ''
      },
      productionConfig: {
        checkpointInterval: '',
        rebootStrategy: ''
      },
      rebootStrategyOptions: [],
      flinkParameters: [[]],
      alertSet: {
        alertRule: [],
        alertLevel: '',
        alertUser: [],
        alertFailureLevel: '',
        alertFailureUser: []
      },
      alertLevelOptions: [],
      alertFailureLevelOptions: [],
      alertRuleOptions: [],
      authoritySet: {
        authorityAuthor: '',
        authorityVisible: []
      },
      authorityVisibleOptions: [],
      authorityAuthorOptions: [],
      saveLoading: false,
      fullTree: {},
      hadSaved: false,
      users: [],
      originFullTree: {}
    }
  },
  mounted() {
    this.getUsers()
    this.getConfigs()
  },
  methods: {
    getUsers() {
      api
        .fetch('streamis/streamJobManager/config/getWorkspaceUsers', 'get')
        .then(res => {
          console.log(res)
          if (res && res.users) {
            this.users = res.users
          }
        })
        .catch(e => console.log(e))
    },
    getConfigs() {
      api
        .fetch(
          'streamis/streamJobManager/config/view?jobId=' +
            this.$route.params.id,
          'get'
        )
        .then(res => {
          console.log(res)

          if (res && res.fullTree) {
            const { fullTree } = res
            const {
              resourceConfig,
              produceConfig,
              parameterConfig,
              alarmConfig,
              permissionConfig
            } = fullTree
            this.fullTree = fullTree
            this.originFullTree = { ...fullTree }
            resetFormValue(this, 'resourceConfig', resourceConfig)
            resetFormValue(this, 'productionConfig', produceConfig)
            resetFormValue(this, 'alertSet', alarmConfig)
            resetFormValue(this, 'authoritySet', permissionConfig)
            console.log(this.alertSet)
            if (parameterConfig) {
              const parameters = []
              parameterConfig.forEach(item => {
                const { key, value, configkeyId } = item
                parameters.push([key, value, configkeyId])
              })
              this.flinkParameters = parameters
            }
          }
        })
        .catch(e => console.log(e))
    },
    removeFlinkParameter(index) {
      console.log(index)
      const newParams = [...this.flinkParameters]
      newParams.splice(index, 1)
      this.flinkParameters = newParams
    },
    addFlinkParameter() {
      console.log('add')
      const newParams = [...this.flinkParameters]
      newParams.push([])
      this.flinkParameters = newParams
    },
    handleSaveConfig() {
      const map = {
        resourceConfig: 'resourceConfig',
        productionConfig: 'produceConfig',
        flinkParameters: 'parameterConfig',
        alertSet: 'alarmConfig',
        authoritySet: 'permissionConfig'
      }
      ;[
        'resourceConfig',
        'productionConfig',
        'flinkParameters',
        'alertSet',
        'authoritySet'
      ].forEach(name => {
        const obj = this.fullTree[map[name]]
        const values = this[name]
        if (name === 'flinkParameters') {
          const temp = this.originFullTree.parameterConfig || []
          const hit = temp.find(item => !!item.configkeyId)
          const configkeyId = hit ? hit.configkeyId : 7
          if (values[0][0]) {
            const params = []
            values.forEach(ar => {
              params.push({
                name: ar[0],
                key: ar[0],
                value: ar[1],
                configkeyId: ar[2] || configkeyId
              })
            })
            this.fullTree.parameterConfig = params
          } else {
            this.fullTree.parameterConfig = null
          }
          return
        }
        const keys = Object.keys(values)
        obj.forEach(item => {
          const { key, valueLists } = item
          const temp = (key && key.replace(/\./g, '').toLowerCase()) || ''
          const hit = keys.find(i => temp.endsWith(i.toLowerCase()))
          if (typeof hit !== "undefined"){
            const finalValue = values[hit]
            item.value = Array.isArray(finalValue)
              ? finalValue.join(',')
              : finalValue
            if (valueLists) {
              valueLists.forEach(vl => {
                if (Array.isArray(finalValue)) {
                  vl.selected = finalValue.includes(vl.value)
                } else {
                  vl.selected = vl.value === finalValue
                }
              })
            }
          }
        })
      })
      this.saveLoading = true
      api
        .fetch(
          'streamis/streamJobManager/config/' +
            (this.hadSaved ? 'update' : 'add'),
          {
            jobId: this.$route.params.id,
            fullTree: this.fullTree
          }
        )
        .then(res => {
          this.saveLoading = false
          console.log(res)
          if (res.errorMsg) {
            this.$Message.error(res.errorMsg.desc)
          } else {
            this.$Message.success(this.$t('message.streamis.operationSuccess'))
            this.hadSaved = true
          }
        })
        .catch(e => {
          console.log(e)
          this.saveLoading = false
        })
    }
  }
}
</script>
<style lang="scss" scoped>
.inputWrap {
  display: flex;
}
.unit {
  margin-left: 10px;
}
.itemWrap {
  padding: 10px;
  & > p {
    font-weight: 700;
    font-size: 16px;
  }
  & > div {
    margin-left: 60px;
    margin-top: 10px;
  }
}
.flinkIndex {
  margin-right: 10px;
}
.equity {
  margin-left: 10px;
  margin-right: 10px;
}
.icon {
  font-size: 20px;
  height: 30px;
  margin-left: 10px;
  cursor: pointer;
  color: #666666;
}
.programArguement {
  background: rgba(94, 94, 94, 1);
  color: #fff;
  padding: 10px 20px;
  min-height: 64px;
}
.saveBtn {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
}
</style>
