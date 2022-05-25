<template>
  <div>
    <Col span="25">
      <div class="itemWrap" v-for="part in configs" :key="part.id">
        <div class="normal" v-if="part.child_def && part.child_def.length">
          <h3>{{ part.name }}</h3>
          <div>
            <Form :ref="part.key">
              <FormItem v-for="def in part.child_def" :key="def.key" :label="def.name">
                <div v-if="def.type === 'INPUT'" :data-part="part.key" :data-def="def.key">
                  <Input v-model="valueMap[part.key][def.key]" :rules="{required: def.required, message: 'Error!', trigger: 'blur', pattern: new RegExp(def.validate_rule)}" />
                </div>
                <div v-else-if="def.type === 'NUMBER'" :data-part="part.key" :data-def="def.key">
                  <Input v-model="valueMap[part.key][def.key]" type="number" :rules="{required: def.required, message: 'Error!', trigger: 'blur', pattern: new RegExp(def.validate_rule)}" />
                </div>
                <div v-else>
                  <Select
                    v-model="valueMap[part.key][def.key]"
                    class="select"
                  >
                    <Option
                      v-for="item in def.ref_values"
                      :value="item"
                      :key="item"
                    >
                      {{ item }}
                    </Option>
                  </Select>
                </div>
              </FormItem>
            </Form>
          </div>
        </div>
        <div class="canEdited" v-else-if="part.child_def">
          <h3>{{ part.name }}</h3>
          <div>
            <Form ref="diyForm">
              <Row v-for="(item, index) in diyMap[part.key]" :key="index">
                <Col span="9">
                  <FormItem>
                    <div class="inputWrap">
                      <div class="flinkIndex">{{ index + 1 }}</div>
                      <Input v-model="item.key" />
                      <div class="equity">=</div>
                    </div>
                  </FormItem>
                </Col>
                <Col span="5">
                  <FormItem>
                    <Input v-model="item.value" />
                  </FormItem>
                </Col>
                <Col span="10">
                  <div class="inputWrap">
                    <div
                      class="icon"
                      @click="removeParameter(index, part.key)"
                      v-show="diyMap[part.key].length !== 1"
                    >
                      <Icon type="md-close" />
                    </div>
                    <div
                      class="icon"
                      v-show="index + 1 === diyMap[part.key].length"
                      @click="addParameter(part.key)"
                    >
                      <Icon type="md-add" />
                    </div>
                  </div>
                </Col>
              </Row>
            </Form>
          </div>
        </div>
        <div class="noChild" v-else>
          <h3>{{ part.name }}</h3>
        </div>
      </div>
    </Col>
    <div class="saveBtn">
      <Button
        type="primary"
        @click="handleSaveConfig()"
        :loading="saveLoading"
        style="width:100px;height:40px;background:rgba(22, 155, 213, 1);"
      >
        {{$t('message.streamis.formItems.saveBtn')}}
      </Button>
    </div>
  </div>
</template>
<script>
import api from '@/common/service/api'
export default {
  data() {
    return {
      configs: [],
      valueMap: {},
      returnMap: {},
      diyMap: {},
      saveLoading: false,
    }
  },
  mounted() {
    this.getUsers()
    this.getConfigs()
    window.test = this;
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
        .catch(e => console.warn(e))
    },
    getValues() {
      api
        .fetch(
          'streamis/streamJobManager/config/json/' + this.$route.params.id,
          'get'
        )
        .then(res => {
          this.returnMap = res;
          this.valueMap = {...this.valueMap, ...res};
          Object.keys(this.diyMap).forEach(key => {
            if (Object.keys(this.valueMap).includes(key)) {
              let keyValue = Object.keys(this.valueMap[key] || {}).map(k => ({key: k, value: this.valueMap[key][k]}));
              if (!keyValue.length) keyValue = [{value: '', key: ''}];
              this.diyMap = {
                ...this.diyMap,
                [key]: keyValue
              }
            }
          })
        })
        .catch(e => console.warn(e))
    },
    getConfigs() {
      api
        .fetch(
          'streamis/streamJobManager/config/definitions',
          'get'
        )
        .then(res => {
          console.log(res)
          let configs = res.def;
          configs = configs.map(conf => {
            this.valueMap[conf.key] = {};
            if (!conf.child_def) return conf;
            if (!conf.child_def.length) {
              this.diyMap = {...this.diyMap, [conf.key]: [{value: '', key: ''}]};
            }
            conf.child_def = conf.child_def.map(def => {
              if (def.validate_type !== 'Regex') def.validate_rule = '';
              else def.validate_rule = def.validate_rule || '';
              const defaultValue = def.default_value || '';
              this.valueMap[conf.key][def.key] = def.type === "NUMBER" ? +defaultValue : defaultValue;
              return def;
            }).filter(def => ['SELECT', 'INPUT', 'NUMBER'].includes(def.type));
            return conf;
          });
          this.configs = configs;
          this.getValues()
        })
        .catch(e => console.warn(e))
    },
    removeParameter(index, key) {
      console.log('removeParameter', index);
      const keyValue = this.diyMap[key];
      keyValue.splice(index, 1)
      this.diyMap = {...this.diyMap, [key]: keyValue}
    },
    addParameter(key) {
      console.log('addParameter')
      this.diyMap = {...this.diyMap, [key]: this.diyMap[key].concat({value: '', key: ''})}
    },
    handleSaveConfig() {
      console.log('handleSaveConfig')
      this.saveLoading = true;
      const configuration = { ...this.valueMap };
      let warning = false;
      Object.keys(this.diyMap).forEach(key => {
        configuration[key] = {};
        this.diyMap[key].forEach(mapKey => {
          if (configuration[key][mapKey.key]) warning = true;
          configuration[key][mapKey.key] = mapKey.value;
        })
      });
      if (warning) {
        this.saveLoading = false;
        return this.$Message.error({ content: '自定义字段名称不能重复' });
      }
      api
        .fetch(
          `streamis/streamJobManager/config/json/${this.$route.params.id}`,
          { ...configuration }
        )
        .then(res => {
          this.saveLoading = false
          console.log(res)
          if (res.errorMsg) {
            this.$Message.error(res.errorMsg.desc)
          } else {
            this.$Message.success(this.$t('message.streamis.operationSuccess'))
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
