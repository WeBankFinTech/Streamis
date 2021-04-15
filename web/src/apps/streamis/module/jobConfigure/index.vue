<template>
  <div>
    <Row>
      <Col span="15">
        <div class="itemWrap">
          <p>{{ $t("message.streamis.jobConfigure.resourceConfigure") }}</p>
          <div>
            <Table :columns="columns" :data="flinkDatas" border>
              <template slot-scope="{ row }" slot="operation">
                <div>{{ row.id }}</div>
              </template>
            </Table>
          </div>
        </div>
      </Col>
      <Col span="9">
        <div class="itemWrap">
          <p>{{ $t("message.streamis.jobConfigure.alertSet") }}</p>
          <div class="programArguement">{{ programArguement }}</div>
        </div>
      </Col>
    </Row>
  </div>
</template>
<script>
export default {
  data() {
    return {
      columns: [
        {
          title: "id",
          key: "id"
        },
        {
          title: this.$t("message.streamis.jobDetail.columns.name"),
          key: "name"
        },
        {
          title: this.$t("message.streamis.jobDetail.columns.version"),
          key: "version"
        },
        {
          title: this.$t(
            "message.streamis.jobDetail.columns.versionDescription"
          ),
          key: "versionDescription"
        },
        {
          title: "Entry Class",
          key: "entry"
        },
        {
          title: this.$t(
            "message.streamis.jobDetail.columns.versionUploadTime"
          ),
          key: "versionUploadTime"
        },
        {
          title: this.$t("message.streamis.jobDetail.columns.operation"),
          key: "operation"
        }
      ],
      flinkDatas: [],
      dependJar: [],
      userResource: [],
      programArguement: "fsjfkldsal sgfsajfjdsa fsadjsakfsda"
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
    margin-left: 20px;
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
