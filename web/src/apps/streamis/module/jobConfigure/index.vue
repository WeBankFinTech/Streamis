<template>
  <div>
    <Row :gutter="80">
      <Col span="14">
        <div class="itemWrap">
          <p>{{ $t("message.streamis.jobConfigure.resourceConfigure") }}</p>
          <div>empty</div>
        </div>
      </Col>
      <Col span="10">
        <div class="itemWrap">
          <p>{{ $t("message.streamis.jobConfigure.alertSet") }}</p>
          <div>
            <Form ref="alertSetForm">
              <FormItem
                :label="$t('message.streamis.jobConfigure.formItems.alertRule')"
                :label-width="100"
              >
                <CheckboxGroup v-model="alertSet.alertRule">
                  <Checkbox label="logsError">
                    <span>{{
                      $t(
                        "message.streamis.jobConfigure.formItems.options.logsError"
                      )
                    }}</span>
                  </Checkbox>
                  <br/>
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
                  $t('message.streamis.jobConfigure.formItems.alertLevel')
                "
                :label-width="100"
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
                :label="$t('message.streamis.jobConfigure.formItems.alertUser')"
                :label-width="100"
              >
                <Input v-model="alertSet.alertUser" />
              </FormItem>
              <FormItem
                :label="
                  $t('message.streamis.jobConfigure.formItems.alertLevelFailed')
                "
                :label-width="100"
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
                  $t('message.streamis.jobConfigure.formItems.alertUserFailed')
                "
                :label-width="100"
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
                  $t('message.streamis.jobConfigure.formItems.authorityModel')
                "
                :label-width="100"
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
                  $t('message.streamis.jobConfigure.formItems.authorityPersons')
                "
                :label-width="100"
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
  }
};
</script>
<style lang="scss" scoped>
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
.programArguement {
  background: rgba(94, 94, 94, 1);
  color: #fff;
  padding: 10px 20px;
  min-height: 64px;
}
</style>
