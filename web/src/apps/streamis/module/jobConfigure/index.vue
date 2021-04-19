<template>
  <div>
    <Row :gutter="80">
      <Col span="14">
        <div class="itemWrap">
          <p>{{ $t("message.streamis.jobConfigure.resourceConfigure") }}</p>
          <div>
            <Form ref="alertSetForm">
              <Row :gutter="60">
                <Col span="12">
                  <FormItem
                    :label="
                      $t(
                        'message.streamis.jobConfigure.formItems.taskManagersNum'
                      ) + ':'
                    "
                    :label-width="labelWidth2"
                  >
                    <Input
                      v-model="resourceConfigure.taskManagersNum"
                      type="number"
                    />
                  </FormItem>
                </Col>
                <Col span="12">
                  <FormItem label="JobManager CPUs:" :label-width="labelWidth2">
                    <Input
                      v-model="resourceConfigure.jobManagerCPUs"
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
                        v-model="resourceConfigure.jobManagerMemory"
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
                      v-model="resourceConfigure.taskManagerCPUs"
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
                        v-model="resourceConfigure.taskManagerMemory"
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
          <p>{{ $t("message.streamis.jobConfigure.productionConfigure") }}</p>
          <div>
            <Form ref="alertSetForm">
              <Row :gutter="60">
                <Col span="12">
                  <FormItem
                    :label="
                      $t(
                        'message.streamis.jobConfigure.formItems.checkpointGap'
                      ) + ':'
                    "
                    :label-width="labelWidth2"
                  >
                    <Input
                      v-model="productionConfigure.checkpointGap"
                      type="number"
                    />
                  </FormItem>
                </Col>
                <Col span="12">
                  <FormItem
                    :label="
                      $t(
                        'message.streamis.jobConfigure.formItems.restartStrategy'
                      ) + ':'
                    "
                    :label-width="labelWidth"
                  >
                    <Select
                      v-model="productionConfigure.restartStrategy"
                      class="select"
                    >
                      <Option
                        v-for="(item, index) in restartStrategyOptions"
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
          <p>{{ $t("message.streamis.jobConfigure.flinkParameters") }}</p>
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
                            'message.streamis.jobConfigure.formItems.placeholders.flinkParameters'
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
                          'message.streamis.jobConfigure.formItems.placeholders.variable'
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
      <Col span="10">
        <div class="itemWrap">
          <p>{{ $t("message.streamis.jobConfigure.alertSet") }}</p>
          <div>
            <Form ref="alertSetForm">
              <FormItem
                :label="
                  $t('message.streamis.jobConfigure.formItems.alertRule') + ':'
                "
                :label-width="labelWidth"
              >
                <CheckboxGroup v-model="alertSet.alertRule">
                  <Checkbox label="logsError">
                    <span>{{
                      $t(
                        "message.streamis.jobConfigure.formItems.options.logsError"
                      )
                    }}</span>
                  </Checkbox>
                  <br />
                  <Checkbox label="coreException">
                    <span>{{
                      $t(
                        "message.streamis.jobConfigure.formItems.options.coreException"
                      )
                    }}</span>
                  </Checkbox>
                </CheckboxGroup>
              </FormItem>
              <FormItem
                :label="
                  $t('message.streamis.jobConfigure.formItems.alertLevel') + ':'
                "
                :label-width="labelWidth"
              >
                <Select v-model="alertSet.alertLevel" class="select">
                  <Option
                    v-for="(item, index) in levelOptions"
                    :value="item.value"
                    :key="index"
                  >
                    {{ item.title }}
                  </Option>
                </Select>
              </FormItem>
              <FormItem
                :label="
                  $t('message.streamis.jobConfigure.formItems.alertUser') + ':'
                "
                :label-width="labelWidth"
              >
                <Input v-model="alertSet.alertUser" />
              </FormItem>
              <FormItem
                :label="
                  $t(
                    'message.streamis.jobConfigure.formItems.alertLevelFailed'
                  ) + ':'
                "
                :label-width="labelWidth"
              >
                <Select v-model="alertSet.alertLevelFailed" class="select">
                  <Option
                    v-for="(item, index) in levelFailedOptions"
                    :value="item.value"
                    :key="index"
                  >
                    {{ item.title }}
                  </Option>
                </Select>
              </FormItem>
              <FormItem
                :label="
                  $t(
                    'message.streamis.jobConfigure.formItems.alertUserFailed'
                  ) + ':'
                "
                :label-width="labelWidth"
              >
                <Input v-model="alertSet.alertUserFailed" />
              </FormItem>
            </Form>
          </div>
        </div>
        <div class="itemWrap">
          <p>{{ $t("message.streamis.jobConfigure.alertSet") }}</p>
          <div>
            <Form ref="authorityForm">
              <FormItem
                :label="
                  $t('message.streamis.jobConfigure.formItems.authorityModel') +
                    ':'
                "
                :label-width="labelWidth"
              >
                <Select v-model="authoritySet.authorityModel" class="select">
                  <Option
                    v-for="(item, index) in authorityModelOptions"
                    :value="item.value"
                    :key="index"
                  >
                    {{ item.title }}
                  </Option>
                </Select>
              </FormItem>
              <FormItem
                :label="
                  $t(
                    'message.streamis.jobConfigure.formItems.authorityPersons'
                  ) + ':'
                "
                :label-width="labelWidth"
              >
                <Select
                  v-model="authoritySet.authorityPersons"
                  class="select"
                  multiple
                >
                  <Option
                    v-for="(item, index) in authorityPersonsOptions"
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
  </div>
</template>
<script>
export default {
  data() {
    return {
      labelWidth: 80,
      labelWidth2: 140,
      resourceConfigure: {
        taskManagersNum: "",
        jobManagerCPUs: "",
        jobManagerMemory: "",
        taskManagerCPUs: "",
        taskManagerMemory: ""
      },
      productionConfigure: {
        checkpointGap: "",
        restartStrategy: ""
      },
      restartStrategyOptions: [
        {
          value: "notRestart",
          title: this.$t(
            "message.streamis.jobConfigure.formItems.options.notRestart"
          )
        },
        {
          value: "autoRestartBasedCheckpoint",
          title: this.$t(
            "message.streamis.jobConfigure.formItems.options.autoRestartBasedCheckpoint"
          )
        },
        {
          value: "notStarNoCheckpoint",
          title: this.$t(
            "message.streamis.jobConfigure.formItems.options.notStarNoCheckpoint"
          )
        }
      ],
      flinkParameters: [[]],
      alertSet: {
        alertRule: "",
        alertLevel: "",
        alertUser: "",
        alertLevelFailed: "",
        alertUserFailed: ""
      },
      levelOptions: [{ value: "MAJOR", title: "MAJOR" }],
      levelFailedOptions: [
        { value: "Critical", title: "Critical" },
        { value: "MAJOR", title: "MAJOR" }
      ],
      authoritySet: {
        authorityModel: "",
        authorityPersons: ""
      },
      authorityPersonsOptions: ["shh", "guo", "li"],
      authorityModelOptions: [
        {
          value: "privite",
          title: this.$t(
            "message.streamis.jobConfigure.formItems.options.privite"
          )
        },
        {
          value: "specifiedPersonVisible",
          title: this.$t(
            "message.streamis.jobConfigure.formItems.options.specifiedPersonVisible"
          )
        },
        {
          value: "all",
          title: this.$t("message.streamis.jobConfigure.formItems.options.all")
        }
      ]
    };
  },
  methods: {
    removeFlinkParameter(index) {
      console.log(index);
      const newParams = [...this.flinkParameters];
      newParams.splice(index, 1);
      this.flinkParameters = newParams;
    },
    addFlinkParameter() {
      console.log("add");
      const newParams = [...this.flinkParameters];
      newParams.push([]);
      this.flinkParameters = newParams;
    }
  }
};
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
</style>
